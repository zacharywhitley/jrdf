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

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.SortedDiskJRDFFactory;
import org.jrdf.collection.BdbCollectionFactory;
import org.jrdf.collection.BdbMapFactory;
import org.jrdf.collection.CollectionFactory;
import org.jrdf.collection.MapFactory;
import org.jrdf.graph.AbstractBlankNode;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CopyGraphUtilImplUnitTest extends TestCase {
    private static final TempDirectoryHandler DIR_HANDLER = new TempDirectoryHandler();
    private JRDFFactory factory;
    private BdbEnvironmentHandler handler1;
    private BdbEnvironmentHandler handler2;
    private MapFactory mapFactory;
    private CollectionFactory setFactory;
    private CopyGraphUtil cgUtil;

    private String url1 = "http://www.example.com/1#";
    private String url2 = "http://www.example.com/2#";
    private Graph graph1;
    private Graph graph2;
    private Triple triple1;
    private Triple triple2;
    private Triple triple3;
    private Triple triple4;
    private Triple triple5;
    private PredicateNode pNode1;
    private PredicateNode pNode2;
    private PredicateNode pNode3;
    private Node node1;
    private Node node2;
    private Node node3;
    private Node node4;
    private Node node5;
    private Node node6;
    private BlankNode bNode1;
    private BlankNode bNode2;
    private BlankNode bNode3;
    private BlankNode bNode4;
    private TripleFactory tFac1;
    private GraphElementFactory eFac1;
    private GraphElementFactory eFac2;

    private Graph newGraph() {
        return factory.getGraph();
    }

    public void setUp() throws Exception {
        //DIR_HANDLER.makeDir();
        factory = SortedDiskJRDFFactory.getFactory();

        graph1 = newGraph();
        graph2 = newGraph();

        eFac1 = graph1.getElementFactory();
        eFac2 = graph2.getElementFactory();

        tFac1 = graph1.getTripleFactory();

        node1 = eFac1.createURIReference(URI.create(url1 + "node1"));
        pNode1 = eFac1.createURIReference(URI.create(url1 + "p1"));
        pNode2 = eFac1.createURIReference(URI.create(url1 + "p2"));
        pNode3 = eFac1.createURIReference(URI.create(url1 + "p3"));
        node2 = eFac1.createURIReference(URI.create(url1 + "node2"));
        node3 = eFac1.createURIReference(URI.create(url1 + "node3"));
        node4 = eFac1.createURIReference(URI.create(url1 + "node4"));
        node5 = eFac1.createURIReference(URI.create(url1 + "node5"));
        node6 = eFac1.createURIReference(URI.create(url1 + "node6"));

        bNode1 = eFac1.createBlankNode();
        bNode2 = eFac1.createBlankNode();
        bNode3 = eFac1.createBlankNode();
        bNode4 = eFac1.createBlankNode();

        handler1 = new BdbEnvironmentHandlerImpl(DIR_HANDLER);
        handler2 = new BdbEnvironmentHandlerImpl(DIR_HANDLER);
        mapFactory = new BdbMapFactory(handler1, "testDb");
        setFactory = new BdbCollectionFactory(handler2, "foo");
        cgUtil = new CopyGraphUtilImpl(mapFactory, setFactory);
    }

    public void tearDown() {
        cgUtil.close();
        mapFactory.close();
        setFactory.close();
        factory.close();
        DIR_HANDLER.removeDir();
    }

    public void testInitialInsert() throws GraphException {
        triple1 = tFac1.createTriple((SubjectNode) node1, pNode1, (ObjectNode) node2);
        graph1.add(triple1);

        graph2 = cgUtil.copyGraph(graph1, graph2);

        ClosableIterator<Triple> it = graph2.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        if (it.hasNext()) {
            Triple triple = it.next();
            Node n1 = triple.getSubject();
            Node n2 = triple.getObject();
            PredicateNode pn = triple.getPredicate();
            assertTrue("Subjects match", node1.equals(n1));
            assertTrue("Objects match", node2.equals(n2));
            assertTrue("Predicates match", pNode1.equals(pn));
        }
    }

    public void testAddOneBlankNodeAsObject() throws GraphException {
        triple1 = tFac1.createTriple((SubjectNode) node1, pNode1, bNode2);
        graph1.add(triple1);

        graph2 = cgUtil.copyGraph(graph1, graph2);
        ClosableIterator<Triple> it = graph2.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        if (it.hasNext()) {
            Triple triple = it.next();
            Node n1 = triple.getSubject();
            Node n2 = triple.getObject();
            PredicateNode pn = triple.getPredicate();
            assertTrue("Subjects match", node1.equals(n1));
            assertTrue("Predicates match", pNode1.equals(pn));
            assertTrue("Object is bn", BlankNode.class.isAssignableFrom(n2.getClass()));
        }
    }

    public void testAddOneBlankNodeAsSubject() throws GraphException {
        triple1 = tFac1.createTriple(bNode1, pNode1, (ObjectNode) node2);
        graph1.add(triple1);

        cgUtil.copyGraph(graph1, graph2);
        graph2 = cgUtil.getGraph();
        ClosableIterator<Triple> it = graph2.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        if (it.hasNext()) {
            Triple triple = it.next();
            Node n1 = triple.getSubject();
            Node n2 = triple.getObject();
            PredicateNode pn = triple.getPredicate();
            assertTrue("Subject is bn", BlankNode.class.isAssignableFrom(n1.getClass()));
            assertTrue("Predicates match", pNode1.equals(pn));
            assertTrue("Objects match", node2.equals(n2));
        }
    }

    public void testLiteralCopy() throws GraphException {
        String value = "whatever";
        URI typeURI = URI.create("xsd:string");
        Literal literal = eFac1.createLiteral(value, typeURI);
        triple1 = tFac1.createTriple((SubjectNode) node1, pNode1, literal);
        graph1.add(triple1);

        graph2 = cgUtil.copyGraph(graph1, graph2);
        ClosableIterator<Triple> triples = graph2.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).
            iterator();
        Triple trip = triples.next();
        assertTrue("Value ok", "whatever".equals(((Literal) trip.getObject()).getValue()));
        assertTrue("Type ok", "xsd:string".equals(((Literal) trip.getObject()).getDatatypeURI().toString()));
    }

    public void testBNodeReference() throws GraphException {
        triple1 = tFac1.createTriple((SubjectNode) node1, pNode1, bNode1);
        triple2 = tFac1.createTriple(bNode1, pNode2, bNode2);
        graph1.add(triple1);
        graph1.add(triple2);

        Map<Integer, HashSet<Triple>> bNodeMap = new HashMap<Integer, HashSet<Triple>>();
        int bNode1Hash = bNode1.hashCode();
        int bNode2Hash = bNode2.hashCode();
        HashSet<Triple> set1 = new HashSet<Triple>();
        set1.add(triple1);
        set1.add(triple2);
        bNodeMap.put(bNode1Hash, set1);

        HashSet<Triple> set2 = new HashSet<Triple>();
        set2.add(triple2);
        bNodeMap.put(bNode2Hash, set2);

        bNode3 = eFac2.createBlankNode();
        Set<Triple> set = bNodeMap.get(bNode1Hash);
        Iterator<Triple> it = set.iterator();
        while (it.hasNext()) {
            Triple tmpT = it.next();
            SubjectNode tmpSN = tmpT.getSubject();
            // TODO YF Fix this so that it actually tests something.
//            if (AbstractBlankNode.isBlankNode(tmpSN)) {
//                if (tmpSN.hashCode() == bNode1Hash) {
//
//                }
//            }
        }
    }

    public void testNestedBlankNodes() throws GraphException {
        triple1 = tFac1.createTriple((SubjectNode) node1, pNode1, bNode1);
        triple2 = tFac1.createTriple(bNode1, pNode2, (ObjectNode) node2);

        graph1.add(triple1);
        graph1.add(triple2);
        graph2 = cgUtil.copyGraph(graph1, graph2);
        ClosableIterator<Triple> it = graph2.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        Triple t1 = it.next();
        Triple t2 = it.next();
        it.close();

        if (pNode1.hashCode() == t1.getPredicate().hashCode()) {
            Node tmpNode1 = t1.getObject();
            Node tmpNode2 = t2.getSubject();
            assertTrue("BNode", AbstractBlankNode.isBlankNode(tmpNode1));
            assertTrue("BNode", AbstractBlankNode.isBlankNode(tmpNode2));
            assertTrue("Hash values equal", tmpNode1.hashCode() == tmpNode2.hashCode());
            assertTrue("Hahs values different", tmpNode2.hashCode() != t2.getObject().hashCode());
        } else if (pNode2.hashCode() == t1.getPredicate().hashCode()) {
            Node tmpNode1 = t1.getSubject();
            Node tmpNode2 = t2.getObject();
            assertTrue("BNode", AbstractBlankNode.isBlankNode(tmpNode1));
            assertTrue("BNode", AbstractBlankNode.isBlankNode(tmpNode2));
            assertTrue("Hash values equal", tmpNode1.hashCode() == tmpNode2.hashCode());
        }
    }

    public void testCopyFromNode() throws GraphException {
        triple1 = tFac1.createTriple(bNode1, pNode1, bNode1);
        triple2 = tFac1.createTriple(bNode1, pNode2, bNode2);
        triple3 = tFac1.createTriple(bNode2, pNode3, bNode3);
        triple4 = tFac1.createTriple((SubjectNode) node4, pNode1, (ObjectNode) node1);

        graph1.add(triple1);
        graph1.add(triple2);
        graph1.add(triple3);
        graph1.add(triple4);

        cgUtil.copyTriplesForNode(graph1, graph2, node1, null);
        cgUtil.replaceNode(graph2, node1, null);
        graph2 = cgUtil.getGraph();
        assertEquals("graph2 size", 1, graph2.getNumberOfTriples());
        graph2.clear();
        cgUtil.copyTriplesForNode(graph1, graph2, bNode1, null);
        cgUtil.replaceNode(graph2, node1, null);
        graph2 = cgUtil.getGraph();
        assertEquals("graph2 size", 3, graph2.getNumberOfTriples());
    }

    public void testCircularBlankNodes() throws GraphException {
        triple1 = tFac1.createTriple(bNode1, pNode1, bNode2);
        triple2 = tFac1.createTriple(bNode2, pNode2, bNode3);
        triple3 = tFac1.createTriple(bNode3, pNode3, bNode1);

        graph1.add(triple1);
        graph1.add(triple2);
        graph1.add(triple3);

        PredicateNode np1Node = eFac2.createURIReference(URI.create(url1 + "p1"));
        PredicateNode np2Node = eFac2.createURIReference(URI.create(url1 + "p2"));
        PredicateNode np3Node = eFac2.createURIReference(URI.create(url1 + "p3"));

        graph2 = cgUtil.copyGraph(graph1, graph2);
        ClosableIterator<Triple> trps1 = graph2.find(ANY_SUBJECT_NODE, np1Node, ANY_OBJECT_NODE).iterator();
        Triple trp1 = null;
        while (trps1.hasNext()) {
            trp1 = trps1.next();
            assertTrue("not empty", trp1 != null);
        }

        ClosableIterator<Triple> trps2 = graph2.find(ANY_SUBJECT_NODE, np2Node, ANY_OBJECT_NODE).iterator();
        Triple trp2 = null;
        while (trps2.hasNext()) {
            trp2 = trps2.next();
            assertTrue("Not empty", trp2 != null);
        }
        ClosableIterator<Triple> trps3 = graph2.find(ANY_SUBJECT_NODE, np3Node, ANY_OBJECT_NODE).iterator();
        Triple trp3 = null;
        while (trps3.hasNext()) {
            trp3 = trps3.next();
            assertTrue("Not empty", trp3 != null);
        }
        assertEquals("bNode2 the same", trp1.getObject(), trp2.getSubject());
        assertTrue("1 and 2 diff", !trp1.getSubject().equals(trp2.getSubject()));
        assertEquals("bNode3 the same", trp2.getObject(), trp3.getSubject());
        assertTrue("2 and 3 diff", !trp2.getSubject().equals(trp3.getSubject()));
        assertEquals("bNode1 the same", trp1.getSubject(), trp3.getObject());
    }

    public void testCopyGraphForSubject() throws GraphException {
        triple1 = tFac1.createTriple(bNode1, pNode1, bNode1);
        triple2 = tFac1.createTriple((SubjectNode) node2, pNode2, bNode3);
        triple3 = tFac1.createTriple(bNode1, pNode1, bNode3);
        triple4 = tFac1.createTriple(bNode4, pNode3, (ObjectNode) node4);
        triple5 = tFac1.createTriple((SubjectNode) node5, pNode3, bNode4);
        graph1.add(triple1);
        graph1.add(triple2);
        graph1.add(triple3);
        graph1.add(triple4);
        graph1.add(triple5);

        assertEquals("graph1 size", 5, graph1.getNumberOfTriples());
        node5 = eFac2.createURIReference(URI.create(url2 + "node5"));
        cgUtil.copyTriplesForSubjectNode(graph1, graph2, (SubjectNode) node2, (SubjectNode) node5);
        cgUtil.replaceNode(graph2, node2, node5);
        assertEquals("Graph2 size should be 3", 3, graph2.getNumberOfTriples());
        ClosableIterator<Triple> iterator = graph2.find(eFac2.createURIReference(URI.create(node2.toString())),
            ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        assertTrue("Graph2 doesn't contain node2", !iterator.hasNext());
        iterator.close();
    }

    public void testCopyGraphForObject() throws GraphException {
        triple1 = tFac1.createTriple(bNode1, pNode1, bNode1);
        triple2 = tFac1.createTriple((SubjectNode) node2, pNode2, bNode3);
        triple3 = tFac1.createTriple(bNode1, pNode1, bNode3);
        triple4 = tFac1.createTriple(bNode4, pNode3, (ObjectNode) node4);
        triple5 = tFac1.createTriple((SubjectNode) node5, pNode3, bNode4);

        graph1.add(triple1);
        graph1.add(triple2);
        graph1.add(triple3);
        graph1.add(triple4);
        graph1.add(triple5);

        cgUtil.copyTriplesForObjectNode(graph1, graph2, bNode4, null);
        cgUtil.replaceNode(graph2, bNode4, null);
        assertEquals("Graph2 size should be 3", 2, graph2.getNumberOfTriples());
    }

    public void testDuplicates() throws GraphException {
        triple1 = tFac1.createTriple(bNode1, pNode1, (ObjectNode) node1);
        triple2 = tFac1.createTriple(bNode1, pNode1, (ObjectNode) node2);
        triple3 = tFac1.createTriple((SubjectNode) node1, pNode2, (ObjectNode) node3);
        triple4 = tFac1.createTriple((SubjectNode) node2, pNode2, (ObjectNode) node4);

        graph1.add(triple1);
        graph1.add(triple2);
        graph1.add(triple3);
        graph1.add(triple4);

        node5 = eFac2.createURIReference(URI.create(url2 + "node5"));
        node6 = eFac2.createURIReference(URI.create(url2 + "node6"));

        cgUtil.copyTriplesForNode(graph1, graph2, node1, node5);
        assertEquals("Graph size is 3", 3, graph2.getNumberOfTriples());

        cgUtil.copyTriplesForNode(graph1, graph2, node2, node6);
        assertEquals("Graph size is 4", 4, graph2.getNumberOfTriples());
    }

    public void testCopyNonExistentNode() throws GraphException {
        triple1 = tFac1.createTriple(bNode1, pNode1, (ObjectNode) node1);
        graph1.add(triple1);
        node5 = eFac2.createURIReference(URI.create(node3.toString()));

        cgUtil.copyTriplesForNode(graph1, graph2, node2, node5);
        assertEquals("Graph size is 0", 0, graph2.getNumberOfTriples());

        cgUtil.copyTriplesForNode(graph1, graph2, node1, null);
        assertEquals("Graph size is 1", 1, graph2.getNumberOfTriples());
    }

    public void testCopyURINode() throws GraphException {
        TripleUtil tUtil = new TripleUtilImpl(setFactory);

        triple1 = tFac1.createTriple((SubjectNode) node1, pNode1, (ObjectNode) node1);
        triple2 = tFac1.createTriple((SubjectNode) node2, pNode1, (ObjectNode) node1);
        triple3 = tFac1.createTriple((SubjectNode) node2, pNode1, (ObjectNode) node2);
        graph1.add(triple1, triple2, triple3);
        Set<Triple> triples = tUtil.getAllTriplesForTriple(triple1, graph1);
        assertEquals("Set size is 1", 1, triples.size());
    }
}
