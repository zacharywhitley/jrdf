/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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

package org.jrdf.parser.ntriples;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.parser.RDFEventReader;
import org.jrdf.parser.RDFInputFactory;
import org.jrdf.parser.mem.ParserBlankNodeFactoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Set;
import java.util.HashSet;

public class NTriplesEventReaderIntegrationTest extends TestCase {
    private static final String TEST_DATA = "org/jrdf/parser/ntriples/test.nt";
    private static final int TOTAL_TRIPLES = 33;
    private static final TestJRDFFactory TEST_JRDF_FACTORY = TestJRDFFactory.getFactory();
    private static final Graph NEW_GRAPH = TEST_JRDF_FACTORY.getNewGraph();
    private static final ParserBlankNodeFactory BLANK_NODE_FACTORY = new ParserBlankNodeFactoryImpl(NEW_GRAPH.getElementFactory());

    public void testParseFile() throws Exception {
        RDFEventReader eventReader = init();
        try {
            Set<Triple> actualResults = new HashSet<Triple>();
            while (eventReader.hasNext()) {
                Triple triple = eventReader.next();
                actualResults.add(triple);
            }
            assertEquals("Should get 33 triples from file", TOTAL_TRIPLES, actualResults.size());
            Set<Triple> triples = NTriplesParserTestUtil.expectedResults(NEW_GRAPH, BLANK_NODE_FACTORY, TOTAL_TRIPLES);
            for (Triple triple : triples) {
                assertTrue("Expected: " + triple, actualResults.contains(triple));
            }
        } finally {
            eventReader.close();
        }
    }

    private RDFEventReader init() throws IOException {
        InputStream in = getSampleData();
        RDFInputFactory factory = NTriplesRDFInputFactoryImpl.newInstance();
        return factory.createRDFEventReader(in, URI.create("foo"), NEW_GRAPH, BLANK_NODE_FACTORY);
    }

    public InputStream getSampleData() throws IOException {
        URL source = getClass().getClassLoader().getResource(TEST_DATA);
        return source.openStream();
    }
}
