package org.jrdf.writer.bdb;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import org.jrdf.graph.BlankNode;
import org.jrdf.map.MapFactory;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructorSetsFieldsAndFieldsPrivateFinal;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAssertions;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ParameterDefinition;
import org.jrdf.writer.BlankNodeRegistry;

import java.lang.reflect.Modifier;
import java.util.HashMap;

public class BdbBlankNodeRegistryImplUnitTest extends TestCase {
    private static final Class[] PARAM_TYPES = {MapFactory.class};
    private static final String[] PARAMETER_NAMES = {"newMapFactory"};
    private MockFactory mockFactory = new MockFactory();
    private MapFactory mapFactory;
    private BlankNodeRegistry bdbBlankNodeRegistry;

    public void setUp() {
        mapFactory = mockFactory.createMock(MapFactory.class);
    }

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(BlankNodeRegistry.class, BdbBlankNodeRegistryImpl.class);
        checkConstructor(BdbBlankNodeRegistryImpl.class, Modifier.PUBLIC, PARAM_TYPES);
        checkConstructNullAssertion(BdbBlankNodeRegistryImpl.class, PARAM_TYPES);
        checkConstructorSetsFieldsAndFieldsPrivateFinal(BdbBlankNodeRegistryImpl.class, PARAM_TYPES, PARAMETER_NAMES);
        checkMethodNullAssertions(new BdbBlankNodeRegistryImpl(mapFactory), "getNodeId", new ParameterDefinition(
                new String[] {"node"}, new Class[] {BlankNode.class}));
    }

    public void testGetNodeIdAndClear() {
        expect(mapFactory.createMap(BlankNode.class, Integer.class)).andReturn(new HashMap<BlankNode, Integer>());
        BlankNode blankNode1 = mockFactory.createMock(BlankNode.class);
        BlankNode blankNode2 = mockFactory.createMock(BlankNode.class);
        BlankNode blankNode3 = mockFactory.createMock(BlankNode.class);
        mockFactory.replay();
        bdbBlankNodeRegistry = new BdbBlankNodeRegistryImpl(mapFactory);
        checkId(blankNode1, "bNode_0");
        checkId(blankNode1, "bNode_0");
        checkId(blankNode2, "bNode_1");
        checkId(blankNode3, "bNode_2");
        checkId(blankNode1, "bNode_0");
        bdbBlankNodeRegistry.clear();
        checkId(blankNode3, "bNode_0");
        checkId(blankNode2, "bNode_1");
        mockFactory.verify();
    }

    public void testClose() {
        expect(mapFactory.createMap(BlankNode.class, Integer.class)).andReturn(new HashMap<BlankNode, Integer>());
        mapFactory.close();
        expectLastCall();
        mockFactory.replay();
        bdbBlankNodeRegistry = new BdbBlankNodeRegistryImpl(mapFactory);
        bdbBlankNodeRegistry.close();
        mockFactory.verify();
    }

    private void checkId(BlankNode blankNode, String expectedValue) {
        String id = bdbBlankNodeRegistry.getNodeId(blankNode);
        assertEquals(expectedValue, id);
    }
}
