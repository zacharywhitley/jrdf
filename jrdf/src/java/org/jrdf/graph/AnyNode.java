package org.jrdf.graph;

/**
 * A node which represents any node (subject, predicate or object) - unconstrained.
 * Shouldn't be needed unless the positional information (subject, predicate, or object) is lost or you want an
 * object to represent all three positions in one object.  The former, should probably be avoided if possible.
 * @author Andrew Newman
 * @author Tom Adams
 * @version $Id$
 */
public final class AnyNode implements ObjectNode {

    /**
     * The any node constant - represents an unconstrained object node.
     */
    public static final AnyNode ANY_NODE = new AnyNode();

    // TODO serialVersionUID not test driven.
    private static final long serialVersionUID = -4846208755020186880L;
    private static final String STRING_FORM = "ANY";

    private AnyNode() {
    }

    Object readResolve() throws java.io.ObjectStreamException {
        return ANY_NODE;
    }

    public String toString() {
        return STRING_FORM;
    }
}