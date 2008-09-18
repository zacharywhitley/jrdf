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

package org.jrdf.restlet.client;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import junit.framework.TestCase;
import org.jrdf.PersistentGlobalJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactoryImpl;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.query.AnswerXMLWriter;
import static org.jrdf.query.AnswerXMLWriter.BINDING;
import static org.jrdf.query.AnswerXMLWriter.BNODE;
import static org.jrdf.query.AnswerXMLWriter.RESULT;
import static org.jrdf.query.AnswerXMLWriter.LITERAL;
import org.jrdf.restlet.server.Server;
import static org.jrdf.restlet.server.Server.PORT;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import static org.w3c.dom.Node.ELEMENT_NODE;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class DistributedQueryUnitTest extends TestCase {
    private static final String FOO = "foo";
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private PersistentGlobalJRDFFactory factory = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);

    private MoleculeGraph graph;
    private GraphElementFactory elementFactory;
    private Thread serverThread;
    private Server server;

    protected void setUp() throws Exception {
        super.setUp();
        factory = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
        graph = factory.getGraph(FOO);
        graph.clear();
        elementFactory = graph.getElementFactory();
        server = new Server();
        serverThread = new Thread(server);
        serverThread.start();
        System.err.println("Server started");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        graph.close();
        factory.close();
        server.stop();
        serverThread.join();
    }

    public void testGraphClient() throws GraphException, IOException, SAXException {
        final URIReference p = elementFactory.createURIReference(URI.create("urn:p"));
        final BlankNode b1 = elementFactory.createBlankNode();
        final BlankNode b2 = elementFactory.createBlankNode();
        graph.add(b1, p, b2);
        graph.add(b2, p, b1);
        assertEquals(2, graph.getNumberOfTriples());
        GraphClient client = new GraphClientImpl("127.0.0.1", PORT);
        client.constructPostQuery(FOO, "SELECT * WHERE { ?s <urn:p> ?o. }");
        String answer = client.processResponse();
        checkAnswerXML(answer, 2, b1.toString(), p.toString(), b2.toString());
    }                                            

    public void testEmptyDistributedClient() throws IOException, SAXException {
        String[] servers = new String[] {"127.0.0.1"};
        DistributedQueryClient client = new DistributedQueryClientImpl(servers);
        final String queryString = "SELECT * WHERE { ?s <urn:p> ?o. }";
        final String answer = client.postQuery(FOO, queryString);
        checkAnswerXML(answer, 0);
    }

    public void testDistributedClient() throws IOException, SAXException, GraphException {
        final URIReference p = elementFactory.createURIReference(URI.create("urn:p"));
        final BlankNode b1 = elementFactory.createBlankNode();
        final BlankNode b2 = elementFactory.createBlankNode();
        graph.add(b1, p, b2);
        graph.add(b2, p, b1);
        assertEquals(2, graph.getNumberOfTriples());
        String[] servers = new String[] {"127.0.0.1"};
        DistributedQueryClient client = new DistributedQueryClientImpl(servers);
        final String queryString = "SELECT * WHERE { ?s <urn:p> ?o. }";
        final String answer = client.postQuery(FOO, queryString);
        checkAnswerXML(answer, 2, b1.toString(), p.toString(), b2.toString());
    }

    private void checkAnswerXML(String answer, int resultSize, String... strings) throws SAXException, IOException {
        Set<String> set = new HashSet<String>();
        for (String s : strings) {
            set.add(s);
        }
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(answer)));
        Document document = parser.getDocument();
        final NodeList list = document.getElementsByTagName(RESULT);
        assertEquals("One answer", resultSize, list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            final Element node = (Element) list.item(i);
            final NodeList bindings = node.getElementsByTagName(BINDING);
            assertEquals("spo", 3, bindings.getLength());
            checkBindings(bindings, set);
        }
    }

    private void checkBindings(NodeList bindings, Set<String> set) {
        int size = 0;
        for (int j = 0; j < bindings.getLength(); j++) {
            final Node node1 = bindings.item(j);
            if (node1.getNodeType() == ELEMENT_NODE) {
                size += checkOneBinding(set, node1, BNODE);
                size += checkOneBinding(set, node1, AnswerXMLWriter.URI);
                size += checkOneBinding(set, node1, LITERAL);
            }
        }
        assertEquals("Same size", set.size(), size);
    }

    private int checkOneBinding(Set<String> set, Node node1, String tagName) {
        int size = 0;
        NodeList elementsByTagName = ((Element) node1).getElementsByTagName(tagName);
        for (int k = 0; k < elementsByTagName.getLength(); k++) {
            final Node node = elementsByTagName.item(k);
            if (node.getNodeType() == ELEMENT_NODE) {
                final String s = node.getTextContent();
                assertTrue("Contains string", set.contains(s));
                size++;
            }
        }
        return size;
    }
}
