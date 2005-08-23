package org.jrdf.query.relation.constants;

import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.query.relation.AttributeNameValue;

/**
 * Something in here
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class TrueAttributeNameValue implements AttributeNameValue {
    private static final Node TRUE_NODE = TrueNode.TRUE;
    public static final AttributeNameValue TRUE_NAME_VALUE =
        new TrueAttributeNameValue();

    private TrueAttributeNameValue() {
    }

    public PredicateNode getName() {
        return (PredicateNode) TRUE_NODE;
    }

    public ObjectNode getValue() {
        return (ObjectNode) TRUE_NODE;
    }
}
