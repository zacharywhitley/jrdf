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

package org.jrdf.query.answer.xml;

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
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.SelectAnswer;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$Id:$
 */

public class AnswerXMLPagenatedStreamWriterIntegrationTest extends AbstractAnswerXMLStreamWriterIntegrationTest {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final TestJRDFFactory TEST_FACTORY = TestJRDFFactory.getFactory();
    private static final PersistentGlobalJRDFFactory FACTORY = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
    private UrqlConnection urqlConnection;

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
        TEST_FACTORY.refresh();
        urqlConnection = TEST_FACTORY.getNewUrqlConnection();
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
        HANDLER.removeDir();
    }

    public void testVariables() throws GraphException, InvalidQuerySyntaxException, XMLStreamException, IOException {
        graph.add(b1, p1, l1);
        graph.add(b2, p2, l2);
        graph.add(b3, p3, l3);
        String queryString = "SELECT * WHERE {?s ?p ?o .}";
        final Answer answer = urqlConnection.executeQuery(graph, queryString);
        xmlWriter = new AnswerXMLPagenatedStreamWriter((SelectAnswer) answer, writer);
        Set<String> vars = getVariables();
        Set<String> set = new HashSet<String>();
        for (String var : new String[]{"s", "p", "o"}) {
            set.add(var);
        }
        checkVariables(set, vars);
    }

    public void testResult() throws GraphException, InvalidQuerySyntaxException, XMLStreamException, IOException {
        graph.add(b1, p1, l1);
        graph.add(b2, p2, l2);
        graph.add(b3, p3, l3);
        String queryString = "SELECT * WHERE {?s ?p ?o .}";
        final Answer answer = urqlConnection.executeQuery(graph, queryString);
        xmlWriter = new AnswerXMLPagenatedStreamWriter((SelectAnswer) answer, writer);
        xmlWriter.writeStartResults();
        int count = 0;
        while (xmlWriter.hasMoreResults()) {
            xmlWriter.writeResult();
            count++;
        }
        xmlWriter.writeEndResults();
        xmlWriter.flush();
        assertEquals(3, count);
    }
}
