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

package org.jrdf.query.server.local;

import org.jrdf.MemoryJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactoryImpl;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;
import org.jrdf.query.server.RdfXmlRepresentationFactory;
import org.jrdf.query.server.SpringLocalServer;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.net.URI.create;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import static org.jrdf.query.server.GraphRepresentationParameters.GRAPH_RETURNED;
import static org.jrdf.util.test.matcher.GraphNumberOfTriplesMatcher.hasNumberOfTriples;
import static org.restlet.data.MediaType.APPLICATION_RDF_XML;
import static org.restlet.data.Method.DELETE;
import static org.restlet.data.Method.GET;
import static org.restlet.data.Method.POST;
import static org.restlet.data.Method.PUT;
import static org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST;
import static org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND;
import static org.restlet.data.Status.SUCCESS_CREATED;
import static org.restlet.data.Status.SUCCESS_OK;

public class LocalRepresentationIntegrationTest {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler("perstMoleculeGraph");
    private static final PersistentGlobalJRDFFactory FACTORY = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
    private static final Graph EMPTY_GRAPH = MemoryJRDFFactory.getFactory().getGraph();
    private SpringLocalServer localQueryServer;
    private Client client;

    @Before
    public void setUp() throws Exception {
        HANDLER.removeDir();
        HANDLER.makeDir();
        FACTORY.refresh();
        localQueryServer = new SpringLocalServer();
        localQueryServer.start();
        client = new Client("http");
    }

    @After
    public void tearDown() throws Exception {
        localQueryServer.stop();
    }

    @Test
    public void getNonExistentGraphNotFound() throws Exception {
        final Response response = getGraph(currentTimeMillis() + "");
        assertThat(response.getStatus(), equalTo(CLIENT_ERROR_NOT_FOUND));
    }

    @Test
    public void getExistingRdfXmlGraph() throws Exception {
        final String graphName = "graph" + currentTimeMillis();
        final Graph graph = FACTORY.getGraph(graphName);
        addTriples(graph);
        assertSameGraph(graphName, graph);
    }

    @Test
    public void putOnceRdfXmlTriples() throws Exception {
        final Graph graphToPut = putTriplesToGraph("http://www.example.org/abc");
        assertSameGraph("http://www.example.org/abc", graphToPut);
    }

    @Test
    public void putTwiceRdfXmlTriplesReplacesTriples() throws Exception {
        putTriplesToGraph("http://www.example.org/abc");
        final Graph graphToPut = putTriplesToGraph("http://www.example.org/abc");
        assertSameGraph("http://www.example.org/abc", graphToPut);
    }

    @Test
    public void postRdfXmlTriplesAddsTriples() throws Exception {
        final Graph graph1 = postTriplesToGraph("http://www.example.org/def");
        final Graph graph2 = postTriplesToGraph("http://www.example.org/def");
        for (Triple triple : graph2.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE)) {
            graph1.add(triple);
        }
        assertSameGraph("http://www.example.org/def", graph1);
    }

    @Test
    public void deleteGraphRemovesAllTriples() throws Exception {
        postTriplesToGraph("http://www.example.org/def");
        Request request = new Request(DELETE, createRef("http://www.example.org/def"));
        Response response = client.handle(request);
        assertThat(response.getStatus(), equalTo(SUCCESS_OK));
        assertSameGraph("http://www.example.org/def", EMPTY_GRAPH);
    }

    @Test
    public void deleteNonExistentGraphIsBadRequest() throws Exception {
        Request request = new Request(DELETE, createRef("http://www.example.org/def"));
        Response response = client.handle(request);
        assertThat(response.getStatus(), equalTo(CLIENT_ERROR_BAD_REQUEST));
    }

    private Graph putTriplesToGraph(final String graphName) throws Exception {
        return addTriplesToGraph(PUT, graphName);
    }

    private Graph postTriplesToGraph(final String graphName) throws Exception {
        return addTriplesToGraph(POST, graphName);
    }

    private Graph addTriplesToGraph(final Method method, final String graphName) throws Exception {
        final Graph graph = MemoryJRDFFactory.getFactory().getGraph();
        addTriples(graph);
        addToGraph(method, graphName, graph);
        return graph;
    }

    private void addTriples(final Graph graph) {
        final TripleFactory tripleFactory = graph.getTripleFactory();
        long oId = currentTimeMillis();
        tripleFactory.addTriple(create("urn:s"), create("urn:p"), create("urn:o" + ++oId));
        tripleFactory.addTriple(create("urn:s"), create("urn:p"), create("urn:o" + ++oId));
        tripleFactory.addTriple(create("urn:s"), create("urn:p"), create("urn:o" + ++oId));
        assertThat(graph, hasNumberOfTriples(3L));
    }

    private Response getGraph(String graphName) throws UnsupportedEncodingException {
        Request request = new Request(GET, createRef(graphName));
        ClientInfo clientInfo = request.getClientInfo();
        clientInfo.setAcceptedMediaTypes(Arrays.asList(new Preference<MediaType>(APPLICATION_RDF_XML)));
        return client.handle(request);
    }

    private void assertSameGraph(final String graphName, final Graph expectedGraph) throws Exception {
        Response response = getGraph(graphName);
        final Graph resultGraph = parseResult(response);
        assertThat(resultGraph.getNumberOfTriples(), equalTo(expectedGraph.getNumberOfTriples()));
        for (Triple triple : expectedGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE)) {
            assertThat("Result Graph should contain: " + triple, resultGraph.contains(triple));
        }
    }

    private void addToGraph(final Method method, final String graphName, final Graph graph) throws Exception {
        Request request = new Request(method, createRef(graphName), createGraphRepresentation(graph));
        Response response = client.handle(request);
        assertThat(response.getStatus(), equalTo(SUCCESS_CREATED));
    }

    private Reference createRef(final String graphName) throws UnsupportedEncodingException {
        return new Reference("http://127.0.0.1:8182/graph/" + URLEncoder.encode(graphName, "UTF-8"));
    }

    // Use an internal JRDF/Restlet way of creating a representation of a graph.
    private Representation createGraphRepresentation(Graph graph) {
        final Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put(GRAPH_RETURNED.toString(), graph);
        return new RdfXmlRepresentationFactory().createRepresentation(APPLICATION_RDF_XML, dataModel);
    }

    private Graph parseResult(Response response) throws Exception {
        final Graph resultGraph = MemoryJRDFFactory.getFactory().getGraph();
        final StringReader reader = new StringReader(response.getEntity().getText());
        new GraphRdfXmlParser(resultGraph, new MemMapFactory()).parse(reader, "");
        return resultGraph;
    }
}
