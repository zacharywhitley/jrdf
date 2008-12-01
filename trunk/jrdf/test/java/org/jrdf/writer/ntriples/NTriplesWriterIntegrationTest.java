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

package org.jrdf.writer.ntriples;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.collection.MapFactory;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import static org.jrdf.parser.ntriples.ParserTestUtil.checkGraph;
import org.jrdf.parser.RDFEventReader;
import org.jrdf.parser.RDFEventReaderFactory;
import static org.jrdf.parser.line.LineParserTestUtil.addStandardValuesToGraph;
import static org.jrdf.parser.line.LineParserTestUtil.getSampleData;
import static org.jrdf.parser.line.LineParserTestUtil.parseNTriplesFile;
import org.jrdf.parser.ntriples.NTriplesEventReaderFactory;
import org.jrdf.writer.RdfWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import static java.net.URI.create;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class NTriplesWriterIntegrationTest extends TestCase {
    private static final String TEST_DATA = "org/jrdf/parser/ntriples/test.nt";
    private static final TestJRDFFactory TEST_JRDF_FACTORY = TestJRDFFactory.getFactory();
    private static final Graph NEW_GRAPH = TEST_JRDF_FACTORY.getNewGraph();
    private static final MapFactory CREATOR = new MemMapFactory();
    private static final RDFEventReaderFactory NTRIPLES_RDF_INPUT_FACTORY = new NTriplesEventReaderFactory(CREATOR);

    public void testWriteTestGraph() throws Exception {
        final Set<String> strings = getTriplesAsStrings();
        assertSame("Expected to get 33 triples", 33, strings.size());
        final Scanner scanner = new Scanner(getSampleData(this.getClass(), TEST_DATA));
        int start = 1;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // Simple removing white space and lines to ignore - including blank node triples.
            line = line.replace("\t", "").replace(" ", "");
            if (line.length() != 0 && !line.contains("#") && !line.contains("_:")) {
                assertTrue("Could not find: [" + line + "]" + " at " + start, strings.contains(line));
            }
            start++;
        }
    }

    private Set<String> getTriplesAsStrings() throws Exception {
        final Writer out = printOutGraph();
        final String[] strings = out.toString().split(RdfWriter.NEW_LINE);
        final Set<String> results = new HashSet<String>();
        for (final String string : strings) {
            results.add(string.replace(" ", ""));
        }
        return results;
    }

    public void testRoundTriple() throws Exception {
        final Set<Triple> expectedResults = expectedResults();
        final Set<Triple> actualResults = getActualResults();
        checkGraph(expectedResults, actualResults);
    }

    private Set<Triple> expectedResults() throws IOException {
        NEW_GRAPH.clear();
        final InputStream in = getSampleData(this.getClass(), TEST_DATA);
        return parseNTriplesFile(in, NEW_GRAPH, CREATOR);
    }

    private Set<Triple> getActualResults() throws Exception {
        final Writer writer = printOutGraph();
        NEW_GRAPH.clear();
        final StringReader reader = new StringReader(writer.toString());
        final RDFEventReader eventReader = NTRIPLES_RDF_INPUT_FACTORY.createRDFEventReader(reader, create("foo"),
            NEW_GRAPH);
        final Set<Triple> actualResults = new HashSet<Triple>();
        while (eventReader.hasNext()) {
            actualResults.add(eventReader.next());
        }
        return actualResults;
    }

    private Writer printOutGraph() throws Exception {
        NEW_GRAPH.clear();
        addStandardValuesToGraph(NEW_GRAPH);
        final StringWriter out = new StringWriter();
        final NTriplesWriter writer = new NTriplesWriterImpl();
        writer.write(NEW_GRAPH, out);
        return out;
    }
}
