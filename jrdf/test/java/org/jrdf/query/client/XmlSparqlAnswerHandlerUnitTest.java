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

package org.jrdf.query.client;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.query.MediaTypeExtensions.APPLICATION_SPARQL_XML;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.SparqlAnswerFactory;
import org.jrdf.query.answer.SparqlParser;
import org.jrdf.query.answer.xml.parser.SparqlXmlParserImpl;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAndEmptyAssertions;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAssertions;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkClassPublic;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterface;
import org.jrdf.util.test.ParameterDefinition;
import static org.jrdf.util.test.ReflectTestUtil.insertFieldValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Request;
import org.restlet.resource.Representation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(XmlSparqlAnswerHandler.class)
public class XmlSparqlAnswerHandlerUnitTest {
    private static final Class<?> TEST_CLASS = XmlSparqlAnswerHandler.class;
    private static final Class<?> TARGET_INTERFACE = SparqlAnswerHandler.class;
    private static final Class[] PARAM_TYPES = {};
    private SparqlAnswerHandler handler = new XmlSparqlAnswerHandler();

    @Test
    public void classProperties() {
        //checkClassFinal(TEST_CLASS);
        checkClassPublic(TEST_CLASS);
        checkImplementationOfInterface(TARGET_INTERFACE, TEST_CLASS);
        checkConstructNullAssertion(TEST_CLASS, PARAM_TYPES);
    }

    @Test
    public void methodProperties() {
        checkMethodNullAssertions(handler, "getParser", new ParameterDefinition(
            new String[]{"inputStream"}, new Class[]{InputStream.class}));
        checkMethodNullAndEmptyAssertions(handler, "getAnswer", new ParameterDefinition(
            new String[]{"output"}, new Class[]{Representation.class}));
        checkMethodNullAndEmptyAssertions(handler, "setAcceptedMediaTypes",
            new ParameterDefinition(new String[]{"request"}, new Class[]{Request.class}));
    }

    @Test
    public void getParserCreatesNewParser() throws Exception {
        final SparqlParser mockParser = createMock(SparqlXmlParserImpl.class);
        final InputStream mockInput = createMock(InputStream.class);
        expectNew(SparqlXmlParserImpl.class, mockInput).andReturn((SparqlXmlParserImpl) mockParser);
        final SparqlAnswerHandler tested = new XmlSparqlAnswerHandler();
        replayAll();
        assertThat(tested.getParser(mockInput), is(mockParser));
        verifyAll();
    }

    @Test
    public void getAnswerReturnsAnswer() throws Exception {
        final SparqlAnswerHandler tested = new XmlSparqlAnswerHandler();
        final SparqlAnswerFactory mockFactory = createMock(SparqlAnswerFactory.class);
        insertFieldValue(tested, "SPARQL_ANSWER_STREAMING_FACTORY", mockFactory);
        final Representation mockRepresentation = createMock(Representation.class);
        final InputStream mockInput = createMock(InputStream.class);
        expect(mockRepresentation.getStream()).andReturn(mockInput);
        final Answer mockAnswer = createMock(Answer.class);
        expect(mockFactory.createStreamingXmlAnswer(mockInput)).andReturn(mockAnswer);
        replayAll();
        assertThat(tested.getAnswer(mockRepresentation), is(mockAnswer));
        verifyAll();
    }

    @Test(expected = RuntimeException.class)
    public void getAnswerThrowsException() throws Exception {
        final SparqlAnswerHandler tested = new XmlSparqlAnswerHandler();
        final SparqlAnswerFactory mockFactory = createMock(SparqlAnswerFactory.class);
        final Representation mockRepresentation = createMock(Representation.class);
        expect(mockRepresentation.getStream()).andThrow(new IOException());
        insertFieldValue(tested, "SPARQL_ANSWER_STREAMING_FACTORY", mockFactory);
        replayAll();
        tested.getAnswer(mockRepresentation);
        verifyAll();
    }

    @Test
    public void setAcceptedMediaTypesSetsSparqlXml() throws Exception {
        final Request request = new Request();
        final SparqlAnswerHandler tested = new XmlSparqlAnswerHandler();
        tested.setAcceptedMediaTypes(request);
        final List<Preference<MediaType>> preferenceList = request.getClientInfo().getAcceptedMediaTypes();
        assertThat(preferenceList.get(0).getMetadata(), equalTo(APPLICATION_SPARQL_XML));
    }
}
