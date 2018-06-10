package org.apache.spark.sql.command

import java.util.HashMap
import scala.collection.JavaConversions._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.execution.command.RunnableCommand

/**
  * Created by taofu on 2018/6/10.
  */
case class LoadCommand(path:String, tableName:String, options:HashMap[String, String]) extends RunnableCommand{

    override def run(sparkSession: SparkSession): Seq[Row] = {
        val hadoopConf = sparkSession.sparkContext.hadoopConfiguration
        val fs = FileSystem.get(hadoopConf)
        if(!fs.exists(new Path(path))) throw new RuntimeException(path+" does not exists!")

        var reader = sparkSession.read.format("csv")
        for(key <- options.keySet()) reader = reader.option(key, options.get(key))
        reader.load(path).createOrReplaceTempView(tableName)
        Seq.empty[Row]
    }
}
