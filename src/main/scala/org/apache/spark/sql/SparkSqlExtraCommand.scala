package org.apache.spark.sql

import org.apache.spark.SparkContext
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.execution.QueryExecution
import org.apache.spark.sql.parser.SparkSqlParser

/**
  * Created by taofu on 2018/6/10.
  */
object SparkSqlExtraCommand {

    var sparkContext:SparkContext = null

    def parserSql(sparkSession:SparkSession, sql:String): Dataset[_] ={
        sparkContext = sparkSession.sparkContext
        val parser = new SparkSqlParser(sparkSession)
        val plan = parser.parse(sql)
        if(plan!=null){
            val execution = sparkSession.sessionState.executePlan(plan)
            execution.assertAnalyzed()
            val clazz = classOf[Dataset[_]]
            val constructor = clazz.getDeclaredConstructor(classOf[SparkSession], classOf[QueryExecution], classOf[Encoder[_]])
            val dataSet = constructor.newInstance(sparkSession, execution, RowEncoder.apply(execution.analyzed.schema))
            return dataSet
        }
        null
    }
}
