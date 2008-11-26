package org.jrdf.parser.ntriples

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE
import static java.net.URI.create
import org.jrdf.graph.BlankNode
import org.jrdf.graph.Graph
import org.jrdf.graph.Triple
import org.jrdf.graph.URIReference
import org.jrdf.parser.GraphStatementHandler
import org.jrdf.parser.ParserBlankNodeFactory
import org.jrdf.graph.Resource
import org.jrdf.util.test.RdfBuilder
import org.jrdf.util.test.RdfNamespace
import org.jrdf.TestJRDFFactory

class NTriplesParserTestUtil {


    private NTriplesParserTestUtil() {
    }

    static InputStream getSampleData(Class clazz, String fileName) throws IOException {
        URL source = clazz.getClassLoader().getResource(fileName)
        return source.openStream()
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
        def refs = new ArrayList<Resource>()
        def graphElementFactory = newGraph.getElementFactory()
        (0..32).each {
            refs.add(graphElementFactory.createResource(create("http://example.org/resource" + it)))
        }
        URI p = create("http://example.org/property")
        Resource anon = graphElementFactory.createResource();
        def rdf = new RdfBuilder(newGraph)
        def eg = rdf.namespace("eg", "http://example.org/");
        rdf.eg.resource1 "eg:property":"eg:resource2"
        rdf."_:anon" "eg:property":"eg:resource2"
        rdf."eg:resource2" "eg:property":"_:anon"
        (3..6).each {
            rdf."eg:resource$it" "eg:property":"eg:resource2"
        }
        rdf."eg:resource7" "eg:property":'"simple literal"'
        rdf."eg:resource8" "eg:property":'"backslash:\\"'
        rdf."eg:resource9" "eg:property":'"dquote:\""'
        newGraph.add(refs[10].asTriple(p, "newline:\n"))
        newGraph.add(refs[11].asTriple(p, "return\r"))
        newGraph.add(refs[12].asTriple(p, "tab:\t"))
        newGraph.add(refs[13].asTriple(p, refs[2]))
        newGraph.add(refs[14].asTriple(p, "x"))
        newGraph.add(refs[15].asTriple(p, anon))
        newGraph.add(refs[16].asTriple(p, "\u00E9"))
        newGraph.add(refs[17].asTriple(p, "\u20AC"))
        newGraph.add(refs[17].asTriple(p, "\uD800\uDC00"))
        newGraph.add(refs[17].asTriple(p, "\uD84C\uDFB4"))
        newGraph.add(refs[17].asTriple(p, "\uDBFF\uDFFF"))
        URI xmlLiteral = create("http://www.w3.org/2000/01/rdf-schema#XMLLiteral");
        newGraph.add(refs[21].asTriple(p, "", xmlLiteral))
        newGraph.add(refs[22].asTriple(p, " ", xmlLiteral))
        newGraph.add(refs[23].asTriple(p, "x", xmlLiteral))
        newGraph.add(refs[23].asTriple(p, "\"", xmlLiteral))
        newGraph.add(refs[24].asTriple(p, "<a></a>", xmlLiteral))
        newGraph.add(refs[25].asTriple(p, "a <b></b>", xmlLiteral))
        newGraph.add(refs[26].asTriple(p, "a <b></b> c", xmlLiteral))
        newGraph.add(refs[26].asTriple(p, "a\n<b></b>\nc", xmlLiteral))
        newGraph.add(refs[27].asTriple(p, "chat", xmlLiteral))
        newGraph.add(refs[30].asTriple(p, "chat", "fr"))
        newGraph.add(refs[31].asTriple(p, "chat", "en"))
        newGraph.add(refs[32].asTriple(p, "abc", create("http://example.org/datatype1")))
    }
}