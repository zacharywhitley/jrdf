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
    public SubjectNode getSubjectNode(GraphElementFactory elementFactory, String value) {
        SubjectNode subjectNode = getNode(elementFactory, value);
        if (subjectNode == null) {
            subjectNode = AnySubjectNode.ANY_SUBJECT_NODE;
        }
        return subjectNode;
    }

    public PredicateNode getPredicateNode(GraphElementFactory elementFactory, String value) {
        PredicateNode predicateNode = getNode(elementFactory, value);
        if (predicateNode == null) {
            predicateNode = AnyPredicateNode.ANY_PREDICATE_NODE;
        }
        return predicateNode;
    }

    public ObjectNode getObjectNode(GraphElementFactory elementFactory, String value) {
        ObjectNode objectNode = getNode(elementFactory, value);
        if (objectNode == null) {
            objectNode = AnyObjectNode.ANY_OBJECT_NODE;
        }
        return objectNode;
    }

    private URIReference getNode(GraphElementFactory elementFactory, String value) {
        try {
            return elementFactory.createURIReference(create(value.substring(1, value.length() - 1)));
        } catch (Exception e) {
            return null;
        }
    }
}
