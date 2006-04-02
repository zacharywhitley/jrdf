package org.jrdf.example;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.mem.GraphImpl;
import org.jrdf.parser.StatementHandler;
import org.jrdf.parser.rdfxml.RdfXmlParser;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public final class RdfXmlParserExample {

    private static final String DEFAULT_RDF_URL = "http://rss.slashdot.org/Slashdot/slashdot";

    public static void main(String[] args) throws Exception {
        URL url = getDocumentURL(args);
        InputStream in = url.openStream();
        final Graph jrdfMem = new GraphImpl();
        RdfXmlParser parser = new RdfXmlParser(jrdfMem.getElementFactory());
        ExampleStatementHandler sh = new ExampleStatementHandler(jrdfMem);
        parser.setStatementHandler(sh);
        parser.parse(in, url.toURI().toString());
        Iterator iter = jrdfMem.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        while (iter.hasNext()) {
            System.out.println("Graph: " + iter.next());
        }
        in.close();
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

    private static class ExampleStatementHandler implements StatementHandler {

        private final Graph jrdfMem;

        public ExampleStatementHandler(Graph jrdfMem) {
            this.jrdfMem = jrdfMem;
        }

        public void handleStatement(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
            try {
                jrdfMem.add(subject, predicate, object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
