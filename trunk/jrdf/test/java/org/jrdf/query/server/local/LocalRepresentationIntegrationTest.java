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

import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.MemoryJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactoryImpl;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;
import org.jrdf.query.server.SpringLocalServer;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import static org.jrdf.util.test.matcher.GraphNumberOfTriplesMatcher.hasNumberOfTriples;
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

import java.io.StringReader;
import java.net.URI;
import static java.net.URI.create;
import java.util.Arrays;

public class LocalRepresentationIntegrationTest {
    private static final String GRAPH = "foo";
    private static final URI LOCAL_SERVER_END_POINT = create("http://127.0.0.1:8182/graph/" + GRAPH);
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler("perstMoleculeGraph");
    private MoleculeGraph graph;
    private GraphElementFactory elementFactory;
    private Client client;
    private Reference ref;

    @Before
    public void setUp() throws Exception {
        HANDLER.removeDir();
        HANDLER.makeDir();
        PersistentGlobalJRDFFactory factory = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
        graph = factory.getGraph(GRAPH);
        elementFactory = graph.getElementFactory();
        SpringLocalServer localQueryServer = new SpringLocalServer();
        localQueryServer.start();
        client = new Client(LOCAL_SERVER_END_POINT.getScheme());
        ref = new Reference(LOCAL_SERVER_END_POINT.getScheme(), LOCAL_SERVER_END_POINT.getHost(),
            LOCAL_SERVER_END_POINT.getPort(), LOCAL_SERVER_END_POINT.getPath(), null, null);
    }

    @Test
    public void gettingRdfXml() throws Exception {
        addTriples();
        Request request = new Request(Method.GET, ref);
        ClientInfo clientInfo = request.getClientInfo();
        clientInfo.setAcceptedMediaTypes(Arrays.asList(new Preference<MediaType>(MediaType.APPLICATION_RDF_XML)));
        final Response response = client.handle(request);
        final Graph resultGraph = MemoryJRDFFactory.getFactory().getNewGraph();
        final StringReader reader = new StringReader(response.getEntity().getText());
        new GraphRdfXmlParser(resultGraph, new MemMapFactory()).parse(reader, "");
        assertThat(resultGraph, hasNumberOfTriples(3L));
    }

    private void addTriples() {
        final URIReference s = elementFactory.createURIReference(create("urn:p"));
        final URIReference p = elementFactory.createURIReference(create("urn:p"));
        graph.add(s, p, elementFactory.createBlankNode());
        graph.add(s, p, elementFactory.createBlankNode());
        graph.add(s, p, elementFactory.createBlankNode());
        assertThat(graph, hasNumberOfTriples(3L));
    }
}
