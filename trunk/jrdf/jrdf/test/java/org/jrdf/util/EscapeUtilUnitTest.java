/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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
package org.jrdf.util;

import junit.framework.TestCase;

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
        testEscapedValue("\\U00010001", "\uD800\uDC01");
        testEscapedValue("\\U00010401", "\uD801\uDC01");
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
        testEscapedValue("\\uD800\\uDBFF", "\uD800\uDBFF");
        testEscapedValue("\\uD801\\uDBFF", "\uD801\uDBFF");
        testEscapedValue("\\uDC00\\uDFFF", "\uDC00\uDFFF");
        testEscapedValue("\\uDBFF\\uE000", "\uDBFF\uE000");
    }

    public void testExampleCodePoints() {
        testEscapedValue("\\U000233B4", "\uD84C\uDFB4");
        testEscapedValue("\\u2260", "\u2260");
        testEscapedValue("q", "\u0071");
        testEscapedValue("\\u030C", "\u030c");
    }

    public void testControlCharacters() {
        testEscapedValue("\\\\", new String(new char[]{(char) 92}));
        testEscapedValue("\\\"", new String(new char[]{(char) 34}));
        testEscapedValue("\\r", new String(new char[]{(char) 13}));
        testEscapedValue("\\n", new String(new char[]{(char) 10}));
        testEscapedValue("\\t", new String(new char[]{(char) 9}));
    }

    private void testEscapedValue(String expectedValue, String testString) {
        assertEquals(expectedValue, EscapeUtil.escape(testString).toString());
    }
}