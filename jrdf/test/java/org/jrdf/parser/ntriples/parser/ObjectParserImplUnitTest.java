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
import static org.easymock.EasyMock.expect;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.URIReference;
import org.jrdf.parser.ParseException;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.test.ArgumentTestUtil;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ParameterDefinition;
import static org.jrdf.util.test.StandardClassPropertiesTestUtil.hasClassStandardProperties;

public class ObjectParserImplUnitTest extends TestCase {
    private static final Class<ObjectParser> TARGET_INTERFACE = ObjectParser.class;
    private static final Class<ObjectParserImpl> TEST_CLASS = ObjectParserImpl.class;
    private static final Class[] PARAM_TYPES = new Class[]{URIReferenceParser.class, BlankNodeParser.class,
        LiteralParser.class};
    private static final String[] PARAMETER_NAMES = new String[]{"uriReferenceParser", "blankNodeParser",
        "literalParser"};
    private static final String MATCHER = "match" + Math.random();
    private static final String LINE = "line" + Math.random();
    private final MockFactory mockFactory = new MockFactory();
    private URIReferenceParser uriReferenceParser;
    private BlankNodeParser blankNodeParser;
    private LiteralParser literalParser;
    private ObjectParser objectParser;
    private RegexMatcher regexMatcher;

    public void setUp() {
        uriReferenceParser = mockFactory.createMock(URIReferenceParser.class);
        blankNodeParser = mockFactory.createMock(BlankNodeParser.class);
        literalParser = mockFactory.createMock(LiteralParser.class);
        objectParser = new ObjectParserImpl(uriReferenceParser, blankNodeParser, literalParser);
        regexMatcher = mockFactory.createMock(RegexMatcher.class);
    }

    public void testClassProperties() {
        hasClassStandardProperties(TARGET_INTERFACE, TEST_CLASS, PARAM_TYPES, PARAMETER_NAMES);
    }

    public void testMethodProperties() {
        ArgumentTestUtil.checkMethodNullAssertions(objectParser, "parseObject", new ParameterDefinition(
            new String[]{"regexMatcher"}, new Class[]{RegexMatcher.class}));
    }

    public void testParseObjectURI() throws Exception {
        URIReference expectedUriReference = mockFactory.createMock(URIReference.class);
        expect(uriReferenceParser.parseURIReference(MATCHER)).andReturn(expectedUriReference);
        expect(regexMatcher.group(8)).andReturn(MATCHER).times(2);
        checkParse(expectedUriReference);
    }

    public void testParseBlankNode() throws Exception {
        BlankNode expectedBlankNode = mockFactory.createMock(BlankNode.class);
        expect(blankNodeParser.parseBlankNode(MATCHER)).andReturn(expectedBlankNode);
        expect(regexMatcher.group(8)).andReturn(null).times(1);
        expect(regexMatcher.group(9)).andReturn(MATCHER).times(2);
        checkParse(expectedBlankNode);
    }

    public void testParseLiteral() throws Exception {
        Literal expectedLiteral = mockFactory.createMock(Literal.class);
        expect(literalParser.parseLiteral(MATCHER)).andReturn(expectedLiteral);
        expect(regexMatcher.group(8)).andReturn(null).times(1);
        expect(regexMatcher.group(9)).andReturn(null).times(1);
        expect(regexMatcher.group(11)).andReturn(MATCHER).times(2);
        checkParse(expectedLiteral);
    }

    public void testDoesntParse() throws Exception {
        expect(regexMatcher.group(8)).andReturn(null).times(1);
        expect(regexMatcher.group(9)).andReturn(null).times(1);
        expect(regexMatcher.group(11)).andReturn(null).times(1);
        expect(regexMatcher.group(0)).andReturn(LINE).times(1);
        mockFactory.replay();
        checkThrowsException();
        mockFactory.verify();
    }

    private void checkParse(ObjectNode expectedUriReference) throws ParseException {
        mockFactory.replay();
        ObjectNode objectNode = objectParser.parseObject(regexMatcher);
        assertTrue(expectedUriReference == objectNode);
        mockFactory.verify();
    }

    private void checkThrowsException() {
        try {
            objectParser.parseObject(regexMatcher);
            fail("Didn't throw parse exception");
        } catch (ParseException p) {
            assertEquals("Failed to parse line: " + LINE, p.getMessage());
            assertEquals(1, p.getColumnNumber());
        } catch (Throwable t) {
            fail("Should not throw exception: " + t.getClass() + " msg: " + t.getMessage());
        }
    }
}
