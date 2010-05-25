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

package org.jrdf.query.execute;

import junit.framework.TestCase;
import org.jrdf.PersistentGlobalJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactoryImpl;
import org.jrdf.collection.MapFactory;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.Parser;
import org.jrdf.parser.StatementHandlerException;
import org.jrdf.parser.line.GraphLineParser;
import org.jrdf.parser.line.LineHandlerFactory;
import org.jrdf.parser.turtle.TurtleParserFactory;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.AskAnswer;
import org.jrdf.query.answer.SelectAnswer;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.jrdf.parser.line.LineParserTestUtil.getSampleData;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */
public class SPARQLPerformanceTest extends TestCase {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final PersistentGlobalJRDFFactory FACTORY = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
    private static final LineHandlerFactory PARSER_FACTORY = new TurtleParserFactory();
    private static final MapFactory MAP_FACTORY = new MemMapFactory();
    private static final Map<String, Integer> FILE_RESULT_MAP = new LinkedHashMap<String, Integer>() {
        {
            put("rdf-tests/sparql/q1.sparql", 1);
            put("rdf-tests/sparql/q2.sparql", 965);
            put("rdf-tests/sparql/q3a.sparql", 3647);
            put("rdf-tests/sparql/q3b.sparql", 25);
            put("rdf-tests/sparql/q3c.sparql", 0);
            put("rdf-tests/sparql/q4.sparql", 104746);
            put("rdf-tests/sparql/q5a.sparql", 1085);
            put("rdf-tests/sparql/q5b.sparql", 1085);
            put("rdf-tests/sparql/q6.sparql", 1959);
            put("rdf-tests/sparql/q7.sparql", 2);
            put("rdf-tests/sparql/q8.sparql", 264);
            put("rdf-tests/sparql/q9.sparql", 4);
            put("rdf-tests/sparql/q10.sparql", 307);
            put("rdf-tests/sparql/q11.sparql", 3697);
            put("rdf-tests/sparql/q12a.sparql", 1);
            put("rdf-tests/sparql/q12b.sparql", 1);
            put("rdf-tests/sparql/q12c.sparql", 1);
        }
    };

    private SparqlConnection connection;
    private MoleculeGraph graph;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HANDLER.removeDir();
        HANDLER.makeDir();
        FACTORY.refresh();
        graph = FACTORY.getGraph("sparql_test");
        connection = FACTORY.getNewSparqlConnection();
        parseGraph("rdf-tests/sparql/sp2b.n3.zip");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        graph.close();
        HANDLER.removeDir();
    }

    public void testSparqlPerformance() throws Exception {
        for (Map.Entry<String, Integer> entry : FILE_RESULT_MAP.entrySet()) {
            String fileName = entry.getKey();
            String sparql = getFileContent(fileName);
            System.out.println("Executing: " + fileName);
            Answer answer = connection.executeQuery(graph, sparql);
            if (answer instanceof SelectAnswer) {
                System.out.println("Answer # = " + answer.numberOfTuples() + ", time = " + answer.getTimeTaken() +
                    "\n");
            } else if (answer instanceof AskAnswer) {
                System.out.println("Answer = " + answer.toString() + ", time = " + answer.getTimeTaken() + "\n");
            }
            assertEquals(entry.getValue().intValue(), answer.numberOfTuples());
        }
    }

    private String getFileContent(String fileName) throws Exception {
        StringBuilder builder = new StringBuilder();
        String line;
        final URI uri = this.getClass().getClassLoader().getResource(fileName).toURI();
        BufferedReader input = new BufferedReader(new FileReader(new File(uri)));
        try {
            while ((line = input.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();
        } finally {
            input.close();
        }
    }

    private void parseGraph(final String fileName) throws GraphException, IOException, StatementHandlerException,
        ParseException {
        System.out.println("Parsing Graph");
        long startTime = System.currentTimeMillis();
        assertEquals(0, graph.getNumberOfTriples());
        final InputStream in = getSampleData(this.getClass(), fileName);
        final Parser parser = new GraphLineParser(graph, PARSER_FACTORY.createParser(graph, MAP_FACTORY));
        parser.parse(in, fileName);
        assertEquals(50168, graph.getNumberOfTriples());
        System.out.println("Parsed Graph in: " + (System.currentTimeMillis() - startTime));
    }
}
