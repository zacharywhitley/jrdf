package org.jrdf.graph;

import java.io.Serializable;
import java.io.ObjectStreamException;

/**
 * A node which represents any node (subject, predicate or object) - unconstrained.
 * Shouldn't be needed unless the positional information (subject, predicate, or object) is lost or you want an
 * object to represent all three positions in one object.  The former, should probably be avoided if possible.
 * @author Andrew Newman
 * @author Tom Adams
 * @version $Id$
 */
public final class AnyNode implements ObjectNode, Serializable {

    /**
     * The any node constant - represents an unconstrained object node.
     */
    public static final AnyNode ANY_NODE = new AnyNode();

    private static final long serialVersionUID = -4846208755020186880L;
    private static final String STRING_FORM = "ANY";

    private AnyNode() {
    }

    public String toString() {
        return STRING_FORM;
    }

    private Object readResolve() throws ObjectStreamException {
        return ANY_NODE;
    }
}