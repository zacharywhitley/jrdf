package org.jrdf.util;

import junit.framework.TestCase;
import org.jrdf.util.EqualsUtil;
import org.jrdf.graph.AnySubjectNode;

/**
 * Unit test for {@link EscapeUtil}.
 *
 * @author Andrew Newman
 */
public class EscapeUtilUnitTest extends TestCase {

    public void testSurrgates() {
        // ISO 10646 Amendment 1
        // High surrogates from D800...DBFF, low surrogates from DC00...DFFF.
        testEscapedValue("\\U00010000", "\uD800\uDC00");
        testEscapedValue("\\U00020000", "\uD840\uDC00");
        testEscapedValue("\\U00030000", "\uD880\uDC00");
        testEscapedValue("\\U00100000", "\uDBC0\uDC00");
        testEscapedValue("\\U0010FFFF", "\uDBFF\uDFFF");
    }

    public void testNearSurrogates() {
        // ISO 10646 Amendment 1
        // High surrogates from D800...DBFF, low surrogates from DC00...DFFF.
        testEscapedValue("\\uD799\\uDC00", "\uD799\uDC00");
        testEscapedValue("\\uD799\\uDC01", "\uD799\uDC01");
        testEscapedValue("\\uD840\\uDB00", "\uD840\uDB00");
        testEscapedValue("\\uD740\\uDFFF", "\uD740\uDFFF");
    }

    private void testEscapedValue(String expectedValue, String testString) {
        assertEquals(expectedValue, EscapeUtil.escape(testString));
    }
}
