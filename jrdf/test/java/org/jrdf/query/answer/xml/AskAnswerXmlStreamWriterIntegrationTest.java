/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.query.answer.xml;

import com.ctc.wstx.api.WstxInputProperties;
import junit.framework.TestCase;
import org.jrdf.PersistentGlobalJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactoryImpl;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.answer.AskAnswer;
import org.jrdf.query.answer.SparqlProtocol;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;

import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import static javax.xml.stream.events.XMLEvent.CHARACTERS;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */
public class AskAnswerXmlStreamWriterIntegrationTest extends TestCase {
    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
    {
        XML_INPUT_FACTORY.setProperty(WstxInputProperties.P_INPUT_PARSING_MODE,
            WstxInputProperties.PARSING_MODE_FRAGMENT);
    }
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final PersistentGlobalJRDFFactory FACTORY = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
    private static final UrqlConnection URQL_CONNECTION = TestJRDFFactory.getFactory().getNewUrqlConnection();

    private Writer writer;
    private XMLStreamReader reader;
    private AnswerXmlWriter xmlWriter;
    private MoleculeGraph graph;
    private GraphElementFactory elementFactory;
    private BlankNode b1;
    private BlankNode b2;
    private BlankNode b3;
    private URIReference p1;
    private URIReference p2;
    private URIReference p3;
    private Literal l1;
    private Literal l2;
    private Literal l3;

    public void setUp() throws Exception {
        super.setUp();
        HANDLER.removeDir();
        HANDLER.makeDir();
        FACTORY.refresh();
        writer = new StringWriter();
        graph = FACTORY.getNewGraph("foo");
        elementFactory = graph.getElementFactory();
        b1 = elementFactory.createBlankNode();
        b2 = elementFactory.createBlankNode();
        b3 = elementFactory.createBlankNode();
        p1 = elementFactory.createURIReference(URI.create("urn:p1"));
        p2 = elementFactory.createURIReference(URI.create("urn:p2"));
        p3 = elementFactory.createURIReference(URI.create("urn:p3"));
        l1 = elementFactory.createLiteral("l1");
        l2 = elementFactory.createLiteral("l2");
        l3 = elementFactory.createLiteral("l3");
    }

    public void tearDown() throws Exception {
        super.tearDown();
        graph.close();
        if (xmlWriter != null) {
            xmlWriter.close();
        }
        HANDLER.removeDir();
    }

    public void testAskAnswer() throws GraphException, InvalidQuerySyntaxException, XMLStreamException {
        graph.add(b1, p1, l1);
        graph.add(b2, p2, l2);
        graph.add(b3, p3, l3);
        String queryString = "ASK WHERE {?s ?p ?o .}";
        final AskAnswer answer = (AskAnswer) URQL_CONNECTION.executeQuery(graph, queryString);
        xmlWriter = new AskAnswerXmlStreamWriter(writer, answer);
        checkResult(true);
    }

    public void testAskEmptyGraph() throws XMLStreamException, InvalidQuerySyntaxException, GraphException {
        String queryString = "ASK WHERE {?s ?p ?o .}";
        final AskAnswer answer = (AskAnswer) URQL_CONNECTION.executeQuery(graph, queryString);
        xmlWriter = new AskAnswerXmlStreamWriter(writer, answer);
        checkResult(false);
    }

    public void testAskNonMatchingGraph() throws GraphException, XMLStreamException, InvalidQuerySyntaxException {
        graph.add(b1, p1, l1);
        String queryString = "ASK WHERE {?s ?p ?o FILTER ( str(?o) = \"ab\" ) }";
        final AskAnswer answer = (AskAnswer) URQL_CONNECTION.executeQuery(graph, queryString);
        xmlWriter = new AskAnswerXmlStreamWriter(writer, answer);
        checkResult(false);
    }

    private void checkResult(boolean value) throws XMLStreamException {
        assertTrue(xmlWriter.hasMoreResults());
        xmlWriter.writeResult();
        String result = writer.toString();
        reader = XML_INPUT_FACTORY.createXMLStreamReader(new StringReader(result));
        while (reader.hasNext()) {
            int eventType = reader.getEventType();
            switch (eventType) {
                case START_ELEMENT:
                    assertEquals(SparqlProtocol.BOOLEAN, reader.getLocalName());
                    break;
                case CHARACTERS:
                    assertEquals(value, Boolean.parseBoolean(reader.getText()));
                    break;
                default:
                    break;
            }
            reader.next();
        }
        assertFalse(xmlWriter.hasMoreResults());
    }
}
