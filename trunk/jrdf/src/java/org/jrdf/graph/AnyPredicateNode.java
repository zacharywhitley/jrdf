package org.jrdf.graph;

/**
 * A node which represents any predicate - unconstrained.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class AnyPredicateNode implements PredicateNode {
    /**
     * The any predicate node constant - represents an unconstrained predicate node.
     */
    public static final AnyPredicateNode ANY_PREDICATE_NODE = new AnyPredicateNode();

    // TODO serialVersionUID not test driven.
    private static final long serialVersionUID = 1764088613140821732L;
    private static final String STRING_FORM = "ANY_PREDICATE";

    private AnyPredicateNode() {
    }

    Object readResolve() throws java.io.ObjectStreamException {
        return ANY_PREDICATE_NODE;
    }

    public String toString() {
        return STRING_FORM;
    }
}