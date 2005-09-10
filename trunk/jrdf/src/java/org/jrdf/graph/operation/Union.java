package org.jrdf.graph.operation;

import org.jrdf.graph.Graph;

/**
 * The set union of two graphs - all of the items in the first graph are
 * added to the second graph (no duplicates).
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface Union extends SetOperation {
    Graph perform(Graph a, Graph b);
}
