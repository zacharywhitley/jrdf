package org.jrdf.query.relation.mem;

import org.jrdf.query.relation.AttributeNameValue;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode;

/**
 * Something in here
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class AttributeNameValueImpl implements AttributeNameValue {
  private PredicateNode predicate;
  private ObjectNode object;

  public AttributeNameValueImpl(PredicateNode predicate, ObjectNode object) {
    this.predicate = predicate;
    this.object = object;
  }

  public PredicateNode getName() {
    return predicate;
  }

  public ObjectNode getValue() {
    return object;
  }
}