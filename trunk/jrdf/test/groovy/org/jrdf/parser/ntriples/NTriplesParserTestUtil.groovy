package org.jrdf.parser.ntriples

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE
import org.jrdf.graph.Graph
import org.jrdf.graph.Triple
import org.jrdf.parser.GraphStatementHandler
import org.jrdf.parser.ParserBlankNodeFactory
import org.jrdf.util.test.RdfBuilder
import org.jrdf.TestJRDFFactory
import org.jrdf.parser.RDFEventReader
import org.jrdf.parser.RDFInputFactory

class NTriplesParserTestUtil {



    private NTriplesParserTestUtil() {
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

    static Set<Triple> parseNTriplesFile(InputStream input, Graph graph, ParserBlankNodeFactory factory) {
        def parserFactory = new NTriplesParserFactoryImpl()
        def ntriplesParser = parserFactory.createParser(graph, factory)
        def parser = new LineParserImpl(ntriplesParser)
        parser.setStatementHandler(new GraphStatementHandler(graph))
        parser.parse(input, "foo")
        def actualResults = new HashSet<Triple>()
        graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).each {
            actualResults.add(it)
        }
        return actualResults
    }

    static Set<Triple> standardTestWithN3() {
        Graph newGraph = TestJRDFFactory.factory.graph
        addStandardValuesToGraph(newGraph)
        addN3ValuesToGraph(newGraph)
        Set<Triple> answers = new HashSet<Triple>()
        newGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).each {
            answers.add(it)
        }
        return answers
    }

    static Set<Triple> standardTest() {
        Graph newGraph = TestJRDFFactory.factory.graph
        addStandardValuesToGraph(newGraph)
        Set<Triple> answers = new HashSet<Triple>()
        newGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).each {
            answers.add(it)
        }
        return answers
    }

    static def addStandardValuesToGraph(Graph newGraph) {
        def rdf = new RdfBuilder(newGraph)
        rdf.namespace("eg", "http://example.org/")
        rdf.namespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#")
        rdf."eg:resource1" "eg:property":"eg:resource2"
        rdf."_:anon" "eg:property":"eg:resource2"
        rdf."eg:resource2" "eg:property":"_:anon"
        (3..6).each {
            rdf."eg:resource$it" "eg:property":"eg:resource2"
        }
        rdf."eg:resource7" "eg:property":'"simple literal"'
        rdf."eg:resource8" "eg:property":'"backslash:\\"'
        rdf."eg:resource9" "eg:property":'"dquote:\""'
        rdf."eg:resource10" "eg:property":'"newline:\\n"'
        rdf."eg:resource11" "eg:property":'"return\\r"'
        rdf."eg:resource12" "eg:property":'"tab:\\t"'
        rdf."eg:resource13" "eg:property":"eg:resource2"
        rdf."eg:resource14" "eg:property":'"x"'
        rdf."eg:resource15" "eg:property":"_:anon"
        rdf."eg:resource16" "eg:property":'"\\u00E9"'
        rdf."eg:resource17" ("eg:property":['"\\u20AC"', '"\\uD800\\uDC00"', '"\\uD84C\\uDFB4"', '"\\uDBFF\\uDFFF"'])
        rdf."eg:resource21" "eg:property":'""^^rdfs:XMLLiteral'
        rdf."eg:resource22" "eg:property":'" "^^rdfs:XMLLiteral'
        rdf."eg:resource23" ("eg:property":['"x"^^rdfs:XMLLiteral', '"\""^^rdfs:XMLLiteral'] )
        rdf."eg:resource24" "eg:property":'"<a></a>"^^rdfs:XMLLiteral'
        rdf."eg:resource25" "eg:property":'"a <b></b>"^^rdfs:XMLLiteral'
        rdf."eg:resource26" ("eg:property":['"a <b></b> c"^^rdfs:XMLLiteral', '"a\\n<b></b>\\nc"^^rdfs:XMLLiteral'])
        rdf."eg:resource27" "eg:property":'"chat"^^rdfs:XMLLiteral'
        rdf."eg:resource30" "eg:property":'"chat"@fr'
        rdf."eg:resource31" "eg:property":'"chat"@en'
        rdf."eg:resource32" "eg:property":'"abc"^^eg:datatype1'
    }

    static def addN3ValuesToGraph(Graph newGraph) {
        def rdf = new RdfBuilder(newGraph)
        rdf.namespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
        rdf."_:references1" "rdf:type":"rdf:Bag"
        rdf."_:references1" "rdf:_1":"<http://localhost/misc/UnknownDocument>"
    }
}