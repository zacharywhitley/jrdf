package org.jrdf.graph.mem.iterator;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.jrdf.graph.AbstractURIReference;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.index.graphhandler.GraphHandler;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.index.nodepool.NodePool;
import org.jrdf.graph.mem.NodeComparatorImpl;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.NodeTypeComparatorImpl;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.MockFactory;

import java.lang.reflect.Modifier;
import java.net.URI;
import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

public class OrderedIteratorFactoryImplUnitTest extends TestCase {
    private static final Class[] PARAM_TYPES = { IteratorFactory.class, NodePool.class, LongIndex.class,
            GraphHandler.class, NodeComparator.class};
    private static final PredicateNode IMRAN = new TestURIReference(URI.create("urn:imran"));
    private static final PredicateNode FOO = new TestURIReference(URI.create("urn:foo"));
    private static final PredicateNode BAR = new TestURIReference(URI.create("urn:bar"));
    private static final PredicateNode BAZ = new TestURIReference(URI.create("urn:baz"));
    private static final List<PredicateNode> ORDER_VALUES = asList(BAR, BAZ, FOO, IMRAN);
    private final MockFactory mockFactory = new MockFactory();
    private IteratorFactory iteratorFactory;
    private NodePool nodePool;
    private LongIndex longIndex;
    private GraphHandler graphHandler;
    private NodeComparator nodeComparator;

    public void setUp() {
        iteratorFactory = mockFactory.createMock(IteratorFactory.class);
        nodePool = mockFactory.createMock(NodePool.class);
        longIndex = mockFactory.createMock(LongIndex.class);
        graphHandler = mockFactory.createMock(GraphHandler.class);
        nodeComparator = mockFactory.createMock(NodeComparator.class);
    }

    public void testClassProperties() throws Exception {
        checkImplementationOfInterfaceAndFinal(IteratorFactory.class, OrderedIteratorFactoryImpl.class);
        checkConstructor(OrderedIteratorFactoryImpl.class, Modifier.PUBLIC, PARAM_TYPES);
        checkConstructNullAssertion(OrderedIteratorFactoryImpl.class, PARAM_TYPES);
    }

    public void testEmptyClosableIterator() {
        ClosableMemIterator<Triple> returnIterator = mockFactory.createMock(ClosableMemIterator.class);
        expect(iteratorFactory.newEmptyClosableIterator()).andReturn(returnIterator);
        mockFactory.replay();
        IteratorFactory orderedIteratorFactory = createOrderedIteratorFactory(nodeComparator);
        ClosableMemIterator<Triple> actualIterator = orderedIteratorFactory.newEmptyClosableIterator();
        assertTrue(returnIterator == actualIterator);
        mockFactory.verify();
    }

    public void testSortedNewPredicateIterator() {
        Iterator<PredicateNode> iterator = createTestNodes().iterator();
        PredicateClosableIterator predicateIterator = new PredicateClosableIterator(iterator);
        expect(iteratorFactory.newPredicateIterator()).andReturn(predicateIterator);
        NodeComparator comparator = new NodeComparatorImpl(new NodeTypeComparatorImpl());
        IteratorFactory factory = createOrderedIteratorFactory(comparator);
        mockFactory.replay();
        checkValuesAreSorted(factory, ORDER_VALUES);
        mockFactory.verify();
    }

    private IteratorFactory createOrderedIteratorFactory(NodeComparator comparator) {
        return new OrderedIteratorFactoryImpl(iteratorFactory, nodePool, longIndex, graphHandler,
                comparator);
    }

    private Set<PredicateNode> createTestNodes() {
        final Set<PredicateNode> nodes = new HashSet<PredicateNode>();
        nodes.add(FOO);
        nodes.add(BAR);
        nodes.add(BAZ);
        nodes.add(IMRAN);
        return nodes;
    }

    private void checkValuesAreSorted(IteratorFactory factory, List<PredicateNode> expectedValues) {
        ClosableIterator<PredicateNode> actualIterator = factory.newPredicateIterator();
        int index = 0;
        while (actualIterator.hasNext()) {
            assertEquals(expectedValues.get(index), actualIterator.next());
            index++;
        }
    }

    private static class TestURIReference extends AbstractURIReference {
        public TestURIReference(URI newUri) throws IllegalArgumentException {
            super(newUri);
        }

        public ClosableIterator<PredicateNode> getUniquePredicates() {
            return null;
        }
    }
}
