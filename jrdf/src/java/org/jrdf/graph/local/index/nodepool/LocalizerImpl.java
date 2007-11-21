/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

import org.jrdf.graph.Node;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.Resource;
import org.jrdf.graph.Literal;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import org.jrdf.graph.local.mem.LocalizedNode;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import static org.jrdf.util.param.ParameterUtil.*;

// TODO AN Comeback and review this - calls by class rather than type.
public class LocalizerImpl implements Localizer {
    private static final int TRIPLE = 3;
    private NodePool nodePool;

    public LocalizerImpl(NodePool newNodePool) {
        checkNotNull(newNodePool);
        this.nodePool = newNodePool;
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

    public Long convertSubject(Node first) throws GraphException {
        Long subjectValue = null;
        if (ANY_SUBJECT_NODE != first) {
            if (LocalizedNode.class.isAssignableFrom(first.getClass())) {
                if (BlankNode.class.isAssignableFrom(first.getClass())) {
                    subjectValue = getBlankNode(first);
                } else if (URIReference.class.isAssignableFrom(first.getClass())) {
                    subjectValue = nodePool.getNodeIdByString(((URIReference) first).getURI().toString());
                }
            }
            if (null == subjectValue) {
                throw new GraphException("Subject does not exist in graph: " + first);
            }
        }
        return subjectValue;
    }

    public Long convertPredicate(Node second) throws GraphException {
        Long predicateValue = null;
        if (ANY_PREDICATE_NODE != second) {
            predicateValue = nodePool.getNodeIdByString(((URIReference) second).getURI().toString());
            if (null == predicateValue) {
                throw new GraphException("Predicate does not exist in graph: " + second);
            }
        }
        return predicateValue;
    }

    public Long convertObject(Node third) throws GraphException {
        if (ANY_OBJECT_NODE != third) {
            return convertRealObjectNode(third);
        } else {
            return null;
        }
    }

    private Long convertRealObjectNode(Node third) throws GraphException {
        if (LocalizedNode.class.isAssignableFrom(third.getClass())) {
            return convertLocalObjectNode(third);
        } else {
            throw new GraphException("Object does not exist in graph: " + third);
        }
    }

    private Long convertLocalObjectNode(Node third) throws GraphException {
        Long objectValue;
        if (Resource.class.isAssignableFrom(third.getClass())) {
            objectValue = getResource(third);
        } else if (BlankNode.class.isAssignableFrom(third.getClass())) {
            objectValue = getBlankNode(third);
        } else if (Literal.class.isAssignableFrom(third.getClass())) {
            objectValue = nodePool.getNodeIdByString(((Literal) third).getEscapedForm());
        } else if (URIReference.class.isAssignableFrom(third.getClass())) {
            objectValue = nodePool.getNodeIdByString(((URIReference) third).getURI().toString());
        } else {
            throw new GraphException("Unknown node type: " + third.getClass());
        }
        return objectValue;
    }

    private Long getResource(Node third) throws GraphException {
        Long objectValue;
        if (((Resource) third).isURIReference()) {
            objectValue = nodePool.getNodeIdByString(((URIReference) third).getURI().toString());
        } else {
            objectValue = getBlankNode(third);
        }
        return objectValue;
    }

    private Long getBlankNode(Node blankNode) throws GraphException {
        Long nodeId = ((LocalizedNode) blankNode).getId();
        Node node = nodePool.getNodeById(nodeId);
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
