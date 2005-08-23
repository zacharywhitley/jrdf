package org.jrdf.graph;

/**
 * A node which represents any predicate - unconstrained.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public final class AnyPredicateNode implements PredicateNode {
    public static final AnyPredicateNode ANY_PREDICATE_NODE = new AnyPredicateNode();

    private AnyPredicateNode() {
    }

    Object readResolve() throws java.io.ObjectStreamException {
        return ANY_PREDICATE_NODE;
    }
}