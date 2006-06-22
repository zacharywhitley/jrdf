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

package org.jrdf.util.param;

import junit.framework.TestCase;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkClassFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;

import static java.lang.reflect.Modifier.PRIVATE;

/**
 * Unit test for {@link ParameterUtil}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class ParameterUtilUnitTest extends TestCase {

    private static final String NULL = ParameterTestUtil.NULL_STRING;
    private static final String EMPTY_STRING = ParameterTestUtil.EMPTY_STRING;
    private static final String SINGLE_SPACE = ParameterTestUtil.SINGLE_SPACE;
    private static final String NON_EMPTY_STRING = ParameterTestUtil.NON_EMPTY_STRING;
    private static final Object NON_NULL_OBJECT = ParameterTestUtil.NON_NULL_OBJECT;
    private static final String DUMMY_PARAM_NAME = "foo";

    public void testClassProperties() {
        checkConstructor(ParameterUtil.class, PRIVATE, NO_ARG_CONSTRUCTOR);
        checkClassFinal(ParameterUtil.class);
    }

    public void testNoNullsAllowed() {
        try {
            ParameterUtil.checkNotNull(DUMMY_PARAM_NAME, NULL);
            fail("Nulls should not be allowed");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testEmptyStringNotAllowed() {
        checkStringNotAllowed(NULL);
        checkStringNotAllowed(EMPTY_STRING);
        checkStringNotAllowed(SINGLE_SPACE);
    }

    public void testNonEmptyStringAllowed() {
        ParameterUtil.checkNotEmptyString(DUMMY_PARAM_NAME, NON_EMPTY_STRING);
    }

    public void testNonNullObjectAllowed() {
        ParameterUtil.checkNotNull(DUMMY_PARAM_NAME, NON_NULL_OBJECT);
    }

    private void checkStringNotAllowed(String param) {
        try {
            ParameterUtil.checkNotEmptyString(DUMMY_PARAM_NAME, param);
            fail("Empty strings should not be allowed");
        } catch (IllegalArgumentException expected) {
        }
    }
}
