package org.jrdf.graph.local.mem.copyUtil;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Node;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.Triple;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Literal;

import java.util.HashMap;
import java.util.Iterator;

public class GraphToGraphMapperImpl implements GraphToGraphMapper {
    private Graph graph;
    private GraphElementFactory elementFactory;
    private TripleFactory tripleFactory;
    private HashMap<Integer, BlankNode> newBNodeMap = new HashMap<Integer, BlankNode>();

    public GraphToGraphMapperImpl(Graph g) {
        graph = g;
        elementFactory = graph.getElementFactory();
        tripleFactory = graph.getTripleFactory();
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
        Triple newTriple;
        while (it.hasNext()) {
            Triple triple = it.next();
            if (!triple.isGrounded()) {
                newTriple = createNewTriple(triple);
                graph.add(newTriple);
            }
        }
        return graph;
    }

    public Triple createNewTriple(Triple triple) throws GraphElementFactoryException {
        SubjectNode newsn;
        PredicateNode newpn;
        ObjectNode newon;
        Triple newTriple;
        SubjectNode sn = triple.getSubject();
        PredicateNode pn = triple.getPredicate();
        ObjectNode on = triple.getObject();

        newsn = (SubjectNode) createNewNode(sn);
        newpn = elementFactory.createURIReference(((URIReference) pn).getURI());
        newon = (ObjectNode) createNewNode(on);

        newTriple = tripleFactory.createTriple(newsn, newpn, newon);
        return newTriple;
    }

    public void addTripleToGraph(Triple triple) throws GraphElementFactoryException, GraphException {
        SubjectNode sN = elementFactory.createURIReference(((URIReference) triple.getSubject()).getURI());
        PredicateNode pN = elementFactory.createURIReference(((URIReference) triple.getPredicate()).getURI());
        ObjectNode oON = triple.getObject();
        ObjectNode oN = createLiteralOrURI(oON);
        graph.add(tripleFactory.createTriple(sN, pN, oN));
    }

    private Node createNewNode(Node node) throws GraphElementFactoryException {
        Node newNode;
        if (GraphToGraphMapperImpl.isBlankNode(node)) {
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
