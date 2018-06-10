package org.apache.spark.sql.plan

import java.util
import java.util.LinkedList

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path, PathFilter}
import org.apache.parquet.example.data.Group
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import scala.collection.JavaConversions._
import org.apache.spark.sql.{CombineParquetInputFormat, Row, SparkSession}
import org.apache.spark.sql.antlr4.SqlBaseParser.{PartitionSpecContext, TableIdentifierContext}
import org.apache.spark.sql.catalyst.{InternalRow, TableIdentifier}
import org.apache.spark.sql.catalyst.expressions.Attribute
import org.apache.spark.sql.execution.SparkPlan
import org.apache.spark.sql.parser.SparkParserConfig
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.utils.MergeTableUtils
import org.slf4j.LoggerFactory

/**
  * Created by taofu on 2018/6/10.
  */
case class MergeTablePlan(output: Seq[Attribute], tableIdentifier:TableIdentifierContext, partitionSpec: PartitionSpecContext) extends SparkPlan {

    private final val logger = LoggerFactory.getLogger(classOf[MergeTablePlan])


    private def isValidFile(path:Path, sparkContext:SparkContext): Boolean ={
        val fs = path.getFileSystem(sparkContext.hadoopConfiguration)
        if(fs.isFile(path)){
            val fileLength = fs.getFileStatus(path).getLen
            val maxLength = sparkContext.getConf.get(SparkParserConfig.sparkMergeFilterSize, "134217728").toLong
            if (fileLength <= maxLength){
                return true
            }
        }
        false
    }

    private def mergePath(sparkSession: SparkSession, hadoopConf:Configuration, schema: StructType, location: String): Unit ={

        val fs = FileSystem.get(hadoopConf)
        if(!fs.exists(new Path(location))){
            throw new RuntimeException(location+" does not exist!")
        }

        val mergeNum = sparkContext.getConf.get(SparkParserConfig.sparkMergeFileNum, "6")
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

        logger.info("prepare to merge data under path:{} total {} files", location, inputFileList.size())
        val time = System.currentTimeMillis()
        val fieldNum = schema.fields.length

        hadoopConf.set("mapreduce.input.fileinputformat.inputdir", location+"/*")
        hadoopConf.setClass("mapreduce.input.pathFilter.class", classOf[CombineParquetInputFormat.CombineFilter], classOf[PathFilter])
        val rdd = sparkContext.newAPIHadoopRDD(hadoopConf, classOf[CombineParquetInputFormat[Group]], classOf[Void], classOf[Group])
        val rowRdd = rdd.map(tuple =>{
            val group = tuple._2
            val array = new Array[Any](fieldNum)
            var index = 0
            for(field <- schema.fields){
                array(index) = MergeTableUtils.getValue(field.dataType.typeName, group, field.name)
                index += 1
            }
            Row.fromSeq(array.toSeq)
        })

        val tempDir = location+"/.mergeTemp"
        val tempPath = new Path(tempDir)
        if(fs.exists(tempPath)){
            fs.delete(tempPath, true)
        }
        val writer = sparkSession.createDataFrame(rowRdd.coalesce(mergeNum.toInt, false), schema).write
        writer.parquet(tempDir)

        val destList = MergeTableUtils.getPathFromDirectory(hadoopConf, tempDir)
        for(path <- destList){
            if(path.getName.endsWith("parquet")){
                fs.rename(path, new Path(location+"/"+path.getName))
            }
        }

        for(path <- inputFileList){
            fs.delete(path.getPath, true)
        }
        fs.delete(tempPath, true)

        logger.info("merge finished, cost {} ms", (System.currentTimeMillis()-time))
    }


    def mergeRecursive(sparkSession: SparkSession, hadoopConf:Configuration, schema: StructType, location: String): Unit ={
        val fs = FileSystem.get(hadoopConf)
        for(status <- fs.listStatus(new Path(location))){
            if(status.isDirectory){
                mergeRecursive(sparkSession, hadoopConf, schema, status.getPath.toString)
            }
        }
        mergePath(sparkSession, hadoopConf, schema, location)
    }

    override protected def doExecute(): RDD[InternalRow] = {
        try{
            val maxSize = sparkContext.getConf.get(SparkParserConfig.sparkInputSplitMaxSize, "134217728")
            val minSize = sparkContext.getConf.get(SparkParserConfig.sparkInputSplitMinSize, "1024")

            SparkParserConfig.skipPathFilter = false
            val sparkSession = SparkSession.builder().config(sparkContext.getConf).getOrCreate()
            sparkSession.sparkContext.setLocalProperty("spark.sql.execution.id", null)
            val catalog = sparkSession.sessionState.catalog
            val tableName = tableIdentifier.table.getText
            var db = catalog.getCurrentDatabase
            if(tableIdentifier.db!=null){
                db = tableIdentifier.db.getText
            }
            val tableMeta = catalog.getTableMetadata(TableIdentifier(tableName, Option(db)))
            logger.info("table {} type {}, location: {}", tableName, tableMeta.tableType, tableMeta.location.getPath)

            val hadoopConf = sparkContext.hadoopConfiguration
            hadoopConf.setLong("mapreduce.input.fileinputformat.split.maxsize", maxSize.toLong)
            hadoopConf.setLong("mapreduce.input.fileinputformat.split.minsize", minSize.toLong)
            hadoopConf.set("mapreduce.input.fileinputformat.input.dir.recursive", "true")

            if(partitionSpec==null){
                val recursive = sparkContext.getConf.get(SparkParserConfig.sparkMergeRecursive, "true")
                if(recursive.equalsIgnoreCase("true")){
                    logger.info("merge table {} recursively", tableName)
                    mergeRecursive(sparkSession, hadoopConf, tableMeta.schema, tableMeta.location.getPath)
                }else{
                    mergePath(sparkSession, hadoopConf, tableMeta.schema, tableMeta.location.getPath)
                }
            }else{
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
                mergePath(sparkSession, hadoopConf, tableMeta.schema, location)
            }
        }catch {
            case e:Throwable=>
                logger.error("merge table error:"+ExceptionUtils.getStackTrace(e))
                throw e
        }finally {
            SparkParserConfig.skipPathFilter = true
            sparkContext.hadoopConfiguration.unset("mapreduce.input.fileinputformat.inputdir")
            sparkContext.hadoopConfiguration.unset("mapreduce.input.pathFilter.class")
            sparkContext.hadoopConfiguration.unset("mapreduce.input.fileinputformat.split.maxsize")
            sparkContext.hadoopConfiguration.unset("mapreduce.input.fileinputformat.split.minsize")
            sparkContext.hadoopConfiguration.unset("mapreduce.input.fileinputformat.input.dir.recursive")
        }
        sparkContext.emptyRDD
    }

    override def children: Seq[SparkPlan] = Nil
}
