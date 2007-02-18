/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.BlankNode;
import org.jrdf.parser.GraphStatementHandler;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.parser.mem.ParserBlankNodeFactoryImpl;
import org.jrdf.util.ClosableIterator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class NTriplesParserIntegrationTest extends TestCase {
    private static final String TEST_DATA = "org/jrdf/parser/ntriples/test.nt";
    private static final TestJRDFFactory TEST_JRDF_FACTORY = TestJRDFFactory.getFactory();
    private static final Graph NEW_GRAPH = TEST_JRDF_FACTORY.getNewGraph();
    private static final ParserBlankNodeFactory BLANK_NODE_FACTORY = new ParserBlankNodeFactoryImpl(NEW_GRAPH.getElementFactory());

    public void testParseFile() throws Exception {
        ClosableIterator<Triple> closableIterator = init();
        try {
            Set<Triple> actualResults = new HashSet<Triple>();
            while (closableIterator.hasNext()) {
                Triple triple = closableIterator.next();
                actualResults.add(triple);
            }
            assertEquals(30, actualResults.size());
            Set<Triple> triples = expectedResults();
            Iterator<Triple> iterator = triples.iterator();
            while (iterator.hasNext()) {
                assertTrue(actualResults.contains(iterator.next()));
            }
        } finally {
            closableIterator.close();
        }
    }

    private ClosableIterator<Triple> init() throws Exception {
        InputStream in = getSampleData();
        ParserFactory factory = new ParserFactoryImpl();
        NTriplesParser nTriplesParser = factory.createParser(NEW_GRAPH.getElementFactory(), BLANK_NODE_FACTORY);
        GraphStatementHandler statementHandler = new GraphStatementHandler(NEW_GRAPH);
        nTriplesParser.setStatementHandler(statementHandler);
        nTriplesParser.parse(in, "foo");
        return NEW_GRAPH.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
    }

    private Set<Triple> expectedResults() throws Exception {
        Set<Triple> answers = new HashSet<Triple>();
        GraphElementFactory graphElementFactory = NEW_GRAPH.getElementFactory();
        TripleFactory tripleFactory = NEW_GRAPH.getTripleFactory();
        List<URIReference> refs = new ArrayList<URIReference>();
        for (int i = 0; i < 32; i++) {
            refs.add(graphElementFactory.createResource(URI.create("http://example.org/resource" + i)));
        }
        URIReference p = graphElementFactory.createResource(URI.create("http://example.org/property"));
        BlankNode anon = BLANK_NODE_FACTORY.createBlankNode("anon");
        answers.add(tripleFactory.createTriple(refs.get(1), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(anon, p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(2), p, anon));
        answers.add(tripleFactory.createTriple(refs.get(3), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(4), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(5), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(6), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(13), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(15), p, anon));
        return answers;
    }

    public InputStream getSampleData() throws IOException {
        URL source = getClass().getClassLoader().getResource(TEST_DATA);
        return source.openStream();
    }
}
