package org.jrdf.graph;

import com.gargoylesoftware.base.testing.TestUtil;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;

import java.lang.reflect.Modifier;

/**
 * Test the properties of the AnySubjectNode.
 * @author Andrew Newman
 * @author Tom Adams
 * @version $Id$
 */
public class AnySubjectNodeUnitTest extends TestCase {

    private static final String EXPECTED_TO_STRING = "ANY_SUBJECT";
    private static final boolean WHO_KNOWS_WHAT_THIS_MEANS = true;

    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(SubjectNode.class, AnySubjectNode.class);
        ClassPropertiesTestUtil.checkConstructor(AnySubjectNode.class, Modifier.PRIVATE, NO_ARG_CONSTRUCTOR);
        TestUtil.testSerialization(AnySubjectNode.ANY_SUBJECT_NODE, WHO_KNOWS_WHAT_THIS_MEANS);
    }

    public void testToString() {
        assertEquals(EXPECTED_TO_STRING, AnySubjectNode.ANY_SUBJECT_NODE.toString());
    }
}