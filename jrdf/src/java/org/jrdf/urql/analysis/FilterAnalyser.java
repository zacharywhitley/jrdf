package org.jrdf.urql.analysis;

import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.Expression;
import org.jrdf.urql.parser.parser.ParserException;
import org.jrdf.urql.parser.node.Switch;

public interface FilterAnalyser<V extends ExpressionVisitor> extends Switch {
    Expression<V> getExpression() throws ParserException;
}
