package org.jrdf.graph;

/**
 * A node which represents any predicate - unconstrained.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public final class AnyPredicateNode implements PredicateNode {
    private static final long serialVersionUID = 1764088613140821732L;
    public static final AnyPredicateNode ANY_PREDICATE_NODE = new AnyPredicateNode();

    private AnyPredicateNode() {
    }

    Object readResolve() throws java.io.ObjectStreamException {
        return ANY_PREDICATE_NODE;
    }
}