/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */

package org.jrdf.graph;

import junit.framework.TestCase;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkExtensionOf;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterface;
import static org.jrdf.util.test.SerializationTestUtil.checkSerialialVersionUid;
import org.jrdf.util.test.URIReference1;
import org.jrdf.util.test.URIReference2;
import static org.jrdf.util.test.AssertThrows.*;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.net.URI;

/**
 * Unit test for {@link AbstractURIReference}.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public abstract class AbstractURIReferenceUnitTest extends TestCase {
    private URI uri1 = URI.create("http://foo/bar");
    private URI uri2 = URI.create("http://bar/baz");
    private URI uri3 = URI.create("ftp://bar/baz");
    private URI notAbsURI = URI.create("hello/there");
    private URIReference ref1;
    private URIReference ref2;
    private URIReference ref3;

    /**
     * Constructs a new test with the given name.
     *
     * @param name the name of the test
     */
    public AbstractURIReferenceUnitTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        ref1 = createResource(uri1);
        ref2 = createResource(uri2);
        ref3 = createResource(uri3);
    }

    public abstract URIReference createResource(URI uri) throws Exception;

    public abstract URIReference createResource(URI uri, boolean check) throws Exception;

    public abstract void testClassProperties();

    protected void checkClassProperties(Class newClass, Class[] paramTypes) {
        checkImplementationOfInterface(URIReference.class, newClass);
        checkImplementationOfInterface(Serializable.class, newClass);
        checkExtensionOf(AbstractURIReference.class, newClass);
        checkConstructNullAssertion(newClass, paramTypes);
    }

    protected void checkAbstractClassProperties() {
        checkImplementationOfInterface(URIReference.class, AbstractURIReference.class);
        checkImplementationOfInterface(Serializable.class, AbstractURIReference.class);
        checkConstructor(AbstractURIReference.class, Modifier.PROTECTED, URI.class);
        checkConstructor(AbstractURIReference.class, Modifier.PROTECTED, URI.class, boolean.class);
        checkSerialialVersionUid(AbstractURIReference.class, 8034954863132812197L);
    }

    public void testConstructor() throws Exception {
        checkNotAbsoluteURIThrowsException();
        checkNotAbsoluteURINoCheck();
    }

    public void testHashCode() {
        checkConsistentHashCode();
        checkEqualObjectsReturnSameHashCode();
    }

    public void testEquals() throws Exception {
        checkNullComparisonObject();
        checkReflexive();
        checkDifferentClass();
        checkSymmetric();
        checkTransitive();
        checkConsistentEquals();
        checkUnequal();
        checkDifferentImplementationsAreEqual();
    }

    private void checkConsistentHashCode() {
        checkConsistentHashCode(ref1);
        checkConsistentHashCode(ref2);
    }

    private void checkConsistentHashCode(URIReference ref) {
        int hashCode1 = ref.hashCode();
        int hashCode2 = ref.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    private void checkReflexive() throws Exception {
        checkSameValueSameReference();
        checkSameValueDifferentReference();
    }

    private void checkDifferentClass() {
        checkNotEquals(uri1, URI.create("mailto:here.com"));
    }

    private void checkSymmetric() throws Exception {
        URIReference x = createResource(uri1);
        URIReference y = createResource(uri1);
        checkEquals(x, y);
        checkEquals(y, y);
    }

    private void checkTransitive() throws Exception {
        URIReference x = createResource(uri2);
        URIReference y = createResource(uri2);
        URIReference z = createResource(uri2);
        checkEquals(x, y);
        checkEquals(y, z);
        checkEquals(x, z);
    }

    private void checkConsistentEquals() throws Exception {
        URIReference x = createResource(uri1);
        URIReference y = createResource(uri1);
        checkEquals(x, y);
        checkEquals(x, y);
    }

    private void checkUnequal() {
        checkNotEquals(ref1, ref2);
        checkNotEquals(ref1, ref3);
    }

    private void checkSameValueSameReference() {
        URIReference x = ref1;
        URIReference y = x;
        checkEquals(x, y);
    }

    private void checkSameValueDifferentReference() throws Exception {
        URIReference x = createResource(uri1);
        URIReference y = createResource(uri1);
        checkEquals(x, y);
    }

    private void checkNullComparisonObject() {
        checkNotEquals(ref1, null);
    }

    private void checkNotAbsoluteURIThrowsException() {
        checkCreateException(new AssertThrows.Block() {
            public void execute() throws Throwable {
                createResource(notAbsURI);
            }
        });
    }

    private void checkNotAbsoluteURINoCheck() throws Exception {
        try {
            createResource(notAbsURI, false);
        } catch (Exception e) {
            fail("Should not throw an exception when an relative URI is given and checking is off.");
        }
        checkCreateException(new AssertThrows.Block() {
            public void execute() throws Throwable {
                createResource(notAbsURI, true);
            }
        });

    }

    private void checkCreateException(AssertThrows.Block block) {
        assertThrows(IllegalArgumentException.class, "\"" + notAbsURI + "\" is not absolute", block);
    }

    private void checkEqualObjectsReturnSameHashCode() {
        URIReference x = ref1;
        URIReference y = ref1;
        checkEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }

    private void checkEquals(URIReference x, URIReference y) {
        assertEquals(x, y);
    }

    private void checkNotEquals(Object x, Object y) {
        assertFalse(x.equals(y));
    }

    private void checkDifferentImplementationsAreEqual() {
        URIReference testRef1 = new URIReference1(uri1);
        URIReference testRef2 = new URIReference2(uri1);
        assertEquals(testRef1, testRef2);
    }

}
