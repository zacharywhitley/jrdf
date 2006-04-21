/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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
 */
package org.jrdf.graph.index.mem;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.mem.BlankNodeImpl;
import org.jrdf.graph.mem.LiteralImpl;
import org.jrdf.graph.mem.MemNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Stuff goes in here.
 *
 * @author Andrew Newman
 * @version $Id: ClosableIterator.java 436 2005-12-19 13:19:55Z newmana $
 */
public class NodePoolMemImpl implements NodePoolMem {
    /**
     * Three being the number, and the number being 3.
     */
    private static final int TRIPLE = 3;

    /**
     * The pool of all nodes, mapped from their ids.
     */
    private Map<Long, Node> nodePool;

    /**
     * A reverse mapping of all ids, mapped from their string.
     */
    private Map<String, Long> stringPool;

    /**
     * The next available node id.
     */
    private long nextNode;

    public NodePoolMemImpl() {
        nodePool = new HashMap<Long, Node>();
        stringPool = new HashMap<String, Long>();
        nextNode = 1L;
    }

    public Node getNodeById(Long id) {
        return nodePool.get(id);
    }


    public Long getNodeIdByString(String str) {
        return stringPool.get(str);
    }

    public void registerNode(MemNode node) {
        // get the id for this node
        Long id = node.getId();

        // look the node up to see if it already exists in the graph
        MemNode existingNode = (MemNode) nodePool.get(id);
        if (null != existingNode) {
            // check that the node is equal to the one that is already in the graph
            if (existingNode.equals(node)) {
                return;
            }
            // node does not match
            throw new IllegalArgumentException("Node conflicts with one already in the graph");
        }
        // add the node
        nodePool.put(id, node);

        // check if the node has a string representation
        if (!(node instanceof BlankNode)) {

            if (node instanceof Literal) {
                stringPool.put(((Literal) node).getEscapedForm(), node.getId());
            } else {
                stringPool.put(node.toString(), node.getId());
            }
        }

        // update the nextNode counter to a unique number
        if (!(id < nextNode)) {
            nextNode = id + 1L;
        }
    }


    public java.util.Collection<Node> getNodePoolValues() {
        return nodePool.values();
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

    private Long convertSubject(Node first) throws GraphException {
        Long subjectValue = null;
        if (ANY_SUBJECT_NODE != first) {
            if (first instanceof BlankNodeImpl) {
                subjectValue = ((BlankNodeImpl) first).getId();
            } else {
                subjectValue = getNodeIdByString(String.valueOf(first));
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
            predicateValue = getNodeIdByString(String.valueOf(second));

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
            if (third instanceof BlankNodeImpl) {
                objectValue = ((BlankNodeImpl) third).getId();
            } else if (third instanceof LiteralImpl) {
                objectValue = getNodeIdByString(((LiteralImpl) third).getEscapedForm());
            } else {
                objectValue = getNodeIdByString(String.valueOf(third));
            }

            if (null == objectValue) {
                throw new GraphException("Object does not exist in graph: " + third);
            }
        }

        return objectValue;
    }
}
