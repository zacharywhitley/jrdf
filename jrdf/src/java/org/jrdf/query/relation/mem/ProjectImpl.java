package org.jrdf.query.relation.mem;

import org.jrdf.graph.PredicateNode;
import org.jrdf.query.relation.Project;
import org.jrdf.query.relation.Relation;

import java.util.Set;

/**
 * An implementation of ${@link Project} that uses the in memory Graph
 * implementation.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class ProjectImpl implements Project {
  public Relation include(Relation relation, Set<PredicateNode> attributes) {
    return null;
  }

  public Relation exclude(Relation relation, Set<PredicateNode> attributes) {
    return null;
  }
}