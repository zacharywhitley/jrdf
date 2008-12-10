package org.jrdf.query.expression;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.util.EqualsUtil;

import java.util.Map;

public final class StrOperator<V extends ExpressionVisitor> implements Operator<V> {
    private static final long serialVersionUID = -3910514962392635053L;
    private static final int DUMMY_HASHCODE = 47;
    private Map<Attribute, ValueOperation> singleAvp;
    protected static final String STR = "str";

    private StrOperator() {
    }

    public StrOperator(Map<Attribute, ValueOperation> newSingleAvp) {
        this.singleAvp = newSingleAvp;
    }

    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visitStr(this);
    }

    public Map<Attribute, ValueOperation> getAVO() {
        return singleAvp;
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

    public int size() {
        return 2;
    }

    @Override
    public int hashCode() {
        // FIXME TJA: Test drive out values of triple.hashCode()
        return DUMMY_HASHCODE + DUMMY_HASHCODE * singleAvp.hashCode() + STR.hashCode();
    }

    @Override
    public String toString() {
        Map.Entry<Attribute, ValueOperation> attributeValueOperationEntry = singleAvp.entrySet().iterator().next();
        Attribute attribute = attributeValueOperationEntry.getKey();
        return STR + " (" + attribute + ") ";
    }

    private boolean determineEqualityFromFields(StrOperator s1, StrOperator s2) {
        return s1.singleAvp.equals(s2.singleAvp);
    }
}
