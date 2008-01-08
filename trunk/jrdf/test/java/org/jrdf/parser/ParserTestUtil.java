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

package org.jrdf.parser;

import static junit.framework.Assert.assertTrue;
import org.jrdf.TestJRDFFactory;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.util.ClosableIterator;
import org.jrdf.map.MapFactory;
import org.jrdf.map.MemMapFactory;
import org.jrdf.parser.bnodefactory.ParserBlankNodeFactoryImpl;
import static org.jrdf.parser.ntriples.NTriplesRDFInputFactoryImpl.newInstance;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;
import static org.jrdf.util.param.ParameterUtil.*;
import org.jrdf.util.test.AssertThrows;

import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ParserTestUtil {
    private static final TestJRDFFactory TEST_JRDF_FACTORY = TestJRDFFactory.getFactory();
    private static final Graph NEW_GRAPH = TEST_JRDF_FACTORY.getNewGraph();
    private static final MapFactory CREATOR = new MemMapFactory();
    private static final ParserBlankNodeFactory BLANK_NODE_FACTORY = new ParserBlankNodeFactoryImpl(CREATOR,
        NEW_GRAPH.getElementFactory());

    private ParserTestUtil() {
    }

    public static void checkPositiveNtRdfTest(URL expectedFile, URL actualFile, String baseURI, Graph actualGraph,
        ParserBlankNodeFactory blankNodeFactory) throws Exception {
        checkNotNull(expectedFile, actualFile, baseURI, actualGraph, blankNodeFactory);
        RDFInputFactory factory = newInstance();
        RDFEventReader eventReader = factory.createRDFEventReader(expectedFile.openStream(), URI.create(baseURI),
            NEW_GRAPH, BLANK_NODE_FACTORY);
        Parser rdfXmlParser = new GraphRdfXmlParser(actualGraph, blankNodeFactory);
        rdfXmlParser.parse(actualFile.openStream(), baseURI);
        ClosableIterator<Triple> results = actualGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        Set<Triple> resultTriples = new HashSet<Triple>();
        while (results.hasNext()) {
            resultTriples.add(results.next());
        }
        while (eventReader.hasNext()) {
            Triple triple = eventReader.next();
            assertTrue("Invalid result for positive test.  Should contain: " + triple, resultTriples.contains(triple));
        }
    }

    public static void checkNegativeRdfTestParseException(final URL errorFile, Graph actualGraph,
        ParserBlankNodeFactory blankNodeFactory) throws Exception {
        final Parser rdfXmlParser = new GraphRdfXmlParser(actualGraph, blankNodeFactory);
        AssertThrows.assertThrows(ParseException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                rdfXmlParser.parse(errorFile.openStream(), "http://example.org/");
            }
        });
    }
}
