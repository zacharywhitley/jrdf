package org.jrdf.graph.local.util;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.ObjectNode;

public interface CopyGraphUtil {
    /**
     * Copies all the triples in source graph to target graph,
     * respecting the blank node "identifies".
     *
     * @param sourceGraph
     * @param targetGraph
     * @return
     * @throws GraphException
     */
    Graph copyGraph(Graph sourceGraph, Graph targetGraph) throws GraphException;

    /**
     * Given a node, copies all the triples that include.
     * (1) triples that contain this node
     * (2) triples in (1) & (2) that contain blank nodes (recursively)
     *
     * @param sourceGraph
     * @param targetGraph
     * @param node @return
     * @throws GraphException
     */
    Node copyTriplesForNode(Graph sourceGraph, Graph targetGraph, Node node)
        throws GraphException;

    Graph getGraph();

    SubjectNode copyTriplesForSubjectNode(Graph newSourceGraph, Graph newTargetGraph,
                                          SubjectNode node, SubjectNode newNode)
        throws GraphException;

    ObjectNode copyTriplesForObjectNode(Graph newSourceGraph, Graph newTargetGraph,
                                        ObjectNode node, ObjectNode newNode)
        throws GraphException;
}
