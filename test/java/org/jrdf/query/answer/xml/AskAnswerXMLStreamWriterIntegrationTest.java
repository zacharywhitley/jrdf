package org.jrdf.query.answer.xml;

import com.ctc.wstx.api.WstxInputProperties;
import junit.framework.TestCase;
import org.jrdf.PersistentGlobalJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactoryImpl;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.answer.AskAnswer;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.BOOLEAN;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;

import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import static javax.xml.stream.events.XMLEvent.CHARACTERS;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */

public class AskAnswerXMLStreamWriterIntegrationTest extends TestCase {
    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
    {
        XML_INPUT_FACTORY.setProperty(WstxInputProperties.P_INPUT_PARSING_MODE,
            WstxInputProperties.PARSING_MODE_FRAGMENT);
    }
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final PersistentGlobalJRDFFactory FACTORY = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
    private static final UrqlConnection URQL_CONNECTION = TestJRDFFactory.getFactory().getNewUrqlConnection();

    private Writer writer;
    private XMLStreamReader reader;
    private AnswerXMLWriter xmlWriter;
    private MoleculeGraph graph;
    private GraphElementFactory elementFactory;
    private BlankNode b1;
    private BlankNode b2;
    private BlankNode b3;
    private URIReference p1;
    private URIReference p2;
    private URIReference p3;
    private Literal l1;
    private Literal l2;
    private Literal l3;

    public void setUp() throws Exception {
        super.setUp();
        HANDLER.removeDir();
        HANDLER.makeDir();
        FACTORY.refresh();
        writer = new StringWriter();
        graph = FACTORY.getNewGraph("foo");
        elementFactory = graph.getElementFactory();
        b1 = elementFactory.createBlankNode();
        b2 = elementFactory.createBlankNode();
        b3 = elementFactory.createBlankNode();
        p1 = elementFactory.createURIReference(URI.create("urn:p1"));
        p2 = elementFactory.createURIReference(URI.create("urn:p2"));
        p3 = elementFactory.createURIReference(URI.create("urn:p3"));
        l1 = elementFactory.createLiteral("l1");
        l2 = elementFactory.createLiteral("l2");
        l3 = elementFactory.createLiteral("l3");
    }

    public void tearDown() throws Exception {
        super.tearDown();
        graph.close();
        if (xmlWriter != null) {
            xmlWriter.close();
        }
        HANDLER.removeDir();
    }

    public void testAskAnswer() throws GraphException, InvalidQuerySyntaxException, XMLStreamException {
        graph.add(b1, p1, l1);
        graph.add(b2, p2, l2);
        graph.add(b3, p3, l3);
        String queryString = "ASK WHERE {?s ?p ?o .}";
        final AskAnswer answer = (AskAnswer) URQL_CONNECTION.executeQuery(graph, queryString);
        xmlWriter = new AskAnswerXMLStreamWriter(answer, writer);
        checkResult(true);
    }

    public void testAskEmptyGraph() throws XMLStreamException, InvalidQuerySyntaxException, GraphException {
        String queryString = "ASK WHERE {?s ?p ?o .}";
        final AskAnswer answer = (AskAnswer) URQL_CONNECTION.executeQuery(graph, queryString);
        xmlWriter = new AskAnswerXMLStreamWriter(answer, writer);
        checkResult(false);
    }

    public void testAskNonMatchingGraph() throws GraphException, XMLStreamException, InvalidQuerySyntaxException {
        graph.add(b1, p1, l1);
        String queryString = "ASK WHERE {?s ?p ?o FILTER ( str(?o) = \"ab\" ) }";
        final AskAnswer answer = (AskAnswer) URQL_CONNECTION.executeQuery(graph, queryString);
        xmlWriter = new AskAnswerXMLStreamWriter(answer, writer);
        checkResult(false);
    }

    private void checkResult(boolean value) throws XMLStreamException {
        assertTrue(xmlWriter.hasMoreResults());
        xmlWriter.writeResult();
        String result = writer.toString();
        reader = XML_INPUT_FACTORY.createXMLStreamReader(new StringReader(result));
        while (reader.hasNext()) {
            int eventType = reader.getEventType();
            switch (eventType) {
                case START_ELEMENT:
                    assertEquals(BOOLEAN, reader.getLocalName());
                    break;
                case CHARACTERS:
                    assertEquals(value, Boolean.parseBoolean(reader.getText()));
                    break;
                default:
                    break;
            }
            reader.next();
        }
        assertFalse(xmlWriter.hasMoreResults());
    }
}
