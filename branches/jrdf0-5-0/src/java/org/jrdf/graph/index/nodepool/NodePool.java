/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */

package org.jrdf.graph.index.nodepool;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.mem.LocalizedNode;

import java.util.Collection;

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
     * Adds a node that was not created by this pool but still uses the MemNode interface.
     *
     * @param node The node to add.
     * @throws IllegalArgumentException The node conflicts with one already in use.
     */
    void registerNode(LocalizedNode node);

    /**
     * Returns all the nodes in the node pool.
     *
     * @return The node pool.
     */
    Collection<Node> getNodePoolValues();

    /**
     * Returns the current next node Id increments it by one.
     *
     * @return the current next node Id.
     */
    Long getNextNodeId();

    /**
     * Converts a globalized set of triple objects to an array of longs.
     *
     * @param first  The first node.
     * @param second The second node.
     * @param third  The last node.
     * @return an array of longs that match the given first, second and third nodes.
     *
     * @throws org.jrdf.graph.GraphException If there was an error adding the statement.
     */
    Long[] localize(Node first, Node second, Node third) throws GraphException;

    /**
     * Converts a node into it's localized version.
     *
     * @param node the node to localize.
     * @return the unique node identifier.
     * @throws GraphException if the node was not part of this node pool.
     */
    Long localize(Node node) throws GraphException;

    /**
     * Removes all entries from the nodepool.
     */
    void clear();
}