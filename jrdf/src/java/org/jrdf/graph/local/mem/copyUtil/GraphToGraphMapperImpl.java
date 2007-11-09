package org.jrdf.graph.local.mem.copyUtil;

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

    public void updateBlankNodes(Triple triple) throws GraphElementFactoryException {
        final SubjectNode bsNode = triple.getSubject();
        if (isBlankNode(bsNode)) {
            int bnHash = bsNode.hashCode();
            newBNodeMap.put(bnHash, elementFactory.createBlankNode());
        }
        final ObjectNode boNode = triple.getObject();
        if (isBlankNode(boNode)) {
            int bnHash = boNode.hashCode();
            newBNodeMap.put(bnHash, elementFactory.createBlankNode());
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
        SubjectNode sn = triple.getSubject();
        PredicateNode pn = triple.getPredicate();
        ObjectNode on = triple.getObject();
        SubjectNode newsn = (SubjectNode) createNewNode(sn);
        PredicateNode newpn = elementFactory.createURIReference(((URIReference) pn).getURI());
        ObjectNode newon = (ObjectNode) createNewNode(on);
        return tripleFactory.createTriple(newsn, newpn, newon);
    }

    public void addTripleToGraph(Triple triple) throws GraphElementFactoryException, GraphException {
        SubjectNode subjectNode = elementFactory.createURIReference(((URIReference) triple.getSubject()).getURI());
        PredicateNode predicateNode = elementFactory.createURIReference(
            ((URIReference) triple.getPredicate()).getURI());
        ObjectNode objectNode = createLiteralOrURI(triple.getObject());
        graph.add(tripleFactory.createTriple(subjectNode, predicateNode, objectNode));
    }

    private Node createNewNode(Node node) throws GraphElementFactoryException {
        Node newNode;
        if (isBlankNode(node)) {
            newNode = newBNodeMap.get(new Integer(node.hashCode()));
        } else {
            newNode = createLiteralOrURI(node);
        }
        return newNode;
    }

    public ObjectNode createLiteralOrURI(Node oON) throws GraphElementFactoryException {
        ObjectNode oN;
        if (Literal.class.isAssignableFrom(oON.getClass())) {
            Literal lit = (Literal) oON;
            if (lit.isDatatypedLiteral()) {
                oN = elementFactory.createLiteral(lit.getValue().toString(), lit.getDatatypeURI());
            } else {
                oN = elementFactory.createLiteral(lit.getValue().toString());
            }
        } else {
            oN = elementFactory.createURIReference(((URIReference) oON).getURI());
        }
        return oN;
    }

    public static boolean isBlankNode(Node node) {
        return BlankNode.class.isAssignableFrom(node.getClass());
    }
}
