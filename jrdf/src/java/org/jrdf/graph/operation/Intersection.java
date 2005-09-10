package org.jrdf.graph.operation;

import org.jrdf.graph.Graph;

/**
 * The set of all object which appear in both graphs.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface Intersection extends SetOperation {
    Graph perform(Graph a, Graph b);
}
