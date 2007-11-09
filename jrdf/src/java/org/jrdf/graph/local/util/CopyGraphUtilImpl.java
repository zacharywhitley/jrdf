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

package org.jrdf.graph.local.util;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.map.MapFactory;
import org.jrdf.util.ClosableIterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CopyGraphUtilImpl implements CopyGraphUtil {
    private final MapFactory mapFactory;
    private GraphToGraphMapper mapper;

    public CopyGraphUtilImpl(MapFactory newMapFactory) {
        this.mapFactory = newMapFactory;
    }

    public Graph copyGraph(Graph newSourceGraph, Graph newTargetGraph) throws GraphException {
        mapper = new GraphToGraphMapperImpl(newTargetGraph, mapFactory);
        ClosableIterator<Triple> triples = newSourceGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        readSourceGraph(triples);
        triples = newSourceGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        try {
            mapper.createNewTriples(triples);
        } catch (Exception e) {
            throw new GraphException("Cannot create new triples", e);
        } finally {
            triples.close();
        }
        return mapper.getGraph();
    }

    public Graph copyTriplesForNode(Graph newSourceGraph, Graph newTargetGraph, Node node) throws GraphException {
        mapper = new GraphToGraphMapperImpl(newTargetGraph, mapFactory);
        Iterator<Triple> triples;
        try {
            Set<Triple> set = getAllTriplesForNode(node, newSourceGraph);
            triples = set.iterator();
            readSourceGraph(triples);
            triples = set.iterator();
            mapper.createNewTriples(triples);
        } catch (Exception e) {
            throw new GraphException("Cannot copy RDF graph with node", e);
        }
        return newTargetGraph;
    }

    private Set<Triple> getAllTriplesForNode(Node node, Graph graph) throws GraphException {
        Set<Triple> set = new HashSet<Triple>();
        Set<BlankNode> bSet = new HashSet<BlankNode>();
        getAllTriplesForNode0(graph, set, bSet, node);
        return set;
    }

    /**
     * Get all the triples that contain node, and all the other triples that contain blank nodes appear
     * in the existing triples recursively
     *
     * @param graph
     * @param node
     * @return
     * @throws GraphException
     */
    private void getAllTriplesForNode0(Graph graph, Set<Triple> set, Set<BlankNode> bSet, Node node)
        throws GraphException {

        addDirectTriplesToSet(graph, set, node);

        // add all other triples that contain blank nodes shared with set
        for (Triple triple : set) {
            final SubjectNode sNode = triple.getSubject();
            if (GraphToGraphMapperImpl.isBlankNode(sNode) && !bSet.contains(sNode)) {
                bSet.add((BlankNode) sNode);
                getAllTriplesForNode0(graph, set, bSet, sNode);
            }
            final ObjectNode oNode = triple.getObject();
            if (GraphToGraphMapperImpl.isBlankNode(oNode) && !bSet.contains(oNode)) {
                bSet.add((BlankNode) oNode);
                getAllTriplesForNode0(graph, set, bSet, oNode);
            }
        }
    }

    /**
     * Add triples that contain node as subject or object to the set
     *
     * @param graph
     * @param set
     * @param node
     * @return
     * @throws GraphException
     */
    private void addDirectTriplesToSet(Graph graph, Set<Triple> set, Node node) throws GraphException {
        // add all triples that have node as subject
        if (!Literal.class.isAssignableFrom(node.getClass())) {
            addTriplesToSet(graph, set, (SubjectNode) node, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        }
        // add all triples that have node as object
        addTriplesToSet(graph, set, ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, (ObjectNode) node);
    }

    private void addTriplesToSet(Graph graph, Set<Triple> set, SubjectNode subjectNode,
        PredicateNode predicateNode,
        ObjectNode objectNode) throws GraphException {
        ClosableIterator<Triple> iterator = graph.find(subjectNode, predicateNode, objectNode);
        try {
            while (iterator.hasNext()) {
                final Triple triple = iterator.next();
                set.add(triple);
            }
        } finally {
            iterator.close();
        }
    }

    private void readSourceGraph(Iterator<Triple> triples) throws GraphException {
        try {
            Triple triple;
            while (triples.hasNext()) {
                triple = triples.next();
                if (triple.isGrounded()) {
                    mapper.addTripleToGraph(triple);
                } else {
                    mapper.updateBlankNodes(triple);
                }
            }
        } catch (Exception e) {
            throw new GraphException("Cannot read RDF graph", e);
        } finally {
            if (ClosableIterator.class.isAssignableFrom(triples.getClass())) {
                ((ClosableIterator<Triple>) triples).close();
            }
        }
    }
}