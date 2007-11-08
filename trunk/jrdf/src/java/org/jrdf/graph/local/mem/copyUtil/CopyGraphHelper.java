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

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: Nov 8, 2007
 * Time: 10:11:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class CopyGraphHelper {
    private Graph graph;
    private GraphElementFactory eFac;
    private TripleFactory tFac;

    public CopyGraphHelper(Graph g) {
        graph = g;
        eFac = graph.getElementFactory();
        tFac = graph.getTripleFactory();
    }

    public Graph getGraph() {
        return graph;
    }

    public void addTripleToGraph(Triple triple) throws GraphElementFactoryException, GraphException {
        SubjectNode sN = eFac.createURIReference(((URIReference) triple.getSubject()).getURI());
        PredicateNode pN = eFac.createURIReference(((URIReference) triple.getPredicate()).getURI());

        ObjectNode oON = triple.getObject();
        ObjectNode oN = createLiteralOrURI(oON);
        graph.add(tFac.createTriple(sN, pN, oN));
    }

    public ObjectNode createLiteralOrURI(Node oON) throws GraphElementFactoryException {
        ObjectNode oN;

        if (Literal.class.isAssignableFrom(oON.getClass())) {
            Literal lit = (Literal) oON;
            if (lit.isDatatypedLiteral()) {
                oN = eFac.createLiteral(lit.getValue().toString(), lit.getDatatypeURI());
            } else {
                oN = eFac.createLiteral(lit.getValue().toString());
            }
        } else {
            oN = eFac.createURIReference(((URIReference) oON).getURI());
        }
        return oN;
    }

    public static boolean isBlankNode(Node node) {
        return BlankNode.class.isAssignableFrom(node.getClass());
    }
}
