/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.parser.ntriples.parser;

import junit.framework.TestCase;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.jrdf.util.boundary.PatternArgumentMatcher.eqPattern;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAndEmptyAssertions;
import static org.jrdf.util.test.ArgumentTestUtil.*;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ParameterDefinition;
import org.jrdf.util.test.ArgumentTestUtil;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.*;

import java.util.regex.Pattern;

public class RegexLiteralMatcherUnitTest extends TestCase {
    private static final Class<LiteralMatcher> TARGET_INTERFACE = LiteralMatcher.class;
    private static final Class<RegexLiteralMatcher> TEST_CLASS = RegexLiteralMatcher.class;
    private static final Class[] PARAM_TYPES = new Class[]{RegexMatcherFactory.class, NTripleUtil.class};
    private static final String[] PARAMETER_NAMES = new String[]{"regexFactory", "nTripleUtil"};
    private static final Pattern LANGUAGE_REGEX = Pattern.compile("\\\"([\\x20-\\x7E]*)\\\"" +
        "(" +
        "((\\@(\\p{Lower}+(\\-a-z0-9]+)*))|(\\^\\^\\<([\\x20-\\x7E]+)\\>))?" +
        ").*");
    private static final int LITERAL_INDEX = 1;
    private static final int LANGUAGE_INDEX = 5;
    private static final int DATATYPE_INDEX = 8;
    private static final String LINE = "line" + Math.random();
    private final MockFactory mockFactory = new MockFactory();
    private RegexMatcherFactory regexMatcherFactory;
    private RegexMatcher matcher;
    private LiteralMatcher parser;
    private NTripleUtil nTripleUtil;

    public void setUp() {
        regexMatcherFactory = mockFactory.createMock(RegexMatcherFactory.class);
        matcher = mockFactory.createMock(RegexMatcher.class);
        nTripleUtil = mockFactory.createMock(NTripleUtil.class);
        parser = new RegexLiteralMatcher(regexMatcherFactory, nTripleUtil);
    }

    public void testClassProperties() {
        checkClassFinal(TEST_CLASS);
        checkClassPublic(TEST_CLASS);
        checkImplementationOfInterface(TARGET_INTERFACE, TEST_CLASS);
        checkConstructNullAssertion(TEST_CLASS, PARAM_TYPES);
    }

    public void testMethodProperties() {
        checkMethodNullAndEmptyAssertions(parser, "matches", new ParameterDefinition(
            new String[]{"s"}, new Class[]{String.class}));
        checkMethodNullAndEmptyAssertions(parser, "parse", new ParameterDefinition(
            new String[]{"s"}, new Class[]{String.class}));
    }

    public void testMatches() {
        expect(regexMatcherFactory.createMatcher(eqPattern(LANGUAGE_REGEX), eq(LINE))).andReturn(matcher);
        expect(matcher.matches()).andReturn(true);
        mockFactory.replay();
        parser.matches(LINE);
        mockFactory.verify();
    }

    public void testParser() {
        expect(regexMatcherFactory.createMatcher(eqPattern(LANGUAGE_REGEX), eq(LINE))).andReturn(matcher);
        expect(matcher.matches()).andReturn(true);
        final String literal = "string" + Math.random();
        expect(matcher.group(LITERAL_INDEX)).andReturn(literal);
        expect(nTripleUtil.unescapeLiteral(literal)).andReturn("foo");
        expect(matcher.group(LANGUAGE_INDEX)).andReturn("bar");
        expect(matcher.group(DATATYPE_INDEX)).andReturn("baz");
        mockFactory.replay();
        String[] strings = parser.parse(LINE);
        assertEquals(3, strings.length);
        assertEquals("foo", strings[0]);
        assertEquals("bar", strings[1]);
        assertEquals("baz", strings[2]);
        mockFactory.verify();
    }
}