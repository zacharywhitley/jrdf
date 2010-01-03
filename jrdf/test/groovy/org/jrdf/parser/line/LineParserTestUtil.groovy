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

package org.jrdf.parser.line

import org.jrdf.TestJRDFFactory
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE
import org.jrdf.graph.Graph
import org.jrdf.graph.Triple
import org.jrdf.parser.GraphStatementHandler
import org.jrdf.parser.ParserBlankNodeFactory
import org.jrdf.parser.RDFEventReader
import org.jrdf.parser.n3.N3ParserFactory
import org.jrdf.util.test.RdfBuilder
import org.jrdf.collection.MapFactory
import org.jrdf.parser.line.LineHandler
import org.jrdf.parser.line.LineParserImpl
import org.jrdf.parser.ntriples.NTriplesParserFactory
import org.jrdf.parser.n3.N3ParserFactory
import java.util.zip.ZipFile
import java.util.zip.ZipEntry

class LineParserTestUtil {

    private LineParserTestUtil() {
    }

    static InputStream getSampleData(Class clazz, String fileName) throws IOException {
        URL source = clazz.getClassLoader().getResource(fileName)
        if (fileName.endsWith("zip")) {
            def file = new ZipFile(new File(source.getFile()))
            def entry = file.entries().nextElement()
            return file.getInputStream(entry)
        } else {
            return source.openStream()
        }
    }

    static Set<Triple> getTriplesWithReader(RDFEventReader eventReader) {
        def actualResults = new HashSet<Triple>()
        eventReader.each {
            actualResults.add(it);
        }
        return actualResults
    }

    static Set<Triple> parseNTriplesFile(InputStream input, Graph graph, MapFactory mapFactory) {
        def parserFactory = new NTriplesParserFactory()
        def ntriplesParser = parserFactory.createParser(graph, mapFactory)
        return parseFile(ntriplesParser, input, graph)
    }

    static Set<Triple> parseN3File(InputStream input, Graph graph, MapFactory mapFactory) {
        def parserFactory = new N3ParserFactory()
        def ntriplesParser = parserFactory.createParser(graph, mapFactory)
        return parseFile(ntriplesParser, input, graph)
    }

    static Set<Triple> parseFile(LineHandler lineHandler, InputStream input, Graph graph) {
        def parser = new LineParserImpl(lineHandler)
        parser.setStatementHandler(new GraphStatementHandler(graph))
        parser.parse(input, "foo")
        def actualResults = new HashSet<Triple>()
        graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).each {
            actualResults.add(it)
        }
        return actualResults
    }

    static Set<Triple> standardTestWithN3() {
        Graph newGraph = TestJRDFFactory.factory.newGraph
        addStandardValuesToGraph(newGraph)
        addN3ValuesToGraph(newGraph)
        Set<Triple> answers = new HashSet<Triple>()
        newGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).each {
            answers.add(it)
        }
        return answers
    }

    static Set<Triple> standardTest() {
        Graph newGraph = TestJRDFFactory.factory.newGraph
        addStandardValuesToGraph(newGraph)
        Set<Triple> answers = new HashSet<Triple>()
        newGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).each {
            answers.add(it)
        }
        return answers
    }

    static def addStandardValuesToGraph(Graph newGraph) {
        def rdf = new RdfBuilder(newGraph)
        rdf.with {
            namespace("eg", "http://example.org/")
            namespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#")
            "eg:resource1" "eg:property": "eg:resource2"
            "_:anon" "eg:property": "eg:resource2"
            "eg:resource2" "eg:property": "_:anon"
            (3..6).each {
                "eg:resource$it" "eg:property": "eg:resource2"
            }
            "eg:resource7" "eg:property": '"simple literal"'
            "eg:resource8" "eg:property": '"backslash:\\"'
            "eg:resource9" "eg:property": '"dquote:\""'
            "eg:resource10" "eg:property": '"newline:\\n"'
            "eg:resource11" "eg:property": '"return\\r"'
            "eg:resource12" "eg:property": '"tab:\\t"'
            "eg:resource13" "eg:property": "eg:resource2"
            "eg:resource14" "eg:property": '"x"'
            "eg:resource15" "eg:property": "_:anon"
            "eg:resource16" "eg:property": '"\\u00E9"'
            "eg:resource17"("eg:property": ['"\\u20AC"', '"\\uD800\\uDC00"', '"\\uD84C\\uDFB4"', '"\\uDBFF\\uDFFF"'])
            "eg:resource21" "eg:property": '""^^rdfs:XMLLiteral'
            "eg:resource22" "eg:property": '" "^^rdfs:XMLLiteral'
            "eg:resource23"("eg:property": ['"x"^^rdfs:XMLLiteral', '"\""^^rdfs:XMLLiteral'])
            "eg:resource24" "eg:property": '"<a></a>"^^rdfs:XMLLiteral'
            "eg:resource25" "eg:property": '"a <b></b>"^^rdfs:XMLLiteral'
            "eg:resource26"("eg:property": ['"a <b></b> c"^^rdfs:XMLLiteral', '"a\\n<b></b>\\nc"^^rdfs:XMLLiteral'])
            "eg:resource27" "eg:property": '"chat"^^rdfs:XMLLiteral'
            "eg:resource30" "eg:property": '"chat"@fr'
            "eg:resource31" "eg:property": '"chat"@en'
            "eg:resource32" "eg:property": '"abc"^^eg:datatype1'
        }
    }

    static def addN3ValuesToGraph(Graph newGraph) {
        def rdf = new RdfBuilder(newGraph)
        rdf.namespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
        rdf."_:references1" "rdf:type": "rdf:Bag"
        rdf."_:references1" "rdf:_1": "<http://localhost/misc/UnknownDocument>"
    }
}