package org.jrdf.graph;

/**
 * A resource stands for either a Blank Node or a URI Reference.  This is a convienence interface designed to make it
 * easier to create triples programmatically.
 */
public interface Resource extends URIReference, BlankNode {
    boolean isURIReference();
//    void addValue(URI uri, String lexicalValue);
//    void addValue(URI uri, String lexicalValue, String languageType);
//    void addValue(URI uri, String lexicalValue, URI datatypeURI);
//    void addValue(PredicateNode predicate, ObjectNode object);
}
