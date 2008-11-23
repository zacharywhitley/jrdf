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

package org.jrdf.parser.ntriples;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.collection.MapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.parser.GraphStatementHandler;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.parser.ParserTestUtil;
import org.jrdf.parser.bnodefactory.ParserBlankNodeFactoryImpl;
import static org.jrdf.parser.ntriples.NTriplesParserTestUtil.getSampleData;
import static org.jrdf.parser.ntriples.NTriplesParserTestUtil.parseNTriplesFile;
import static org.jrdf.parser.ntriples.NTriplesParserTestUtil.standardTest;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NTripleParserIntegrationTest extends TestCase {
    private static final String TEST_DATA = "org/jrdf/parser/ntriples/test.nt";
    private static final TestJRDFFactory TEST_JRDF_FACTORY = TestJRDFFactory.getFactory();

    // Commented out tests are due to the lack of inferencing for types and blank node equivalence.

    private static final Map<String, String> POSITIVE_TESTS = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            put("rdf-tests/rdfcore/datatypes/test002.nt", "rdf-tests/rdfcore/datatypes/test002.nt");
            //put("rdf-tests/datatypes/test002.nt", "rdf-tests/datatypes/test002b.nt");
            put("rdf-tests/rdfcore/datatypes/test003a.nt", "rdf-tests/rdfcore/datatypes/test003b.nt");
            put("rdf-tests/rdfcore/datatypes/test003b.nt", "rdf-tests/rdfcore/datatypes/test003a.nt");
            put("rdf-tests/rdfcore/datatypes/test005a.nt", "rdf-tests/rdfcore/datatypes/test005b.nt");
            //put("rdf-tests/datatypes/test008a.nt", "rdf-tests/datatypes/test008b.nt");
            //put("rdf-tests/datatypes/test009a.nt", "rdf-tests/datatypes/test009b.nt");
            //put("rdf-tests/datatypes/test0010a.nt", "rdf-tests/datatypes/test0010b.nt");
            put("rdf-tests/rdfcore/datatypes/test011a.nt", "rdf-tests/rdfcore/datatypes/test011b.nt");
        }
    };
    private static final Set<String> NEGATIVE_TESTS = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
//        {
            //add("rdf-tests/datatypes/test006.nt");
//        }
    };

    public void testStandardTest() throws Exception {
        Graph newGraph = TEST_JRDF_FACTORY.getGraph();
        MapFactory creator = new MemMapFactory();
        ParserBlankNodeFactory factory = new ParserBlankNodeFactoryImpl(creator, newGraph.getElementFactory());
        InputStream in = getSampleData(this.getClass(), TEST_DATA);
        Set<Triple> actualResults = parseNTriplesFile(in, newGraph, factory);
        Set<Triple> expectedResults = standardTest(newGraph, factory);
        ParserTestUtil.checkGraph(actualResults, expectedResults);
    }

    public void testPositiveTests() throws Exception {
        for (String fileName : POSITIVE_TESTS.keySet()) {
            Graph newGraph = TEST_JRDF_FACTORY.getGraph();
            MapFactory creator = new MemMapFactory();
            ParserBlankNodeFactory factory = new ParserBlankNodeFactoryImpl(creator, newGraph.getElementFactory());
            Set<Triple> actualResults = getResults(fileName, newGraph, factory);
            newGraph = TEST_JRDF_FACTORY.getGraph();
            Set<Triple> expectedResults = getResults(POSITIVE_TESTS.get(fileName), newGraph, factory);
            ParserTestUtil.checkGraph(actualResults, expectedResults);
        }
    }

    public void testNegativeTests() throws Exception {
        for (String fileName : NEGATIVE_TESTS) {
            final URL file = getClass().getClassLoader().getResource(fileName);
            Graph graph = TEST_JRDF_FACTORY.getGraph();
            NTriplesParserFactory parserFactory = new NTriplesParserFactoryImpl();
            MapFactory creator = new MemMapFactory();
            ParserBlankNodeFactory factory = new ParserBlankNodeFactoryImpl(creator, graph.getElementFactory());
            NTriplesParser nTriplesParser = parserFactory.createParser(graph, factory);
            LineParser parser = new LineParserImpl(nTriplesParser);
            parser.setStatementHandler(new GraphStatementHandler(graph));
            parser.parse(file.openStream(), "foo");
        }
    }

    private Set<Triple> getResults(String fileName, Graph newGraph, ParserBlankNodeFactory factory) throws Exception {
        InputStream in = getSampleData(this.getClass(), fileName);
        try {
            return parseNTriplesFile(in, newGraph, factory);
        } finally {
            in.close();
        }
    }
}
