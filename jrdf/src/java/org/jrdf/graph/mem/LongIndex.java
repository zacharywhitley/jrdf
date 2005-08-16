package org.jrdf.graph.mem;

import org.jrdf.graph.GraphException;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/**
 * Represents an indexed set of longs.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface LongIndex {
  /**
   * Adds a triple to a single index.  This method defines the internal structure.
   *
   * @param first The first node id.
   * @param second The second node id.
   * @param third The last node id.
   * @throws org.jrdf.graph.GraphException If there was an error adding the statement.
   */
  void add(Long first, Long second, Long third) throws GraphException;

  /**
   * Removes a triple from a single index.
   *
   * @param first The first node.
   * @param second The second node.
   * @param third The last node.
   * @throws GraphException If there was an error revoking the statement, for
   *     example if it didn't exist.
   */
  void remove(Long first, Long second, Long third)
      throws GraphException;

  /**
   * Returns an iterator which contains all the elements in the graph as a
   * collections of distinct longs, contains a map of longs to other longs.
   * This prevents any duplication.
   *
   * @return an iterator which contains all the elements in the graph as a
   * collections of distinct longs, contains a map of longs to other longs.
   * This prevents any duplication.
   */ 
  Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator();
}
