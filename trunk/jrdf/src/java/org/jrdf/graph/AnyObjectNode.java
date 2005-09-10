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

    // TODO Not test driven.
    private static final long serialVersionUID = 8654340032080018169L;

    private AnyObjectNode() {
    }

    Object readResolve() throws java.io.ObjectStreamException {
        return ANY_OBJECT_NODE;
    }
}