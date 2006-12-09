package org.jrdf.graph;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * A node which represents any predicate - unconstrained.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class AnyPredicateNode implements PredicateNode, Serializable {
    /**
     * The any predicate node constant - represents an unconstrained predicate node.
     */
    public static final AnyPredicateNode ANY_PREDICATE_NODE = new AnyPredicateNode();

    private static final long serialVersionUID = 1764088613140821732L;
    private static final String STRING_FORM = "ANY_PREDICATE";

    private AnyPredicateNode() {
    }

    public String toString() {
        return STRING_FORM;
    }

    private Object readResolve() throws ObjectStreamException {
        return ANY_PREDICATE_NODE;
    }
}