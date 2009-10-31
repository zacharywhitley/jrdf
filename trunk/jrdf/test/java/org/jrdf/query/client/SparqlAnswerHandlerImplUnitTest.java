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
import org.jrdf.query.MediaTypeExtensions;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.SparqlAnswerFactory;
import org.jrdf.query.answer.SparqlParserFactory;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAndEmptyAssertions;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkClassFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkClassPublic;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterface;
import org.jrdf.util.test.ParameterDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.jrdf.util.test.MockTestUtil.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Request;
import org.restlet.resource.Representation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class SparqlAnswerHandlerImplUnitTest {
    private static final Class<?> TEST_CLASS = SparqlAnswerHandlerImpl.class;
    private static final Class<?> TARGET_INTERFACE = SparqlAnswerHandler.class;
    private static final Class[] PARAM_TYPES = {SparqlAnswerFactory.class, Map.class};
    @Mock private SparqlAnswerFactory mockAnswerFactory;
    @Mock private SparqlParserFactory mockParserFactory;
    private MediaType mediaType = MediaTypeExtensions.APPLICATION_SPARQL_XML;
    private SparqlAnswerHandler handler;

    @Before
    public void createHandler() {
        final Map<MediaType, SparqlParserFactory> map = new HashMap<MediaType, SparqlParserFactory>();
        map.put(mediaType, mockParserFactory);
        handler = new SparqlAnswerHandlerImpl(mockAnswerFactory, map);
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
        checkMethodNullAndEmptyAssertions(handler, "getAnswer", new ParameterDefinition(
            new String[]{"output"}, new Class[]{Representation.class}));
        checkMethodNullAndEmptyAssertions(handler, "setAcceptedMediaTypes",
            new ParameterDefinition(new String[]{"request"}, new Class[]{Request.class}));
    }

    @Test
    public void getAnswerReturnsAnswer() throws Exception {
        final Representation mockRepresentation = createMock(Representation.class);
        expect(mockRepresentation.getMediaType()).andReturn(mediaType);
        final InputStream mockInput = createMock(InputStream.class);
        expect(mockRepresentation.getStream()).andReturn(mockInput);
        final Answer mockAnswer = createMock(Answer.class);
        expect(mockAnswerFactory.createStreamingAnswer(mockInput, mockParserFactory)).andReturn(mockAnswer);
        replayAll();
        assertThat(handler.getAnswer(mockRepresentation), is(mockAnswer));
        verifyAll();
    }

    @Test(expected = RuntimeException.class)
    public void wrongMediaTypeThrowsException() throws Exception {
        final Representation mockRepresentation = createMock(Representation.class);
        expect(mockRepresentation.getMediaType()).andReturn(MediaType.ALL);
        replayAll();
        handler.getAnswer(mockRepresentation);
        verifyAll();
    }

    @Test(expected = RuntimeException.class)
    public void getStreamThrowsIOExceptionIsWrapped() throws Exception {
        final Representation mockRepresentation = createMock(Representation.class);
        expect(mockRepresentation.getMediaType()).andReturn(mediaType);
        expect(mockRepresentation.getStream()).andThrow(new IOException());
        replayAll();
        handler.getAnswer(mockRepresentation);
        verifyAll();
    }

    @Test
    public void setAcceptedMediaTypesSetsSparqlXml() throws Exception {
        final Request request = new Request();
        handler.setAcceptedMediaTypes(request);
        final List<Preference<MediaType>> preferenceList = request.getClientInfo().getAcceptedMediaTypes();
        assertThat(preferenceList.get(0).getMetadata(), equalTo(mediaType));
    }
}
