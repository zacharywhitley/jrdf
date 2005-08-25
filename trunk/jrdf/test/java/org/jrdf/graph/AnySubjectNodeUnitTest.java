package org.jrdf.graph;

import com.gargoylesoftware.base.testing.TestUtil;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;

/**
 * Test the properties of the AnySubjectNode.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class AnySubjectNodeUnitTest extends TestCase {
    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(SubjectNode.class,
            AnySubjectNode.class);
        TestUtil.testSerialization(AnySubjectNode.ANY_SUBJECT_NODE, true);
    }
}