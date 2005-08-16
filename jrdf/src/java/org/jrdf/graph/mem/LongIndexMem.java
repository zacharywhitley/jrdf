package org.jrdf.graph.mem;

import org.jrdf.graph.GraphException;

import java.util.*;
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

  public Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator() {
    return index.entrySet().iterator();
  }

  // TODO Remove this.
  public Map<Long, Map<Long, Set<Long>>> getIndex() {
    return index;
  }
}