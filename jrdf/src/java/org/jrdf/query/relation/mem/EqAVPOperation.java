package org.jrdf.query.relation.mem;

import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;

import java.util.SortedSet;

public final class EqAVPOperation implements AVPOperation {
    /**
     * The constant to indicate equals operation.
     */
    public static final AVPOperation EQUALS = new EqAVPOperation();
    private static final long serialVersionUID = 7483134960149229688L;

    private EqAVPOperation() {
    }

    public boolean addAttributeValuePair(AttributeValuePairComparator avpComparator,
            SortedSet<AttributeValuePair> newAttributeValues, AttributeValuePair lhs, AttributeValuePair rhs) {
        if (avpComparator.compare(lhs, rhs) == 0) {
            newAttributeValues.add(lhs);
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public String toString() {
        return "=";
    }
}
