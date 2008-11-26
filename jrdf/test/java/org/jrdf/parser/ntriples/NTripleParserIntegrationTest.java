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
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.parser.ParserBlankNodeFactory;
import static org.jrdf.parser.ParserTestUtil.checkGraph;
import org.jrdf.parser.bnodefactory.ParserBlankNodeFactoryImpl;
import static org.jrdf.parser.ntriples.NTriplesParserTestUtil.getSampleData;
import static org.jrdf.parser.ntriples.NTriplesParserTestUtil.parseNTriplesFile;
import static org.jrdf.parser.ntriples.NTriplesParserTestUtil.standardTest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// TODO N3 Add Negative tests.
public class NTripleParserIntegrationTest extends TestCase {
    private static final String TEST_DATA = "org/jrdf/parser/ntriples/test.nt";
    private static final TestJRDFFactory TEST_JRDF_FACTORY = TestJRDFFactory.getFactory();
    private ParserBlankNodeFactory factory;

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
        final Set<Triple> actualResults = getActualResults(TEST_DATA);
        final Set<Triple> expectedResults = standardTest();
        checkGraph(expectedResults, actualResults);
    }

    public void testPositiveTests() throws Exception {
        for (final String fileName : POSITIVE_TESTS.keySet()) {
            final Set<Triple> actualResults = getActualResults(fileName);
            final Set<Triple> expectedResults = getResults(POSITIVE_TESTS.get(fileName), TEST_JRDF_FACTORY.getGraph());
            checkGraph(expectedResults, actualResults);
        }
    }

    public void testNegativeTests() throws Exception {
        for (final String fileName : NEGATIVE_TESTS) {
            // TODO Finish - this should expect an exception to be thrown.
            getActualResults(fileName);
        }
    }

    private Set<Triple> getActualResults(final String fileName) throws Exception {
        final Graph newGraph = TEST_JRDF_FACTORY.getGraph();
        factory = new ParserBlankNodeFactoryImpl(new MemMapFactory(), newGraph.getElementFactory());
        return getResults(fileName, newGraph);
    }

    private Set<Triple> getResults(String fileName, Graph newGraph) throws Exception {
        final InputStream in = getSampleData(this.getClass(), fileName);
        try {
            return parseNTriplesFile(in, newGraph, factory);
        } finally {
            in.close();
        }
    }
}
