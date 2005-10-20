package org.jrdf.graph;

import java.io.Serializable;
import java.io.ObjectStreamException;

/**
 * A node which represents any object - unconstrained.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class AnyObjectNode implements ObjectNode, Serializable {

    /**
     * The any object node constant - represents an unconstrained object node.
     */
    public static final AnyObjectNode ANY_OBJECT_NODE = new AnyObjectNode();

    private static final long serialVersionUID = 8654340032080018169L;
    private static final String STRING_FORM = "ANY_OBJECT";

    private AnyObjectNode() {
    }

    public String toString() {
        return STRING_FORM;
    }

    private Object readResolve() throws ObjectStreamException {
        return ANY_OBJECT_NODE;
    }
}