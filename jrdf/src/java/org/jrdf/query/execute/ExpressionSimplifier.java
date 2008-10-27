package org.jrdf.query.execute;

import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;

public interface ExpressionSimplifier extends ExpressionVisitor {

    Expression<ExpressionVisitor> getExpression();
}
