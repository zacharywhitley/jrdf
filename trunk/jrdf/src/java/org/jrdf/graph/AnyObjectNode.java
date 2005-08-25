package org.jrdf.graph;

/**
 * A node which represents any object - unconstrained.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public final class AnyObjectNode implements ObjectNode {
    private static final long serialVersionUID = 8654340032080018169L;

    public static final AnyObjectNode ANY_OBJECT_NODE = new AnyObjectNode();

    private AnyObjectNode() {
    }

    Object readResolve() throws java.io.ObjectStreamException {
        return ANY_OBJECT_NODE;
    }
}