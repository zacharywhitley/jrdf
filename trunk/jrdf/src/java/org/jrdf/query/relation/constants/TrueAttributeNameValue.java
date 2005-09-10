package org.jrdf.query.relation.constants;

import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.query.relation.AttributeNameValue;

/**
 * A class which simply contains the True Name Value constant.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class TrueAttributeNameValue implements AttributeNameValue {
    /**
     * Represents an attribute name value pair, or TRUE.
     */
    public static final AttributeNameValue TRUE_NAME_VALUE =
            new TrueAttributeNameValue();

    private static final Node TRUE_NODE = TrueNode.TRUE;

    private TrueAttributeNameValue() {
    }

    public PredicateNode getName() {
        return (PredicateNode) TRUE_NODE;
    }

    public ObjectNode getValue() {
        return (ObjectNode) TRUE_NODE;
    }
}
