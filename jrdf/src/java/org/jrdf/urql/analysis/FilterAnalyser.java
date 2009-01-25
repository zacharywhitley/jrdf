package org.jrdf.urql.analysis;

import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.urql.parser.node.Switch;
import org.jrdf.urql.parser.parser.ParserException;

public interface FilterAnalyser extends Switch {
    LogicExpression<ExpressionVisitor> getExpression() throws ParserException;
}
