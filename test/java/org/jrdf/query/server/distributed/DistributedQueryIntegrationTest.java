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

package org.jrdf.query.server.distributed;

import junit.framework.TestCase;
import org.jrdf.PersistentGlobalJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactoryImpl;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.AskAnswer;
import org.jrdf.query.answer.xml.TypeValue;
import org.jrdf.query.client.CallableGraphQueryClient;
import org.jrdf.query.client.QueryClient;
import org.jrdf.query.client.QueryClientImpl;
import org.jrdf.query.server.SpringDistributedServer;
import org.jrdf.query.server.SpringLocalServer;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import static org.jrdf.util.test.SetUtil.asSet;
import org.restlet.Client;
import org.restlet.data.Method;
import static org.restlet.data.Protocol.HTTP;
import org.restlet.data.Request;
import org.restlet.data.Response;
import static org.restlet.data.Status.SUCCESS_OK;
import org.restlet.resource.StringRepresentation;

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */
public class DistributedQueryIntegrationTest extends TestCase {
    private static final String FOO = "foo";
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler("perstMoleculeGraph");
    private static final PersistentGlobalJRDFFactory FACTORY = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
    private static final String SELECT_QUERY_STRING = "SELECT * WHERE { ?s ?p ?o. }";
    private static final String ASK_QUERY_STRING = "ASK WHERE { ?s ?p ?o. }";
    private MoleculeGraph graph;
    private GraphElementFactory elementFactory;
    private SpringLocalServer localQueryServer;
    private SpringDistributedServer distributedServer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HANDLER.removeDir();
        HANDLER.makeDir();
        graph = FACTORY.getGraph(FOO);
        graph.clear();
        elementFactory = graph.getElementFactory();
        localQueryServer = new SpringLocalServer();
        localQueryServer.start();
        distributedServer = new SpringDistributedServer();
        distributedServer.start();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        graph.close();
        FACTORY.close();
        localQueryServer.stop();
        distributedServer.stop();
    }

    public void testDistributedServerGraphsResource() throws Exception {
        URL url = new URL(HTTP.getSchemeName(), "127.0.0.1", 8183, "/graphs");
        Request request = new Request(Method.GET, url.toString(), new StringRepresentation(""));
        Client client = new Client(HTTP);
        final Response response = client.handle(request);
        assertEquals(SUCCESS_OK, response.getStatus());
        assertNotNull(response);
    }

    public void testEmptyDistributedClient() throws Exception {
        assertEquals(0, graph.getNumberOfTriples());
        DistributedServerClient serverClient = new DistributedServerClient("127.0.0.1:8183");
        serverClient.postDistributedServer("add", "127.0.0.1");
        QueryClient client = new QueryClientImpl("127.0.0.1:8183");
        client.getQuery(FOO, SELECT_QUERY_STRING, "all");
        final Answer answer = client.executeQuery();
        checkAnswer(answer, 0, Collections.<String>emptySet());
    }

    public void testGraphClient() throws Exception {
        final URIReference p = elementFactory.createURIReference(URI.create("urn:p"));
        final BlankNode b1 = elementFactory.createBlankNode();
        final BlankNode b2 = elementFactory.createBlankNode();
        graph.add(b1, p, b1);
        graph.add(b2, p, b2);
        assertEquals(2, graph.getNumberOfTriples());
        CallableGraphQueryClient queryClient = new QueryClientImpl("127.0.0.1:8182");
        queryClient.getQuery(FOO, SELECT_QUERY_STRING, "all");
        Answer answer = queryClient.executeQuery();
        checkAnswer(answer, 2, asSet("s", "p", "o"));
    }

    public void testDistributedClient() throws Exception {
        final URIReference p = elementFactory.createURIReference(URI.create("urn:p"));
        final BlankNode b1 = elementFactory.createBlankNode();
        final BlankNode b2 = elementFactory.createBlankNode();
        graph.add(b1, p, b2);
        graph.add(b2, p, b1);
        assertEquals(2, graph.getNumberOfTriples());
        DistributedServerClient serverClient = new DistributedServerClient("127.0.0.1:8183");
        serverClient.postDistributedServer("add", "127.0.0.1");
        QueryClient client = new QueryClientImpl("127.0.0.1:8183");
        client.getQuery(FOO, SELECT_QUERY_STRING, "all");
        Answer answer = client.executeQuery();
        checkAnswer(answer, 2, asSet("s", "p", "o"));
    }

    public void testLocalClientAskQuery() throws Exception {
        assertEquals(0, graph.getNumberOfTriples());
        QueryClient client = new QueryClientImpl("127.0.0.1:8182");
        client.getQuery(FOO, ASK_QUERY_STRING, "all");
        AskAnswer answer = (AskAnswer) client.executeQuery();
        assertEquals(false, answer.getResult());
    }

    public void testDistributedClientEmptyAskQuery() throws Exception {
        assertEquals(0, graph.getNumberOfTriples());
        DistributedServerClient serverClient = new DistributedServerClient("127.0.0.1:8183");
        serverClient.postDistributedServer("add", "127.0.0.1");
        QueryClient client = new QueryClientImpl("127.0.0.1:8183");
        client.getQuery(FOO, ASK_QUERY_STRING, "all");
        AskAnswer answer = (AskAnswer) client.executeQuery();
        assertEquals(false, answer.getResult());
    }

    public void testDistributedClientAskQuery() throws Exception {
        final URIReference p = elementFactory.createURIReference(URI.create("urn:p"));
        final BlankNode b1 = elementFactory.createBlankNode();
        final BlankNode b2 = elementFactory.createBlankNode();
        graph.add(b1, p, b2);
        graph.add(b2, p, b1);
        assertEquals(2, graph.getNumberOfTriples());
        DistributedServerClient serverClient = new DistributedServerClient("127.0.0.1:8183");
        serverClient.postDistributedServer("add", "127.0.0.1");
        QueryClient client = new QueryClientImpl("127.0.0.1:8183");
        client.getQuery(FOO, ASK_QUERY_STRING, "all");
        AskAnswer answer = (AskAnswer) client.executeQuery();
        assertEquals(true, answer.getResult());
    }

    private void checkAnswer(Answer answer, int noResults, Set<String> expectedVariableNames) throws Exception {
        Set<String> actualVariableNames = asSet(answer.getVariableNames());
        assertEquals(expectedVariableNames, actualVariableNames);
        Iterator<TypeValue[]> iterator = answer.columnValuesIterator();
        int counter = 0;
        while (iterator.hasNext()) {
            counter++;
            iterator.next();
        }
        assertEquals(noResults, counter);
    }
}
