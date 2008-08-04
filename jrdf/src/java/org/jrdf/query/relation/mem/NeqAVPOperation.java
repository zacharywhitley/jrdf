package org.jrdf.query.relation.mem;

import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.AttributeValuePair;

import java.util.SortedSet;

public final class NeqAVPOperation implements AVPOperation {
    /**
     * The constant to indicate equals operation.
     */
    public static final AVPOperation NEQUALS = new NeqAVPOperation();
    private static final long serialVersionUID = -5281476871321027939L;

    private NeqAVPOperation() {
    }

    public boolean addAttributeValuePair(AttributeValuePairComparator avpComparator,
            SortedSet<AttributeValuePair> newAttributeValues, AttributeValuePair lhs, AttributeValuePair rhs) {
        if (avpComparator.compare(lhs, rhs) != 0) {
            newAttributeValues.add(rhs);
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
        return "!=";
    }
}