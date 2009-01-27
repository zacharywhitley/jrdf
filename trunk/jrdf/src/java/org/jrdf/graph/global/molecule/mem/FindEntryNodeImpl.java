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

package org.jrdf.graph.global.molecule.mem;

import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.global.molecule.FindEntryNode;
import static org.jrdf.graph.local.BlankNodeImpl.isBlankNode;
import org.jrdf.util.ClosableIterable;

import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

public class FindEntryNodeImpl implements FindEntryNode {
    private Set<BlankNode> visitedNodes;
    private Graph graph;
    private Triple currentTriple;

    public FindEntryNodeImpl() {
        visitedNodes = new HashSet<BlankNode>();
    }

    public Triple find(Graph newGraph, Triple triple) throws GraphException {
        this.graph = newGraph;
        this.currentTriple = triple;
        if (!isBlankNode(triple.getSubject())) {
            return triple;
        }
        // TODO Work out if this needs to be kept.
        addObjectNodeIfBlank(triple);
        if (!graph.contains(triple)) {
            throw new GraphException("Cannot find triple: " + triple);
        } else {
            findNextLevelOfNodes(new HashSet<BlankNode>(asList((BlankNode) triple.getSubject())));
            return currentTriple;
        }
    }

    private void addObjectNodeIfBlank(Triple triple) {
        ObjectNode object = triple.getObject();
        if (isBlankNode(object)) {
            visitedNodes.add((BlankNode) object);
        }
    }

    private BlankNode findNextLevelOfNodes(Set<BlankNode> currentLevelNodes) throws GraphException {
        Set<BlankNode> newNodes = new HashSet<BlankNode>();
        for (BlankNode bNode : currentLevelNodes) {
            visitedNodes.add(bNode);
            getNewBlankNodes(newNodes, graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, bNode));
        }
        if (newNodes.isEmpty()) {
            return currentLevelNodes.iterator().next();
        } else {
            return findNextLevelOfNodes(newNodes);
        }
    }

    private void getNewBlankNodes(Set<BlankNode> newNodes, ClosableIterable<Triple> triples) {
        for (Triple triple : triples) {
            SubjectNode newNode = triple.getSubject();
            if (isBlankNode(newNode) && (!visitedNodes.contains((BlankNode) newNode))) {
                currentTriple = triple;
                newNodes.add((BlankNode) newNode);
            }
        }
        triples.iterator().close();
    }
}

