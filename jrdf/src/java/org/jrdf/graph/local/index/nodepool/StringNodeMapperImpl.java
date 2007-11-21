package org.jrdf.graph.local.index.nodepool;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.Resource;
import static org.jrdf.graph.AnySubjectNode.*;
import static org.jrdf.graph.AnyPredicateNode.*;
import static org.jrdf.graph.AnyObjectNode.*;
import org.jrdf.graph.local.mem.BlankNodeImpl;
import org.jrdf.graph.local.mem.LiteralImpl;
import org.jrdf.graph.local.mem.LiteralMutableId;
import org.jrdf.graph.local.mem.URIReferenceImpl;
import org.jrdf.parser.ntriples.parser.LiteralMatcher;

import java.net.URI;

public class StringNodeMapperImpl implements StringNodeMapper {
    private static final String PATTERN = "\\\"([\\t\\r\\n\\x20-\\x7E]*)\\\"" +
        "(" +
        "((\\@(\\p{Lower}+(\\-a-z0-9]+)*))|(\\^\\^\\<([\\x20-\\x7E]+)\\>))?" +
        ").*";
    private final LiteralMatcher literalMatcher;
    private String currentString;

    public StringNodeMapperImpl(LiteralMatcher newLiteralMatcher) {
        literalMatcher = newLiteralMatcher;
        literalMatcher.setPattern(PATTERN);
    }

    public String convertToString(Node node) {
        if (node != ANY_SUBJECT_NODE && node != ANY_PREDICATE_NODE && node != ANY_OBJECT_NODE) {
            node.accept(this);
            return currentString;
        } else {
            return null;
        }
    }

    public BlankNode convertToBlankNode(String string) {
        return BlankNodeImpl.valueOf(string);
    }

    public URIReference convertToURIReference(String string, Long nodeId) {
        return new URIReferenceImpl(URI.create(string), nodeId);
    }

    public Literal convertToLiteral(String string, Long nodeId) {
        String[] strings = literalMatcher.parse(string);
        String lexicalForm = strings[0];
        String language = strings[1];
        String datatype = strings[2];
        Literal literal;
        if (language != null) {
            literal = new LiteralImpl(lexicalForm, language);
        } else if (datatype != null) {
            literal = new LiteralImpl(lexicalForm, URI.create(datatype));
        } else {
            literal = new LiteralImpl(lexicalForm);
        }
        ((LiteralMutableId) literal).setId(nodeId);
        return literal;
    }

    public void visitBlankNode(BlankNode blankNode) {
        currentString = blankNode.toString();
    }

    public void visitURIReference(URIReference uriReference) {
        currentString = uriReference.getURI().toString();
    }

    public void visitLiteral(Literal literal) {
        currentString = literal.getEscapedForm();
    }

    public void visitNode(Node node) {
        illegalNode(node);
    }

    public void visitResource(Resource resource) {
        illegalNode(resource);
    }

    private void illegalNode(Node node) {
        throw new IllegalArgumentException("Failed to convert node: " + node);
    }
}
