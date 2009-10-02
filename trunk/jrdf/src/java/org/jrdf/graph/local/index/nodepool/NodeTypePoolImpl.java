/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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
import org.jrdf.graph.Resource;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.local.LocalizedNode;

import java.util.List;
import java.util.Map;

public class NodeTypePoolImpl implements NodeTypePool {
    /**
     * The pool of all nodes, mapped from their ids.
     */
    private Map<Long, String> blankNodePool;
    private Map<Long, String> uriNodePool;
    private Map<Long, String> literalNodePool;
    private StringNodeMapper mapper;
    private Long nodeIdForVisitor;
    private String nodeValueForVisitor;

    public NodeTypePoolImpl(StringNodeMapper newMapper, Map<Long, String> newBlankNodePool,
        Map<Long, String> newUriNodePool, Map<Long, String> newLiteralNodePool) {
        this.mapper = newMapper;
        this.blankNodePool = newBlankNodePool;
        this.uriNodePool = newUriNodePool;
        this.literalNodePool = newLiteralNodePool;
    }

    public boolean nodeExists(Long nodeToTest) {
        return (getNodeIfExists(nodeToTest) != null);
    }

    public Node get(Long nodeId) {
        Node node = getNodeIfExists(nodeId);
        if (node == null) {
            throw new IllegalArgumentException("Couldn't find: " + nodeId);
        } else {
            return node;
        }
    }

    public Node getNodeIfExists(Long nodeId) {
        Node node = null;
        if (blankNodePool.keySet().contains(nodeId)) {
            node = mapper.convertToBlankNode(blankNodePool.get(nodeId));
        } else if (uriNodePool.keySet().contains(nodeId)) {
            node = mapper.convertToURIReference(uriNodePool.get(nodeId), nodeId);
        } else if (literalNodePool.keySet().contains(nodeId)) {
            node = mapper.convertToLiteral(literalNodePool.get(nodeId), nodeId);
        }
        return node;
    }

    public void put(Long id, LocalizedNode node) {
        nodeIdForVisitor = id;
        nodeValueForVisitor = mapper.convertToString(node);
        node.accept(this);
    }

    public void addNodeValues(NodePool nodePool, List<Map<Long, String>> values) {
        blankNodePool = values.get(0);
        uriNodePool = values.get(1);
        literalNodePool = values.get(2);
    }

    public String removeNode(Long nodeId) {
        String node = null;
        if (blankNodePool.keySet().contains(nodeId)) {
            node = blankNodePool.remove(nodeId);
        } else if (uriNodePool.keySet().contains(nodeId)) {
            node = uriNodePool.remove(nodeId);
        } else if (literalNodePool.keySet().contains(nodeId)) {
            node = literalNodePool.remove(nodeId);
        }
        return node;
    }

    public Map<Long, String> getBNodeValues() {
        return blankNodePool;
    }

    public Map<Long, String> getURINodeValues() {
        return uriNodePool;
    }

    public Map<Long, String> getLiteralNodeValues() {
        return literalNodePool;
    }

    public void clear() {
        blankNodePool.clear();
        uriNodePool.clear();
        literalNodePool.clear();
    }

    public long getNumberOfEntries() {
        long number = (long) blankNodePool.size();
        number += (long) uriNodePool.size();
        number += (long) literalNodePool.size();
        return number;
    }

    public void visitBlankNode(BlankNode blankNode) {
        blankNodePool.put(nodeIdForVisitor, nodeValueForVisitor);
    }

    public void visitURIReference(URIReference uriReference) {
        uriNodePool.put(nodeIdForVisitor, nodeValueForVisitor);
    }

    public void visitLiteral(Literal literal) {
        literalNodePool.put(nodeIdForVisitor, nodeValueForVisitor);
    }

    public void visitNode(Node node) {
        throw new IllegalArgumentException("Failed to add node with id: " + nodeIdForVisitor + " Node: " + node);
    }

    public void visitResource(Resource resource) {
    }
}
