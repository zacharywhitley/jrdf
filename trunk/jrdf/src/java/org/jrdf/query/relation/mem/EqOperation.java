package org.jrdf.query.relation.mem;

import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.AttributeValuePair;

import java.util.SortedSet;

public class EqOperation implements Operation {
    private static final long serialVersionUID = 7483134960149229688L;

    public boolean addAttributeValuePair(AttributeValuePairComparator avpComparator,
            SortedSet<AttributeValuePair> newAttributeValues, AttributeValuePair lhs, AttributeValuePair rhs) {
        if (avpComparator.compare(lhs, rhs) == 0) {
            newAttributeValues.add(lhs);
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "=";
    }
}
