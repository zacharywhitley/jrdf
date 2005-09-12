package org.jrdf.graph.mem;

import org.jrdf.graph.Node;
import org.jrdf.graph.GraphException;

/**
 * Maps between the Long identifier and a Node (globalized value) or from a String to a Long (local value).
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface NodePool {

    /**
     * Package method to find a node in the node pool by its id.
     *
     * @param id The id of the node to search for.
     * @return The node referred to by the id, null if not found.
     */
    Node getNodeById(Long id);

    /**
     * Package method to find a node id based on its string representation.
     *
     * @param str The string representation of a node.
     * @return The id of the node with the given string.
     */
    Long getNodeIdByString(String str);

    /**
     * Converts a globalized set of triple objects to an array of longs.
     *
     * @param first  The first node.
     * @param second The second node.
     * @param third  The last node.
     * @throws org.jrdf.graph.GraphException If there was an error adding the statement.
     */
    Long[] localize(Node first, Node second, Node third) throws GraphException;
}
