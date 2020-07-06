/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 *  ====================================================================
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
 */

package org.jrdf.parser.turtle.parser;

import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcher;
import static org.jrdf.util.boundary.PatternArgumentMatcher.eqPattern;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAssertions;
import org.jrdf.util.test.ParameterDefinition;
import static org.jrdf.util.test.StandardClassPropertiesTestUtil.hasClassStandardProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.eq;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.regex.Pattern
import org.jrdf.util.test.AssertThrows
import static org.jrdf.util.test.AssertThrows.assertThrows
import org.jrdf.util.test.AssertThrows.Block
import org.jrdf.util.boundary.RegexMatcherImpl
import java.util.regex.Matcher;

@RunWith(PowerMockRunner.class)
public class DirectiveParserImplUnitTest {

    private static final Class<?> TARGET_INTERFACE = DirectiveParser.class
    private static final Class<?> TEST_CLASS = DirectiveParserImpl.class
    private static final Class[] PARAM_TYPES = [RegexMatcherFactory.class, PrefixParser.class, BaseParser.class]
    private static final String[] PARAM_NAMES = ["regexFactory", "prefixParser"]
    private static final String PATTERN =  "\\p{Blank}*(@prefix.*)|(@base.*)|(@.*)"
    private static final Pattern DIRECTIVE_REGEX = Pattern.compile(PATTERN)
    private static final String VALID_PREFIX = "@prefix foo <http://foo> ."
    private static final String VALID_BASE = "@base <http://foo> ."
    private static final String INVALID_DIRECTIVE = "@justwrong ."
    private static final int PREFIX_GROUP = 1
    private static final int BASE_GROUP = 2
    @Mock private RegexMatcherFactory matcherFactory
    @Mock private PrefixParser prefixParser
    @Mock private BaseParser baseParser
    private DirectiveParser directiveParser
    private RegexMatcher matcher

    @Before
    def void create() {
        directiveParser = new DirectiveParserImpl(matcherFactory, prefixParser, baseParser)
    }

    @Test
    def void classProperties() {
        hasClassStandardProperties(TARGET_INTERFACE, TEST_CLASS, PARAM_TYPES, PARAM_NAMES)
    }

    @Test
    def void methodProperties() {
        checkMethodNullAssertions(directiveParser, "handleDirective",
                new ParameterDefinition(["line"] as String[], [CharSequence.class] as Class<?>[]))
    }

    @Test
    def void validPrefix() throws Exception {
        matcher = new RegexMatcherImpl(new Matcher(DIRECTIVE_REGEX, VALID_PREFIX))
        def prefixExpectations = {
            expect(prefixParser.handlePrefix(VALID_PREFIX)).andReturn(true)
        }
        checkValidDirective(prefixExpectations, VALID_PREFIX)
    }

    @Test
    def void validBase() throws Exception {
        matcher = new RegexMatcherImpl(new Matcher(DIRECTIVE_REGEX, VALID_BASE))
        def baseExpectations = {
            expect(baseParser.handleBase(VALID_BASE)).andReturn(true)
        }
        checkValidDirective(baseExpectations, VALID_BASE)
    }

    @Test
    def void illegalGroup() throws Exception {
        matcher = new RegexMatcherImpl(new Matcher(DIRECTIVE_REGEX, INVALID_DIRECTIVE))
        expect(matcherFactory.createMatcher(eqPattern(DIRECTIVE_REGEX), eq(INVALID_DIRECTIVE))).andReturn(matcher)
        replayAll()
        assertThrows(IllegalArgumentException.class, { directiveParser.handleDirective(INVALID_DIRECTIVE) })
        verifyAll()
    }

    def checkValidDirective(Closure matchedExpectations, CharSequence line) {
        expect(matcherFactory.createMatcher(eqPattern(DIRECTIVE_REGEX), eq(line))).andReturn(matcher)
        matchedExpectations()
        replayAll()
        final boolean matched = directiveParser.handleDirective(line)
        verifyAll()
        assertThat("Directive should have matched", matched)
    }
}
