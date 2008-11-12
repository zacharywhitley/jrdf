package org.jrdf.query.expression;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.util.EqualsUtil;

import java.util.Map;

public class EmptyOperator<V extends ExpressionVisitor> implements Operator<V>, LogicExpression<V> {
    private static final long serialVersionUID = 4636572000909954329L;
    private static final int DUMMY_HASHCODE = 47;

    //TODO Come back and make these two methods sensible.
    public Map<Attribute, ValueOperation> getAttributeValuePair() {
        return null;
    }

    public void accept(ExpressionVisitor v) {
    }

    public int size() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (EqualsUtil.isNull(obj)) {
            return false;
        }
        if (EqualsUtil.sameReference(this, obj)) {
            return true;
        }
        if (EqualsUtil.differentClasses(this, obj)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        // FIXME TJA: Test drive out values of triple.hashCode()
        return DUMMY_HASHCODE + toString().hashCode();
    }

    @Override
    public String toString() {
        return " EMPTY ";
    }
}