package org.jrdf.query.relation.mem;

import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;

import java.util.Map;

public final class NeqAVPOperation implements AVPOperation {
    private static final NodeComparator COMPARATOR = new ComparatorFactoryImpl().createNodeComparator();
    /**
     * The constant to indicate not equals operation.
     */
    public static final AVPOperation NEQUALS = new NeqAVPOperation();
    private static final long serialVersionUID = -5281476871321027939L;

    private NeqAVPOperation() {
    }

    public boolean addAttributeValuePair(Attribute attribute, Map<Attribute, ValueOperation> newAttributeValues,
        ValueOperation lhs, ValueOperation rhs) {
        Node lhsValue = lhs.getValue();
        Node rhsValue = rhs.getValue();
        if (COMPARATOR.compare(lhsValue, rhsValue) != 0) {
            newAttributeValues.put(attribute, rhs);
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