package org.jrdf.graph.local.mem.copyUtil;

import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Node;

import java.util.Iterator;

public interface GraphToGraphMapper {
    Graph getGraph();

    void addTripleToGraph(Triple triple) throws GraphElementFactoryException, GraphException;

    ObjectNode createLiteralOrURI(Node oON) throws GraphElementFactoryException;

    Triple createNewTriple(Triple triple) throws GraphElementFactoryException;

    void updateBlankNodes(Triple triple) throws GraphElementFactoryException;

    Graph createNewTriples(Iterator<Triple> it) throws GraphException, GraphElementFactoryException;
}
