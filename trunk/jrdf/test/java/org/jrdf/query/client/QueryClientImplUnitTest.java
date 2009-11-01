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
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.PositionName;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAssertions;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.assertThrows;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.MockTestUtil.createMock;
import org.jrdf.util.test.ParameterDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.createMockAndExpectNew;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Request;

import java.lang.reflect.Modifier;
import java.net.URI;
import static java.net.URI.create;
import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest(QueryClientImpl.class)
public class QueryClientImplUnitTest {
    private static final URI TEST_END_POINT_URI = create("http://localhost:8182/graphs/bar");
    @Mock
    private SparqlAnswerHandler answerHandler;
    private QueryClient queryClient;

    @Before
    public void createClient() {
        URI mockUri = createMock(URI.class);
        queryClient = new QueryClientImpl(mockUri, answerHandler);
    }

    @Test
    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(AttributeName.class, PositionName.class);
        checkConstructor(QueryClientImpl.class, Modifier.PUBLIC, URI.class, SparqlAnswerHandler.class);
    }

    @Test
    public void methodProperties() {
        checkMethodNullAssertions(queryClient, "setQuery", new ParameterDefinition(
            new String[]{"sparqlQuery", "queryExtensions"}, new Class[]{String.class, Map.class}));
        checkMethodNullAssertions(queryClient, "executeQuery", new ParameterDefinition(
            new String[]{"sparqlQuery", "queryExtensions"}, new Class[]{String.class, Map.class}));
        checkMethodNullAssertions(queryClient, "executeQuery", new ParameterDefinition(new String[]{},
            new Class<?>[]{}));
    }

    @Test
    public void setQueryCreatesRequest() throws Exception {
        final Reference ref = createMockAndExpectNew(Reference.class, TEST_END_POINT_URI.getScheme(),
            TEST_END_POINT_URI.getHost(), TEST_END_POINT_URI.getPort(), TEST_END_POINT_URI.getPath(), null,
            null);
        expect(ref.addQueryParameter("query", "select *")).andReturn(ref);
        final Request req = createMockAndExpectNew(Request.class, Method.GET, ref);
        answerHandler.setAcceptedMediaTypes(req);
        expectLastCall();
        replayAll();
        queryClient = new QueryClientImpl(TEST_END_POINT_URI, answerHandler);
        queryClient.setQuery("select *", new HashMap<String, String>());
        verifyAll();
    }

    @Test
    public void testBadUri() throws Exception {
        assertThrows(IllegalArgumentException.class, "The SPARQL end point must have a scheme.  Given URI: ",
            new AssertThrows.Block() {
                public void execute() throws Throwable {
                    final QueryClient client = new QueryClientImpl(new URI(""), answerHandler);
                    client.executeQuery("select *", new HashMap<String, String>());
                }
            });
    }

    @Test
    public void executeQueryFailsWhenNotSent() throws Exception {
        assertThrows(IllegalStateException.class, "No query to execute, call setQuery first", new AssertThrows.Block() {
            public void execute() throws Throwable {
                queryClient.executeQuery();
            }
        });
    }
}
