package org.jrdf.query.relation;

import org.jrdf.graph.PredicateNode;

import java.util.Set;

/**
 * Derives a new relation by removing attributes.  The list of attributes may
 * either be the set of attributes to include or the list of attributes to
 * exclude.
 *
 * May have to work out a way of specifying both variables and attributes?
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface Project extends Operation {
  /**
   * The attributes to include in the projection.  Will overwrite any existing
   * inclusion or exclusion.
   *
   * @param attributes the set of attributes to keep during projection.
   */
  void attributesToInclude(Set<PredicateNode> attributes);

  /**
   * The attributes to exclude in the project.  Will overwrite any existing
   * inclusion or exclusion.
   *
   * @param attributes the set of attributes to keep during projection.
   */
  void attributesToExclude(Set<PredicateNode> attributes);
}
