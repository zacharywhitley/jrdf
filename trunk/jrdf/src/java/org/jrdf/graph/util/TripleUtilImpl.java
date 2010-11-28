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

package org.jrdf.graph.util;

import org.jrdf.collection.CollectionFactory;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;

import java.util.Set;
import java.util.SortedSet;

import static org.jrdf.graph.AbstractBlankNode.isBlankNode;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;

public class TripleUtilImpl implements TripleUtil {
    private final CollectionFactory setFactory;
    private Set<BlankNode> blankNodes;
    private GraphToSetOfTriples triples;

    public TripleUtilImpl(CollectionFactory newSetFactory) {
        this(newSetFactory, null);
    }

    public TripleUtilImpl(CollectionFactory newSetFactory, TripleComparator comparator) {
        this.setFactory = newSetFactory;
        this.triples = new GraphToSetOfTriplesImpl(setFactory, comparator);
    }

    public SortedSet<Triple> getAllTriplesForSubjectNode(Graph newGraph, SubjectNode node) throws GraphException {
        blankNodes = setFactory.createSet(BlankNode.class);
        SortedSet<Triple> triplesWithNode = triples.graphToSetOfTriples(newGraph, node, ANY_PREDICATE_NODE,
            ANY_OBJECT_NODE);
        getAllTriplesForNodeAndAnyBlankNodes(triplesWithNode, node, newGraph);
        return triplesWithNode;
    }

    public SortedSet<Triple> getAllTriplesForObjectNode(Graph newGraph, ObjectNode node) throws GraphException {
        blankNodes = setFactory.createSet(BlankNode.class);
        SortedSet<Triple> triplesWithNode = triples.graphToSetOfTriples(newGraph, ANY_SUBJECT_NODE, ANY_PREDICATE_NODE,
            node);
        getAllTriplesForNodeAndAnyBlankNodes(triplesWithNode, node, newGraph);
        return triplesWithNode;
    }

    public SortedSet<Triple> getAllTriplesForNode(Graph newGraph, Node node) throws GraphException {
        blankNodes = setFactory.createSet(BlankNode.class);
        // Nodes are always object nodes
        SortedSet<Triple> triplesWithNode = triples.graphToSetOfTriples(newGraph, ANY_SUBJECT_NODE, ANY_PREDICATE_NODE,
            (ObjectNode) node);
        // Add any if it's a bnode or URIRef
        if (SubjectNode.class.isAssignableFrom(node.getClass())) {
            triples.addGraphToSetOfTriples(triplesWithNode, newGraph, (SubjectNode) node, ANY_PREDICATE_NODE,
                ANY_OBJECT_NODE);
        }
        // Add any if it's a URIRef
        if (PredicateNode.class.isAssignableFrom(node.getClass())) {
            triples.addGraphToSetOfTriples(triplesWithNode, newGraph, ANY_SUBJECT_NODE, (PredicateNode) node,
                ANY_OBJECT_NODE);
        }
        getAllTriplesForNodeAndAnyBlankNodes(triplesWithNode, node, newGraph);
        return triplesWithNode;
    }

    private void getAllTriplesForNodeAndAnyBlankNodes(SortedSet<Triple> triplesWithNode, Node node, Graph newGraph)
        throws GraphException {
        getAllBNodesForNode(node, newGraph);
        for (BlankNode bNode : blankNodes) {
            triples.addGraphToSetOfTriples(triplesWithNode, newGraph, bNode, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
            triples.addGraphToSetOfTriples(triplesWithNode, newGraph, ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, bNode);
        }
    }

    private void getAllBNodesForNode(Node node, Graph newGraph) throws GraphException {
        SortedSet<Triple> tmpSet = setFactory.createSet(Triple.class);
        if (SubjectNode.class.isAssignableFrom(node.getClass())) {
            triples.addGraphToSetOfTriples(tmpSet, newGraph, (SubjectNode) node, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        }
        if (ObjectNode.class.isAssignableFrom(node.getClass())) {
            triples.addGraphToSetOfTriples(tmpSet, newGraph, ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, (ObjectNode) node);
        }
        for (Triple triple : tmpSet) {
            final SubjectNode sNode = triple.getSubject();
            addBlankNodeToSet(sNode, newGraph);
            final ObjectNode oNode = triple.getObject();
            addBlankNodeToSet(oNode, newGraph);
        }
    }

    private void addBlankNodeToSet(Node sNode, Graph newGraph) throws GraphException {
        if (isBlankNode(sNode) && !blankNodes.contains(sNode)) {
            blankNodes.add((BlankNode) sNode);
            getAllBNodesForNode(sNode, newGraph);
        }
    }
}
