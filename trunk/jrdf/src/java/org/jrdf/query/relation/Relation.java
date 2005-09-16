package org.jrdf.query.relation;

import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;

import java.util.Map;
import java.util.Set;

/**
 * Represents a relation or truth propisition.  The subjects map to the names of
 * the tuple, the predicates to the attribute names (or column names),
 * the object nodes to tuples, and the combination of all three values can be
 * retrieved as triples.
 * <p/>
 * Unsure how operations are going to be implemented - this interface may change.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface Relation {

    /**
     * Returns the set of subject nodes - can then be used to get all associated
     * predicates and objects associated with this subject.
     *
     * @return the set of subject nodes.
     */
    Set<SubjectNode> getTupleNames();

    /**
     * Returns the set of predicates in this relation.
     *
     * @return the set of predicate nodes.
     */
    Set<PredicateNode> getAttributeNames();

    /**
     * Returns the tuples for this relation.  A tuple is made of a set of
     * attribute names and values.  Each distinct tuple has an associated subject
     * node (or tuple name).  SubjectNodes with no attributes are not listed.
     *
     * @param tupleNames the subject nodes (tuple names) to get.
     * @return a map containing tuples.
     */
    Map<? super SubjectNode, Set<AttributeNameValue>> getTuples(Set<SubjectNode> tupleNames);
}