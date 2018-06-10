package org.apache.spark.sql.parser

import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream, IntStream}
import org.apache.commons.lang.StringUtils
import org.apache.spark.sql.{ParseErrorListener, SparkSession}
import org.apache.spark.sql.antlr4.{SqlBaseLexer, SqlBaseParser}
import org.apache.spark.sql.ParseException
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.slf4j.LoggerFactory

/**
  * Created by taofu on 2018/6/10.
  */
class SparkSqlParser(sparkSession: SparkSession) {

    private final val logger = LoggerFactory.getLogger(classOf[SparkSqlParser])

    val visitor = new SparkSqlParserVisitor(sparkSession.sessionState.conf)

    @throws[Exception]
    def parse(command: String): LogicalPlan ={
        val charStream = new ANTLRNoCaseStringStream(command)
        val lexer = new SqlBaseLexer(charStream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(new ParseErrorListener)

        val tokens = new CommonTokenStream(lexer)
        val parser = new SqlBaseParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(new ParseErrorListener)

        try{
            val plan = visitor.visitSingleStatement(parser.singleStatement)
            logger.info("command parsed as {}", plan)
            plan
        } catch {
            case e:ParseException =>
                if(StringUtils.isNotEmpty(e.getCommand)) throw e
                else throw e.withCommand(command)
        }
    }
}

class ANTLRNoCaseStringStream(input: String) extends ANTLRInputStream(input){

    override def LA(i: Int): Int = {
        val la = super.LA(i)
        if (la == 0 || la == IntStream.EOF) return la
        else return Character.toUpperCase(la)
    }
}