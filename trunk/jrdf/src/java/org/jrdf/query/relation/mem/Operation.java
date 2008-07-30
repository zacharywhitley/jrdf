package org.jrdf.query.relation.mem;

import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;

import java.io.Serializable;
import java.util.SortedSet;

public interface Operation extends Serializable {
    boolean addAttributeValuePair(AttributeValuePairComparator avpComparator,
            SortedSet<AttributeValuePair> newAttributeValues, AttributeValuePair lhs, AttributeValuePair rhs);
}
