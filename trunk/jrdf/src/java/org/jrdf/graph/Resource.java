package org.jrdf.graph;

import org.jrdf.util.ClosableIterator;

import java.io.Serializable;

/**
 * A resource stands for either a Blank Node or a URI Reference.  This is a convienence interface designed to make it
 * easier to create triples programmatically.
 */
public interface Resource extends URIReference, BlankNode, Serializable {

    /**
     * Returns true if this is a URIReference, otherwise (currently) it's a BlankNode.
     *
     * @return true if this is a URIReference, otherwise (currently) it's a BlankNode.
     */
    boolean isURIReference();

    /**
     * Add a new triple with this as the subject, the given predicate and object.
     *
     * @param predicate the existing predicate in the graph to create the triple with.
     * @param object the existing object in the graph to create the triple with.
     * @throws GraphException if the predicate or object do not exist in the graph.
     */
    void addValue(PredicateNode predicate, ObjectNode object) throws GraphException;

    /**
     * Remove any other triples with this as the subject, the given predicate and any object and
     * add a new triple with this as the subject and the given predicate and object.  This many that multiple
     * object values will be replaced by a single object value.
     *
     * @param predicate the existing predicate in the graph to set.
     * @param object the existing object in the graph to set.
     * @throws GraphException if the predicate or object do not exist in the graph.
     */
    void setValue(PredicateNode predicate, ObjectNode object) throws GraphException;

    /**
     * Remove the triple with this as the subject, the given predicate and object.
     *
     * @param predicate the existing predicate in the graph to remove.
     * @param object the existing object in the graph to remove.
     * @throws GraphException if the predicate or object does not exist in the graph.
     */
    void removeValue(PredicateNode predicate, ObjectNode object) throws GraphException;

    /**
     * Remove all the triples with this as the subject and the given predicate.
     *
     * @param predicate the existing predicate in the graph.
     * @throws GraphException if the predicate does not exist in the graph.
     */
    void removeValues(PredicateNode predicate) throws GraphException;

    /**
     * Remove the triple with this as the object, the given subject and predicate.
     *
     * @param subject the existing subject in the graph to remove.
     * @param predicate the existing predicate in the graph to remove.
     * @throws GraphException if the subject or predicate do not exist in the graph.
     */
    void removeSubject(SubjectNode subject, PredicateNode predicate) throws GraphException;

    /**
     * With this as the subject and using the given predicate return all the objects.
     *
     * @param predicate the existing predicate in the graph to use to find the objects.
     * @return all the objects in the graph with this resource as the subject and the given predicate.
     * @throws GraphException if the subject or predicate do not exist in the graph.
     */
    ClosableIterator<ObjectNode> getObjects(PredicateNode predicate) throws GraphException;

    /**
     * With this as the object and using the given predicate return all the subjects.
     *
     * @param predicate the existing predicate in the graph to use to find the subjects.
     * @return all the objects in the graph with this resource as the subject and the given predicate.
     * @throws GraphException if the subject or predicate do not exist in the graph.
     */
    ClosableIterator<SubjectNode> getSubjects(PredicateNode predicate) throws GraphException;
}
