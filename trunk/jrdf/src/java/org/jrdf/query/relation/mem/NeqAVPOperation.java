package org.jrdf.query.relation.mem;

import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.AttributeValuePair;

import java.util.SortedSet;

public class NeqAVPOperation implements AVPOperation {
    private static final long serialVersionUID = -5281476871321027939L;

    public boolean addAttributeValuePair(AttributeValuePairComparator avpComparator,
            SortedSet<AttributeValuePair> newAttributeValues, AttributeValuePair lhs, AttributeValuePair rhs) {
        if (avpComparator.compare(lhs, rhs) != 0) {
            newAttributeValues.add(rhs);
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "!=";
    }
}