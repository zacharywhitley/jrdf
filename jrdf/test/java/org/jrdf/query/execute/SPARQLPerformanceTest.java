/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.query.execute;

import junit.framework.TestCase;
import org.jrdf.PersistentGlobalJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactoryImpl;
import org.jrdf.collection.MapFactory;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.parser.GraphStatementHandler;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.parser.StatementHandlerException;
import org.jrdf.parser.bnodefactory.ParserBlankNodeFactoryImpl;
import org.jrdf.parser.n3.N3Parser;
import org.jrdf.parser.n3.N3ParserFactory;
import org.jrdf.parser.n3.N3ParserFactoryImpl;
import org.jrdf.parser.ntriples.LineParser;
import org.jrdf.parser.ntriples.LineParserImpl;
import static org.jrdf.parser.ntriples.NTriplesParserTestUtil.getSampleData;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.AskAnswer;
import org.jrdf.query.answer.SelectAnswer;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */

public class SPARQLPerformanceTest extends TestCase {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private PersistentGlobalJRDFFactory factory;
    private static final N3ParserFactory PARSER_FACTORY = new N3ParserFactoryImpl();
    private static final MapFactory MAP_FACTORY = new MemMapFactory();
    private static final Set<String> FILE_NAMES = new LinkedHashSet<String>() {
        {
            add("rdf-tests/sparql/q1.sparql");
            add("rdf-tests/sparql/q2.sparql");
            add("rdf-tests/sparql/q3a.sparql");
            add("rdf-tests/sparql/q3b.sparql");
            add("rdf-tests/sparql/q3c.sparql");
            add("rdf-tests/sparql/q4.sparql");
            add("rdf-tests/sparql/q5a.sparql");
            add("rdf-tests/sparql/q5b.sparql");
            add("rdf-tests/sparql/q6.sparql");
            add("rdf-tests/sparql/q7.sparql");
            add("rdf-tests/sparql/q8.sparql");
            add("rdf-tests/sparql/q9.sparql");
            add("rdf-tests/sparql/q10.sparql");
            add("rdf-tests/sparql/q11.sparql");
            add("rdf-tests/sparql/q12a.sparql");
            add("rdf-tests/sparql/q12b.sparql");
            add("rdf-tests/sparql/q12c.sparql");
        }
    };

    private UrqlConnection connection;
    private MoleculeGraph graph;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HANDLER.removeDir();
        HANDLER.makeDir();
        factory = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
        graph = factory.getGraph("sparql_test");
        connection = factory.getNewUrqlConnection();
        parseGraph("rdf-tests/sparql/sp2b.n3");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        graph.clear();
        graph.close();
        factory.close();
        HANDLER.removeDir();
    }

    public void testSparqlPerformance() throws Exception {
        for (String fileName : FILE_NAMES) {
            String sparql = getFileContent(fileName);
            System.err.println("Executing: " + fileName);
            final Answer answer = connection.executeQuery(graph, sparql);
            if (answer instanceof SelectAnswer) {
                System.err.println("Answer # = " + answer.numberOfTuples() + ", time = " + answer.getTimeTaken());
            } else if (answer instanceof AskAnswer) {
                System.err.println("Answer = " + answer.toString() + ", time = " + answer.getTimeTaken());
            }
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

    private void parseGraph(String fileName) throws GraphException, IOException, StatementHandlerException,
        ParseException {
        assertEquals(0, graph.getNumberOfTriples());
        InputStream in = getSampleData(this.getClass(), fileName);
        ParserBlankNodeFactory factory = new ParserBlankNodeFactoryImpl(MAP_FACTORY, graph.getElementFactory());
        N3Parser n3Parser = PARSER_FACTORY.createParser(graph, factory);
        LineParser parser = new LineParserImpl(n3Parser);
        parser.setStatementHandler(new GraphStatementHandler(graph));
        parser.parse(in, fileName);
        assertEquals(50168, graph.getNumberOfTriples());
    }
}
