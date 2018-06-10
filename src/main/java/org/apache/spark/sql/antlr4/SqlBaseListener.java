// Generated from /Users/taofu/workspace/sparksql-command/src/main/antlr4/org/apache/spark/sql/SqlBase.g4 by ANTLR 4.5
package org.apache.spark.sql.antlr4;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SqlBaseParser}.
 */
public interface SqlBaseListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singleStatement}.
	 * @param ctx the parse tree
	 */
	void enterSingleStatement(SqlBaseParser.SingleStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singleStatement}.
	 * @param ctx the parse tree
	 */
	void exitSingleStatement(SqlBaseParser.SingleStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mergeTable}
	 * labeled alternative in {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterMergeTable(SqlBaseParser.MergeTableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mergeTable}
	 * labeled alternative in {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitMergeTable(SqlBaseParser.MergeTableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LOADTABLE}
	 * labeled alternative in {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterLOADTABLE(SqlBaseParser.LOADTABLEContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LOADTABLE}
	 * labeled alternative in {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitLOADTABLE(SqlBaseParser.LOADTABLEContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EXPORTCSV}
	 * labeled alternative in {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterEXPORTCSV(SqlBaseParser.EXPORTCSVContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EXPORTCSV}
	 * labeled alternative in {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitEXPORTCSV(SqlBaseParser.EXPORTCSVContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#partitionSpec}.
	 * @param ctx the parse tree
	 */
	void enterPartitionSpec(SqlBaseParser.PartitionSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#partitionSpec}.
	 * @param ctx the parse tree
	 */
	void exitPartitionSpec(SqlBaseParser.PartitionSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#partitionVal}.
	 * @param ctx the parse tree
	 */
	void enterPartitionVal(SqlBaseParser.PartitionValContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#partitionVal}.
	 * @param ctx the parse tree
	 */
	void exitPartitionVal(SqlBaseParser.PartitionValContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#loadOptions}.
	 * @param ctx the parse tree
	 */
	void enterLoadOptions(SqlBaseParser.LoadOptionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#loadOptions}.
	 * @param ctx the parse tree
	 */
	void exitLoadOptions(SqlBaseParser.LoadOptionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#optionVal}.
	 * @param ctx the parse tree
	 */
	void enterOptionVal(SqlBaseParser.OptionValContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#optionVal}.
	 * @param ctx the parse tree
	 */
	void exitOptionVal(SqlBaseParser.OptionValContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nullLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterNullLiteral(SqlBaseParser.NullLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nullLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitNullLiteral(SqlBaseParser.NullLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intervalLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterIntervalLiteral(SqlBaseParser.IntervalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intervalLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitIntervalLiteral(SqlBaseParser.IntervalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeConstructor}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterTypeConstructor(SqlBaseParser.TypeConstructorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code typeConstructor}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitTypeConstructor(SqlBaseParser.TypeConstructorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numericLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterNumericLiteral(SqlBaseParser.NumericLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numericLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitNumericLiteral(SqlBaseParser.NumericLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(SqlBaseParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(SqlBaseParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(SqlBaseParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(SqlBaseParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#tableIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterTableIdentifier(SqlBaseParser.TableIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#tableIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitTableIdentifier(SqlBaseParser.TableIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(SqlBaseParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(SqlBaseParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unquotedIdentifier}
	 * labeled alternative in {@link SqlBaseParser#strictIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterUnquotedIdentifier(SqlBaseParser.UnquotedIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unquotedIdentifier}
	 * labeled alternative in {@link SqlBaseParser#strictIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitUnquotedIdentifier(SqlBaseParser.UnquotedIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code quotedIdentifierAlternative}
	 * labeled alternative in {@link SqlBaseParser#strictIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterQuotedIdentifierAlternative(SqlBaseParser.QuotedIdentifierAlternativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code quotedIdentifierAlternative}
	 * labeled alternative in {@link SqlBaseParser#strictIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitQuotedIdentifierAlternative(SqlBaseParser.QuotedIdentifierAlternativeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#quotedIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterQuotedIdentifier(SqlBaseParser.QuotedIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#quotedIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitQuotedIdentifier(SqlBaseParser.QuotedIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#interval}.
	 * @param ctx the parse tree
	 */
	void enterInterval(SqlBaseParser.IntervalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#interval}.
	 * @param ctx the parse tree
	 */
	void exitInterval(SqlBaseParser.IntervalContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#intervalField}.
	 * @param ctx the parse tree
	 */
	void enterIntervalField(SqlBaseParser.IntervalFieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#intervalField}.
	 * @param ctx the parse tree
	 */
	void exitIntervalField(SqlBaseParser.IntervalFieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#intervalValue}.
	 * @param ctx the parse tree
	 */
	void enterIntervalValue(SqlBaseParser.IntervalValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#intervalValue}.
	 * @param ctx the parse tree
	 */
	void exitIntervalValue(SqlBaseParser.IntervalValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#booleanValue}.
	 * @param ctx the parse tree
	 */
	void enterBooleanValue(SqlBaseParser.BooleanValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#booleanValue}.
	 * @param ctx the parse tree
	 */
	void exitBooleanValue(SqlBaseParser.BooleanValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code decimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterDecimalLiteral(SqlBaseParser.DecimalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code decimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitDecimalLiteral(SqlBaseParser.DecimalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code integerLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterIntegerLiteral(SqlBaseParser.IntegerLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code integerLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitIntegerLiteral(SqlBaseParser.IntegerLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bigIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterBigIntLiteral(SqlBaseParser.BigIntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bigIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitBigIntLiteral(SqlBaseParser.BigIntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code smallIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterSmallIntLiteral(SqlBaseParser.SmallIntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code smallIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitSmallIntLiteral(SqlBaseParser.SmallIntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tinyIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterTinyIntLiteral(SqlBaseParser.TinyIntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tinyIntLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitTinyIntLiteral(SqlBaseParser.TinyIntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code doubleLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterDoubleLiteral(SqlBaseParser.DoubleLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code doubleLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitDoubleLiteral(SqlBaseParser.DoubleLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bigDecimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterBigDecimalLiteral(SqlBaseParser.BigDecimalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bigDecimalLiteral}
	 * labeled alternative in {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitBigDecimalLiteral(SqlBaseParser.BigDecimalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#nonReserved}.
	 * @param ctx the parse tree
	 */
	void enterNonReserved(SqlBaseParser.NonReservedContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#nonReserved}.
	 * @param ctx the parse tree
	 */
	void exitNonReserved(SqlBaseParser.NonReservedContext ctx);
}