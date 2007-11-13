package org.jrdf.graph.local.index.nodepool;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;

public interface StringNodeMapper {
    String convertToString(Node node);

    BlankNode convertToBlankNode(String string);

    URIReference convertToURIReference(String string, Long nodeId);

    Literal convertToLiteral(String string, Long nodeId);
}
