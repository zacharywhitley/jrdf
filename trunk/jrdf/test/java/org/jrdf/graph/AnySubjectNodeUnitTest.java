package org.jrdf.graph;

import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;
import org.jrdf.util.test.SerializationTestUtil;

/**
 * Test the properties of the AnySubjectNode.
 * @author Andrew Newman
 * @author Tom Adams
 * @version $Id$
 */
public class AnySubjectNodeUnitTest extends TestCase {

    private static final String EXPECTED_TO_STRING = "ANY_SUBJECT";

    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(SubjectNode.class, AnySubjectNode.class);
        ClassPropertiesTestUtil.checkConstructor(AnySubjectNode.class, Modifier.PRIVATE, NO_ARG_CONSTRUCTOR);
        // FIXME TJA: This probably belongs in an integration test
        SerializationTestUtil.checkSerialization(AnySubjectNode.ANY_SUBJECT_NODE);
    }

    public void testSerialVersionUid() {
        SerializationTestUtil.checkSerialialVersionUid(AnySubjectNode.class, -971680612480915602L);
    }

    public void testToString() {
        assertEquals(EXPECTED_TO_STRING, AnySubjectNode.ANY_SUBJECT_NODE.toString());
    }
}