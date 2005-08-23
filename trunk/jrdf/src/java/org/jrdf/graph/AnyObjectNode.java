package org.jrdf.graph;

/**
 * A node which represents any object - unconstrained.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public final class AnyObjectNode implements ObjectNode {
    public static final AnyObjectNode ANY_OBJECT_NODE = new AnyObjectNode();

    private AnyObjectNode() {
    }

    Object readResolve() throws java.io.ObjectStreamException {
        return ANY_OBJECT_NODE;
    }
}