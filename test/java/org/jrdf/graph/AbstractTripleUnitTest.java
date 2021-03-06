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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkExtensionOf;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterface;
import static org.jrdf.util.test.EqualsHashCodeTestUtil.assertHashCode;
import org.jrdf.util.test.SerializationTestUtil;
import org.jrdf.util.test.Triple1;
import org.jrdf.util.test.Triple2;
import static org.jrdf.util.test.TripleTestUtil.LITERAL_BOOK_TITLE;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_1;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_3;
import static org.jrdf.util.test.TripleTestUtil.URI_DC_TITLE;
import org.jrdf.vocabulary.XSD;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.net.URI;

/**
 * Unit test for {@link AbstractTriple}.
 *
 * @author Tom Adams
 * @author Andrew Newman
 * @version $Revision$
 */
public abstract class AbstractTripleUnitTest {
    private Triple triple2;
    private Triple triple3;
    private Triple triple4;
    private Triple triple5;
    private Triple triple6;
    private URIReference uriUrnFoo;

    @Before
    public void setUp() throws Exception {
        triple2 = createTriple(URI_BOOK_3, URI_DC_TITLE, URI_BOOK_1);
        triple3 = createTriple(URI_BOOK_3, URI_DC_TITLE, URI_BOOK_1);
        triple4 = createTriple(URI_BOOK_3, URI_DC_TITLE, LITERAL_BOOK_TITLE);
        triple5 = createTriple(URI_BOOK_3, URI_DC_TITLE, LITERAL_BOOK_TITLE, XSD.STRING);
        triple6 = createTriple(URI_BOOK_3, URI_DC_TITLE, LITERAL_BOOK_TITLE, "en");
        uriUrnFoo = createResource(URI.create("urn:foo"));
    }

    public abstract Triple createTriple(SubjectNode subject, PredicateNode predicate, ObjectNode object);

    public abstract Triple createTriple(URI subject, URI predicate, URI object);

    public abstract Triple createTriple(URI subject, URI predicate, String object);

    public abstract Triple createTriple(URI subject, URI predicate, String object, String language);

    public abstract Triple createTriple(URI subject, URI predicate, String object, URI dataType);

    public abstract URIReference createResource(URI uri);

    public abstract void testClassProperties();

    @Test
    public void testHashCode() {
        checkConsistentHashCode();
        checkEqualObjectsReturnSameHashCode();
    }

    @Test
    public void testEquals() {
        checkNullComparisonObject();
        checkReflexive();
        checkDifferentClass();
        checkSymmetric();
        checkTransitive();
        checkConsistentEquals();
        checkDifferentImplementationsAreEqual();
    }

    protected void checkClassProperties(Class newClass) {
        checkImplementationOfInterface(Triple.class, newClass);
        checkImplementationOfInterface(Serializable.class, newClass);
        checkExtensionOf(AbstractTriple.class, newClass);
    }

    protected void checkAbstractClassProperties() {
        checkImplementationOfInterface(Triple.class, AbstractTriple.class);
        checkImplementationOfInterface(Serializable.class, AbstractTriple.class);
        checkConstructor(AbstractTriple.class, Modifier.PUBLIC, NO_ARG_CONSTRUCTOR);
        SerializationTestUtil.checkSerialialVersionUid(AbstractTriple.class, 8737092494833012690L);
    }

    private void checkConsistentHashCode() {
        assertHashCode(triple3, triple3);
        assertHashCode(triple4, triple4);
        assertHashCode(triple5, triple5);
        assertHashCode(triple6, triple6);
    }

    private void checkEqualObjectsReturnSameHashCode() {
        Triple x = triple2;
        Triple y = triple2;
        checkEqual(x, y);
        assertThat(x.hashCode(), equalTo(y.hashCode()));
    }

    private void checkReflexive() {
        checkSameValueSameReference();
        checkSameValueDifferentReference();
    }

    private void checkDifferentClass() {
        checkNotEqual(triple2, uriUrnFoo);
    }

    private void checkSymmetric() {
        Triple x = createTriple(uriUrnFoo, uriUrnFoo, uriUrnFoo);
        Triple y = createTriple(uriUrnFoo, uriUrnFoo, uriUrnFoo);
        checkEqual(x, y);
        checkEqual(y, y);
    }

    private void checkTransitive() {
        Triple x = createTriple(uriUrnFoo, uriUrnFoo, uriUrnFoo);
        Triple y = createTriple(uriUrnFoo, uriUrnFoo, uriUrnFoo);
        Triple z = createTriple(uriUrnFoo, uriUrnFoo, uriUrnFoo);
        checkEqual(x, y);
        checkEqual(y, z);
        checkEqual(x, z);
    }

    private void checkConsistentEquals() {
        Triple x = createTriple(uriUrnFoo, uriUrnFoo, uriUrnFoo);
        Triple y = createTriple(uriUrnFoo, uriUrnFoo, uriUrnFoo);
        checkEqual(x, y);
        checkEqual(x, y);
    }

    private void checkSameValueSameReference() {
        Triple x = triple2;
        Triple y = x;
        checkEqual(x, y);
    }

    private void checkSameValueDifferentReference() {
        Triple x = createTriple(uriUrnFoo, uriUrnFoo, uriUrnFoo);
        Triple y = createTriple(uriUrnFoo, uriUrnFoo, uriUrnFoo);
        checkEqual(x, y);
    }

    private void checkSameTripleIsEqual(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        Triple tmpTriple1 = createTriple(subject, predicate, object);
        Triple tmpTriple2 = createTriple(subject, predicate, object);
        checkEqual(tmpTriple1, tmpTriple2);
    }

    private void checkNullComparisonObject() {
        checkNotEqual(triple2, null);
    }

    private void checkEqual(Triple x, Triple y) {
        assertThat(x, equalTo(y));
    }

    private void checkNotEqual(Object x, Object y) {
        assertThat(x.equals(y), is(false));
    }

    private void checkDifferentImplementationsAreEqual() {
        Triple t1 = new Triple1(uriUrnFoo, uriUrnFoo, uriUrnFoo);
        Triple t2 = new Triple2(uriUrnFoo, uriUrnFoo, uriUrnFoo);
        assertThat(t1, equalTo(t2));
    }
}
