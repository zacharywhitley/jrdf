package org.jrdf.graph.local.util;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
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
import org.jrdf.graph.AbstractBlankNode;
import org.jrdf.graph.local.iterator.ClosableIterator;
import org.jrdf.map.MapFactory;

import java.util.Iterator;
import java.util.Map;

public class GraphToGraphMapperImpl implements GraphToGraphMapper {
    private Graph graph;
    private GraphElementFactory elementFactory;
    private TripleFactory tripleFactory;
    private Map<Long, BlankNode> newBNodeMap;

    public GraphToGraphMapperImpl(Graph newGraph, MapFactory mapFactory) {
        graph = newGraph;
        elementFactory = graph.getElementFactory();
        tripleFactory = graph.getTripleFactory();
        newBNodeMap = mapFactory.createMap(Long.class, BlankNode.class);
    }

    public Graph getGraph() {
        return graph;
    }

    public void addTripleToGraph(Triple triple) throws GraphElementFactoryException, GraphException {
        SubjectNode subjectNode = elementFactory.createURIReference(((URIReference) triple.getSubject()).getURI());
        PredicateNode predicateNode = elementFactory.createURIReference(
            ((URIReference) triple.getPredicate()).getURI());
        ObjectNode objectNode = createLiteralOrURI(triple.getObject());
        final Triple triple1 = tripleFactory.createTriple(subjectNode, predicateNode, objectNode);
        graph.add(triple1);
    }

    public void updateBlankNodes(Triple triple) throws GraphElementFactoryException {
        SubjectNode subjectNode = triple.getSubject();
        long hashcode;
        if (AbstractBlankNode.isBlankNode(subjectNode)) {
            hashcode = (long) subjectNode.hashCode();
            if (!newBNodeMap.containsKey(hashcode)) {
                newBNodeMap.put(hashcode, elementFactory.createBlankNode());
            }
        }
        final ObjectNode objectNode = triple.getObject();
        if (AbstractBlankNode.isBlankNode(objectNode)) {
            hashcode = (long) objectNode.hashCode();
            if (!newBNodeMap.containsKey(hashcode)) {
                newBNodeMap.put(hashcode, elementFactory.createBlankNode());
            }
        }
    }

    public Graph createNewTriples(Iterator<Triple> it) throws GraphException, GraphElementFactoryException {
        while (it.hasNext()) {
            Triple triple = it.next();
            graph.add(createNewTriple(triple));
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

    public Node createNewNode(Node node) throws GraphElementFactoryException {
        Node newNode;
        if (AbstractBlankNode.isBlankNode(node)) {
            newNode = newBNodeMap.get((long) node.hashCode());
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

    public void replaceObjectNode(ObjectNode node, ObjectNode newNode)
        throws GraphException, GraphElementFactoryException {
        if (newNode != null) {
            final ObjectNode oldONode = (ObjectNode) createNewNode(node);
            ClosableIterator<Triple> iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, oldONode);
            while (iterator.hasNext()) {
                Triple triple = iterator.next();
                Triple newTriple = tripleFactory.createTriple(triple.getSubject(), triple.getPredicate(), newNode);
                graph.add(newTriple);
                graph.remove(triple.getSubject(), triple.getPredicate(), oldONode);
            }
            iterator.close();
        }
    }

    public void replaceSubjectNode(SubjectNode node, SubjectNode newNode)
        throws GraphException, GraphElementFactoryException {
        if (newNode != null) {
            final SubjectNode oldSNode = (SubjectNode) createNewNode(node);
            ClosableIterator<Triple> iterator = graph.find(oldSNode, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
            while (iterator.hasNext()) {
                Triple triple = iterator.next();
                graph.add(newNode, triple.getPredicate(), triple.getObject());
                graph.remove(oldSNode, triple.getPredicate(), triple.getObject());
            }
            iterator.close();
        }
    }
}
