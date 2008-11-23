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

import static org.jrdf.graph.AnyObjectNode.*;
import static org.jrdf.graph.AnyPredicateNode.*;
import static org.jrdf.graph.AnySubjectNode.*;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.parser.GraphStatementHandler;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.util.ClosableIterable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NTriplesParserTestUtil {

    private NTriplesParserTestUtil() {
    }

    public static InputStream getSampleData(Class<?> clazz, String fileName) throws IOException {
        URL source = clazz.getClassLoader().getResource(fileName);
        return source.openStream();
    }

    public static Set<Triple> parseNTriplesFile(InputStream in, Graph graph, ParserBlankNodeFactory factory)
        throws Exception {
        NTriplesParserFactory parserFactory = new NTriplesParserFactoryImpl();
        NTriplesParser ntriplesParser = parserFactory.createParser(graph, factory);
        LineParser parser = new LineParserImpl(ntriplesParser);
        parser.setStatementHandler(new GraphStatementHandler(graph));
        parser.parse(in, "foo");
        Set<Triple> actualResults = new HashSet<Triple>();
        ClosableIterable<Triple> tripleClosableIterable = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE,
            ANY_OBJECT_NODE);
        for (Triple triple : tripleClosableIterable) {
            actualResults.add(triple);
        }
        return actualResults;
    }

    public static Set<Triple> standardTest(Graph newGraph, ParserBlankNodeFactory blankNodeFactory) throws Exception {
        Set<Triple> answers = new HashSet<Triple>();
        GraphElementFactory graphElementFactory = newGraph.getElementFactory();
        TripleFactory tripleFactory = newGraph.getTripleFactory();
        List<URIReference> refs = new ArrayList<URIReference>();
        for (int i = 0; i < 33; i++) {
            refs.add(graphElementFactory.createURIReference(URI.create("http://example.org/resource" + i)));
        }
        URIReference p = graphElementFactory.createURIReference(URI.create("http://example.org/property"));
        BlankNode anon = blankNodeFactory.createBlankNode("anon");
        answers.add(tripleFactory.createTriple(refs.get(1), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(anon, p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(2), p, anon));
        answers.add(tripleFactory.createTriple(refs.get(3), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(4), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(5), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(6), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(7), p, graphElementFactory.createLiteral("simple literal")));
        answers.add(tripleFactory.createTriple(refs.get(8), p, graphElementFactory.createLiteral("backslash:\\")));
        answers.add(tripleFactory.createTriple(refs.get(9), p, graphElementFactory.createLiteral("dquote:\"")));
        answers.add(tripleFactory.createTriple(refs.get(10), p, graphElementFactory.createLiteral("newline:\n")));
        answers.add(tripleFactory.createTriple(refs.get(11), p, graphElementFactory.createLiteral("return\r")));
        answers.add(tripleFactory.createTriple(refs.get(12), p, graphElementFactory.createLiteral("tab:\t")));
        answers.add(tripleFactory.createTriple(refs.get(13), p, refs.get(2)));
        answers.add(tripleFactory.createTriple(refs.get(14), p, graphElementFactory.createLiteral("x")));
        answers.add(tripleFactory.createTriple(refs.get(15), p, anon));
        answers.add(tripleFactory.createTriple(refs.get(16), p, graphElementFactory.createLiteral("\u00E9")));
        answers.add(tripleFactory.createTriple(refs.get(17), p, graphElementFactory.createLiteral("\u20AC")));
        answers.add(tripleFactory.createTriple(refs.get(17), p, graphElementFactory.createLiteral("\uD800\uDC00")));
        answers.add(tripleFactory.createTriple(refs.get(17), p, graphElementFactory.createLiteral("\uD84C\uDFB4")));
        answers.add(tripleFactory.createTriple(refs.get(17), p, graphElementFactory.createLiteral("\uDBFF\uDFFF")));
        URI xmlLiteral = URI.create("http://www.w3.org/2000/01/rdf-schema#XMLLiteral");
        answers.add(tripleFactory.createTriple(refs.get(21), p, graphElementFactory.createLiteral("", xmlLiteral)));
        answers.add(tripleFactory.createTriple(refs.get(22), p, graphElementFactory.createLiteral(" ", xmlLiteral)));
        answers.add(tripleFactory.createTriple(refs.get(23), p, graphElementFactory.createLiteral("x", xmlLiteral)));
        answers.add(tripleFactory.createTriple(refs.get(23), p, graphElementFactory.createLiteral("\"", xmlLiteral)));
        answers.add(tripleFactory.createTriple(refs.get(24), p, graphElementFactory.createLiteral("<a></a>",
            xmlLiteral)));
        answers.add(tripleFactory.createTriple(refs.get(25), p, graphElementFactory.createLiteral("a <b></b>",
            xmlLiteral)));
        answers.add(tripleFactory.createTriple(refs.get(26), p, graphElementFactory.createLiteral("a <b></b> c",
            xmlLiteral)));
        answers.add(tripleFactory.createTriple(refs.get(26), p, graphElementFactory.createLiteral("a\n<b></b>\nc",
            xmlLiteral)));
        answers.add(tripleFactory.createTriple(refs.get(27), p, graphElementFactory.createLiteral("chat", xmlLiteral)));
        answers.add(tripleFactory.createTriple(refs.get(30), p, graphElementFactory.createLiteral("chat", "fr")));
        answers.add(tripleFactory.createTriple(refs.get(31), p, graphElementFactory.createLiteral("chat", "en")));
        URI datatype = URI.create("http://example.org/datatype1");
        answers.add(tripleFactory.createTriple(refs.get(32), p, graphElementFactory.createLiteral("abc", datatype)));
        return answers;
    }
}
