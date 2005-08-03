package org.jrdf.query.relation;

import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode;

/**
 * Something in here
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface AttributeNameValue {
  PredicateNode getName();
  ObjectNode getValue();
}
