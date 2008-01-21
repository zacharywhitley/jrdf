/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
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
 */

package org.jrdf.parser.ntriples.parser;

import junit.framework.TestCase;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.jrdf.util.boundary.PatternArgumentMatcher.eqPattern;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.test.ArgumentTestUtil;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ParameterDefinition;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import static org.jrdf.util.test.ArgumentTestUtil.*;
import static org.jrdf.util.test.ClassPropertiesTestUtil.*;

import java.util.regex.Pattern;

public class LiteralUtilImplUnitTest extends TestCase {
    private static final Class<?> TARGET_INTERFACE = NTripleUtil.class;
    private static final Class<?> TEST_CLASS = NTripleUtilImpl.class;
    private static final Class[] PARAM_TYPES = {RegexMatcherFactory.class};
    private static final String[] PARAMETER_NAMES = {"regexMatcherFactory"};
    private static final Pattern LITERAL_ESCAPE_REGEX = Pattern.compile(
        "(\\\\((\\\\)|(\")|(n)|(r)|(t)|(u(\\p{XDigit}{4}))|(U(\\p{XDigit}{8}))))");
    private MockFactory factory = new MockFactory();
    private RegexMatcherFactory regexMatcherFactory;
    private RegexMatcher matcher;
    private NTripleUtil util;
    private static final String LINE = "string" + Math.random();

    public void setUp() {
        regexMatcherFactory = factory.createMock(RegexMatcherFactory.class);
        matcher = factory.createMock(RegexMatcher.class);
        util = new NTripleUtilImpl(regexMatcherFactory);
    }

    public void testClassProperties() {
        checkClassFinal(TEST_CLASS);
        checkClassPublic(TEST_CLASS);
        checkImplementationOfInterface(TARGET_INTERFACE, TEST_CLASS);
        checkConstructNullAssertion(TEST_CLASS, PARAM_TYPES);
    }

    public void testMethodProperties() {
        checkMethodNullAssertions(util, "unescapeLiteral", new ParameterDefinition(
            new String[]{"literal"}, new Class[]{String.class}));
    }

    public void testUnescapeLiteral() {
        final String line = "string" + Math.random();
        expect(regexMatcherFactory.createMatcher(eqPattern(LITERAL_ESCAPE_REGEX), eq(line))).andReturn(matcher);
        expect(matcher.find()).andReturn(false);
        factory.replay();
        String s = util.unescapeLiteral(line);
        assertTrue(s == line);
        factory.verify();
    }

    public void testEscapeLiteralLookup() {
        checkCharacterEscape("\\\\", "\\\\");
        checkCharacterEscape("\\\"", "\\\"");
        checkCharacterEscape("\\n", "\n");
        checkCharacterEscape("\\r", "\r");
        checkCharacterEscape("\\t", "\t");
    }

    public void testEscapeLiteral4DigitUnicode() {
        checkUnicode("\\u000f", 9);
    }

    public void testEscapeLiteral8DigitUnicode() {
        checkUnicode("\\U00000000f", 11);
    }

    private void checkCharacterEscape(String key, String value) {
        expect(regexMatcherFactory.createMatcher(eqPattern(LITERAL_ESCAPE_REGEX), eq(LINE))).andReturn(matcher);
        expect(matcher.find()).andReturn(true);
        expect(matcher.group(0)).andReturn(key);
        matcher.appendReplacement((StringBuffer) anyObject(), eq(value));
        expect(matcher.find()).andReturn(false);
        matcher.appendTail((StringBuffer) anyObject());
        factory.replay();
        String s = util.unescapeLiteral(LINE);
        assertEquals("", s);
        factory.verify();
        factory.reset();
    }

    private void checkUnicode(String string, int group) {
        expect(regexMatcherFactory.createMatcher(eqPattern(LITERAL_ESCAPE_REGEX), eq(LINE))).andReturn(matcher);
        expect(matcher.find()).andReturn(true);
        expect(matcher.group(0)).andReturn(string);
        expect(matcher.group(group)).andReturn("0f");
        matcher.appendReplacement((StringBuffer) anyObject(), eq(new String(Character.toChars(15))));
        expect(matcher.find()).andReturn(false);
        matcher.appendTail((StringBuffer) anyObject());
        factory.replay();
        String s = util.unescapeLiteral(LINE);
        assertEquals("", s);
        factory.verify();
    }
}
