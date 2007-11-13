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
package org.jrdf.graph.local.index.nodepool;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.Resource;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.local.mem.LocalizedNode;

import java.util.Collection;
import java.util.Map;

public final class NodePoolImpl implements NodePool {
    /**
     * Three being the number, and the number being 3.
     */
    private static final int TRIPLE = 3;

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

    public NodePoolImpl(NodeTypePool newNodeTypePool, Map<String, Long> newStringPool) {
        nodeTypePool = newNodeTypePool;
        stringPool = newStringPool;
    }

    public Node getNodeById(Long id) {
        return nodeTypePool.get(id);
    }

    public Long getNodeIdByString(String str) {
        return stringPool.get(str);
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

    public Collection<Node> getNodePoolValues() {
        return nodeTypePool.values();
    }

    public Long getNextNodeId() {
        return nextNode++;
    }

    public Long[] localize(Node first, Node second, Node third) throws GraphException {
        Long[] localValues = new Long[TRIPLE];

        // convert the nodes to local memory nodes for convenience
        localValues[0] = convertSubject(first);
        localValues[1] = convertPredicate(second);
        localValues[2] = convertObject(third);
        return localValues;
    }

    public Long localize(Node node) throws GraphException {
        return convertObject(node);
    }

    public void clear() {
        nodeTypePool.clear();
        stringPool.clear();
        nextNode = 1L;
    }

    private Long convertSubject(Node first) throws GraphException {
        Long subjectValue = null;
        if (ANY_SUBJECT_NODE != first) {
            if (LocalizedNode.class.isAssignableFrom(first.getClass())) {
                if (BlankNode.class.isAssignableFrom(first.getClass())) {
                    subjectValue = getBlankNode(first);
                } else if (URIReference.class.isAssignableFrom(first.getClass())) {
                    subjectValue = getNodeIdByString(((URIReference) first).getURI().toString());
                }
            }
            if (null == subjectValue) {
                throw new GraphException("Subject does not exist in graph: " + first);
            }
        }
        return subjectValue;
    }

    private Long convertPredicate(Node second) throws GraphException {
        Long predicateValue = null;
        if (ANY_PREDICATE_NODE != second) {
            predicateValue = getNodeIdByString(((URIReference) second).getURI().toString());
            if (null == predicateValue) {
                throw new GraphException("Predicate does not exist in graph: " + second);
            }
        }
        return predicateValue;
    }

    // TODO (AN) String of instanceof should be changed to calls by type.
    private Long convertObject(Node third) throws GraphException {
        Long objectValue = null;
        if (ANY_OBJECT_NODE != third) {
            if (LocalizedNode.class.isAssignableFrom(third.getClass())) {
                if (Resource.class.isAssignableFrom(third.getClass())) {
                    objectValue = getResource(third);
                } else if (BlankNode.class.isAssignableFrom(third.getClass())) {
                    objectValue = getBlankNode(third);
                } else if (Literal.class.isAssignableFrom(third.getClass())) {
                    objectValue = getNodeIdByString(((Literal) third).getEscapedForm());
                } else if (URIReference.class.isAssignableFrom(third.getClass())) {
                    objectValue = getNodeIdByString(((URIReference) third).getURI().toString());
                }
            }
            if (null == objectValue) {
                throw new GraphException("Object does not exist in graph: " + third);
            }
        }
        return objectValue;
    }

    private Long getResource(Node third) throws GraphException {
        Long objectValue;
        if (((Resource) third).isURIReference()) {
            objectValue = getNodeIdByString(((URIReference) third).getURI().toString());
        } else {
            objectValue = getBlankNode(third);
        }
        return objectValue;
    }

    private Long getBlankNode(Node blankNode) throws GraphException {
        Long nodeId = ((LocalizedNode) blankNode).getId();
        Node node = nodeTypePool.get(nodeId);
        if (node == null) {
            throw new GraphException("The node id was not found in the graph: " + nodeId);
        }
        if (!blankNode.equals(node)) {
            throw new GraphException("The node returned by the nodeId (" + nodeId + ") was not the same blank " +
                "node.  Got: " + node + ", expected: " + blankNode);
        }
        return nodeId;
    }
}
