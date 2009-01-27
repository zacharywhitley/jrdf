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

package org.jrdf.parser.ntriples;

import static junit.framework.Assert.assertEquals;
import org.jrdf.TestJRDFFactory;
import org.jrdf.collection.MapFactory;
import org.jrdf.collection.MemMapFactory;
import static org.jrdf.graph.AbstractBlankNode.isBlankNode;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.Triple;
import org.jrdf.graph.datatype.LexicalComparator;
import org.jrdf.graph.datatype.LexicalComparatorImpl;
import org.jrdf.graph.datatype.SemanticLiteralComparator;
import org.jrdf.graph.datatype.SemanticLiteralComparatorImpl;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.Parser;
import org.jrdf.parser.RDFEventReader;
import org.jrdf.parser.RDFEventReaderFactory;
import org.jrdf.parser.StatementHandlerException;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;
import org.jrdf.util.ClosableIterable;
import static org.jrdf.util.EqualsUtil.hasSuperClassOrInterface;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.jrdf.util.test.AssertThrows;

import java.io.IOException;
import static java.net.URI.create;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ParserTestUtil {
    private static final TestJRDFFactory TEST_JRDF_FACTORY = TestJRDFFactory.getFactory();
    private static final Graph NEW_GRAPH = TEST_JRDF_FACTORY.getNewGraph();
    private static final MapFactory CREATOR = new MemMapFactory();
    private static final RDFEventReaderFactory NTRIPLES_RDF_INPUT_FACTORY = new NTriplesEventReaderFactory(CREATOR);
    public static final LexicalComparator LEX_COMPARATOR = new LexicalComparatorImpl();
    public static final SemanticLiteralComparator SEM_COMPARATOR = new SemanticLiteralComparatorImpl(LEX_COMPARATOR);

    private ParserTestUtil() {
    }

    public static void checkPositiveNtNtTest(URL expectedFile, URL actualFile, String baseURI, Graph actualGraph)
        throws Exception {
        checkNotNull(expectedFile, actualFile, baseURI, actualGraph);
        Set<Triple> resultTriples = getNTripleResult(actualFile, baseURI);
        Set<Triple> expectedTriples = getNTripleResult(expectedFile, baseURI);
        checkResults(expectedFile, actualFile, resultTriples, expectedTriples);
    }

    public static void checkPositiveNtRdfTest(URL expectedFile, URL actualFile, String baseURI, Graph actualGraph)
        throws Exception {
        checkNotNull(expectedFile, actualFile, baseURI, actualGraph);
        Set<Triple> resultTriples = getRdfXmlResult(actualFile, baseURI, actualGraph, CREATOR);
        Set<Triple> expectedTriples = getNTripleResult(expectedFile, baseURI);
        checkResults(expectedFile, actualFile, resultTriples, expectedTriples);
    }

    private static void checkResults(URL expectedFile, URL actualFile, Set<Triple> resultTriples,
        Set<Triple> expectedTriples) {
        assertEquals("Wrong number of triples returned: Expected: " + expectedFile + ", Result " + actualFile,
            expectedTriples.size(), resultTriples.size());
        int noTriples = findNumberOfEqualTriples(expectedTriples, resultTriples);
        assertEquals("Invalid result for positive test.  Expected: " + expectedFile + ", Result " + actualFile + ". " +
            "Should contain: " + expectedTriples + " but was: " + resultTriples, expectedTriples.size(), noTriples);
    }

    private static Set<Triple> getRdfXmlResult(URL actualFile, String baseURI, Graph actualGraph,
        MapFactory mapFactory)
        throws GraphException, IOException, ParseException, StatementHandlerException {
        Parser rdfXmlParser = new GraphRdfXmlParser(actualGraph, mapFactory);
        rdfXmlParser.parse(actualFile.openStream(), baseURI);
        Set<Triple> resultTriples = new HashSet<Triple>();
        ClosableIterable<Triple> results = actualGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        try {
            for (final Triple o : results) {
                resultTriples.add(o);
            }
        } finally {
            results.iterator().close();
        }
        return resultTriples;
    }

    private static Set<Triple> getNTripleResult(URL expectedFile, String baseURI) throws IOException {
        RDFEventReader eventReader = NTRIPLES_RDF_INPUT_FACTORY.createRDFEventReader(expectedFile.openStream(),
            create(baseURI), NEW_GRAPH);
        Set<Triple> expectedTriples = new HashSet<Triple>();
        while (eventReader.hasNext()) {
            final Triple o = eventReader.next();
            expectedTriples.add(o);
        }
        return expectedTriples;
    }

    public static void checkNegativeRdfTestParseException(final URL errorFile, Graph actualGraph,
        MapFactory mapFactory) throws Exception {
        final Parser rdfXmlParser = new GraphRdfXmlParser(actualGraph, mapFactory);
        AssertThrows.assertThrows(ParseException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                rdfXmlParser.parse(errorFile.openStream(), "http://example.org/");
            }
        });
    }

    /**
     * This only works where there is one blank node in the set of triples - multiple blank nodes will give false
     * positive results.
     *
     * @param expectedTriples the expected triples.
     * @param actualTriples the triples produced.
     * @throws Exception if anything goes wrong.
     */
    public static void checkGraph(Set<Triple> expectedTriples, Set<Triple> actualTriples) throws Exception {
        int numberFound = findNumberOfEqualTriples(actualTriples, expectedTriples);
        assertEquals(expectedTriples.size(), numberFound);
    }

    public static int findNumberOfEqualTriples(Set<Triple> actualTriples, Set<Triple> expectedTriples) {
        assertEquals("Wrong number of triples returned", expectedTriples.size(), actualTriples.size());
        int numberFound = 0;
        for (Triple tripleToFind : expectedTriples) {
            boolean found = false;
            Iterator<Triple> it = actualTriples.iterator();
            while (it.hasNext() && !found) {
                Triple triple = it.next();
                found = hasTriple(tripleToFind, triple);
                if (found) {
                    numberFound++;
                }
            }
        }
        return numberFound;
    }

    private static boolean hasTriple(Triple tripleToFind, Triple triple) {
        boolean found = false;
        if ((nodesAreBlankOrEqual(tripleToFind.getSubject(), triple.getSubject())) &&
            tripleToFind.getPredicate().equals(triple.getPredicate())) {
            // TODO Change to use visitor.
            if (hasSuperClassOrInterface(Literal.class, tripleToFind.getObject())) {
                Literal literal = (Literal) tripleToFind.getObject();
                Node node = triple.getObject();
                found = SEM_COMPARATOR.compare(literal, node) == 0;
            } else {
                found = nodesAreBlankOrEqual(tripleToFind.getObject(), triple.getObject());
            }
        }
        return found;
    }

    private static boolean nodesAreBlankOrEqual(Node nodeToFind, Node currentNode) {
        return (isBlankNode(nodeToFind) && (isBlankNode(currentNode))) || nodeToFind.equals(currentNode);
    }
}
