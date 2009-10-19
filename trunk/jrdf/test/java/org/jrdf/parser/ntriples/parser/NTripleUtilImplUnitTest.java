/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.parser.ntriples.parser;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.util.boundary.PatternArgumentMatcher.eqPattern;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAssertions;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkClassFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkClassPublic;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterface;
import org.jrdf.util.test.ParameterDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.regex.Pattern;

@RunWith(PowerMockRunner.class)
public class NTripleUtilImplUnitTest {
    private static final Class<?> TARGET_INTERFACE = NTripleUtil.class;
    private static final Class<?> TEST_CLASS = NTripleUtilImpl.class;
    private static final Class[] PARAM_TYPES = {RegexMatcherFactory.class};
    private static final Pattern LITERAL_ESCAPE_REGEX = Pattern.compile(
        "(\\\\((\\\\)|(\")|(n)|(r)|(t)|(u(\\p{XDigit}{4}))|(U(\\p{XDigit}{8}))))");
    private static final String LINE = "string" + Math.random();
    @Mock private RegexMatcherFactory regexMatcherFactory;
    @Mock private RegexMatcher matcher;
    private NTripleUtil util;

    @Before

    public void setUp() {
        util = new NTripleUtilImpl(regexMatcherFactory);
    }

    @Test
    public void classProperties() {
        checkClassFinal(TEST_CLASS);
        checkClassPublic(TEST_CLASS);
        checkImplementationOfInterface(TARGET_INTERFACE, TEST_CLASS);
        checkConstructNullAssertion(TEST_CLASS, PARAM_TYPES);
    }

    @Test
    public void methodProperties() {
        checkMethodNullAssertions(util, "unescapeLiteral", new ParameterDefinition(
            new String[]{"literal"}, new Class[]{String.class}));
    }

    @Test
    public void unescapeLiteralCallsCreateMatcherAndFailsToFind() {
        final String expectedLine = "string" + Math.random();
        expect(regexMatcherFactory.createMatcher(eqPattern(LITERAL_ESCAPE_REGEX), eq(expectedLine))).andReturn(matcher);
        expect(matcher.find()).andReturn(false);
        replayAll();
        assertThat(expectedLine, is(util.unescapeLiteral(expectedLine)));
        verifyAll();
    }

    @Test
    public void backslashEscaping() {
        checkCharacterEscape("\\\\", "\\\\");
    }

    @Test
    public void quoteEscaping() {
        checkCharacterEscape("\\\"", "\\\"");
    }

    @Test
    public void newLineEscaping() {
        checkCharacterEscape("\\n", "\n");
    }

    @Test
    public void carriageReturnEscaping() {
        checkCharacterEscape("\\r", "\r");
    }

    @Test
    public void tabEscaping() {
        checkCharacterEscape("\\t", "\t");
    }

    @Test
    public void testEscapeLiteral4DigitUnicode() {
        checkUnicode("\\u000f", 9);
    }

    @Test
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
        replayAll();
        String s = util.unescapeLiteral(LINE);
        assertThat(s, equalTo(""));
        verifyAll();
    }

    private void checkUnicode(String string, int group) {
        expect(regexMatcherFactory.createMatcher(eqPattern(LITERAL_ESCAPE_REGEX), eq(LINE))).andReturn(matcher);
        expect(matcher.find()).andReturn(true);
        expect(matcher.group(0)).andReturn(string);
        expect(matcher.group(group)).andReturn("0f");
        matcher.appendReplacement((StringBuffer) anyObject(), eq(new String(Character.toChars(15))));
        expect(matcher.find()).andReturn(false);
        matcher.appendTail((StringBuffer) anyObject());
        replayAll();
        String s = util.unescapeLiteral(LINE);
        assertThat(s, equalTo(""));
        verifyAll();
    }
}
