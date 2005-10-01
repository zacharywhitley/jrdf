/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.graph.mem.operation;

import junit.framework.TestCase;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.AnyPredicateNode;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.operation.Comparison;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.TripleTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;
import org.jrdf.util.ClosableIterator;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.expectLastCall;

import java.lang.reflect.Modifier;

/**
 * Tests {@see org.jrdf.graph.mem.operation.ComparisonImpl}.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class ComparisonImplUnitTest extends TestCase {
    private static final boolean GRAPH_EMPTY = true;
    private static final boolean GRAPH_CONTAINS_NODES = false;
    private static final boolean ARE_UNEQUAL = false;
    private static final boolean ARE_EQUAL = true;

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkClassFinal(ComparisonImpl.class);
        ClassPropertiesTestUtil.checkImplementationOfInterface(Comparison.class, ComparisonImpl.class);
        ClassPropertiesTestUtil.checkConstructor(ComparisonImpl.class, Modifier.PUBLIC, NO_ARG_CONSTRUCTOR);
    }

    public void testIsGroundedEmptyGraph() throws Exception {
        Graph mockGraph = createMock(Graph.class);
        Comparison comparison = new ComparisonImpl();
        mockGraph.isEmpty();
        expectLastCall().andReturn(GRAPH_EMPTY);
        replay(mockGraph);
        assertTrue(comparison.isGrounded(mockGraph));
        verify(mockGraph);
    }

    public void testEmptyGraphEquality() throws Exception {
        checkEmptyGroundedGraphs(GRAPH_EMPTY, GRAPH_EMPTY, ARE_EQUAL);
        checkEmptyGroundedGraphs(GRAPH_CONTAINS_NODES, GRAPH_EMPTY, ARE_UNEQUAL);
        checkEmptyGroundedGraphs(GRAPH_EMPTY, GRAPH_CONTAINS_NODES, ARE_UNEQUAL);
    }

    public void testSameSizedGraphsAreIsomorphic() throws Exception {
        checkDifferentSizeGraphsAreIsomorphic(1L, 123L, ARE_UNEQUAL);
        checkDifferentSizeGraphsAreIsomorphic(12L, 1L, ARE_UNEQUAL);
    }

    public void testGraphContent() throws Exception {
        Triple tripleAllSame1 = TripleTestUtil.createTripleAllSame(TripleTestUtil.URI_BOOK_1);
        Triple tripleAllSame2 = TripleTestUtil.createTripleAllSame(TripleTestUtil.URI_BOOK_2);
        Triple tripleAllSame3 = TripleTestUtil.createTripleAllSame(TripleTestUtil.URI_BOOK_3);
        Triple[] triples1 = new Triple[]{tripleAllSame1, tripleAllSame2};
        Triple[] triples2 = new Triple[]{tripleAllSame2, tripleAllSame3};
        checkGraphContent(triples1, triples2, ARE_UNEQUAL);
        checkGraphContent(triples1, triples1, ARE_EQUAL);
        checkGraphContent(triples2, triples2, ARE_EQUAL);
    }

    private void checkGraphContent(Triple[] triples1, Triple[] triples2, boolean areEqual) throws GraphException {
        Graph mockGraph1 = createMock(Graph.class);
        Graph mockGraph2 = createMock(Graph.class);
        Comparison comparison = new ComparisonImpl();
        setUpEmptyCalls(mockGraph1, GRAPH_CONTAINS_NODES, mockGraph2,  GRAPH_CONTAINS_NODES);
        setUpNumberOfTripleCalls(mockGraph1, triples1.length, mockGraph2, triples2.length);
        setUpIteratorCalls(mockGraph1, mockGraph2, triples1, triples2);
        replay(mockGraph1);
        replay(mockGraph2);
        assertEquals("Graph 1 size: " + triples1.length + " Graph 2 size: " + triples2.length, areEqual,
                comparison.groundedGraphsAreIsomorphic(mockGraph1,  mockGraph2));
        verify(mockGraph1);
        verify(mockGraph2);
    }

    private void checkDifferentSizeGraphsAreIsomorphic(long graph1Size, long graph2Size, boolean areEqual) throws GraphException {
        Graph mockGraph1 = createMock(Graph.class);
        Graph mockGraph2 = createMock(Graph.class);
        Comparison comparison = new ComparisonImpl();
        setUpEmptyCalls(mockGraph1, GRAPH_CONTAINS_NODES, mockGraph2, GRAPH_CONTAINS_NODES);
        setUpNumberOfTripleCalls(mockGraph1, graph1Size, mockGraph2, graph2Size);
        replay(mockGraph1);
        replay(mockGraph2);
        assertEquals("Graph 1 size: " + graph1Size + " Graph 2 size: " + graph2Size, areEqual,
                comparison.groundedGraphsAreIsomorphic(mockGraph1,  mockGraph2));
        verify(mockGraph1);
        verify(mockGraph2);
    }

    private void checkEmptyGroundedGraphs(boolean graph1Empty, boolean graph2Empty, boolean areEqual) throws GraphException {
        Graph mockGraph1 = createMock(Graph.class);
        Graph mockGraph2 = createMock(Graph.class);
        Comparison comparison = new ComparisonImpl();
        setUpEmptyCalls(mockGraph1, graph1Empty, mockGraph2, graph2Empty);
        replay(mockGraph1);
        replay(mockGraph2);
        assertEquals("Graph 1 empty: " + graph1Empty + " Graph 2 empty: " + graph2Empty, areEqual,
                comparison.groundedGraphsAreIsomorphic(mockGraph1,  mockGraph2));
        verify(mockGraph1);
        verify(mockGraph2);
    }

    private void setUpEmptyCalls(Graph mockGraph1, boolean graph1Empty, Graph mockGraph2, boolean graph2Empty) throws GraphException {
        mockGraph1.isEmpty();
        expectLastCall().andReturn(graph1Empty);
        mockGraph2.isEmpty();
        expectLastCall().andReturn(graph2Empty);
    }

    private void setUpNumberOfTripleCalls(Graph mockGraph1, long graph1Size, Graph mockGraph2, long graphSize2) throws GraphException {
        mockGraph1.getNumberOfTriples();
        expectLastCall().andReturn(graph1Size);
        mockGraph2.getNumberOfTriples();
        expectLastCall().andReturn(graphSize2);
    }

    private void setUpIteratorCalls(Graph mockGraph1, Graph mockGraph2, Triple[] triples1, Triple[] triples2) throws GraphException {
        mockGraph1.find(AnySubjectNode.ANY_SUBJECT_NODE, AnyPredicateNode.ANY_PREDICATE_NODE, AnyObjectNode.ANY_OBJECT_NODE);
        expectLastCall().andReturn(TripleTestUtil.createTripleIterator(triples1));
        ClosableIterator<Triple> iterator1 = TripleTestUtil.createTripleIterator(triples1);
        ClosableIterator<Triple> iterator2 = TripleTestUtil.createTripleIterator(triples2);
        while (iterator1.hasNext()) {
            Triple triple1 = iterator1.next();
            Triple triple2 = iterator2.next();
            mockGraph2.contains(triple1);
            boolean b = triple1.equals(triple2);
            expectLastCall().andReturn(b);
            if (!b) break;
        }
    }
}
