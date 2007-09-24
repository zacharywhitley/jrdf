package org.jrdf.graph.global.molecule;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;

import java.util.SortedSet;

/**
 * Decompose a graph into molecules.
 *
 * @author Imran Khan
 * @version $Revision: 1226 $
 */
public interface GraphDecomposer {

    /**
     * Given the graph, this method returns the graph as a set of Molecules conataining the Most Self Contained Graph.
     *
     * @param graph to decompose
     * @return set of molecules (subgraphs) which make up the graph
     * @throws GraphException
     */
    SortedSet<Molecule> decompose(Graph graph) throws GraphException, MoleculeInsertionException;
}
