package org.jrdf.query.relation;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;

/**
 * An attribute name/value consists of the predicate and object of a relation.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface AttributeNameValue {
    /**
     * Returns the name of the attribute.
     *
     * @return the name of the attribute.
     */
    PredicateNode getName();

    /**
     * Returns the value of the attribute.
     *
     * @return the value of the attribute.
     */
    ObjectNode getValue();
}
