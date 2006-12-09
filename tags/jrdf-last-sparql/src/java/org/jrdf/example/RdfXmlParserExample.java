package org.jrdf.example;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.index.longindex.mem.LongIndexMem;
import org.jrdf.graph.index.nodepool.mem.NodePoolMemImpl;
import org.jrdf.graph.mem.GraphFactory;
import org.jrdf.graph.mem.GraphFactoryImpl;
import org.jrdf.parser.Parser;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.EscapeURL;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple example of parsing in a RDF/XML file into an in memory JRDF graph.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class RdfXmlParserExample {

    private static final String DEFAULT_RDF_URL = "http://rss.slashdot.org/Slashdot/slashdot";

    public static void main(String[] args) throws Exception {
        URL url = getDocumentURL(args);
        InputStream in = url.openStream();
        try {
            final Graph jrdfMem = getGraph();
            Parser parser = new GraphRdfXmlParser(jrdfMem);
            parser.parse(in, EscapeURL.toEscapedString(url));
            ClosableIterator iter = jrdfMem.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
            try {
                while (iter.hasNext()) {
                    System.out.println("Graph: " + iter.next());
                }
                System.out.println("Total number of statements: " + jrdfMem.getNumberOfTriples());
            } finally {
                iter.close();
            }
        } finally {
            in.close();
        }
    }

    private static Graph getGraph() {
        LongIndex[] longIndexes = {new LongIndexMem(), new LongIndexMem(), new LongIndexMem()};
        GraphFactory factory = new GraphFactoryImpl(longIndexes, new NodePoolMemImpl());
        return factory.getGraph();
    }

    private static URL getDocumentURL(String[] args) throws MalformedURLException {
        String baseURL;
        if (args.length == 0 || args[0].length() == 0) {
            System.out.println("First argument empty so using: " + DEFAULT_RDF_URL);
            baseURL = DEFAULT_RDF_URL;
        } else {
            baseURL = args[0];
        }
        return new URL(baseURL);
    }
}
