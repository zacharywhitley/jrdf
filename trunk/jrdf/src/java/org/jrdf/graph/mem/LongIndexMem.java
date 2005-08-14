package org.jrdf.graph.mem;

import org.jrdf.graph.GraphException;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.io.Serializable;

/**
 * An memory version of ${@link LongIndex}.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class LongIndexMem implements LongIndex, Serializable {
  private static final long serialVersionUID = -5378262791792959679L;
  
  private Map<Long, Map<Long, Set<Long>>> index;

  public LongIndexMem(Map<Long, Map<Long, Set<Long>>> newIndex) {
    index = newIndex;
  }

  /**
   * Adds a triple to a single index.  This method defines the internal structure.
   *
   * @param first The first node id.
   * @param second The second node id.
   * @param third The last node id.
   * @throws org.jrdf.graph.GraphException If there was an error adding the statement.
   */
  public void add(Long first, Long second, Long third) throws GraphException {
    // find the sub index
    Map<Long, Set<Long>> subIndex = index.get(first);
    // check that the subindex exists
    if (null == subIndex) {
      // no, so create it and add it to the index
      subIndex = new HashMap<Long, Set<Long>>();
      index.put(first, subIndex);
    }

    // find the final group
    Set<Long> group = subIndex.get(second);
    // check that the group exists
    if (null == group) {
      // no, so create it and add it to the subindex
      group = new HashSet<Long>();
      subIndex.put(second, group);
    }

    // Add the final node to the group
    group.add(third);
  }

  /**
   * Removes a triple from a single index.
   *
   * @param first The first node.
   * @param second The second node.
   * @param third The last node.
   * @throws GraphException If there was an error revoking the statement, for
   *     example if it didn't exist.
   */
  public void remove(Long first, Long second, Long third)
      throws GraphException {

    // find the sub index
    Map<Long, Set<Long>> subIndex = index.get(first);
    // check that the subindex exists
    if (null == subIndex) {
      throw new GraphException("Unable to remove nonexistent statement");
    }
    // find the final group
    Set<Long> group = subIndex.get(second);
    // check that the group exists
    if (null == group) {
      throw new GraphException("Unable to remove nonexistent statement");
    }
    // remove from the group, report error if it didn't exist
    if (!group.remove(third)) {
      throw new GraphException("Unable to remove nonexistent statement");
    }
    // clean up the graph
    if (group.isEmpty()) {
      subIndex.remove(second);
      if (subIndex.isEmpty()) {
        index.remove(first);
      }
    }
  }

  // TODO Remove this.
  public Map<Long, Map<Long, Set<Long>>> getIndex() {
    return index;
  }
}