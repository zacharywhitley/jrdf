package org.jrdf.graph.operation;

import org.jrdf.graph.Graph;

/**
 * The set difference between two graphs - all the elements that belong in
 * the first graph that do not belong in the second.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface Difference extends SetOperation {
    Graph perform(Graph a, Graph b);
}