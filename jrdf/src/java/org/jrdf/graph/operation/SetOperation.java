package org.jrdf.graph.operation;

import org.jrdf.graph.Graph;

/**
 * The interface the indicates a set operation.  Take in two graphs and
 * returns a new one.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface SetOperation {
    Graph perform(Graph a, Graph b);
}
