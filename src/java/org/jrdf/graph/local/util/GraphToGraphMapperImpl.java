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
import org.jrdf.collection.MapFactory;
import org.jrdf.graph.AbstractBlankNode;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.URIReference;
import org.jrdf.util.ClosableIterable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GraphToGraphMapperImpl implements GraphToGraphMapper {
    private Graph graph;
    private GraphElementFactory elementFactory;
    private TripleFactory tripleFactory;
    private Map<Long, BlankNode> newBNodeMap;
    private Set<Triple> triplesToRemove;
    private Set<Triple> triplesToAdd;
    private CollectionFactory setFactory;

    public GraphToGraphMapperImpl(Graph newGraph, MapFactory mapFactory, CollectionFactory sFac) {
        graph = newGraph;
        elementFactory = graph.getElementFactory();
        tripleFactory = graph.getTripleFactory();
        newBNodeMap = mapFactory.createMap(Long.class, BlankNode.class);
        setFactory = sFac;
        triplesToRemove = setFactory.createSet(Triple.class);
        triplesToAdd = setFactory.createSet(Triple.class);
    }

    public Graph getGraph() {
        return graph;
    }

    public void close() {
        newBNodeMap.clear();
        triplesToRemove.clear();
        triplesToAdd.clear();
    }

    public void addTripleToGraph(Triple triple) throws GraphException {
        SubjectNode subjectNode = elementFactory.createURIReference(((URIReference) triple.getSubject()).getURI());
        PredicateNode predicateNode = elementFactory.createURIReference(
            ((URIReference) triple.getPredicate()).getURI());
        ObjectNode objectNode = createLiteralOrURI(triple.getObject());
        final Triple triple1 = tripleFactory.createTriple(subjectNode, predicateNode, objectNode);
        graph.add(triple1);
    }

    public void updateBlankNodes(Triple triple) throws GraphElementFactoryException {
        SubjectNode subjectNode = triple.getSubject();
        long hashcode;
        if (AbstractBlankNode.isBlankNode(subjectNode)) {
            hashcode = (long) subjectNode.hashCode();
            if (!newBNodeMap.containsKey(hashcode)) {
                newBNodeMap.put(hashcode, elementFactory.createBlankNode());
            }
        }
        final ObjectNode objectNode = triple.getObject();
        if (AbstractBlankNode.isBlankNode(objectNode)) {
            hashcode = (long) objectNode.hashCode();
            if (!newBNodeMap.containsKey(hashcode)) {
                newBNodeMap.put(hashcode, elementFactory.createBlankNode());
            }
        }
    }

    public Graph createNewTriples(Iterator<Triple> it) throws GraphException {
        while (it.hasNext()) {
            Triple triple = it.next();
            final Triple newTriple = createNewTriple(triple);
            graph.add(newTriple);
        }
        return graph;
    }

    private Triple createNewTriple(Triple triple) throws GraphElementFactoryException {
        SubjectNode subjectNode = triple.getSubject();
        PredicateNode predicateNode = triple.getPredicate();
        ObjectNode objectNode = triple.getObject();
        SubjectNode newSubjectNode = (SubjectNode) createNewNode(subjectNode);
        PredicateNode newPredicateNode = predicateNode;
        ObjectNode newObjectNode = (ObjectNode) createNewNode(objectNode);
        return tripleFactory.createTriple(newSubjectNode, newPredicateNode, newObjectNode);
    }

    public Node createNewNode(Node node) throws GraphElementFactoryException {
        Node newNode;
        if (AbstractBlankNode.isBlankNode(node)) {
            newNode = newBNodeMap.get((long) node.hashCode());
        } else {
            newNode = createLiteralOrURI((ObjectNode) node);
        }
        return newNode;
    }

    private ObjectNode createLiteralOrURI(ObjectNode objectNode) {
        return objectNode;
    }

    public void replaceNode(Node node, Node newNode) throws GraphException {
        replaceSubjectNode(node, newNode);
        replaceObjectNode(node, newNode);
        replacePredicateNode(node, newNode);
    }

    public void replaceObjectNode(Node node, Node newNode) throws GraphException {
        if (node == null || newNode == null) {
            return;
        }
        if (ObjectNode.class.isAssignableFrom(node.getClass()) &&
            ObjectNode.class.isAssignableFrom(newNode.getClass())) {
            final ObjectNode oldONode = (ObjectNode) createNewNode(node);
            ClosableIterable<Triple> triples = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, oldONode);
            for (Triple triple : triples) {
                Triple newTriple =
                    tripleFactory.createTriple(triple.getSubject(), triple.getPredicate(), (ObjectNode) newNode);
                triplesToAdd.add(newTriple);
                triplesToRemove.add(new TripleImpl(triple.getSubject(), triple.getPredicate(), oldONode));
            }
            triples.iterator().close();
            addRemoveTriples();
        }
    }

    public void replacePredicateNode(Node node, Node newNode) throws GraphException {
        if (node == null || newNode == null) {
            return;
        }
        if (PredicateNode.class.isAssignableFrom(node.getClass()) &&
            PredicateNode.class.isAssignableFrom(newNode.getClass())) {
            final PredicateNode oldONode = (PredicateNode) createNewNode(node);
            ClosableIterable<Triple> triples = graph.find(ANY_SUBJECT_NODE, oldONode, ANY_OBJECT_NODE);
            for (Triple triple : triples) {
                Triple newTriple =
                    tripleFactory.createTriple(triple.getSubject(), (PredicateNode) newNode, triple.getObject());
                triplesToAdd.add(newTriple);
                triplesToRemove.add(new TripleImpl(triple.getSubject(), oldONode, triple.getObject()));
            }
            triples.iterator().close();
            addRemoveTriples();
        }
    }

    public void replaceSubjectNode(Node node, Node newNode) throws GraphException {
        if (node == null || newNode == null) {
            return;
        }
        if (SubjectNode.class.isAssignableFrom(node.getClass()) &&
            SubjectNode.class.isAssignableFrom(newNode.getClass())) {
            final SubjectNode oldSNode = (SubjectNode) createNewNode(node);
            ClosableIterable<Triple> triples = graph.find(oldSNode, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
            for (Triple triple : triples) {
                Triple newTriple =
                    tripleFactory.createTriple((SubjectNode) newNode, triple.getPredicate(), triple.getObject());
                triplesToAdd.add(newTriple);
                triplesToRemove.add(new TripleImpl(oldSNode, triple.getPredicate(), triple.getObject()));
            }
            triples.iterator().close();
            addRemoveTriples();
        }
    }

    private void addRemoveTriples() throws GraphException {
        for (Triple tr : triplesToAdd) {
            graph.add(tr);
        }
        for (Triple tr : triplesToRemove) {
            try {
                graph.remove(tr);
            } catch (GraphException e) {
                System.err.println("Removing non-existent triple: " + tr.toString());
            }
        }
        triplesToAdd.clear();
        triplesToRemove.clear();
    }
}
