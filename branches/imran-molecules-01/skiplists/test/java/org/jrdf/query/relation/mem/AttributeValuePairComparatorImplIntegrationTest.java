/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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
import static org.jrdf.graph.local.mem.NodeComparatorImplIntegrationTest.LITERAL_1;
import static org.jrdf.graph.local.mem.NodeComparatorImplIntegrationTest.LITERAL_2;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;
import static org.jrdf.query.relation.constants.NullaryAttributeValuePair.NULLARY_ATTRIBUTE_VALUE_PAIR;
import static org.jrdf.query.relation.mem.AttributeComparatorImplIntegrationTest.TEST_VAR_BAR_LITERAL;
import static org.jrdf.query.relation.mem.AttributeComparatorImplIntegrationTest.TEST_VAR_FOO_LITERAL;
import static org.jrdf.util.test.ComparatorTestUtil.*;

/**
 * Test for the implementation of NodeComparatorImpl.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class AttributeValuePairComparatorImplIntegrationTest extends TestCase {
    private static final int EQUAL = 0;
    private static final int BEFORE = -1;
    private static final int AFTER = 1;
    private AttributeValuePairComparator avpComparator;

    public static final AttributeValuePair TEST_AVP_1 = new AttributeValuePairImpl(TEST_VAR_BAR_LITERAL, LITERAL_1);
    public static final AttributeValuePair TEST_AVP_2 = new AttributeValuePairImpl(TEST_VAR_FOO_LITERAL, LITERAL_1);
    public static final AttributeValuePair TEST_AVP_3 = new AttributeValuePairImpl(TEST_VAR_BAR_LITERAL, LITERAL_2);
    public static final AttributeValuePair TEST_AVP_4 = new AttributeValuePairImpl(TEST_VAR_FOO_LITERAL, LITERAL_2);

    protected void setUp() throws Exception {
        super.setUp();
        avpComparator = TestJRDFFactory.getFactory().getNewAttributeValuePairComparator();
    }

    public void testNullPointerException() {
        checkNullPointerException(avpComparator, TEST_AVP_1, null);
        checkNullPointerException(avpComparator, null, TEST_AVP_1);
    }

    public void testIdentity() {
        assertEquals(EQUAL, avpComparator.compare(TEST_AVP_1, TEST_AVP_1));
        assertEquals(EQUAL, avpComparator.compare(TEST_AVP_1, NULLARY_ATTRIBUTE_VALUE_PAIR));
        assertEquals(EQUAL, avpComparator.compare(NULLARY_ATTRIBUTE_VALUE_PAIR, TEST_AVP_1));
    }

    public void testOrderOfAttributeValues() {
        assertEquals(BEFORE, avpComparator.compare(TEST_AVP_1, TEST_AVP_2));
        assertEquals(BEFORE, avpComparator.compare(TEST_AVP_3, TEST_AVP_4));
    }

    public void testOrderOfAttributeValuesAntiCommutation() {
        assertEquals(AFTER, avpComparator.compare(TEST_AVP_2, TEST_AVP_1));
        assertEquals(AFTER, avpComparator.compare(TEST_AVP_4, TEST_AVP_3));
    }

    public void testOrderOfValues() {
        assertEquals(BEFORE, avpComparator.compare(TEST_AVP_1, TEST_AVP_3));
    }

    public void testOrderOfValuesAntiCommutation() {
        assertEquals(AFTER, avpComparator.compare(TEST_AVP_3, TEST_AVP_1));
    }
}
