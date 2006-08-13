package org.jrdf.example;

import org.jrdf.JRDFFactoryImpl;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.parser.Parser;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;
import org.jrdf.util.EscapeURL;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

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
        final Graph jrdfMem = getGraph();
        Parser parser = new GraphRdfXmlParser(jrdfMem);
        parser.parse(in, EscapeURL.toEscapedString(url));
        Iterator iter = jrdfMem.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        while (iter.hasNext()) {
            System.out.println("Graph: " + iter.next());
        }
        in.close();
    }

    private static Graph getGraph() {
        return JRDFFactoryImpl.getFactory().getNewGraph();
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
