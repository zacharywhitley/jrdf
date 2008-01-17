/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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
import org.jrdf.graph.local.mem.LocalizedNode;
import org.jrdf.map.MapFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class NodePoolImpl implements NodePool {
    /**
     * Maps nodes to node types.
     */
    private NodeTypePool nodeTypePool;

    /**
     * A reverse mapping of all ids, mapped from their string.
     */
    private Map<String, Long> stringPool;

    /**
     * The next available node id.
     */
    private long nextNode = 1L;

    public NodePoolImpl(NodeTypePool newNodeTypePool, MapFactory newMapFactory) {
        nodeTypePool = newNodeTypePool;
        stringPool = newMapFactory.createMap(String.class, Long.class);
    }

    public Node getNodeById(Long id) {
        return nodeTypePool.get(id);
    }

    // TODO - Review these two methods.
    public Long getNodeIdByString(String str) {
        return stringPool.get(str);
    }

    public Long getNewNodeId() {
        return nextNode++;
    }

    public void registerNode(LocalizedNode node) {
        // get the id for this node
        Long id = node.getId();

        // look the node up to see if it already exists in the graph
        LocalizedNode existingNode = (LocalizedNode) nodeTypePool.get(id);
        if (null != existingNode) {
            // check that the node is equal to the one that is already in the graph
            if (existingNode.equals(node)) {
                return;
            }
            // node does not match
            throw new IllegalArgumentException("Node conflicts with one already in the graph. " +
                "Existing node: " + existingNode + ", new node: " + node);
        }
        // add the node
        nodeTypePool.put(id, node);

        // check if the node has a string representation
        if (!(node instanceof BlankNode)) {
            if (node instanceof Literal) {
                stringPool.put(((Literal) node).getEscapedForm(), node.getId());
            } else {
                stringPool.put(((URIReference) node).getURI().toString(), node.getId());
            }
        }

        // update the nextNode counter to a unique number
        if (!(id < nextNode)) {
            nextNode = id + 1L;
        }
    }

    public void registerNodePoolValues(List<Map<Long, String>> values) {
        // Recalculate size.
        for (Map<Long, String> value : values) {
            nextNode += value.size();
        }
        // Add non-blank nodes to string pool.
        addEntries(values.get(1).entrySet());
        addEntries(values.get(2).entrySet());
        // Add all entries to node pool.
        nodeTypePool.addNodeValues(this, values);
    }

    public String removeNode(Long value) {
        String node = nodeTypePool.removeNode(value);
        if (node != null) {
            stringPool.remove(node);
        }
        return node;
    }

    public List<Map<Long, String>> getNodePoolValues() {
        List<Map<Long, String>> values = new ArrayList<Map<Long, String>>();
        values.add(nodeTypePool.getBNodeValues());
        values.add(nodeTypePool.getURINodeValues());
        values.add(nodeTypePool.getLiteralNodeValues());
        return values;
    }

    public void clear() {
        nodeTypePool.clear();
        stringPool.clear();
        nextNode = 1L;
    }

    private void addEntries(Set<Map.Entry<Long, String>> entries) {
        for (Map.Entry<Long, String> entry : entries) {
            stringPool.put(entry.getValue(), entry.getKey());
        }
    }
}
