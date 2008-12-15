/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.graph.local.util;

import org.jrdf.collection.CollectionFactory;
import org.jrdf.graph.AbstractBlankNode;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.util.ClosableIterable;

import java.util.Set;

public class TripleUtilImpl implements TripleUtil {
    private final CollectionFactory setFactory;
    private Set<Triple> triplesWithNode;
    private Set<BlankNode> blankNodes;
    private Graph graph;
    private TripleComparator tripleComparator;

    public TripleUtilImpl(CollectionFactory newSetFactory) {
        this.setFactory = newSetFactory;
    }

    public TripleUtilImpl(CollectionFactory newSetFactory, TripleComparator comparator) {
        this.setFactory = newSetFactory;
        this.tripleComparator = comparator;
    }

    public Set<Triple> getAllTriplesForSubjectNode(SubjectNode node, Graph newGraph) throws GraphException {
        graph = newGraph;
        createTriplesSet();
        blankNodes = setFactory.createSet(BlankNode.class);
        addTriplesToSet(triplesWithNode, node, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        getAllTriplesForNode(node);
        return triplesWithNode;
    }

    public Set<Triple> getAllTriplesForObjectNode(ObjectNode node, Graph newGraph) throws GraphException {
        graph = newGraph;
        createTriplesSet();
        blankNodes = setFactory.createSet(BlankNode.class);
        addTriplesToSet(triplesWithNode, ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, node);
        getAllTriplesForNode(node);
        return triplesWithNode;
    }

    public Set<Triple> getAllTriplesForNode(Node node, Graph newGraph) throws GraphException {
        graph = newGraph;
        createTriplesSet();
        blankNodes = setFactory.createSet(BlankNode.class);
        // TODO Change to use NodePositionVisitor if waranted
        if (SubjectNode.class.isAssignableFrom(node.getClass())) {
            addTriplesToSet(triplesWithNode, (SubjectNode) node, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        }
        if (PredicateNode.class.isAssignableFrom(node.getClass())) {
            addTriplesToSet(triplesWithNode, ANY_SUBJECT_NODE, (PredicateNode) node, ANY_OBJECT_NODE);
        }
        if (ObjectNode.class.isAssignableFrom(node.getClass())) {
            addTriplesToSet(triplesWithNode, ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, (ObjectNode) node);
        }
        getAllTriplesForNode(node);
        return triplesWithNode;
    }

    public Set<Triple> getAllTriplesForTriple(Triple triple, Graph newGraph) throws GraphException {
        graph = newGraph;
        createTriplesSet();
        triplesWithNode.add(triple);
        if (AbstractBlankNode.isBlankNode(triple.getSubject())) {
            triplesWithNode = this.getAllTriplesForSubjectNode(triple.getSubject(), graph);
        }
        if (AbstractBlankNode.isBlankNode(triple.getObject())) {
            triplesWithNode.addAll(this.getAllTriplesForObjectNode(triple.getObject(), graph));
        }
        return triplesWithNode;
    }

    private void createTriplesSet() {
        if (tripleComparator == null) {
            triplesWithNode = setFactory.createSet(Triple.class);
        } else {
            triplesWithNode = setFactory.createSet(Triple.class, tripleComparator);
        }
    }

    /**
     * Get all the triples that contain node, and all the other triples that contain blank nodes appear
     * in the existing triples recursively.
     *
     * @param node
     * @return
     * @throws GraphException
     */
    private void getAllTriplesForNode(Node node) throws GraphException {
        getAllBNodesForNode(node);
        for (BlankNode bNode : blankNodes) {
            addTriplesToSet(triplesWithNode, bNode, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
            addTriplesToSet(triplesWithNode, ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, bNode);
        }
    }

    private void addTriplesToSet(Set<Triple> set, SubjectNode subjectNode, PredicateNode predicateNode,
        ObjectNode objectNode) throws GraphException {
        ClosableIterable<Triple> triples = graph.find(subjectNode, predicateNode, objectNode);
        try {
            for (Triple triple : triples) {
                set.add(triple);
            }
        } finally {
            triples.iterator().close();
        }
    }

    /**
     * Return all blank nodes related to this particular node.
     *
     * @param node
     * @return
     * @throws GraphException
     */
    private void getAllBNodesForNode(Node node) throws GraphException {
        Set<Triple> tmpSet = setFactory.createSet(Triple.class);
        if (SubjectNode.class.isAssignableFrom(node.getClass())) {
            addTriplesToSet(tmpSet, (SubjectNode) node, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        }
        if (ObjectNode.class.isAssignableFrom(node.getClass())) {
            addTriplesToSet(tmpSet, ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, (ObjectNode) node);
        }
        for (Triple triple : tmpSet) {
            final SubjectNode sNode = triple.getSubject();
            addBlankNodeToSet(sNode);
            final ObjectNode oNode = triple.getObject();
            addBlankNodeToSet(oNode);
        }
        tmpSet.clear();
    }

    private void addBlankNodeToSet(Node sNode) throws GraphException {
        if (AbstractBlankNode.isBlankNode(sNode) && !blankNodes.contains(sNode)) {
            blankNodes.add((BlankNode) sNode);
            getAllBNodesForNode(sNode);
        }
    }
}
