package org.jrdf.query.expression;

import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.util.EqualsUtil;

public class StrOperator<V extends ExpressionVisitor> implements Operator<V> {
    private static final long serialVersionUID = -3910514962392635053L;
    private static final int DUMMY_HASHCODE = 47;
    private AttributeValuePair singleAvp;

    private StrOperator() {
    }

    public StrOperator(AttributeValuePair newSingleAvp) {
        this.singleAvp = newSingleAvp;
    }

    public void accept(ExpressionVisitor expressionVisitor) {
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
        return determineEqualityFromFields(this, (StrOperator) obj);
    }

    @Override
    public int hashCode() {
        // FIXME TJA: Test drive out values of triple.hashCode()
        return DUMMY_HASHCODE;
    }

    @Override
    public String toString() {
        return " str (" + singleAvp.getAttribute() + ") " + singleAvp.getOperation() + " " + singleAvp.getValue() +
            "} ";
    }

    private boolean determineEqualityFromFields(StrOperator s1, StrOperator s2) {
        return s1.singleAvp.equals(s2.singleAvp);
    }
}
