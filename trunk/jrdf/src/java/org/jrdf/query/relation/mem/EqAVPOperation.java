package org.jrdf.query.relation.mem;

import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;

import java.util.Map;

public final class EqAVPOperation implements AVPOperation {
    private static final NodeComparator COMPARATOR = new ComparatorFactoryImpl().createNodeComparator();
    /**
     * The constant to indicate equals operation.
     */
    public static final AVPOperation EQUALS = new EqAVPOperation();
    private static final long serialVersionUID = 7483134960149229688L;

    private EqAVPOperation() {
    }

    public boolean addAttributeValuePair(Attribute attribute, Map<Attribute, ValueOperation> newAttributeValues,
        ValueOperation lhs, ValueOperation rhs) {
        Node lhsValue = lhs.getValue();
        Node rhsValue = rhs.getValue();
        if (lhsValue.hashCode() == rhsValue.hashCode() && COMPARATOR.compare(lhsValue, rhsValue) == 0) {
            newAttributeValues.put(attribute, lhs);
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
