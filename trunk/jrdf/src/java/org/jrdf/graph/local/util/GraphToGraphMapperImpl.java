package org.jrdf.graph.local.util;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.map.MapFactory;

import java.util.Iterator;
import java.util.Map;

public class GraphToGraphMapperImpl implements GraphToGraphMapper {
    private Graph graph;
    private GraphElementFactory elementFactory;
    private TripleFactory tripleFactory;
    private Map<Integer, BlankNode> newBNodeMap;

    public GraphToGraphMapperImpl(Graph newGraph, MapFactory mapFactory) {
        this.graph = newGraph;
        this.elementFactory = graph.getElementFactory();
        this.tripleFactory = graph.getTripleFactory();
        newBNodeMap = mapFactory.createMap(Integer.class, BlankNode.class);
    }

    public Graph getGraph() {
        return graph;
    }

    public void addTripleToGraph(Triple triple) throws GraphElementFactoryException, GraphException {
        SubjectNode subjectNode = elementFactory.createURIReference(((URIReference) triple.getSubject()).getURI());
        PredicateNode predicateNode = elementFactory.createURIReference(
            ((URIReference) triple.getPredicate()).getURI());
        ObjectNode objectNode = createLiteralOrURI(triple.getObject());
        graph.add(tripleFactory.createTriple(subjectNode, predicateNode, objectNode));
    }

    public void updateBlankNodes(Triple triple) throws GraphElementFactoryException {
        SubjectNode subjectNode = triple.getSubject();
        if (isBlankNode(subjectNode)) {
            newBNodeMap.put(subjectNode.hashCode(), elementFactory.createBlankNode());
        }
        final ObjectNode objectNode = triple.getObject();
        if (isBlankNode(objectNode)) {
            newBNodeMap.put(objectNode.hashCode(), elementFactory.createBlankNode());
        }
    }

    public Graph createNewTriples(Iterator<Triple> it) throws GraphException, GraphElementFactoryException {
        while (it.hasNext()) {
            Triple triple = it.next();
            if (!triple.isGrounded()) {
                graph.add(createNewTriple(triple));
            }
        }
        return graph;
    }

    private Triple createNewTriple(Triple triple) throws GraphElementFactoryException {
        SubjectNode subjectNode = triple.getSubject();
        PredicateNode predicateNode = triple.getPredicate();
        ObjectNode objectNode = triple.getObject();
        SubjectNode newSubjectNode = (SubjectNode) createNewNode(subjectNode);
        PredicateNode newPredicateNode = elementFactory.createURIReference(((URIReference) predicateNode).getURI());
        ObjectNode newObjectNode = (ObjectNode) createNewNode(objectNode);
        return tripleFactory.createTriple(newSubjectNode, newPredicateNode, newObjectNode);
    }

    private Node createNewNode(Node node) throws GraphElementFactoryException {
        Node newNode;
        if (isBlankNode(node)) {
            newNode = newBNodeMap.get(node.hashCode());
        } else {
            newNode = createLiteralOrURI((ObjectNode) node);
        }
        return newNode;
    }

    private ObjectNode createLiteralOrURI(ObjectNode objectNode) throws GraphElementFactoryException {
        ObjectNode newObjectNode;
        if (Literal.class.isAssignableFrom(objectNode.getClass())) {
            Literal lit = (Literal) objectNode;
            if (lit.isDatatypedLiteral()) {
                newObjectNode = elementFactory.createLiteral(lit.getValue().toString(), lit.getDatatypeURI());
            } else {
                newObjectNode = elementFactory.createLiteral(lit.getValue().toString());
            }
        } else {
            newObjectNode = elementFactory.createURIReference(((URIReference) objectNode).getURI());
        }
        return newObjectNode;
    }

    public static boolean isBlankNode(Node node) {
        return BlankNode.class.isAssignableFrom(node.getClass());
    }
}
