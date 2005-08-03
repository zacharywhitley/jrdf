package org.jrdf.query.relation;

import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;

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
