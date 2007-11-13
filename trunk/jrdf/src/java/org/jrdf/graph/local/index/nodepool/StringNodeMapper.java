package org.jrdf.graph.local.index.nodepool;

import org.jrdf.graph.Node;
import org.jrdf.graph.Literal;

public interface StringNodeMapper {
    String convertToString(Node node);

    Node convertToBlankNode(String string);

    Node convertToURI(String string, Long nodeId);

    Literal convertToLiteral(String string, Long nodeId);
}
