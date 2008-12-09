package org.jrdf.query.relation.mem;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;

import java.io.Serializable;
import java.util.Map;

public interface AVPOperation extends Serializable {
    boolean addAttributeValuePair(Attribute attribute, Map<Attribute, ValueOperation> newAttributeValues,
        ValueOperation lhs, ValueOperation rhs);
}
