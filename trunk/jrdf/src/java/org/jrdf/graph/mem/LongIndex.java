package org.jrdf.graph.mem;

import org.jrdf.graph.GraphException;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Represents an indexed set of longs.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface LongIndex {
    /**
     * Adds a triple to a single index.  This method defines the internal structure.
     *
     * @param first  The first node id.
     * @param second The second node id.
     * @param third  The last node id.
     * @throws org.jrdf.graph.GraphException If there was an error adding the statement.
     */
    void add(Long first, Long second, Long third) throws GraphException;

    /**
     * Removes a triple from a single index.
     *
     * @param first  The first node.
     * @param second The second node.
     * @param third  The last node.
     * @throws GraphException If there was an error revoking the statement, for
     *                        example if it didn't exist.
     */
    void remove(Long first, Long second, Long third) throws GraphException;

    /**
     * Returns an iterator which contains all the elements in the graph as a
     * collections of distinct longs, contains a map of longs to other longs.
     * This prevents any duplication.
     *
     * @return an iterator which contains all the elements in the graph as a
     *         collections of distinct longs, contains a map of longs to other longs.
     *         This prevents any duplication.
     */
    Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator();

    /**
     * Returns the map of long to set of longs for the given entry of the index.  For example, a given subject id
     * is given and it returns a map of predicates to objects.
     *
     * @param first the entry set to find.
     * @return a map containing the list of longs to set of longs.
     */
    Map<Long, Set<Long>> getSubIndex(Long first);

    /**
     * Removes the given entry of long to set of longs with the given entry.  For example, a given subject id is
     * given and it will remove all the associated predicate and objects for that subject.
     *
     * @param first the entry set to remove.
     * @return true if the entry set was non-null.
     */
    boolean removeSubIndex(Long first);

    /**
     * Returns the number of triples in the index.
     *
     * @return the number of triples in the index.
     */
    long getSize();
}
