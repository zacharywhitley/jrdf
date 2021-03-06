/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

import junit.framework.TestCase;
import org.jrdf.PersistentJRDFFactory;
import org.jrdf.PersistentJRDFFactoryImpl;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Resource;
import org.jrdf.query.answer.Answer;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import static java.net.URI.create;
import static java.util.Arrays.asList;

public class SparqlSelectXmlWriterIntegrationTest extends TestCase {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final TestJRDFFactory TEST_FACTORY = TestJRDFFactory.getFactory();
    private static final PersistentJRDFFactory FACTORY = PersistentJRDFFactoryImpl.getFactory(HANDLER);
    private static final SparqlXmlStreamWriterTestUtil TEST_UTIL = new SparqlXmlStreamWriterTestUtil();
    private SparqlConnection sparqlConnection;
    private Graph graph;
    private SparqlXmlWriter xmlWriter;
    private Writer writer = new StringWriter();

    @Override
    public void setUp() throws Exception {
        HANDLER.removeDir();
        HANDLER.makeDir();
        FACTORY.refresh();
        TEST_FACTORY.refresh();
        sparqlConnection = TEST_FACTORY.getNewSparqlConnection();
        graph = FACTORY.getNewGraph("foo");
        final GraphElementFactory elementFactory = graph.getElementFactory();
        Resource b1 = elementFactory.createResource();
        Resource b2 = elementFactory.createResource();
        Resource b3 = elementFactory.createResource();
        b1.addValue(create("urn:p1"), "l1");
        b2.addValue(create("urn:p2"), "l2");
        b3.addValue(create("urn:p3"), "l3");
    }

    @Override
    public void tearDown() throws Exception {
        if (xmlWriter != null) {
            xmlWriter.close();
        }
        graph.close();
        HANDLER.removeDir();
    }

    public void testVariables() throws Exception {
        String queryString = "SELECT * WHERE {?s ?p ?o .}";
        final Answer answer = sparqlConnection.executeQuery(graph, queryString);
        xmlWriter = new SparqlSelectXmlWriter(writer, answer.getVariableNames(), answer.columnValuesIterator(),
            answer.numberOfTuples());
        Set<String> vars = TEST_UTIL.getVariables(xmlWriter, writer);
        Set<String> set = new HashSet<String>();
        set.addAll(asList("s", "p", "o"));
        TEST_UTIL.checkVariables(set, vars);
    }

    public void testResult() throws Exception {
        String queryString = "SELECT * WHERE {?s ?p ?o .}";
        final Answer answer = sparqlConnection.executeQuery(graph, queryString);
        xmlWriter = new SparqlSelectXmlWriter(writer, answer.getVariableNames(), answer.columnValuesIterator(),
            answer.numberOfTuples());
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
