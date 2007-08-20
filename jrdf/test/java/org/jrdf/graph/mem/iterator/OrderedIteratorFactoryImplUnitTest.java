package org.jrdf.graph.mem.iterator;

import junit.framework.TestCase;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.Triple;
import org.jrdf.graph.index.graphhandler.GraphHandler;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.index.nodepool.NodePool;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.MockFactory;
import org.easymock.EasyMock;

import java.lang.reflect.Modifier;

public class OrderedIteratorFactoryImplUnitTest extends TestCase {
    private static final Class[] PARAM_TYPES = { IteratorFactory.class, NodePool.class, LongIndex.class,
            GraphHandler.class, NodeComparator.class};
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
        EasyMock.expect(iteratorFactory.newEmptyClosableIterator()).andReturn(returnIterator);
        IteratorFactory factory = new OrderedIteratorFactoryImpl(iteratorFactory, nodePool, longIndex, graphHandler,
                nodeComparator);
        mockFactory.replay();
        ClosableMemIterator<Triple> actualIterator = factory.newEmptyClosableIterator();
        assertTrue(returnIterator == actualIterator);
        mockFactory.verify();
    }
}
