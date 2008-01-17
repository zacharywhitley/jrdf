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
import static org.easymock.EasyMock.expect;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.URIReference;
import org.jrdf.parser.ParseException;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAndEmptyAssertions;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ParameterDefinition;
import static org.jrdf.util.test.StandardClassPropertiesTestUtil.hasClassStandardProperties;

import java.net.URI;

public class URIReferenceParserImplUnitTest extends TestCase {
    private static final Class<URIReferenceParser> TARGET_INTERFACE = URIReferenceParser.class;
    private static final Class<URIReferenceParserImpl> TEST_CLASS = URIReferenceParserImpl.class;
    private static final Class[] PARAM_TYPES = new Class[]{GraphElementFactory.class, NTripleUtil.class};
    private static final String[] PARAMETER_NAMES = new String[]{"graphElementFactory", "nTripleUtil"};
    private static final String LINE = "string" + Math.random();
    private static final String ESCAPED_LINE = "escaped" + Math.random();
    private final MockFactory mockFactory = new MockFactory();
    private URIReferenceParser uriReferenceParser;
    private GraphElementFactory graphElementFactory;
    private URIReference uriReference;
    private NTripleUtil nTripleUtil;

    public void setUp() {
        graphElementFactory = mockFactory.createMock(GraphElementFactory.class);
        uriReference = mockFactory.createMock(URIReference.class);
        nTripleUtil = mockFactory.createMock(NTripleUtil.class);
        uriReferenceParser = new URIReferenceParserImpl(graphElementFactory, nTripleUtil);
    }

    public void testClassProperties() {
        hasClassStandardProperties(TARGET_INTERFACE, TEST_CLASS, PARAM_TYPES, PARAMETER_NAMES);
    }

    public void testMethodProperties() {
        checkMethodNullAndEmptyAssertions(uriReferenceParser, "parseURIReference", new ParameterDefinition(
            new String[]{"s"}, new Class[]{String.class}));
    }

    public void testCreateURIReference() throws Exception {
        expect(nTripleUtil.unescapeLiteral(LINE)).andReturn(ESCAPED_LINE);
        expect(graphElementFactory.createURIReference(URI.create(ESCAPED_LINE))).andReturn(uriReference);
        mockFactory.replay();
        URIReference actualURIReference = uriReferenceParser.parseURIReference(LINE);
        assertTrue(uriReference == actualURIReference);
        mockFactory.verify();
    }

    public void testCreateURIReferenceWithException() throws Exception {
        expect(nTripleUtil.unescapeLiteral(LINE)).andReturn(ESCAPED_LINE);
        expect(graphElementFactory.createURIReference(URI.create(ESCAPED_LINE))).andThrow(
            new GraphElementFactoryException(""));
        mockFactory.replay();
        checkThrowsException(LINE);
        mockFactory.verify();
        mockFactory.reset();
    }

    public void testBaseURIThrowsException() throws Exception {
        expect(nTripleUtil.unescapeLiteral((String) anyObject())).andReturn("asd$#@:%!@#!");
        mockFactory.replay();
        checkThrowsException("asd$#@:%!@#!");
    }

    private void checkThrowsException(String line) {
        try {
            uriReferenceParser.parseURIReference(line);
            fail("Didn't throw parse exception");
        } catch (ParseException p) {
            assertEquals("Failed to create URI Reference: " + line, p.getMessage());
            assertEquals(1, p.getColumnNumber());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Should not throw exception: " + t.getClass());
        }
    }
}
