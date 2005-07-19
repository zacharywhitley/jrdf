package org.jrdf.query;

import java.io.Serializable;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;

/**
 * Unit test for {@link DefaultVariable}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class DefaultVariableUnitTest extends TestCase {
    private static final String VARIABLE_NAME_1 = "title";
    private static final String VARIABLE_NAME_2 = "author";

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Serializable.class, DefaultVariable.class);
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Variable.class, DefaultVariable.class);
    }

    // FIXME: Fix serialVersionUID value.
    public void testConstants() {
        assertEquals(-1L, DefaultVariable.serialVersionUID);
    }

    public void testGetName() {
        checkName(VARIABLE_NAME_1);
        checkName(VARIABLE_NAME_2);
    }

    private void checkName(String name) {
        Variable variable = new DefaultVariable(name);
        assertEquals(name, variable.getName());
    }
}
