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

package org.jrdf.graph.local.iterator;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.mem.BlankNodeComparator;
import org.jrdf.graph.local.mem.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.mem.LocalizedNodeComparator;
import org.jrdf.graph.local.mem.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.mem.NodeComparatorImpl;
import org.jrdf.graph.local.mem.iterator.ClosableMemIterator;
import org.jrdf.graph.local.mem.iterator.PredicateClosableIterator;
import org.jrdf.set.SortedSetFactory;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.NodeTypeComparatorImpl;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.URIReference1;
import static org.jrdf.util.test.ArgumentTestUtil.*;
import static org.jrdf.util.test.ClassPropertiesTestUtil.*;

import java.lang.reflect.Modifier;
import java.net.URI;
import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class OrderedIteratorFactoryImplUnitTest extends TestCase {
    private static final Class[] PARAM_TYPES = { IteratorFactory.class, NodePool.class, LongIndex.class,
            GraphHandler.class, SortedSetFactory.class};
    private static final String[] PARAMETER_NAMES = new String[] {"newIteratorFactory", "newNodePool",
            "newLongIndex", "newGraphHandler", "newSetFactory"};
    private static final PredicateNode IMRAN = new URIReference1(URI.create("urn:imran"));
    private static final PredicateNode FOO = new URIReference1(URI.create("urn:foo"));
    private static final PredicateNode BAR = new URIReference1(URI.create("urn:bar"));
    private static final PredicateNode BAZ = new URIReference1(URI.create("urn:baz"));
    private static final List<PredicateNode> ORDER_VALUES = asList(BAR, BAZ, FOO, IMRAN);
    private static final Long RESOURCE_ID = System.currentTimeMillis();
    private final MockFactory mockFactory = new MockFactory();
    private IteratorFactory iteratorFactory;
    private NodePool nodePool;
    private LongIndex longIndex;
    private GraphHandler graphHandler;
    private SortedSetFactory setFactory;

    public void setUp() {
        iteratorFactory = mockFactory.createMock(IteratorFactory.class);
        nodePool = mockFactory.createMock(NodePool.class);
        longIndex = mockFactory.createMock(LongIndex.class);
        graphHandler = mockFactory.createMock(GraphHandler.class);
        setFactory = mockFactory.createMock(SortedSetFactory.class);
    }

    public void testClassProperties() throws Exception {
        checkImplementationOfInterfaceAndFinal(IteratorFactory.class, OrderedIteratorFactoryImpl.class);
        checkConstructor(OrderedIteratorFactoryImpl.class, Modifier.PUBLIC, PARAM_TYPES);
        checkConstructNullAssertion(OrderedIteratorFactoryImpl.class, PARAM_TYPES);
        checkConstructorSetsFieldsAndFieldsPrivateFinal(OrderedIteratorFactoryImpl.class, PARAM_TYPES,
                PARAMETER_NAMES);
    }

    @SuppressWarnings({"unchecked"})
    public void testEmptyClosableIterator() {
        ClosableMemIterator<Triple> returnIterator = mockFactory.createMock(ClosableMemIterator.class);
        expect(iteratorFactory.newEmptyClosableIterator()).andReturn(returnIterator);
        mockFactory.replay();
        IteratorFactory orderedIteratorFactory = createOrderedIteratorFactory();
        ClosableIterator<Triple> actualIterator = orderedIteratorFactory.newEmptyClosableIterator();
        assertTrue(returnIterator == actualIterator);
        mockFactory.verify();
    }

    public void testSortedNewPredicateIterator() {
        expect(iteratorFactory.newPredicateIterator()).andReturn(createPredicateIterator());
        LocalizedNodeComparator localizedNodeComparator = new LocalizedNodeComparatorImpl();
        BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localizedNodeComparator);
        NodeComparator comparator = new NodeComparatorImpl(new NodeTypeComparatorImpl(), blankNodeComparator);
        expect(setFactory.createSet(PredicateNode.class)).andReturn(new TreeSet<PredicateNode>(comparator));
        IteratorFactory factory = createOrderedIteratorFactory();
        mockFactory.replay();
        ClosableIterator<PredicateNode> actualIterator = factory.newPredicateIterator();
        checkValuesAreSorted(actualIterator, ORDER_VALUES);
        mockFactory.verify();
    }

    public void testSortedNewPredicateIteratorWithResource() {
        expect(iteratorFactory.newPredicateIterator(RESOURCE_ID)).andReturn(createPredicateIterator());
        LocalizedNodeComparator localizedNodeComparator = new LocalizedNodeComparatorImpl();
        BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localizedNodeComparator);
        NodeComparator comparator = new NodeComparatorImpl(new NodeTypeComparatorImpl(), blankNodeComparator);
        expect(setFactory.createSet(PredicateNode.class)).andReturn(new TreeSet<PredicateNode>(comparator));
        IteratorFactory factory = createOrderedIteratorFactory();
        mockFactory.replay();
        ClosableIterator<PredicateNode> actualIterator = factory.newPredicateIterator(RESOURCE_ID);
        checkValuesAreSorted(actualIterator, ORDER_VALUES);
        mockFactory.verify();
    }

    private IteratorFactory createOrderedIteratorFactory() {
        return new OrderedIteratorFactoryImpl(iteratorFactory, nodePool, longIndex, graphHandler, setFactory);
    }

    private PredicateClosableIterator createPredicateIterator() {
        Iterator<PredicateNode> iterator = createTestNodes().iterator();
        return new PredicateClosableIterator(iterator);
    }

    private Set<PredicateNode> createTestNodes() {
        final Set<PredicateNode> nodes = new HashSet<PredicateNode>();
        nodes.add(FOO);
        nodes.add(BAR);
        nodes.add(BAZ);
        nodes.add(IMRAN);
        return nodes;
    }

    private void checkValuesAreSorted(ClosableIterator<PredicateNode> actualIterator,
            List<PredicateNode> expectedValues) {
        int index = 0;
        while (actualIterator.hasNext()) {
            assertEquals(expectedValues.get(index), actualIterator.next());
            index++;
        }
    }
}
