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

package org.jrdf.parser.turtle;

import org.jrdf.TestJRDFFactory;
import org.jrdf.collection.MapFactory;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.parser.NamespaceListener;
import org.jrdf.parser.NamespaceListenerImpl;
import org.jrdf.parser.RDFEventReader;
import org.jrdf.parser.RDFEventReaderFactory;
import org.jrdf.parser.line.LineParserTestUtil;
import org.jrdf.parser.ntriples.NTriplesEventReaderFactory;
import org.jrdf.parser.turtle.parser.TurtleAnalyser;
import org.jrdf.parser.turtle.parser.TurtleAnalyserImpl;
import org.jrdf.parser.turtle.parser.lexer.LexerException;
import org.jrdf.parser.turtle.parser.node.Start;
import org.jrdf.parser.turtle.parser.parser.Parser;
import org.jrdf.parser.turtle.parser.parser.ParserException;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;

import static java.net.URI.create;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.jrdf.parser.line.LineParserTestUtil.getSampleData;

public class SableCcTurtleReaderIntegrationTest {
    private static final String TEST_DATA = "org/jrdf/parser/turtle/test.n3";
    private static final String TEST_ZIP_FILE = "org/jrdf/parser/turtle/tests.zip";
    private static final MapFactory CREATOR = new MemMapFactory();
    private static final NamespaceListener LISTENER = new NamespaceListenerImpl(CREATOR);
    private static final TestJRDFFactory TEST_JRDF_FACTORY = TestJRDFFactory.getFactory();
    private static final Graph NEW_GRAPH = TEST_JRDF_FACTORY.getGraph();
    private static final RDFEventReaderFactory NTRIPLES_RDF_INPUT_FACTORY = new NTriplesEventReaderFactory(CREATOR);
    private SableCcTurtleParserFactory factory;
    private TurtleAnalyser analyser;

    @Before
    public void createFactory() {
        factory = new SableCcTurtleParserFactoryImpl();
        analyser = new TurtleAnalyserImpl(LISTENER);
    }

    @Test
    public void parseFile() throws Exception {
        final InputStream input = getSampleData(getClass(), TEST_DATA);
        final Reader streamReader = new InputStreamReader(input);
        final Parser parser = factory.getParser(streamReader);
        parser.parse();
        factory.close();
    }

    @Test
    public void badTurtleFiles() throws Exception {
        for (int i = 0; i <= 14; i++) {
            String fileName = "bad-" + String.format("%02d", i) + ".ttl";
            checkParsingFileFailsFromZip(fileName);
        }
    }

    @Test
    public void goodTurtleFiles() throws Exception {
        for (int i = 0; i <= 28; i++) {
            String fileName = "test-" + String.format("%02d", i);
            getActualTriplesFromZip(fileName);
            final Set<Triple> actualResults = getExpectedTriples(fileName);
        }
    }

    private void checkParsingFileFailsFromZip(String fileName) throws Exception {
        final InputStream input = getSampleData(getClass(), TEST_ZIP_FILE, fileName);
        try {
            checkParsingFileFails(fileName, input);
        } finally {
            input.close();
        }
    }

    private void checkParsingFileFails(String fileName, InputStream input) throws Exception {
        try {
            getActualTriples(input);
            fail("Should throw an exception for file: " + fileName);
        } catch (ParserException pe) {
            // This is good.
            assertTrue(true);
        } catch (LexerException le) {
            // This too is good.
            assertTrue(true);
        }
    }

    private void getActualTriplesFromZip(String fileName) throws Exception {
        final InputStream input = getSampleData(getClass(), TEST_ZIP_FILE, fileName + ".ttl");
        try {
            getActualTriples(input);
        } finally {
            input.close();
        }
    }

    private Set<Triple> getActualTriples(InputStream input) throws Exception {
        Reader streamReader = new InputStreamReader(input);
        try {
            final Parser parser = factory.getParser(streamReader);
            Start start = parser.parse();
            start.apply(analyser);
            return analyser.getTriples();
        } finally {
            streamReader.close();
        }
    }

    private Set<Triple> getExpectedTriples(String fileName) throws Exception {
        final InputStream expectedOutput = getSampleData(getClass(), TEST_ZIP_FILE, fileName + ".out");
        try {
            final RDFEventReader eventReader = NTRIPLES_RDF_INPUT_FACTORY.createRDFEventReader(expectedOutput,
                create("foo"), NEW_GRAPH);
            return LineParserTestUtil.getTriplesWithReader(eventReader);
        } finally {
            expectedOutput.close();
        }
    }
}
