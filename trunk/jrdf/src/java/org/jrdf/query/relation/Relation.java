package org.jrdf.query.relation;

import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.query.Variable;

import java.util.Set;
import java.util.Map;

/**
 * Represents a relation or truth propisition.  The subjects map to the names of
 * the relation (or table), the predicates to the attributes (or column names),
 * the object nodes to tuples, and the combination of all three values can be
 * retrieved as triples.
 *
 * Will have to allow both variables and subject/predicate/object to retrieve
 * values (not just variables at the moment).  Maybe just modify the return
 * type to include a type that is both variable and constant?  Value?
 *
 * Unsure how operations are going to be implemented - this interface may change.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface Relation {
  /**
   * Returns the set of bound subjects to a set of variables.  If empty will
   * return all the unique subjects bound.
   *
   * @param vars the set of variables to use to retrieve the subjects.
   * @return the bound subjects
   */
  Map<Variable, SubjectNode> getNames(Set<Variable> vars);
  Map<Variable, PredicateNode> getAttributes(Set<Variable> vars);
  Map<Variable, ObjectNode> getTuples(Set<Variable> vars);

  /**
   * Returns the set of variables bound to a particular set of triples.
   *
   * @param vars the set of variables to use to return the triples.  If empty
   *   will return all triples and what they are bound to.
   * @return
   */
  Map<Set<Variable>, Set<Triple>> getTriple(Set<Variable> vars);
}