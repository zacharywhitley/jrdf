package org.jrdf.graph.mem;

import org.jrdf.graph.GraphException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An memory version of ${@link LongIndex}.
 *
 * @author Andrew Newman
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

    public void remove(Long first, Long second, Long third) throws GraphException {

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

    public Map<Long, Set<Long>> getSubIndex(Long first) {
        return index.get(first);
    }

    public boolean removeSubIndex(Long first) {
        index.remove(first);
        return index.containsKey(first);
    }

    public long getSize() {
        long size = 0;
        // go over the index map
        for (Map<Long, Set<Long>> map : index.values()) {
            // go over the sub indexes
            for (Set<Long> s : map.values()) {
                // accumulate the sizes of the groups
                size += s.size();
            }
        }
        return size;
    }
}