/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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
 */

package org.jrdf.graph;

import java.io.Serializable;
import junit.framework.TestCase;
import org.jrdf.sparql.SparqlQueryTestUtil;
import org.jrdf.util.test.ClassPropertiesTestUtil;

/**
 * Unit test for {@link org.jrdf.graph.AbstractTriple}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class AbstractTripleUnitTest extends TestCase {

    private static final Triple TRIPLE_ALL_NULL_1 = NodeTestUtil.createTriple(null, null, null);
    private static final Triple TRIPLE_ALL_NULL_2 = NodeTestUtil.createTriple(null, null, null);
    private static final Triple TRIPLE_1 = SparqlQueryTestUtil.TRIPLE_BOOK_1_DC_TITLE_VARIABLE;
    private static final Triple TRIPLE_2 = SparqlQueryTestUtil.TRIPLE_BOOK_1_DC_TITLE_VARIABLE;
    private static final URIReference URI_URN_FOO = NodeTestUtil.createResource("urn:foo");
    private static final Triple TRIPLE_URI_URN_FOO = NodeTestUtil.createTriple(URI_URN_FOO, URI_URN_FOO, URI_URN_FOO);
    private static final Triple TRIPLE_NULL_SUBJECT = NodeTestUtil.createTriple(null, URI_URN_FOO, URI_URN_FOO);
    private static final Triple TRIPLE_NULL_PREDICATE = NodeTestUtil.createTriple(URI_URN_FOO, null, URI_URN_FOO);
    private static final Triple TRIPLE_NULL_OBJECT = NodeTestUtil.createTriple(URI_URN_FOO, URI_URN_FOO, null);

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterface(Triple.class, AbstractTriple.class);
        ClassPropertiesTestUtil.checkImplementationOfInterface(Serializable.class, AbstractTriple.class);
    }

    public void testSerialVersionUid() {
        assertEquals(8737092494833012690L, AbstractTriple.serialVersionUID);
    }

    public void testHashCode() {
        checkConsistentHashCode();
        checkEqualObjectsReturnSameHashCode();
    }

    public void testEquals() {
        checkNullComparisonObject();
        checkNullField();
        checkReflexive();
        checkDifferentClass();
        checkSymmetric();
        checkTransitive();
        checkConsistentEquals();
        checkUnequal();
    }

    private void checkConsistentHashCode() {
        checkConsistentHashCode(TRIPLE_ALL_NULL_1);
        checkConsistentHashCode(TRIPLE_ALL_NULL_2);
        checkConsistentHashCode(TRIPLE_1);
        checkConsistentHashCode(TRIPLE_2);
    }

    private void checkConsistentHashCode(Triple triple) {
        int hashCode1 = triple.hashCode();
        int hashCode2 = triple.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    private void checkReflexive() {
        checkSameValueSameReference();
        checkSameValueDifferentReference();
    }

    private void checkDifferentClass() {
        checkNotEquals(TRIPLE_1, URI_URN_FOO);
    }

    private void checkSymmetric() {
        Triple x = NodeTestUtil.createTriple(URI_URN_FOO, URI_URN_FOO, URI_URN_FOO);
        Triple y = NodeTestUtil.createTriple(URI_URN_FOO, URI_URN_FOO, URI_URN_FOO);
        checkEquals(x, y);
        checkEquals(y, y);
    }

    private void checkTransitive() {
        Triple x = NodeTestUtil.createTriple(URI_URN_FOO, URI_URN_FOO, URI_URN_FOO);
        Triple y = NodeTestUtil.createTriple(URI_URN_FOO, URI_URN_FOO, URI_URN_FOO);
        Triple z = NodeTestUtil.createTriple(URI_URN_FOO, URI_URN_FOO, URI_URN_FOO);
        checkEquals(x, y);
        checkEquals(y, z);
        checkEquals(x, z);
    }

    private void checkConsistentEquals() {
        Triple x = NodeTestUtil.createTriple(URI_URN_FOO, URI_URN_FOO, URI_URN_FOO);
        Triple y = NodeTestUtil.createTriple(URI_URN_FOO, URI_URN_FOO, URI_URN_FOO);
        checkEquals(x, y);
        checkEquals(x, y);
    }

    private void checkUnequal() {
        checkNotEquals(TRIPLE_ALL_NULL_1, TRIPLE_URI_URN_FOO);
        checkNotEquals(TRIPLE_NULL_SUBJECT, TRIPLE_URI_URN_FOO);
    }

    private void checkSameValueSameReference() {
        Triple x = TRIPLE_1;
        Triple y = x;
        checkEquals(x, y);
    }

    private void checkSameValueDifferentReference() {
        Triple x = NodeTestUtil.createTriple(URI_URN_FOO, URI_URN_FOO, URI_URN_FOO);
        Triple y = NodeTestUtil.createTriple(URI_URN_FOO, URI_URN_FOO, URI_URN_FOO);
        checkEquals(x, y);
    }

    private void checkNullField() {
        checkBothNull();
        checkEitherNull();
    }

    private void checkBothNull() {
        checkSameTripleIsEqual(null, URI_URN_FOO, URI_URN_FOO);
        checkSameTripleIsEqual(URI_URN_FOO, null, URI_URN_FOO);
        checkSameTripleIsEqual(URI_URN_FOO, URI_URN_FOO, null);
    }

    private void checkEitherNull() {
        checkNotEquals(TRIPLE_NULL_SUBJECT, TRIPLE_URI_URN_FOO);
        checkNotEquals(TRIPLE_NULL_PREDICATE, TRIPLE_URI_URN_FOO);
        checkNotEquals(TRIPLE_NULL_OBJECT, TRIPLE_URI_URN_FOO);
    }

    private void checkSameTripleIsEqual(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        Triple triple1 = NodeTestUtil.createTriple(subject, predicate, object);
        Triple triple2 = NodeTestUtil.createTriple(subject, predicate, object);
        checkEquals(triple1, triple2);
    }

    private void checkNullComparisonObject() {
        checkNotEquals(TRIPLE_1, null);
    }

    private void checkEqualObjectsReturnSameHashCode() {
        Triple x = TRIPLE_1;
        Triple y = TRIPLE_2;
        checkEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }

    private void checkEquals(Triple x, Triple y) {
        assertEquals(x, y);
    }

    private void checkNotEquals(Object x, Object y) {
        assertFalse(x.equals(y));
    }
}
