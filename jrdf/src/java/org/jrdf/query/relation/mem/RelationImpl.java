package org.jrdf.query.relation.mem;

import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.AttributeNameValue;
import org.jrdf.graph.*;
import org.jrdf.graph.mem.GraphElementFactoryImpl;

import java.util.*;

/**
 * An implementation of ${@link Relation} that uses the in memory Graph
 * implementation.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class RelationImpl implements Relation {
  private Map<Long, Map<Long, Set<Long>>> index012;
  private Map<Long, Map<Long, Set<Long>>> index120;
  private GraphElementFactoryImpl factory;

  // TODO Type these indices.
  public RelationImpl(Map<Long, Map<Long, Set<Long>>> index012,
      Map<Long, Map<Long, Set<Long>>> index120,
      GraphElementFactoryImpl factory) {
    this.index012 = index012;
    this.index120 = index120;
    this.factory = factory;
  }

  public Set<SubjectNode> getTupleNames() {
    Iterator<Long> iter = index012.keySet().iterator();
    Set<SubjectNode> subjects = new HashSet<SubjectNode>();
    while (iter.hasNext()) {
      Long subjectId =  iter.next();
      subjects.add((SubjectNode) factory.getNodeById(subjectId));
    }
    return subjects;
  }

  public Set<PredicateNode> getAttributeNames() {
    Iterator<Long> iter = index120.keySet().iterator();
    Set<PredicateNode> predicates = new HashSet<PredicateNode>();
    while (iter.hasNext()) {
      Long predicateId =  iter.next();
      predicates.add((PredicateNode) factory.getNodeById(predicateId));
    }
    return predicates;
  }

  // TODO Assume this mess of code works.
  public Map<SubjectNode, Set<AttributeNameValue>> getTuples(
      Set<SubjectNode> tupleNames) {
    Map<SubjectNode, Set<AttributeNameValue>> result =
        new HashMap<SubjectNode, Set<AttributeNameValue>>();
    Iterator<Long> iter = index012.keySet().iterator();
    while (iter.hasNext()) {
      Long subjectId =  iter.next();
      SubjectNode subject = (SubjectNode) factory.getNodeById(subjectId);
      Map<Long, Set<Long>> map = index012.get(subjectId);
      Iterator<Map.Entry<Long,Set<Long>>> entryIter = map.entrySet().iterator();
      while (entryIter.hasNext()) {
        Map.Entry<Long, Set<Long>> entry = entryIter.next();
        PredicateNode predicate = (PredicateNode) factory.getNodeById(entry.getKey());
        Iterator<Long> objectIter = entry.getValue().iterator();
        Set<AttributeNameValue> nameValues = new HashSet<AttributeNameValue>();
        result.put(subject, nameValues);
        while (objectIter.hasNext()) {
          ObjectNode object = (ObjectNode) factory.getNodeById(iter.next());
          AttributeNameValue nameValue = new AttributeNameValueImpl(predicate, object);
          nameValues.add(nameValue);
        }
      }
    }
    return result;
  }
}