package org.jrdf.query.answer.xml;

import junit.framework.TestCase;
import static org.jrdf.query.answer.xml.SparqlResultType.BLANK_NODE;
import static org.jrdf.query.answer.xml.SparqlResultType.LITERAL;
import static org.jrdf.query.answer.xml.SparqlResultType.TYPED_LITERAL;
import static org.jrdf.query.answer.xml.SparqlResultType.URI_REFERENCE;
import static org.jrdf.util.test.SetUtil.asSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SparqlAnswerParserUnitTest extends TestCase {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private static final TypeValue R1C1 = new TypeValue(BLANK_NODE, "r1");
    private static final TypeValue R2C1 = new TypeValue(BLANK_NODE, "r2");
    private static final TypeValue R1C2 = new TypeValue(URI_REFERENCE, "http://work.example.org/alice/");
    private static final TypeValue R2C2 = new TypeValue(URI_REFERENCE, "http://work.example.org/bob/");
    private static final TypeValue R1C3 = new TypeValue(LITERAL, "Alice");
    private static final TypeValue R2C3 = new TypeValue(LITERAL, "Bob", false, "en");
    private static final TypeValue R1C4 = new TypeValue(LITERAL, "");
    private static final TypeValue R2C4 = new TypeValue(URI_REFERENCE, "mailto:bob@work.example.org");
    private static final TypeValue R1C5 = new TypeValue();
    private static final TypeValue R2C5 = new TypeValue(TYPED_LITERAL, "30", true, "http://www.w3.org/2001/XMLSchema#integer");
    private static final TypeValue R1C6 = new TypeValue(TYPED_LITERAL, "&lt;p xmlns=\"http://www.w3.org/1999/xhtml\"&gt;My name is &lt;b&gt;alice&lt;/b&gt;&lt;/p&gt;", true, "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");
    private static final TypeValue R2C6 = new TypeValue();
    private static final TypeValue R1C7 = new TypeValue(BLANK_NODE, "r2");
    private static final TypeValue R2C7 = new TypeValue(BLANK_NODE, "r1");
    private static final List<TypeValue> ROW_1 = Arrays.asList(R1C1, R1C2, R1C3, R1C4, R1C5, R1C6, R1C7);
    private static final List<TypeValue> ROW_2 = Arrays.asList(R2C1, R2C2, R2C3, R2C4, R2C5, R2C6, R2C7);
    private SparqlAnswerParser parser;

    public void testParse() throws Exception {
        URL resource = getClass().getClassLoader().getResource("org/jrdf/query/answer/xml/data/output.xml");
        InputStream stream = resource.openStream();
        XMLStreamReader streamReader = INPUT_FACTORY.createXMLStreamReader(stream);
        parser = new SparqlAnswerParser(streamReader);
        assertTrue(parser.hasMoreResults());
        Set<String> expectedVariables = asSet("x", "hpage", "name", "mbox", "age", "blurb", "friend");
        assertEquals(expectedVariables, parser.getVariables());
        checkHasMoreAndGetResult(ROW_1);
        checkHasMoreAndGetResult(ROW_2);
        assertFalse(parser.hasMoreResults());
        assertFalse(parser.hasMoreResults());
    }

    private void checkHasMoreAndGetResult(List<TypeValue> row) throws XMLStreamException {
        assertTrue(parser.hasMoreResults());
        assertTrue(parser.hasMoreResults());
        TypeValue[] results = parser.getResults();
        checkRow(results, row);
    }

    private void checkRow(TypeValue[] actualResults, List<TypeValue> execptedResults) {
        for (int i = 0; i < execptedResults.size(); i++) {
            assertEquals(execptedResults.get(i), actualResults[i]);
        }
    }
}
