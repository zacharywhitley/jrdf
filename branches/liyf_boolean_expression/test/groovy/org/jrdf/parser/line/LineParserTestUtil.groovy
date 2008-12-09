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

class LineParserTestUtil {

    private LineParserTestUtil() {
    }


    static InputStream getSampleData(Class clazz, String fileName) throws IOException {
        URL source = clazz.getClassLoader().getResource(fileName)
        return source.openStream()
    }

    static Set<Triple> getTriplesWithReader(RDFEventReader eventReader) {
        def actualResults = new HashSet<Triple>()
        eventReader.each {
            actualResults.add(it);
        }
        return actualResults;
    }

    static Set<Triple> parseNTriplesFile(InputStream input, Graph graph, MapFactory mapFactory) {
        def parserFactory = new NTriplesParserFactory()
        def ntriplesParser = parserFactory.createParser(graph, mapFactory)
        return parseFile(ntriplesParser, input, graph);
    }

    static Set<Triple> parseN3File(InputStream input, Graph graph, MapFactory mapFactory) {
        def parserFactory = new N3ParserFactory()
        def ntriplesParser = parserFactory.createParser(graph, mapFactory)
        return parseFile(ntriplesParser, input, graph);
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