package org.jrdf.query.expression;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.util.EqualsUtil;

import java.util.Map;

public class StrOperator<V extends ExpressionVisitor> implements Operator<V> {
    private static final long serialVersionUID = -3910514962392635053L;
    private static final int DUMMY_HASHCODE = 47;
    private Map<Attribute, ValueOperation> singleAvp;

    private StrOperator() {
    }

    public StrOperator(Map<Attribute, ValueOperation> newSingleAvp) {
        this.singleAvp = newSingleAvp;
    }

    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visitOperator(this);
    }

    public Map<Attribute, ValueOperation> getAttributeValuePair() {
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

    @Override
    public int hashCode() {
        // FIXME TJA: Test drive out values of triple.hashCode()
        return DUMMY_HASHCODE + singleAvp.hashCode();
    }

    @Override
    public String toString() {
        Map.Entry<Attribute, ValueOperation> attributeValueOperationEntry = singleAvp.entrySet().iterator().next();
        Attribute attribute = attributeValueOperationEntry.getKey();
        return "str (" + attribute + ") ";
    }

    private boolean determineEqualityFromFields(StrOperator s1, StrOperator s2) {
        return s1.singleAvp.equals(s2.singleAvp);
    }
}
