package org.jrdf.query.answer;

import static org.jrdf.graph.AnyNode.ANY_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.Resource;
import org.jrdf.graph.URIReference;
import static org.jrdf.query.answer.xml.SparqlResultType.BLANK_NODE;
import static org.jrdf.query.answer.xml.SparqlResultType.LITERAL;
import static org.jrdf.query.answer.xml.SparqlResultType.URI_REFERENCE;
import org.jrdf.query.answer.xml.TypeValue;
import org.jrdf.query.answer.xml.TypeValueImpl;
import static org.jrdf.query.relation.constants.NullaryNode.NULLARY_NODE;

public class NodeToTypeValueImpl implements NodeToTypeValue {
    private TypeValue currentTypeValue;

    public TypeValue convert(Node value) {
        value.accept(this);
        return currentTypeValue;
    }

    public void visitBlankNode(BlankNode blankNode) {
        currentTypeValue = new TypeValueImpl(BLANK_NODE, blankNode.toString());
    }

    public void visitURIReference(URIReference uriReference) {
        currentTypeValue = new TypeValueImpl(URI_REFERENCE, uriReference.toString());
    }

    public void visitLiteral(Literal literal) {
        currentTypeValue = new TypeValueImpl(LITERAL, literal.toString());
    }

    public void visitNode(Node node) {
        if (node == NULLARY_NODE || node == ANY_NODE) {
            currentTypeValue = new TypeValueImpl();
        } else {
            badNodeType(node.getClass());
        }
    }

    public void visitResource(Resource resource) {
        badNodeType(resource.getClass());
    }

    private void badNodeType(Class<?> aClass) {
        throw new IllegalArgumentException("Cannot convert class of type: " + aClass);
    }
}
