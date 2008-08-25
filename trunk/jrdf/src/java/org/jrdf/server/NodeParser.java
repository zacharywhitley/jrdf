package org.jrdf.server;

import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode;

public interface NodeParser {
    SubjectNode getSubjectNode(GraphElementFactory elementFactory, String literalValue);

    PredicateNode getPredicateNode(GraphElementFactory elementFactory, String literalValue);

    ObjectNode getObjectNode(GraphElementFactory elementFactory, String literalValue);
}
