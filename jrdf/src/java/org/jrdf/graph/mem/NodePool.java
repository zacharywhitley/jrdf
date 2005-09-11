package org.jrdf.graph.mem;

import org.jrdf.graph.Node;

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
}
