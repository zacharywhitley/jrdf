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

package org.jrdf.graph.local.iterator;

import junit.framework.TestCase;
import org.jrdf.collection.IteratorTrackingCollectionFactory;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.graph.local.index.nodepool.Localizer;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.MockTestUtil;
import org.jrdf.util.test.URIReference1;

import java.lang.reflect.Modifier;
import java.net.URI;
import static java.util.Arrays.asList;
import java.util.List;

public class OrderedIteratorFactoryImplUnitTest extends TestCase {
    private static final Class<?>[] PARAM_TYPES = {Localizer.class, GraphHandler[].class,
        IteratorTrackingCollectionFactory.class};
    //private static final String[] PARAMETER_NAMES = new String[]{"newLocalizer", "newGraphHandlers",
    //        "newCollectionFactory"};
    private static final PredicateNode IMRAN = new URIReference1(URI.create("urn:imran"));
    private static final PredicateNode FOO = new URIReference1(URI.create("urn:foo"));
    private static final PredicateNode BAR = new URIReference1(URI.create("urn:bar"));
    private static final PredicateNode BAZ = new URIReference1(URI.create("urn:baz"));
    private static final List<PredicateNode> ORDER_VALUES = asList(BAR, BAZ, FOO, IMRAN);
    private static final Long RESOURCE_ID = System.currentTimeMillis();
    private Localizer localizer;
    private GraphHandler[] graphHandlers;
    private IteratorTrackingCollectionFactory collectionFactory;

    public void setUp() {
        localizer = MockTestUtil.createMock(Localizer.class);
        GraphHandler graphHandler012 = MockTestUtil.createMock(GraphHandler.class);
        GraphHandler graphHandler120 = MockTestUtil.createMock(GraphHandler.class);
        GraphHandler graphHandler201 = MockTestUtil.createMock(GraphHandler.class);
        graphHandlers = new GraphHandler[] {graphHandler012, graphHandler120, graphHandler201};
        collectionFactory = MockTestUtil.createMock(IteratorTrackingCollectionFactory.class);
    }

    public void testClassProperties() throws Exception {
        checkImplementationOfInterfaceAndFinal(IteratorFactory.class, OrderedIteratorFactoryImpl.class);
        checkConstructor(OrderedIteratorFactoryImpl.class, Modifier.PUBLIC, PARAM_TYPES);
        //checkConstructNullAssertion(OrderedIteratorFactoryImpl.class, PARAM_TYPES);
        //checkConstructorSetsFieldsAndFieldsPrivateFinal(OrderedIteratorFactoryImpl.class, PARAM_TYPES,
        //    PARAMETER_NAMES);
    }

//    public void testSortedNewPredicateIterator() {
//        expect(iteratorFactory.newPredicateIterator()).andReturn(createPredicateIterator());
//        LocalizedNodeComparator localizedNodeComparator = new LocalizedNodeComparatorImpl();
//        BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localizedNodeComparator);
//        NodeComparator comparator = new NodeComparatorImpl(new NodeTypeComparatorImpl(), blankNodeComparator);
//        expect(collectionFactory.createSet(PredicateNode.class)).andReturn(new TreeSet<PredicateNode>(comparator));
//        IteratorFactory factory = createOrderedIteratorFactory();
//        mockFactory.replay();
//        ClosableIterator<PredicateNode> actualIterator = factory.newPredicateIterator();
//        checkValuesAreSorted(actualIterator, ORDER_VALUES);
//        mockFactory.verify();
//    }

//    public void testSortedNewPredicateIteratorWithResource() {
//        expect(iteratorFactory.newPredicateIterator(RESOURCE_ID)).andReturn(createPredicateIterator());
//        LocalizedNodeComparator localizedNodeComparator = new LocalizedNodeComparatorImpl();
//        BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localizedNodeComparator);
//        NodeComparator comparator = new NodeComparatorImpl(new NodeTypeComparatorImpl(), blankNodeComparator);
//        expect(collectionFactory.createSet(PredicateNode.class)).andReturn(new TreeSet<PredicateNode>(comparator));
//        IteratorFactory factory = createOrderedIteratorFactory();
//        mockFactory.replay();
//        ClosableIterator<PredicateNode> actualIterator = factory.newPredicateIterator(RESOURCE_ID);
//        checkValuesAreSorted(actualIterator, ORDER_VALUES);
//        mockFactory.verify();
//    }

//    private IteratorFactory createOrderedIteratorFactory() {
//        return new OrderedIteratorFactoryImpl(localizer, graphHandlers, collectionFactory);
//    }
//
//    private PredicateClosableIterator createPredicateIterator() {
//        Iterator<PredicateNode> iterator = createTestNodes().iterator();
//        return new PredicateClosableIterator(iterator);
//    }
//
//    private Set<PredicateNode> createTestNodes() {
//        final Set<PredicateNode> nodes = new HashSet<PredicateNode>();
//        nodes.add(FOO);
//        nodes.add(BAR);
//        nodes.add(BAZ);
//        nodes.add(IMRAN);
//        return nodes;
//    }
//
//    private void checkValuesAreSorted(ClosableIterator<PredicateNode> actualIterator,
//        List<PredicateNode> expectedValues) {
//        int index = 0;
//        while (actualIterator.hasNext()) {
//            assertEquals(expectedValues.get(index), actualIterator.next());
//            index++;
//        }
//    }
}
