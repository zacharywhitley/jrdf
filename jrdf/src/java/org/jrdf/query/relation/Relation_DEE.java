package org.jrdf.query.relation;

import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode;

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
  private static SubjectNode TRUE = new TrueSubjectNode();
  private static Set<SubjectNode> TRUE_SET = Collections.singleton(TRUE);
  private static Set<PredicateNode> TRUE_PREDICATE_SET = Collections.emptySet();
  private static Set<AttributeNameValue> TRUE_NAME_VALUE =
      Collections.emptySet();
  private static Map<SubjectNode, Set<AttributeNameValue>> TRUE_TUPLE =
      Collections.singletonMap(TRUE, TRUE_NAME_VALUE);

  public Set<SubjectNode> getTupleNames() {
    return TRUE_SET;
  }

  public Set<PredicateNode> getAttributeNames() {
    return TRUE_PREDICATE_SET;
  }

  public Map<SubjectNode, Set<AttributeNameValue>> getTuples(
      Set<SubjectNode> tupleNames) {
    return TRUE_TUPLE;
  }

}