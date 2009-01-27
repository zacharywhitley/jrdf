/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.graph.local.index.operation.mem;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expectLastCall;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.operation.Comparison;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkClassFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterface;
import org.jrdf.util.test.MockFactory;
import static org.jrdf.util.test.TripleTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_LITERAL;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_1;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_2;
import static org.jrdf.util.test.TripleTestUtil.createTripleAllSame;
import static org.jrdf.util.test.TripleTestUtil.createTripleIterable;

import java.lang.reflect.Modifier;

// TODO (AN) Add tests for isGrounded - return false when there are blank nodes.
// TODO (AN) Add tests for areIsomorphic.

/**
 * Tests {@link org.jrdf.graph.local.index.operation.mem.ComparisonImpl}.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class ComparisonImplUnitTest extends TestCase {
    private static final boolean GRAPH_EMPTY = true;
    private static final boolean GRAPH_CONTAINS_NODES = false;
    private static final boolean ARE_UNEQUAL = false;
    private static final boolean ARE_EQUAL = true;
    private static final Triple TRIPLE_1 = createTripleAllSame(URI_BOOK_1);
    private static final Triple TRIPLE_2 = createTripleAllSame(URI_BOOK_2);
    private static final Triple TRIPLE_3 = TRIPLE_BOOK_1_DC_SUBJECT_LITERAL;
    private static final Triple[] TRIPLES_1 = {TRIPLE_1, TRIPLE_2};
    private static final Triple[] TRIPLES_2 = {TRIPLE_1, TRIPLE_3};
    private Comparison comparison;
    private Graph mockGraph1;
    private Graph mockGraph2;
    private MockFactory mockFactory;

    public void setUp() throws Exception {
        comparison = new ComparisonImpl();
        mockFactory = new MockFactory();
        mockGraph1 = mockFactory.createMock(Graph.class);
        mockGraph2 = mockFactory.createMock(Graph.class);
    }

    public void testClassProperties() {
        checkClassFinal(ComparisonImpl.class);
        checkImplementationOfInterface(Comparison.class, ComparisonImpl.class);
        checkConstructor(ComparisonImpl.class, Modifier.PUBLIC, NO_ARG_CONSTRUCTOR);
    }

    public void testIsGroundedEmptyGraph() throws Exception {
        mockGraph1.isEmpty();
        expectLastCall().andReturn(GRAPH_EMPTY);
        mockFactory.replay();
        assertTrue(comparison.isGrounded(mockGraph1));
        mockFactory.verify();
    }


    public void testEmptyGraphEquality() throws Exception {
        checkEmptyGroundedGraphs(GRAPH_EMPTY, GRAPH_EMPTY, ARE_EQUAL);
        checkEmptyGroundedGraphs(GRAPH_CONTAINS_NODES, GRAPH_EMPTY, ARE_UNEQUAL);
        checkEmptyGroundedGraphs(GRAPH_EMPTY, GRAPH_CONTAINS_NODES, ARE_UNEQUAL);
    }

    public void testDifferentSizedGraphsAreNotIsomorphic() throws Exception {
        checkDifferentSizeGraphsAreNotIsomorphic(1L, 123L, ARE_UNEQUAL);
        checkDifferentSizeGraphsAreNotIsomorphic(21L, 1L, ARE_UNEQUAL);
    }

    public void testGraphContent() throws Exception {
        checkGraphContent(TRIPLES_1, TRIPLES_2, ARE_UNEQUAL);
        checkGraphContent(TRIPLES_2, TRIPLES_1, ARE_UNEQUAL);
        checkGraphContent(TRIPLES_1, TRIPLES_1, ARE_EQUAL);
        checkGraphContent(TRIPLES_2, TRIPLES_2, ARE_EQUAL);
    }

    private void checkEmptyGroundedGraphs(boolean graph1Empty, boolean graph2Empty, boolean areEqual) throws Exception {
        mockFactory.reset();
        setUpEmptyCalls(graph1Empty, graph2Empty);
        replayAssertAndVerify("Graph 1 empty: " + graph1Empty + " Graph 2 empty: " + graph2Empty, areEqual, mockGraph1,
            mockGraph2);
    }

    private void checkDifferentSizeGraphsAreNotIsomorphic(long graph1Size, long graph2Size, boolean areEqual)
        throws Exception {
        mockFactory.reset();
        setUpEmptyCalls(GRAPH_CONTAINS_NODES, GRAPH_CONTAINS_NODES);
        setUpNumberOfTripleCalls(graph1Size, graph2Size);
        replayAssertAndVerify("Graph 1 size: " + graph1Size + " Graph 2 size: " + graph2Size, areEqual, mockGraph1,
            mockGraph2);
    }

    private void checkGraphContent(Triple[] triples1, Triple[] triples2, boolean areEqual) throws Exception {
        mockFactory.reset();
        setUpEmptyCalls(GRAPH_CONTAINS_NODES, GRAPH_CONTAINS_NODES);
        setUpNumberOfTripleCalls(triples1.length, triples2.length);
        setUpFindAndIteratorCalls(triples1, triples2);
        replayAssertAndVerify("Graph 1 size: " + triples1.length + " Graph 2 size: " + triples2.length, areEqual,
            mockGraph1, mockGraph2);
    }

    private void setUpEmptyCalls(boolean graph1Empty, boolean graph2Empty) throws Exception {
        mockGraph1.isEmpty();
        expectLastCall().andReturn(graph1Empty);
        mockGraph2.isEmpty();
        expectLastCall().andReturn(graph2Empty);
    }

    private void setUpNumberOfTripleCalls(long graph1Size, long graphSize2) throws Exception {
        mockGraph1.getNumberOfTriples();
        expectLastCall().andReturn(graph1Size);
        mockGraph2.getNumberOfTriples();
        expectLastCall().andReturn(graphSize2);
    }

    private void setUpFindAndIteratorCalls(Triple[] triples1, Triple[] triples2) throws GraphException {
        mockGraph1.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        expectLastCall().andReturn(createTripleIterable(triples1));
        ClosableIterator<Triple> iterator1 = createTripleIterable(triples1).iterator();
        ClosableIterator<Triple> iterator2 = createTripleIterable(triples2).iterator();
        while (iterator1.hasNext()) {
            Triple triple1 = iterator1.next();
            Triple triple2 = iterator2.next();
            mockGraph2.contains(triple1);
            boolean b = triple1.equals(triple2);
            expectLastCall().andReturn(b);
            if (!b) {
                break;
            }
        }
    }

    private void replayAssertAndVerify(String message, boolean areEqual, Graph mockGraph1, Graph mockGraph2)
        throws GraphException {
        mockFactory.replay();
        assertEquals(message, areEqual, comparison.groundedGraphsAreEqual(mockGraph1, mockGraph2));
        mockFactory.verify();
    }
}
