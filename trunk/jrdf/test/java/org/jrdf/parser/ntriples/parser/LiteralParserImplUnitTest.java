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

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Literal;
import org.jrdf.parser.ParseException;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAndEmptyAssertions;
import org.jrdf.util.test.ParameterDefinition;
import static org.jrdf.util.test.StandardClassPropertiesTestUtil.hasClassStandardProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.api.easymock.powermocklistener.AnnotationEnabler;
import org.powermock.core.classloader.annotations.Mock;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

@RunWith(PowerMockRunner.class)
@PowerMockListener(AnnotationEnabler.class)
public class LiteralParserImplUnitTest {
    private static final Class<LiteralParser> TARGET_INTERFACE = LiteralParser.class;
    private static final Class<LiteralParserImpl> TEST_CLASS = LiteralParserImpl.class;
    private static final Class[] PARAM_TYPES = new Class[]{GraphElementFactory.class, LiteralMatcher.class};
    private static final String[] PARAMETER_NAMES = new String[]{"graphElementFactory", "literalMatcher"};
    private static final String LINE = "line" + Math.random();
    private static final String LITERAL = "unescapedLiteral" + Math.random();
    private static final String LANGUAGE = "language" + Math.random();
    private static final URI DATATYPE = URI.create("datatype" + Math.random());
    private LiteralParser parser;
    @Mock private GraphElementFactory elementFactory;
    @Mock private Literal expectedLiteral;
    @Mock private LiteralMatcher literalMatcher;

    @Before public void create() {
        parser = new LiteralParserImpl(elementFactory, literalMatcher);
    }

    @Test public void classProperties() {
        hasClassStandardProperties(TARGET_INTERFACE, TEST_CLASS, PARAM_TYPES, PARAMETER_NAMES);
    }

    @Test public void methodProperties() {
        checkMethodNullAndEmptyAssertions(parser, "parseLiteral", new ParameterDefinition(
            new String[]{"s"}, new Class[]{String.class}));
    }

    @Test public void parseLiteralWithException() throws Exception {
        expect(literalMatcher.parse(LINE)).andReturn(new String[3]);
        replayAll();
        checkException(LINE, "Didn't find a matching literal: [" + LINE + "]");
        verifyAll();
    }

    @Test public void parsePlainLiteral() throws Exception {
        final String line = parserExpectations(LITERAL, null, null);
        expect(elementFactory.createLiteral(LITERAL)).andReturn(expectedLiteral);
        replayAll();
        Literal actualLiteral = parser.parseLiteral(line);
        assertThat(actualLiteral, is(expectedLiteral));
        verifyAll();
    }

    @Test public void parseLanguagesLiteral() throws Exception {
        final String line = parserExpectations(LITERAL, LANGUAGE, null);
        expect(elementFactory.createLiteral(LITERAL, LANGUAGE)).andReturn(expectedLiteral);
        replayAll();
        Literal actualLiteral = parser.parseLiteral(line);
        assertThat(actualLiteral, is(expectedLiteral));
        verifyAll();
    }

    @Test public void testParseDatatypeLiteral() throws Exception {
        final String line = parserExpectations(LITERAL, null, DATATYPE.toString());
        expect(elementFactory.createLiteral(LITERAL, DATATYPE)).andReturn(expectedLiteral);
        replayAll();
        Literal actualLiteral = parser.parseLiteral(line);
        assertThat(actualLiteral, is(expectedLiteral));
        verifyAll();
    }

    @Test public void parseThrowsExceptionWithBothLanguageAndDatatypeLiteral() throws Exception {
        final String line = parserExpectations(LITERAL, LANGUAGE, DATATYPE.toString());
        replayAll();
        checkException(line, "Cannot create a literal with both language and data type from line: " + line);
        verifyAll();
    }

    @Test public void handlesGraphElementFactoryExceptionWhenCreatingPlainLiteal() throws Exception {
        final String line = parserExpectations(LITERAL, null, null);
        expect(elementFactory.createLiteral(LITERAL)).andThrow(new GraphElementFactoryException("foo"));
        replayAll();
        checkException(line, "Failed to create literal from line: " + line);
        verifyAll();
    }

    @Test public void handlesGraphElementFactoryExceptionWhenCreatingLanguageLiteal() throws Exception {
        final String line = parserExpectations(LITERAL, LANGUAGE, null);
        expect(elementFactory.createLiteral(LITERAL, LANGUAGE)).andThrow(new GraphElementFactoryException("foo"));
        replayAll();
        checkException(line, "Failed to create literal from line: " + line);
        verifyAll();
    }

    @Test public void handlesGraphElementFactoryExceptionWhenCreatingDatatypeLiteal() throws Exception {
        final String line = parserExpectations(LITERAL, null, DATATYPE.toString());
        expect(elementFactory.createLiteral(LITERAL, DATATYPE)).andThrow(new GraphElementFactoryException("foo"));
        replayAll();
        checkException(line, "Failed to create literal from line: " + line);
        verifyAll();
    }

    private String parserExpectations(String literal, String language, String datatype) {
        final String line = "string" + Math.random();
        expect(literalMatcher.parse(line)).andReturn(new String[]{literal, language, datatype});
        return line;
    }

    private void checkException(String line, String msg) {
        try {
            parser.parseLiteral(line);
            assertThat("Didn't throw parse exception", true);
        } catch (ParseException p) {
            assertThat(p.getMessage(), equalTo(msg));
            assertThat(p.getColumnNumber(), is(1));
        } catch (Throwable t) {
            assertThat("Should not throw exception: " + t.getClass() + " msg: " + t.getMessage(), true);
        }
    }
}
