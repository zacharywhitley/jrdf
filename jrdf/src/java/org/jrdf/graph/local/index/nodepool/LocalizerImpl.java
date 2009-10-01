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

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.Resource;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.local.BlankNodeImpl;
import org.jrdf.graph.local.LocalizedNode;
import org.jrdf.graph.local.URIReferenceImpl;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.net.URI;
import java.util.UUID;

public class LocalizerImpl implements Localizer {
    private static final int TRIPLE = 3;
    private Long currentId;
    private GraphException exception;
    private final NodePool nodePool;
    private final StringNodeMapper mapper;

    public LocalizerImpl(NodePool newNodePool, StringNodeMapper newMapper) {
        checkNotNull(newNodePool, newMapper);
        this.nodePool = newNodePool;
        this.mapper = newMapper;
    }

    public Long[] localize(Node first, Node second, Node third) throws GraphException {
        Long[] localValues = new Long[TRIPLE];
        localValues[0] = localize(first);
        localValues[1] = localize(second);
        localValues[2] = localize(third);
        return localValues;
    }

    public Long localize(Node node) throws GraphException {
        if (ANY_SUBJECT_NODE != node && ANY_PREDICATE_NODE != node && ANY_OBJECT_NODE != node) {
            if (node instanceof LocalizedNode) {
                return getId(node);
            }
            throw new GraphException("Node id was not found in the graph: " + node);
        } else {
            return null;
        }
    }

    public BlankNode createLocalBlankNode() {
        String uid = UUID.randomUUID().toString();
        currentId = nodePool.getNewNodeId();
        BlankNode node = new BlankNodeImpl(uid, currentId);
        nodePool.registerLocalBlankNode(node);
        return node;
    }

    public URIReference createLocalURIReference(URI uri, boolean validate) {
        currentId = nodePool.getNewNodeId();
        URIReference node = new URIReferenceImpl(uri, validate, currentId);
        nodePool.registerURIReference(node);
        return node;
    }

    public Literal createLocalLiteral(String escapedForm) {
        currentId = nodePool.getNewNodeId();
        Literal node = mapper.convertToLiteral(escapedForm, currentId);
        nodePool.registerLiteral(node);
        return node;
    }

    public void visitBlankNode(BlankNode blankNode) {
        currentId = ((LocalizedNode) blankNode).getId();
        Node node = nodePool.getNodeIfExists(currentId);
        if (node == null) {
            try {
                node = createLocalBlankNode();
                currentId = ((LocalizedNode) node).getId();
            } catch (GraphException e) {
                exception = e;
            }
        }
        if (!blankNode.equals(node)) {
            exception = new ExternalBlankNodeException("The node returned by the nodeId (" + currentId + ") was " +
                "not the same blank node.  Got: " + node + ", expected: " + blankNode);
        }
    }

    public void visitURIReference(URIReference uriReference) {
        String uriAsString = uriReference.getURI().toString();
        currentId = nodePool.getNodeIdByString(uriAsString);
        if (currentId == null) {
            currentId = ((LocalizedNode) createLocalURIReference(uriReference.getURI(), false)).getId();
        }
    }

    public void visitLiteral(Literal literal) {
        String escapedForm = literal.getEscapedForm();
        currentId = nodePool.getNodeIdByString(escapedForm);
        if (currentId == null) {
            currentId = ((LocalizedNode) createLocalLiteral(escapedForm)).getId();
        }
    }

    public void visitNode(Node node) {
        exception = new GraphException("Unknown node type: " + node + " class: " + node.getClass());
    }

    public void visitResource(Resource resource) {
        if (resource.isURIReference()) {
            currentId = nodePool.getNodeIdByString(resource.getURI().toString());
        } else {
            visitBlankNode(resource);
        }
    }

    private Long getId(Node node) throws GraphException {
        exception = null;
        currentId = null;
        node.accept(this);
        if (exception != null) {
            throw exception;
        } else if (currentId == null) {
            throw new GraphException("Node id was not found in the graph: " + node);
        } else {
            return currentId;
        }
    }
}
