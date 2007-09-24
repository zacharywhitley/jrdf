/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.util.test.ArgumentTestUtil;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.MockFactory;

import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * Test for in memory tuple factory.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
@SuppressWarnings({"unchecked"})
public class TupleFactoryImplUnitTest extends TestCase {
    private static final MockFactory factory = new MockFactory();
    private static final Class[] CONSTRUCTOR_TYPES = { AttributeValuePairComparator.class };
    private static final String[] CONSTRUCTOR_NAMES = {"attributeValuePairComparator"};
    private static final AttributeValuePairComparator AVP_COMPARATOR =
            factory.createMock(AttributeValuePairComparator.class);
    private static final Set<AttributeValuePair> MOCK_AVP = factory.createMock(Set.class);

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(TupleFactory.class, TupleFactoryImpl.class);
        ClassPropertiesTestUtil.checkConstructor(TupleFactoryImpl.class, Modifier.PUBLIC,
                CONSTRUCTOR_TYPES);
    }

    public void testConstructor() {
        ArgumentTestUtil.checkConstructNullAssertion(TupleFactoryImpl.class, CONSTRUCTOR_TYPES);
        ArgumentTestUtil.checkConstructorSetsFieldsAndFieldsPrivate(TupleFactoryImpl.class, CONSTRUCTOR_TYPES,
                CONSTRUCTOR_NAMES);
    }

    public void testGetTupleBySet() {
        TupleFactory tupleFactory = new TupleFactoryImpl(AVP_COMPARATOR);
        Tuple tuple = tupleFactory.getTuple(MOCK_AVP);
        assertSame(MOCK_AVP, tuple.getAttributeValues());
        assertTrue(tuple instanceof TupleImpl);
    }

// TODO (AN) Add test for testing creating a tuple with a list.
//    public void testGetTupleByList() {
//    }
}
