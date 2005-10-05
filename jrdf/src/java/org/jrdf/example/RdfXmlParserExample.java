package org.jrdf.example;

import static org.jrdf.graph.AnyObjectNode.*;
import static org.jrdf.graph.AnyPredicateNode.*;
import static org.jrdf.graph.AnySubjectNode.*;
import org.jrdf.graph.Graph;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.AnyPredicateNode;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.mem.GraphImpl;
import org.jrdf.parser.StatementHandler;
import org.jrdf.parser.rdfxml.RdfXmlParser;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

public class RdfXmlParserExample {
    public static void main(String[] args) throws Exception {
        String baseURI = "http://rss.slashdot.org/Slashdot/slashdot";
        URL url = new URL(baseURI);
        InputStream is = url.openStream();
        final Graph jrdfMem = new GraphImpl();
        RdfXmlParser parser = new RdfXmlParser(jrdfMem.getElementFactory());
        parser.setStatementHandler(new StatementHandler() {
            public void handleStatement(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
                try {
                    jrdfMem.add(subject, predicate, object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        parser.parse(is, baseURI);
        Iterator iter = jrdfMem.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        while (iter.hasNext()) {
            System.err.println("Graph: " + iter.next());
        }
        is.close();
    }
}
