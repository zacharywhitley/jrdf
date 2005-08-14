package org.jrdf.query.relation;

import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.operation.Operation;

import java.util.Set;

/**
 * Derives a new relation by removing attributes.  The list of attributes may
 * either be the set of attributes to include or the list of attributes to
 * exclude.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface Project {
  /**
   * The attributes to include in the projection.  Will overwrite any existing
   * inclusion or exclusion.
   *
   * @param attributes the set of attributes to keep during projection.
   */
  Relation include(Relation relation, Set<PredicateNode> attributes);

  /**
   * The attributes to exclude in the project.  Will overwrite any existing
   * inclusion or exclusion.
   *
   * @param attributes the set of attributes to keep during projection.
   */
  Relation exclude(Relation relation, Set<PredicateNode> attributes);
}
