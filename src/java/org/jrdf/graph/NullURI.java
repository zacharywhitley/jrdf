package org.jrdf.graph;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.net.URI;

/**
 * Represents no URI.
 *
 * @author Andrew Newman
 * @version $Id: AnyNode.java 1045 2007-01-05 04:56:09Z newmana $
 */
public final class NullURI implements Serializable {

    /**
     * The any node constant - represents an unconstrained object node.
     */
    public static final URI NULL_URI = URI.create("");
    private static final long serialVersionUID = -8345091665456575545L;

    private NullURI() {
    }

    private Object readResolve() throws ObjectStreamException {
        return NULL_URI;
    }
}
