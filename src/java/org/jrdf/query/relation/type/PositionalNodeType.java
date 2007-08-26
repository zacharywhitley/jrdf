package org.jrdf.query.relation.type;

/**
 * Node types - data types belong to RDF nodes.  Positional node types are those grouped by their position in an RDF
 * Triple - Subject, Predicate, Object, SubjectPredicate, SubjectObject and SubjectPredicateObject.
 *
 * @author Andrew Newman
 * @version $Revision: 1045 $
 */

public interface PositionalNodeType extends NodeType {

    /**
     * Given a new node type what is the new compound node type.  For example, if this was a SubjectNodeType and
     * it was given a parameter of ObjectNodeType it should return SubjectObjectNodeType.
     *
     * @param newNodeType the new node type to upgrade the current node type to.
     * @return the new instance of a compound node type.
     */
    PositionalNodeType upgrade(PositionalNodeType newNodeType);
}
