package org.jrdf.graph.local.mem.copyUtil;

import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;

import java.util.Iterator;

public interface GraphToGraphMapper {
    Graph getGraph();

    void addTripleToGraph(Triple triple) throws GraphElementFactoryException, GraphException;

    void updateBlankNodes(Triple triple) throws GraphElementFactoryException;

    Graph createNewTriples(Iterator<Triple> it) throws GraphException, GraphElementFactoryException;
}
