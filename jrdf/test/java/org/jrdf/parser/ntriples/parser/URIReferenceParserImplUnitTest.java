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
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.URIReference;
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
public class URIReferenceParserImplUnitTest {
    private static final Class<URIReferenceParser> TARGET_INTERFACE = URIReferenceParser.class;
    private static final Class<URIReferenceParserImpl> TEST_CLASS = URIReferenceParserImpl.class;
    private static final Class<?>[] PARAM_TYPES = new Class[]{GraphElementFactory.class, NTripleUtil.class};
    private static final String[] PARAMETER_NAMES = new String[]{"graphElementFactory", "nTripleUtil"};
    private static final String LINE = "string" + Math.random();
    private static final String ESCAPED_LINE = "escaped" + Math.random();
    @Mock private GraphElementFactory graphElementFactory;
    @Mock private URIReference uriReference;
    @Mock private NTripleUtil nTripleUtil;
    private URIReferenceParser uriReferenceParser;

    @Before public void create() {
        uriReferenceParser = new URIReferenceParserImpl(graphElementFactory, nTripleUtil);
    }

    @Test public void classProperties() {
        hasClassStandardProperties(TARGET_INTERFACE, TEST_CLASS, PARAM_TYPES, PARAMETER_NAMES);
    }

    @Test public void methodProperties() {
        checkMethodNullAndEmptyAssertions(uriReferenceParser, "parseURIReference", new ParameterDefinition(
            new String[]{"s"}, new Class[]{String.class}));
    }

    @Test public void createURIReference() throws Exception {
        expect(nTripleUtil.unescapeLiteral(LINE)).andReturn(ESCAPED_LINE);
        expect(graphElementFactory.createURIReference(URI.create(ESCAPED_LINE))).andReturn(uriReference);
        replayAll();
        URIReference actualURIReference = uriReferenceParser.parseURIReference(LINE);
        assertThat(actualURIReference, is(uriReference));
        verifyAll();
    }

    @Test public void createURIReferenceWithException() throws Exception {
        expect(nTripleUtil.unescapeLiteral(LINE)).andReturn(ESCAPED_LINE);
        expect(graphElementFactory.createURIReference(URI.create(ESCAPED_LINE))).andThrow(
            new GraphElementFactoryException(""));
        replayAll();
        checkException(LINE);
        verifyAll();
    }

    @Test public void baseURIThrowsException() throws Exception {
        expect(nTripleUtil.unescapeLiteral((String) anyObject())).andReturn("asd$#@:%!@#!");
        replayAll();
        checkException("asd$#@:%!@#!");
        verifyAll();
    }

    private void checkException(String line) {
        try {
            uriReferenceParser.parseURIReference(line);
            assertThat("Didn't throw parse exception", true);
        } catch (ParseException p) {
            assertThat(p.getMessage(), equalTo("Failed to create URI Reference: " + line));
            assertThat(p.getColumnNumber(), is(1));
        } catch (Throwable t) {
            assertThat("Should not throw exception: " + t.getClass(), true);
        }
    }
}
