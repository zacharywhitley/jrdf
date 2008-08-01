package org.jrdf.query.expression;

import org.jrdf.util.EqualsUtil;
import org.jrdf.query.relation.AttributeValuePair;

public class EmptyOperator<V extends ExpressionVisitor> implements Operator<V> {
    private static final long serialVersionUID = 4636572000909954329L;
    private static final int DUMMY_HASHCODE = 47;

    //TODO Come back and make these two methods sensible.
    public AttributeValuePair getAttributeValuePair() {
        return null;
    }

    public void accept(ExpressionVisitor v) {
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
        return DUMMY_HASHCODE;
    }

    @Override
    public String toString() {
        return " EMPTY ";
    }
}