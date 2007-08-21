package org.jrdf.map;

import junit.framework.TestCase;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;

import java.lang.reflect.Modifier;

public class MemMapFactoryUnitTest extends TestCase {
    public void testClassProperties() throws Exception {
        checkImplementationOfInterfaceAndFinal(MapFactory.class, MemMapFactory.class);
        checkConstructor(MemMapFactory.class, Modifier.PUBLIC);
    }
    
    public void testClose() {
        new MemMapFactory().close();
    }
}
