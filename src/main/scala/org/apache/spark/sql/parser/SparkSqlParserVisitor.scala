package org.apache.spark.sql.parser

import java.util

import scala.collection.JavaConversions._
import org.apache.commons.lang.StringUtils
import org.apache.spark.sql.antlr4.{SqlBaseBaseVisitor, SqlBaseParser}
import org.apache.spark.sql.catalyst.parser.ParserUtils.withOrigin
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.command.{ExportTableCommand, LoadCommand, MergeTableCommand}
import org.apache.spark.sql.internal.SQLConf

/**
  * Created by taofu on 2018/6/10.
  */
class SparkSqlParserVisitor(conf: SQLConf) extends SqlBaseBaseVisitor[AnyRef] {

    override def visitSingleStatement(ctx: SqlBaseParser.SingleStatementContext): LogicalPlan = withOrigin(ctx){
        visit(ctx.statement).asInstanceOf[LogicalPlan]
    }

    override def visitMergeTable(ctx: SqlBaseParser.MergeTableContext): LogicalPlan = withOrigin(ctx){
        MergeTableCommand(ctx.tableIdentifier, ctx.partitionSpec)
    }

    override def visitLOADTABLE(ctx: SqlBaseParser.LOADTABLEContext): LogicalPlan = withOrigin(ctx){
        val map = new util.HashMap[String, String]()
        if(ctx.loadOptions()!=null){
            for(option <- ctx.loadOptions().optionVal()){
                val text = option.getText
                if(text.contains("=")){
                    val key = StringUtils.substringBefore(text, "=").trim
                    var value = StringUtils.substringAfter(text, "=").trim
                    if(value.startsWith("'") && value.endsWith("'")) value = StringUtils.substringBetween(value, "'")
                    map.put(key, value)
                }
            }
        }
        var path = ctx.path.getText
        if(path.startsWith("'") && path.endsWith("'")) path = StringUtils.substringBetween(path, "'")

        LoadCommand(path, ctx.table.getText, map)
    }

    override def visitEXPORTCSV(ctx: SqlBaseParser.EXPORTCSVContext): LogicalPlan = withOrigin(ctx){
        var csvName = ctx.name.getText
        if(csvName.startsWith("'") && csvName.endsWith("'")) csvName = StringUtils.substringBetween(csvName, "'")

        ExportTableCommand(ctx.tableIdentifier, ctx.partitionSpec, csvName, ctx.loadOptions)
    }
}
