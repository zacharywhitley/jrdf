package org.jrdf.graph;

/**
 * A node which represents any object - unconstrained.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class AnyObjectNode implements ObjectNode {

    /**
     * The any object node constant - represents an unconstrained object node.
     */
    public static final AnyObjectNode ANY_OBJECT_NODE = new AnyObjectNode();

    // TODO serialVersionUID not test driven.
    private static final long serialVersionUID = 8654340032080018169L;
    private static final String STRING_FORM = "ANY_OBJECT";

    private AnyObjectNode() {
    }

    protected Object readResolve() throws java.io.ObjectStreamException {
        return ANY_OBJECT_NODE;
    }

    public String toString() {
        return STRING_FORM;
    }
}