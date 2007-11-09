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

package org.jrdf.graph.local.mem.copyUtil;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.util.ClosableIterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CopyGraphUtilImpl implements CopyGraphUtil {
    private HashMap<Integer, BlankNode> newBNodeMap;
    private Graph sourceGraph;
    private Graph targetGraph;
    private GraphElementFactory elemFactory;
    private TripleFactory tripleFactory;
    private CopyGraphHelper helper;

    public CopyGraphUtilImpl(Graph sg, Graph tg) {
        newBNodeMap = new HashMap<Integer, BlankNode>();
        sourceGraph = sg;
        targetGraph = tg;
        helper = new CopyGraphHelper(targetGraph);

        elemFactory = targetGraph.getElementFactory();
        tripleFactory = targetGraph.getTripleFactory();
    }

    private void reset() {
        helper = new CopyGraphHelper(targetGraph);
        elemFactory = targetGraph.getElementFactory();
        tripleFactory = targetGraph.getTripleFactory();
    }

    /**
     * Copies all the triples in source graph to target graph,
     * respecting the blank node "identifies".
     *
     * @param sg
     * @param tg
     * @return
     * @throws GraphException
     */
    public Graph copyGraph(Graph sg, Graph tg) throws GraphException {
        sourceGraph = sg;
        targetGraph = tg;
        reset();
        ClosableIterator<Triple> triples = sourceGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        readSourceGraph(triples);
        triples = sourceGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        try {
            createNewTriples(triples);
        } catch (Exception e) {
            throw new GraphException("Cannot create new triples", e);
        } finally {
            triples.close();
        }
        return targetGraph;
    }

    private Set<Triple> getAllTriplesForNode(Node node, Graph graph) throws GraphException {
        Set<Triple> set = new HashSet<Triple>();
        Set<BlankNode> bSet = new HashSet<BlankNode>();
        getAllTriplesForNode0(set, bSet, node, graph);
        return set;
    }

    /**
     * Get all the triples that contain node, and all the other triples that contain blank nodes appear
     * in the existing triples recursively
     *
     * @param node
     * @param graph
     * @return
     * @throws GraphException
     */
    private void getAllTriplesForNode0(Set<Triple> set, Set<BlankNode> bSet, Node node, Graph graph)
        throws GraphException {

        addDirectTriplesToSet(set, node, graph);

        // add all other triples that contain blank nodes shared with set
        for (Triple triple : set) {
            final SubjectNode sNode = triple.getSubject();
            if (CopyGraphHelper.isBlankNode(sNode) && !bSet.contains(sNode)) {
                bSet.add((BlankNode) sNode);
                getAllTriplesForNode0(set, bSet, sNode, graph);
            }
            final ObjectNode oNode = triple.getObject();
            if (CopyGraphHelper.isBlankNode(oNode) && !bSet.contains(oNode)) {
                bSet.add((BlankNode) oNode);
                getAllTriplesForNode0(set, bSet, oNode, graph);
            }
        }
    }

    /**
     * Add triples that contain node as subject or object to the set
     *
     * @param set
     * @param node
     * @param graph
     * @return
     * @throws GraphException
     */
    private void addDirectTriplesToSet(Set<Triple> set, Node node, Graph graph) throws GraphException {
        ClosableIterator<Triple> iterator;

        // add all triples that have node as subject
        if (!Literal.class.isAssignableFrom(node.getClass())) {
            iterator = graph.find((SubjectNode) node, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
            addTriplesToSet(set, iterator);
            iterator.close();
        }

        // add all triples that have node as object
        iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, (ObjectNode) node);
        addTriplesToSet(set, iterator);
        iterator.close();
    }

    /**
     * Add all triples in the iterator to the set
     *
     * @param set
     * @param iterator
     * @return
     */
    private void addTriplesToSet(Set<Triple> set, ClosableIterator<Triple> iterator) {
        while (iterator.hasNext()) {
            final Triple triple = iterator.next();
            set.add(triple);
        }
    }

    /**
     * Given a node, copies all the triples that include.
     * (1) triples that contain this node
     * (2) triples in (1) & (2) that contain blank nodes (recursively)
     *
     * @param node
     * @param sg
     * @param tg
     * @return
     * @throws GraphException
     */
    public Graph copyTriplesForNode(Node node, Graph sg, Graph tg) throws GraphException {
        sourceGraph = sg;
        targetGraph = tg;
        reset();

        Iterator<Triple> triples;
        try {
            Set<Triple> set = getAllTriplesForNode(node, sourceGraph);
            triples = set.iterator();
            readSourceGraph(triples);
            triples = set.iterator();
            createNewTriples(triples);
        } catch (Exception e) {
            throw new GraphException("Cannot copy RDF graph with node", e);
        }
        return targetGraph;
    }

    private void readSourceGraph(Iterator<Triple> triples) throws GraphException {
        try {
            Triple triple;
            while (triples.hasNext()) {
                triple = triples.next();
                if (triple.isGrounded()) {
                    helper.addTripleToGraph(triple);
                    targetGraph = helper.getGraph();
                } else {
                    updateBlankNodes(triple);
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

    private void updateBlankNodes(Triple triple) throws GraphElementFactoryException {
        final SubjectNode bsNode = triple.getSubject();
        if (CopyGraphHelper.isBlankNode(bsNode)) {
            int bnHash = bsNode.hashCode();
            newBNodeMap.put(bnHash, elemFactory.createBlankNode());
        }
        final ObjectNode boNode = triple.getObject();
        if (CopyGraphHelper.isBlankNode(boNode)) {
            int bnHash = boNode.hashCode();
            newBNodeMap.put(bnHash, elemFactory.createBlankNode());
        }
    }

    private Graph createNewTriples(Iterator<Triple> it) throws GraphException, GraphElementFactoryException {
        Triple newTriple;
        while (it.hasNext()) {
            Triple triple = it.next();
            if (!triple.isGrounded()) {
                newTriple = createNewTriple(triple);
                targetGraph.add(newTriple);
            }
        }
        return targetGraph;
    }

    private Triple createNewTriple(Triple triple) throws GraphElementFactoryException {
        SubjectNode newsn;
        PredicateNode newpn;
        ObjectNode newon;
        Triple newTriple;
        SubjectNode sn = triple.getSubject();
        PredicateNode pn = triple.getPredicate();
        ObjectNode on = triple.getObject();

        newsn = (SubjectNode) createNewNode(sn);
        newpn = elemFactory.createURIReference(((URIReference) pn).getURI());
        newon = (ObjectNode) createNewNode(on);

        newTriple = tripleFactory.createTriple(newsn, newpn, newon);
        return newTriple;
    }

    private Node createNewNode(Node node) throws GraphElementFactoryException {
        Node newNode;
        if (CopyGraphHelper.isBlankNode(node)) {
            newNode = newBNodeMap.get(new Integer(node.hashCode()));
        } else {
            newNode = helper.createLiteralOrURI(node);
            targetGraph = helper.getGraph();
        }
        return newNode;
    }

    /*private void addTripleToGraph(Triple triple) throws GraphElementFactoryException, GraphException {
        SubjectNode sN = elemFactory.createURIReference(((URIReference) triple.getSubject()).getURI());
        PredicateNode pN = elemFactory.createURIReference(((URIReference) triple.getPredicate()).getURI());

        ObjectNode oON = triple.getObject();
        ObjectNode oN = createLiteralOrURI(oON);
        targetGraph.add(tripleFactory.createTriple(sN, pN, oN));
    }*/

    /*private ObjectNode createLiteralOrURI(Node oON) throws GraphElementFactoryException {
        ObjectNode oN;

        if (Literal.class.isAssignableFrom(oON.getClass())) {
            Literal lit = (Literal) oON;
            if (lit.isDatatypedLiteral()) {
                oN = elemFactory.createLiteral(lit.getValue().toString(), lit.getDatatypeURI());
            } else {
                oN = elemFactory.createLiteral(lit.getValue().toString());
            }
        } else {
            oN = elemFactory.createURIReference(((URIReference) oON).getURI());
        }
        return oN;
    }*/
}