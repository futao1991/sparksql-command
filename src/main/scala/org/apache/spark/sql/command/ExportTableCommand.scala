package org.apache.spark.sql.command

import java.util.{LinkedList, UUID}

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.hadoop.fs.{FileSystem, FileUtil, Path}
import org.apache.spark.sql._

import scala.collection.JavaConversions._
import org.apache.spark.sql.antlr4.SqlBaseParser.{LoadOptionsContext, PartitionSpecContext, TableIdentifierContext}
import org.apache.spark.sql.catalyst.TableIdentifier
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.execution.QueryExecution
import org.apache.spark.sql.execution.command.RunnableCommand
import org.apache.spark.sql.parser.SparkParserConfig
import org.slf4j.LoggerFactory

/**
  * Created by taofu on 2018/6/10.
  */
case class ExportTableCommand(tableIdentifier:TableIdentifierContext, partitionSpec: PartitionSpecContext, csvPath:String, loadOptions: LoadOptionsContext) extends RunnableCommand {

    private final val logger = LoggerFactory.getLogger(classOf[ExportTableCommand])

    override def run(sparkSession: SparkSession): Seq[Row] = {

        val catalog = sparkSession.sessionState.catalog
        val tableName = tableIdentifier.table.getText
        var db = catalog.getCurrentDatabase
        if(tableIdentifier.db!=null) db = tableIdentifier.db.getText

        var df:DataFrame = null
        val dbTable = TableIdentifier(tableName, Option(db))
        if(catalog.tableExists(dbTable)){
            val tableMeta = catalog.getTableMetadata(dbTable)
            logger.info("table {} type {}, location: {}", tableName, tableMeta.tableType, tableMeta.location.getPath)

            var location = tableMeta.location.getPath
            if(partitionSpec != null){
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
                location = location+"/"+StringUtils.join(list, "/")
            }
            df = sparkSession.read.format("parquet").option("inferSchema", "true").load(location)
        } else if(sparkSession.sessionState.catalog.getTempView(tableName).isDefined){
            val logicPlan = sparkSession.sessionState.catalog.getTempView(tableName).get
            val execution = sparkSession.sessionState.executePlan(logicPlan)
            execution.assertAnalyzed()

            val clazz = classOf[Dataset[Row]]
            val constructor = clazz.getDeclaredConstructor(classOf[SparkSession], classOf[QueryExecution], classOf[Encoder[_]])
            df = constructor.newInstance(sparkSession, execution, RowEncoder.apply(execution.analyzed.schema))
        } else {
            throw new RuntimeException(String.format("table %s is neither exists in database %s nor in the temp view!", tableName, db))
        }

        var savePath = SparkParserConfig.sparkExportDefaultPath
        if(StringUtils.startsWith(csvPath, "/")){
           savePath = StringUtils.substringBeforeLast(csvPath, "/")
        }
        savePath += UUID.randomUUID().toString

        val hadoopConf = sparkSession.sparkContext.hadoopConfiguration
        val fs = FileSystem.get(hadoopConf)

        try{
            if(fs.exists(new Path(savePath))) fs.delete(new Path(savePath), true)
            var writer = df.write.format("csv")
            var delimiter = ","
            if(loadOptions != null){
                for(option <- loadOptions.optionVal()){
                    val key = option.identifier().getText
                    var value = option.constant().getText
                    if(!StringUtils.equalsIgnoreCase(key, "header") && !StringUtils.equalsIgnoreCase(key, "inferSchema")){
                        if(value.startsWith("'") && value.endsWith("'")) value = StringUtils.substringBetween(value, "'")
                        writer = writer.option(key, value)
                        if(StringUtils.equalsIgnoreCase(key, "delimiter")) delimiter = value
                    }
                }
            }
            writer.option("header", "false").option("inferSchema", "true").save(savePath)

            val list = new LinkedList[String]()
            for(field <- df.schema.fields) list.add(field.name)
            val out = fs.create(new Path(savePath+"/000000-head.csv"))
            out.write(StringUtils.join(list, delimiter).getBytes("UTF-8"))
            out.write("\n".getBytes("UTF-8"))
            out.close()

            var dstPath = csvPath
            if(!StringUtils.startsWith(csvPath, "/")){
                dstPath = SparkParserConfig.sparkExportDefaultPath+csvPath
            }

            if(fs.exists(new Path(dstPath))) throw new RuntimeException("file "+dstPath+" already existed.")
            FileUtil.copyMerge(fs, new Path(savePath), fs, new Path(dstPath), true, hadoopConf, null)
            logger.info("saved csv file in "+dstPath)
        } catch {
            case e:Exception => if(fs.exists(new Path(savePath))) fs.delete(new Path(savePath), true)
                throw new RuntimeException(ExceptionUtils.getStackTrace(e))
        }
        Seq.empty[Row]
    }
}
