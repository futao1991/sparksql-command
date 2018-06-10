package org.apache.spark.sql.command

import java.util
import java.util.LinkedList

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path, PathFilter}
import org.apache.spark.SparkContext

import scala.collection.JavaConversions._
import org.apache.spark.sql.{CombineParquetInputFormat, Row, SparkSession}
import org.apache.spark.sql.antlr4.SqlBaseParser.{PartitionSpecContext, TableIdentifierContext}
import org.apache.spark.sql.catalyst.TableIdentifier
import org.apache.spark.sql.execution.command.RunnableCommand
import org.apache.spark.sql.parser.SparkParserConfig
import org.apache.spark.sql.utils.MergeTableUtils
import org.slf4j.LoggerFactory

/**
  * Created by taofu on 2018/6/10.
  */
case class MergeTableCommand(tableIdentifier:TableIdentifierContext, partitionSpec: PartitionSpecContext) extends RunnableCommand {

    private final val logger = LoggerFactory.getLogger(classOf[MergeTableCommand])

    private def isValidFile(path:Path, sparkContext:SparkContext): Boolean ={
        val fs = path.getFileSystem(sparkContext.hadoopConfiguration)
        if(fs.isFile(path) && !path.getName.startsWith(".")){
            val fileLength = fs.getFileStatus(path).getLen
            val maxLength = sparkContext.getConf.get(SparkParserConfig.sparkMergeFilterSize, "134217728").toLong
            if (fileLength <= maxLength){
                return true
            }
        }
        false
    }


    private def mergePath(sparkSession: SparkSession, location: String): Unit ={

        val fs = FileSystem.get(sparkSession.sparkContext.hadoopConfiguration)
        if(!fs.exists(new Path(location))){
            throw new RuntimeException(location+" does not exist!")
        }

        val mergeNum = sparkSession.sparkContext.getConf.get(SparkParserConfig.sparkMergeFileNum, "6")
        val inputFileList = new util.LinkedList[FileStatus]()
        for(path <- fs.listStatus(new Path(location))){
            if(path.isFile && isValidFile(path.getPath, sparkSession.sparkContext)){
                inputFileList.add(path)
            }
        }
        if(inputFileList.size() <= mergeNum.toInt){
            logger.info("path {} contains no valid parquet file!", location)
            return
        }

        val time = System.currentTimeMillis()
        logger.info("prepare to merge data under path:{} total {} files", location, inputFileList.size())

        val tempDir = location+"/.mergeTemp"
        val tempPath = new Path(tempDir)
        if(fs.exists(tempPath)){
            fs.delete(tempPath, true)
        }
        val df = sparkSession.read.format("parquet").load(location)
        df.coalesce(mergeNum.toInt).write.parquet(tempDir)

        val destList = MergeTableUtils.getPathFromDirectory(sparkSession.sparkContext.hadoopConfiguration, tempDir)
        for(path <- destList){
            if(path.getName.endsWith("parquet")){
                fs.rename(path, new Path(location+"/"+path.getName))
            }
        }

        for(path <- inputFileList){
            fs.delete(path.getPath, true)
        }
        fs.delete(tempPath, true)

        logger.info("merge finished, cost {} ms", System.currentTimeMillis()-time)
    }


    def mergeRecursive(sparkSession: SparkSession, location: String): Unit ={
        val fs = FileSystem.get(sparkSession.sparkContext.hadoopConfiguration)
        for(status <- fs.listStatus(new Path(location))){
            if(status.isDirectory){
                mergeRecursive(sparkSession, status.getPath.toString)
            }
        }
        mergePath(sparkSession, location)
    }

    override def run(sparkSession: SparkSession): Seq[Row] = {
        try{
            val catalog = sparkSession.sessionState.catalog
            val tableName = tableIdentifier.table.getText
            var db = catalog.getCurrentDatabase
            if(tableIdentifier.db!=null){
                db = tableIdentifier.db.getText
            }
            val tableMeta = catalog.getTableMetadata(TableIdentifier(tableName, Option(db)))
            logger.info("table {} type {}, location: {}", tableName, tableMeta.location, tableMeta.tableType)

            val maxSize = sparkSession.sparkContext.getConf.get(SparkParserConfig.sparkInputSplitMaxSize, "134217728")
            val minSize = sparkSession.sparkContext.getConf.get(SparkParserConfig.sparkInputSplitMinSize, "1024")

            SparkParserConfig.skipPathFilter = false
            val hadoopConf = sparkSession.sparkContext.hadoopConfiguration
            hadoopConf.setLong("mapreduce.input.fileinputformat.split.maxsize", maxSize.toLong)
            hadoopConf.setLong("mapreduce.input.fileinputformat.split.minsize", minSize.toLong)
            hadoopConf.set("mapreduce.input.fileinputformat.input.dir.recursive", "true")
            hadoopConf.set("mapreduce.input.fileinputformat.inputdir", tableMeta.location.getPath+"/*")
            hadoopConf.setClass("mapreduce.input.pathFilter.class", classOf[CombineParquetInputFormat.CombineFilter], classOf[PathFilter])

            if(partitionSpec == null){
                val recursive = sparkSession.sparkContext.getConf.get(SparkParserConfig.sparkMergeRecursive, "true")
                if(recursive.equalsIgnoreCase("true")){
                    logger.info("merge table {} recursively", tableName)
                    mergeRecursive(sparkSession, tableMeta.location.getPath)
                } else{
                    mergePath(sparkSession, tableMeta.location.getPath)
                }
            } else{
                val list = new LinkedList[String]()
                for(partition <- partitionSpec.partitionVal()){
                    if(partition.getText.contains("<")){
                        list.add(StringUtils.substringBefore(partition.getText, "<"))
                    } else{
                        val key = partition.identifier().getText
                        var value = partition.constant().getText
                        if(value.startsWith("'") && value.endsWith("'")) value = StringUtils.substringBetween(value, "'")
                        list.add(key+"="+value)
                    }
                }
                val location = tableMeta.location.getPath+"/"+StringUtils.join(list, "/")
                mergePath(sparkSession, location)
            }
        } catch {
            case e:Throwable =>
                logger.error("merge table error:"+ExceptionUtils.getStackTrace(e))
                throw e
        } finally {
            SparkParserConfig.skipPathFilter = true
            sparkSession.sparkContext.hadoopConfiguration.unset("mapreduce.input.fileinputformat.inputdir")
            sparkSession.sparkContext.hadoopConfiguration.unset("mapreduce.input.pathFilter.class")
            sparkSession.sparkContext.hadoopConfiguration.unset("mapreduce.input.fileinputformat.split.maxsize")
            sparkSession.sparkContext.hadoopConfiguration.unset("mapreduce.input.fileinputformat.split.minsize")
            sparkSession.sparkContext.hadoopConfiguration.unset("mapreduce.input.fileinputformat.input.dir.recursive")
        }

        Seq.empty[Row]
    }
}
