package org.jrdf.graph.local.util;

import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;

import java.util.Iterator;

public interface GraphToGraphMapper {
    /**
     * Returns the target graph.
     *
     * @return the target graph.
     */
    Graph getGraph();

    /**
     *  Converts a triple from one graph and puts it into the target graph.
     *
     * @param triple The source graph triple.
     * @throws GraphElementFactoryException if there was an error creating the new resources.
     * @throws GraphException if there was an error adding the new resources.
     */
    void addTripleToGraph(Triple triple) throws GraphElementFactoryException, GraphException;

    void updateBlankNodes(Triple triple) throws GraphElementFactoryException;

    Graph createNewTriples(Iterator<Triple> it) throws GraphException, GraphElementFactoryException;

    Node createNewNode(Node node) throws GraphElementFactoryException;

    void replaceObjectNode(ObjectNode node, ObjectNode newNode) throws GraphException, GraphElementFactoryException;

    void replaceSubjectNode(SubjectNode node, SubjectNode newNode) throws GraphException, GraphElementFactoryException;
}
