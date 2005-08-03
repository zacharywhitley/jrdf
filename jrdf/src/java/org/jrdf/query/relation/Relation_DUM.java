package org.jrdf.query.relation;

import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.PredicateNode;

import java.util.Set;
import java.util.Map;
import java.util.Collections;

/**
 * Dum is a relation with no tuples and is the base relation for FALSE.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class Relation_DUM implements Relation {
  private static Set<SubjectNode> FALSE_SET = Collections.emptySet();
  private static Set<PredicateNode> FALSE_PREDICATE_SET = Collections.emptySet();
  private static Map<SubjectNode, Set<AttributeNameValue>> FALSE_TUPLE =
      Collections.emptyMap();

  public Set<SubjectNode> getTupleNames() {
    return FALSE_SET;
  }

  public Set<PredicateNode> getAttributeNames() {
    return FALSE_PREDICATE_SET;
  }

  public Map<SubjectNode, Set<AttributeNameValue>> getTuples(
      Set<SubjectNode> tupleNames) {
    return FALSE_TUPLE;
  }

  public static class FalseSubjectNode implements SubjectNode {
  }
}