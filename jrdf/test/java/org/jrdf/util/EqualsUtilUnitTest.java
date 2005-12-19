package org.jrdf.util;

import junit.framework.TestCase;
import org.jrdf.graph.AnySubjectNode;

/**
 * Unit test for {@link org.jrdf.util.EqualsUtil}.
 *
 * @author Andrew Newman
 */
public class EqualsUtilUnitTest extends TestCase {

    public void testIsNull() {
        assertTrue(EqualsUtil.isNull(null));
        String s = null;
        assertTrue(EqualsUtil.isNull(s));
        assertFalse(EqualsUtil.isNull(AnySubjectNode.ANY_SUBJECT_NODE));
    }

    public void testSameReference() {
        assertTrue(EqualsUtil.sameReference(null, null));
        String s1 = "foo";
        String s2 = s1;
        assertTrue(EqualsUtil.sameReference(s1, s2));
        s2 = new String("bar");
        assertFalse(EqualsUtil.sameReference(s1, s2));
    }

    public void testDifferentClasses() {
        assertTrue(EqualsUtil.differentClasses("foo", new StringBuffer("foo")));
        assertFalse(EqualsUtil.differentClasses("foo", "bar"));
    }

    public void testDifferentSuperClassOrInterface() {
        assertTrue(EqualsUtil.hasSuperClassOrInterface(CharSequence.class, new StringBuffer("foo")));
        assertFalse(EqualsUtil.hasSuperClassOrInterface(String.class, new StringBuffer("foo")));
        assertFalse(EqualsUtil.hasSuperClassOrInterface(String.class, new Integer(1)));
    }
}
