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

package org.jrdf.query.relation.mem;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL_L1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL_L2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_FOO1_LITERAL_L1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_FOO1_LITERAL_L2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createTuple;
import static org.jrdf.util.test.ComparatorTestUtil.checkNullPointerException;

/**
 * Test for the implementation of TupleComparatorImpl.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class TupleComparatorImplIntegrationTest extends TestCase {
    private static final int EQUAL = 0;
    private static final int BEFORE = -1;
    private static final int AFTER = 1;
    private TupleComparator tupleComparator;
    private static final Tuple TEST_VARBAR_LITERAL_TUPLE_1 = new TupleImpl(VAR_BAR1_LITERAL_L1);
    private static final Tuple TEST_VARFOO_LITERAL_TUPLE_2 = new TupleImpl(VAR_FOO1_LITERAL_L1);
    private static final Tuple TEST_VARBAR_LITERAL_TUPLE_3 = new TupleImpl(VAR_BAR1_LITERAL_L2);
    private static final Tuple TEST_VARFOO_LITERAL_TUPLE_4 = new TupleImpl(VAR_FOO1_LITERAL_L2);
    private static final Tuple TEST_TUPLE_1_2 = createTuple(VAR_BAR1_LITERAL_L1, VAR_FOO1_LITERAL_L1);
    private static final Tuple TEST_TUPLE_1_3 = createTuple(VAR_BAR1_LITERAL_L1, VAR_BAR1_LITERAL_L2);
    private static final Tuple TEST_TUPLE_3_1 = createTuple(VAR_BAR1_LITERAL_L2, VAR_BAR1_LITERAL_L1);

    protected void setUp() throws Exception {
        super.setUp();
        tupleComparator = TestJRDFFactory.getFactory().getNewTupleComparator();
    }

    public void testNullPointerException() {
        checkNullPointerException(tupleComparator, TEST_VARBAR_LITERAL_TUPLE_1, null);
        checkNullPointerException(tupleComparator, null, TEST_VARBAR_LITERAL_TUPLE_1);
    }

    public void testIdentity() {
        assertEquals(EQUAL, tupleComparator.compare(TEST_VARBAR_LITERAL_TUPLE_1, TEST_VARBAR_LITERAL_TUPLE_1));
    }

    public void testAttributeOrder() {
        assertEquals(BEFORE, tupleComparator.compare(TEST_VARBAR_LITERAL_TUPLE_1, TEST_VARFOO_LITERAL_TUPLE_2));
        assertEquals(BEFORE, tupleComparator.compare(TEST_VARBAR_LITERAL_TUPLE_3, TEST_VARFOO_LITERAL_TUPLE_2));
        assertEquals(BEFORE, tupleComparator.compare(TEST_VARBAR_LITERAL_TUPLE_3, TEST_VARFOO_LITERAL_TUPLE_4));
    }

    public void testAttributeOrderAntiCommutation() {
        assertEquals(AFTER, tupleComparator.compare(TEST_VARFOO_LITERAL_TUPLE_2, TEST_VARBAR_LITERAL_TUPLE_1));
        assertEquals(AFTER, tupleComparator.compare(TEST_VARFOO_LITERAL_TUPLE_4, TEST_VARBAR_LITERAL_TUPLE_3));
    }

    public void testDifferentTupleSizes() {
        assertEquals(BEFORE, tupleComparator.compare(TEST_VARBAR_LITERAL_TUPLE_1, TEST_TUPLE_1_2));
    }

    public void testDifferentTupleSizesAntiCommutation() {
        assertEquals(AFTER, tupleComparator.compare(TEST_TUPLE_1_2, TEST_VARBAR_LITERAL_TUPLE_1));
    }

    public void testMultipleLength() {
        assertEquals(BEFORE, tupleComparator.compare(TEST_TUPLE_1_3, TEST_TUPLE_1_2));
        assertEquals(BEFORE, tupleComparator.compare(TEST_TUPLE_3_1, TEST_TUPLE_1_2));
        // TODO Tuple Refactor.
//        assertEquals(EQUAL, tupleComparator.compare(TEST_TUPLE_3_1, TEST_TUPLE_1_3));
    }

    public void testMutlipleLengthAntiCommutation() {
        assertEquals(AFTER, tupleComparator.compare(TEST_TUPLE_1_2, TEST_TUPLE_1_3));
        assertEquals(AFTER, tupleComparator.compare(TEST_TUPLE_1_2, TEST_TUPLE_3_1));
        // TODO Tuple Refactor.
//        assertEquals(EQUAL, tupleComparator.compare(TEST_TUPLE_1_3, TEST_TUPLE_3_1));
    }
}
