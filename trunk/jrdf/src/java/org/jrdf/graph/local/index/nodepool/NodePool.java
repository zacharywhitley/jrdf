/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.local.index.nodepool;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import org.jrdf.util.ClosableIterator;

import java.util.List;
import java.util.Map;

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
     * Returns all the nodes in the node pool.
     *
     * @return The node pool.
     */
    List<Map<Long, String>> getNodePoolValues();

    /**
     * Returns a new node id.
     *
     * @return a new node id
     */
    Long getNewNodeId();

    /**
     * Removes all entries from the nodepool.
     */
    void clear();

    /**
     * Registers a large number of nodes in ones go.
     *
     * @param values the map contains a list of longs to serialized nodes (as strings).
     */
    void registerNodePoolValues(List<Map<Long, String>> values);

    /**
     * Remove a node from the node pool.
     *
     * @param value the value to remove.
     * @return null if not delete or the value.
     */
    String removeNode(Long value);

    void registerLocalBlankNode(BlankNode node);

    void registerURIReference(URIReference node);

    void registerLiteral(Literal node);

    ClosableIterator<BlankNode> getBlankNodeIterator();

    ClosableIterator<URIReference> getURIReferenceIterator();

    boolean nodeExists(Long id);

    Node getNodeIfExists(Long nodeId);
}
