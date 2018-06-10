package org.apache.spark.sql.strategy

import org.apache.spark.sql.Strategy
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.command.MergeTableCommand
import org.apache.spark.sql.execution.SparkPlan
import org.apache.spark.sql.plan.MergeTablePlan

/**
  * Created by taofu on 2018/6/10.
  */
object MergeTableStrategy extends Strategy with Serializable{

    override def apply(plan: LogicalPlan): Seq[SparkPlan] = plan match {
        case s:MergeTableCommand =>
            MergeTablePlan(plan.output, s.tableIdentifier, s.partitionSpec) :: Nil
        case _ => Nil
    }
}
