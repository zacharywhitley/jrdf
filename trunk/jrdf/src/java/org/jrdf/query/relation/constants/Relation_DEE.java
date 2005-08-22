package org.jrdf.query.relation.constants;

import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Node;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.AttributeNameValue;

import java.util.*;

/**
 * Dee is a relation with one tuple and is the base relation for TRUE.  It is
 * also the identity with respect to JOIN i.e. JOIN {r, Relation_DEE} is DEE and
 * JOIN {} is Relation_DEE.
 *
 * Again, this is going to change when operations are more properly filled out.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class Relation_DEE implements Relation {
  private static final Node TRUE_NODE = TrueNode.TRUE;
  private static final Set<AttributeNameValue> TRUE_NAME_VALUE_SET =
      Collections.singleton(TrueAttributeNameValue.TRUE_NAME_VALUE);
  private static final Map<SubjectNode, Set<AttributeNameValue>> TRUE_TUPLE =
      Collections.singletonMap((SubjectNode) TRUE_NODE, TRUE_NAME_VALUE_SET);

  public Set<SubjectNode> getTupleNames() {
    return Collections.singleton((SubjectNode) TRUE_NODE);
  }

  public Set<PredicateNode> getAttributeNames() {
    return Collections.singleton((PredicateNode) TRUE_NODE);
  }

  public Map<SubjectNode, Set<AttributeNameValue>> getTuples(
      Set<SubjectNode> tupleNames) {
    return TRUE_TUPLE;
  }

}