package org.jrdf.urql.analysis;

import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.urql.parser.node.Switch;
import org.jrdf.urql.parser.parser.ParserException;

public interface FilterAnalyser<V extends ExpressionVisitor> extends Switch {
    LogicExpression<V> getExpression() throws ParserException;
}
