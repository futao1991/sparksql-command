package org.apache.spark.sql.parser

/**
  * Created by taofu on 2018/6/10.
  */
object SparkParserConfig {

    var skipPathFilter = true

    val sparkMergeRecursive = "spark.parser.merge.recursive"

    val sparkMergeFileNum = "spark.parser.merge.num"

    val sparkMergeFilterSize = "spark.parser.merge.filter.size"

    val sparkInputSplitMaxSize = "spark.parser.input.split.maxsize"

    val sparkInputSplitMinSize = "spark.parser.input.split.minsize"

    val sparkExportDefaultPath = "/user/hive/warehouse/"
}
