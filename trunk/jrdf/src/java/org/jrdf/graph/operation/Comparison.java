package org.jrdf.graph.operation;

import org.jrdf.graph.Graph;

/**
 * Provides the ability to compare two graph with one another.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface Comparison {
    /**
     * Returns true if the graph is grounded (does not contain blank nodes).
     *
     * @param g the graph to test.
     * @return true if the graph is grounded (does not contain blank nodes).
     */
    boolean isGrounded(Graph g);

    /**
     * Return true if both graphs are equivalent (isomorphic) to one another.  That is, that the nodes in one graph map
     * equivalently to nodes in the other.  In a non-grounded graph (ones with blank nodes) nodes can map to other
     * nodes with different values but are equivalent.  For example, <a>, <b>, <c> is equivalient to _x, <b>, <c>, where
     * _a is a blank node.
     *
     * @param g1 The first graph to test.
     * @param g2 The second graph to test.
     * @return true if they are equivalent.
     */
    boolean areIsomorphic(Graph g1, Graph g2);

    /**
     * Return true if both graphs are equivalent (isomophic) to one another.  These graphs must contain only labelled
     * nodes i.e. no blank nodes.
     *
     * @param g1 The first graph to test.
     * @param g2 The second graph to test.
     * @return true if they are equivalent.
     */
    boolean groundedGraphsAreIsomorphic(Graph g1, Graph g2);
}
