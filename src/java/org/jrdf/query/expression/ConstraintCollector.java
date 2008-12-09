package org.jrdf.query.expression;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;

import java.util.Map;

public interface ConstraintCollector extends ExpressionVisitor {
    Map<Attribute, ValueOperation> getOperators();
}
