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

    static Set<Triple> standardTest(Graph newGraph, ParserBlankNodeFactory blankNodeFactory) {
        def answers = new HashSet<Triple>()
        def refs = new ArrayList<Resource>()
        def graphElementFactory = newGraph.getElementFactory()
        (0..32).each {
            refs.add(graphElementFactory.createResource(create("http://example.org/resource" + it)))
        }
        URI p = create("http://example.org/property")
        Resource anon = graphElementFactory.createResource(blankNodeFactory.createBlankNode("anon"));
        answers.add(refs[1].asTriple(p, refs[2]))
        answers.add(anon.asTriple(p, refs[2]))
        answers.add(refs[2].asTriple(p, anon))
        answers.add(refs[3].asTriple(p, refs[2]))
        answers.add(refs[4].asTriple(p, refs[2]))
        answers.add(refs[5].asTriple(p, refs[2]))
        answers.add(refs[6].asTriple(p, refs[2]))
        answers.add(refs[7].asTriple(p, "simple literal"))
        answers.add(refs[8].asTriple(p, "backslash:\\"))
        answers.add(refs[9].asTriple(p, "dquote:\""))
        answers.add(refs[10].asTriple(p, "newline:\n"))
        answers.add(refs[11].asTriple(p, "return\r"))
        answers.add(refs[12].asTriple(p, "tab:\t"))
        answers.add(refs[13].asTriple(p, refs[2]))
        answers.add(refs[14].asTriple(p, "x"))
        answers.add(refs[15].asTriple(p, anon))
        answers.add(refs[16].asTriple(p, "\u00E9"))
        answers.add(refs[17].asTriple(p, "\u20AC"))
        answers.add(refs[17].asTriple(p, "\uD800\uDC00"))
        answers.add(refs[17].asTriple(p, "\uD84C\uDFB4"))
        answers.add(refs[17].asTriple(p, "\uDBFF\uDFFF"))
        URI xmlLiteral = create("http://www.w3.org/2000/01/rdf-schema#XMLLiteral");
        answers.add(refs[21].asTriple(p, "", xmlLiteral))
        answers.add(refs[22].asTriple(p, " ", xmlLiteral))
        answers.add(refs[23].asTriple(p, "x", xmlLiteral))
        answers.add(refs[23].asTriple(p, "\"", xmlLiteral))
        answers.add(refs[24].asTriple(p, "<a></a>", xmlLiteral))
        answers.add(refs[25].asTriple(p, "a <b></b>", xmlLiteral))
        answers.add(refs[26].asTriple(p, "a <b></b> c", xmlLiteral))
        answers.add(refs[26].asTriple(p, "a\n<b></b>\nc", xmlLiteral))
        answers.add(refs[27].asTriple(p, "chat", xmlLiteral))
        answers.add(refs[30].asTriple(p, "chat", "fr"))
        answers.add(refs[31].asTriple(p, "chat", "en"))
        answers.add(refs[32].asTriple(p, "abc", create("http://example.org/datatype1")))
        return answers;
    }
}