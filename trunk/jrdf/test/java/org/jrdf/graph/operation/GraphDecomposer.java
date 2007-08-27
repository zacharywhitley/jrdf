package org.biomanta.comparison;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: imrank
 * Date: 3/08/2007
 * Time: 15:11:47
 * To change this template use File | Settings | File Templates.
 */
public interface GraphDecomposer {

    /**
     * Given the graph, this method returns the graph as a set
     * of Molecules conataining the Most Self Contained Graph.
     *
     * @param graph to decompose
     * @return set of molecules (subgraphs) which make up the graph
     * @throws GraphException
     */
    Set<Molecule> decompose(Graph graph) throws GraphException;
}
