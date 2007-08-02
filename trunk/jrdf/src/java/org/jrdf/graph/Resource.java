package org.jrdf.graph;

import java.io.Serializable;

/**
 * A resource stands for either a Blank Node or a URI Reference.  This is a convienence interface designed to make it
 * easier to create triples programmatically.
 */
public interface Resource extends SubjectNode, ObjectNode, Serializable {
    boolean isURIReference();
    // Implement an AnyResource which will return all unique predicates in the graph when calling getUniquePredicates.
    // A given Resource - return all the
    // TODO IMRAN
    //ClosableIterator<Predicate> getUniquePredicates();
//    void addValue(URI uri, String lexicalValue);
//    void addValue(URI uri, String lexicalValue, String languageType);
//    void addValue(URI uri, String lexicalValue, URI datatypeURI);
//    void addValue(PredicateNode predicate, ObjectNode object);
}
