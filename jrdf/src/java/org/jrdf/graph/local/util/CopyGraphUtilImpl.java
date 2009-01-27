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

package org.jrdf.graph.local.util;

import org.jrdf.collection.CollectionFactory;
import org.jrdf.collection.MapFactory;
import org.jrdf.graph.AbstractBlankNode;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.util.ClosableIterable;
import org.jrdf.util.ClosableIterator;

import java.util.Iterator;
import java.util.Set;

public class CopyGraphUtilImpl implements CopyGraphUtil {
    private final MapFactory mapFactory;
    private final CollectionFactory setFactory;
    private GraphToGraphMapper mapper;
    private TripleUtil tripleUtil;

    public CopyGraphUtilImpl(MapFactory newMapFactory, CollectionFactory newSetFactory) {
        this.mapFactory = newMapFactory;
        this.setFactory = newSetFactory;
        mapper = null;
        tripleUtil = new TripleUtilImpl(setFactory);
    }

    public Graph getGraph() {
        return mapper.getGraph();
    }

    public void close() {
        if (mapper != null) {
            mapper.close();
        }
    }

    public Graph copyGraph(Graph newSourceGraph, Graph newTargetGraph) throws GraphException {
        mapper = new GraphToGraphMapperImpl(newTargetGraph, mapFactory, setFactory);
        ClosableIterator<Triple> triples = newSourceGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).
            iterator();
        readAndUpdateTripleIterator(triples);
        triples = newSourceGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        try {
            mapper.createNewTriples(triples);
        } catch (Exception e) {
            throw new GraphException("Cannot create new triples.", e);
        } finally {
            triples.close();
        }
        return mapper.getGraph();
    }

    public Node copyTriplesForNode(Graph newSourceGraph, Graph newTargetGraph, Node node, Node newNode)
        throws GraphException {
        if (mapper == null) {
            mapper = new GraphToGraphMapperImpl(newTargetGraph, mapFactory, setFactory);
        }
        try {
            Set<Triple> set = tripleUtil.getAllTriplesForNode(node, newSourceGraph);
            createNewGraph(set);
            return newNode;
        } catch (Exception e) {
            throw new GraphException("Cannot copy RDF graph with node", e);
        }
    }

    public void replaceNode(Graph newTargetGraph, Node oldNode, Node newNode) throws GraphException {
        try {
            if (mapper == null) {
                mapper = new GraphToGraphMapperImpl(newTargetGraph, mapFactory, setFactory);
            }
            mapper.replaceNode(oldNode, newNode);
        } catch (GraphException e) {
            throw new GraphException("Cannot replace old node: " + oldNode.toString(), e);
        }
    }

    public SubjectNode copyTriplesForSubjectNode(Graph newSourceGraph, Graph newTargetGraph,
        SubjectNode node, SubjectNode newNode) throws GraphException {
        mapper = new GraphToGraphMapperImpl(newTargetGraph, mapFactory, setFactory);
        try {
            Set<Triple> set = tripleUtil.getAllTriplesForSubjectNode(node, newSourceGraph);
            createNewGraph(set);
            set.clear();
            mapper.replaceSubjectNode(node, newNode);
            return newNode;
        } catch (GraphElementFactoryException e) {
            throw new GraphException("Cannot copy RDF graph with subject node", e);
        }
    }

    public ObjectNode copyTriplesForObjectNode(Graph newSourceGraph, Graph newTargetGraph, ObjectNode node,
        ObjectNode newNode) throws GraphException {
        mapper = new GraphToGraphMapperImpl(newTargetGraph, mapFactory, setFactory);
        try {
            Set<Triple> set = tripleUtil.getAllTriplesForObjectNode(node, newSourceGraph);
            createNewGraph(set);
            mapper.replaceObjectNode(node, newNode);
            set.clear();
            return newNode;
        } catch (GraphElementFactoryException e) {
            throw new GraphException("Cannot copy RDF graph with object node", e);
        }
    }

    private void createNewGraph(Set<Triple> set) throws GraphException {
        Iterator<Triple> triples = set.iterator();
        readAndUpdateTripleIterator(triples);
        triples = set.iterator();
        mapper.createNewTriples(triples);
    }

    /**
     * Return all blank nodes related to this particular node.
     * @param node
     * @param graph
     * @param bSet
     * @return
     * @throws GraphException
     */
    private void getAllBNodesForNode1(Node node, Graph graph, Set<BlankNode> bSet) throws GraphException {
        Set<Triple> set = setFactory.createSet(Triple.class);
        addTriplesToSetForSubject(graph, set, node);
        addTriplesToSetForObject(graph, set, node);
        for (Triple triple : set) {
            final SubjectNode sNode = triple.getSubject();
            addBlankNodeToSet(graph, bSet, sNode);
            final ObjectNode oNode = triple.getObject();
            addBlankNodeToSet(graph, bSet, oNode);
        }
        set.clear();
    }

    private void addBlankNodeToSet(Graph graph, Set<BlankNode> bSet, Node sNode) throws GraphException {
        if (AbstractBlankNode.isBlankNode(sNode) && !bSet.contains(sNode)) {
            bSet.add((BlankNode) sNode);
            getAllBNodesForNode1(sNode, graph, bSet);
        }
    }

    private void addTriplesToSetForSubject(Graph graph, Set<Triple> set, Node node) throws GraphException {
        if (!(node instanceof Literal)) {
            addTriplesToSet(graph, set, (SubjectNode) node, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        }
    }

    private void addTriplesToSetForObject(Graph graph, Set<Triple> set, Node node) throws GraphException {
        // add all triples that have node as object
        addTriplesToSet(graph, set, ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, (ObjectNode) node);
    }

    private void addTriplesToSet(Graph graph, Set<Triple> set, SubjectNode subjectNode,
        PredicateNode predicateNode, ObjectNode objectNode) throws GraphException {
        ClosableIterable<Triple> triples = graph.find(subjectNode, predicateNode, objectNode);
        for (Triple triple : triples) {
            set.add(triple);
        }
        triples.iterator().close();
    }

    private void readAndUpdateTripleIterator(Iterator<Triple> triples) throws GraphException {
        try {
            while (triples.hasNext()) {
                Triple triple = triples.next();
                if (!triple.isGrounded()) {
                    mapper.updateBlankNodes(triple);
                }
            }
        } catch (Exception e) {
            throw new GraphException("Cannot read RDF graph", e);
        } finally {
            if (triples instanceof ClosableIterator) {
                ((ClosableIterator<Triple>) triples).close();
            }
        }
    }
}
