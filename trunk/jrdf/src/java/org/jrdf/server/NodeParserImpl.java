package org.jrdf.server;

import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.AnyPredicateNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.URIReference;

import static java.net.URI.create;

public class NodeParserImpl implements NodeParser {
    public SubjectNode getSubjectNode(GraphElementFactory elementFactory, String literalValue) {
        SubjectNode subjectNode = getNode(elementFactory, literalValue);
        if (subjectNode == null) {
            subjectNode = AnySubjectNode.ANY_SUBJECT_NODE;
        }
        return subjectNode;
    }

    public PredicateNode getPredicateNode(GraphElementFactory elementFactory, String literalValue) {
        PredicateNode predicateNode = getNode(elementFactory, literalValue);
        if (predicateNode == null) {
            predicateNode = AnyPredicateNode.ANY_PREDICATE_NODE;
        }
        return predicateNode;
    }

    public ObjectNode getObjectNode(GraphElementFactory elementFactory, String literalValue) {
        ObjectNode objectNode = getNode(elementFactory, literalValue);
        if (objectNode == null) {
            objectNode = AnyObjectNode.ANY_OBJECT_NODE;
        }
        return objectNode;
    }

    private URIReference getNode(GraphElementFactory elementFactory, String literalValue) {
        try {
            return elementFactory.createURIReference(create(literalValue.substring(1, literalValue.length() - 1)));
        } catch (Exception e) {
            return null;
        }
    }
}
