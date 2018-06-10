package org.apache.spark.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by taofu on 2018/6/10.
 */
public aspect AspectSql {

    private static final Logger logger = LoggerFactory.getLogger(AspectSql.class);

    public pointcut sparkSqlMethod(String sql): execution(public Dataset org.apache.spark.sql.SparkSession.sql(java.lang.String)) && args(sql) ;

    Dataset around(String sql): sparkSqlMethod(sql){
        logger.info("parser sql:"+sql);
        SparkSession sparkSession = (SparkSession)thisJoinPoint.getThis();
        try{
            return SparkSqlExtraCommand.parserSql(sparkSession, sql);
        } catch (ParseException e){
            logger.info("parser failed, execute as SparkSession.sql");
            return proceed(sql);
        }
    }
}
