package org.jrdf.urql.analysis;

import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.urql.parser.node.Switch;
import org.jrdf.urql.parser.parser.ParserException;

public interface NumericExpressionAnalyser extends Switch {
    <V extends ExpressionVisitor> Expression<V> getExpression() throws ParserException;
}