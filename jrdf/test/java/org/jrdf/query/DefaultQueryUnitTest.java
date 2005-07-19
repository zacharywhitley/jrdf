package org.jrdf.query;

import java.io.Serializable;
import java.util.List;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;

/**
 * Unit test for {@link DefaultQuery}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class DefaultQueryUnitTest extends TestCase {

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Query.class, DefaultQuery.class);
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Serializable.class, DefaultQuery.class);
    }

    // FIXME: Fix serialVersionUID value
    public void testSerialVersionUid() {
        assertEquals(-1L, DefaultQuery.serialVersionUID);
    }

    public void testGetProjectedVariables() {
        Query query = new DefaultQuery();
        // FIXME: Breadcrumb - dealing with passing variables here.
        List<? extends Variable> variables = Variable.ALL_VARIABLES;
        query.setProjectedVariables(variables);
//        List<Variable> actualVariables = query.getProjectedVariables();
//        assertEquals(variables, actualVariables);
    }
}
