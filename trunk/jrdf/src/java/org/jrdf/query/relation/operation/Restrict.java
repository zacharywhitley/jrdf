package org.jrdf.query.relation.operation;

import org.jrdf.query.relation.AttributeNameValue;
import org.jrdf.query.relation.Relation;

import java.util.Set;

/**
 * Returns the list of relations with the same set of attributes.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface Restrict extends Operation {
    Relation restrict(Relation relation, Set<AttributeNameValue> nameValues);
}
