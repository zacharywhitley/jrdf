package org.jrdf.query.answer.xml;

import junit.framework.TestCase;
import static org.jrdf.query.answer.xml.SparqlResultType.BLANK_NODE;
import static org.jrdf.query.answer.xml.SparqlResultType.LITERAL;
import static org.jrdf.query.answer.xml.SparqlResultType.TYPED_LITERAL;
import static org.jrdf.query.answer.xml.SparqlResultType.URI_REFERENCE;
import static org.jrdf.util.test.SetUtil.asSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SparqlAnswerParserImplUnitTest extends TestCase {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private static final TypeValueImpl R1C1 = new TypeValueImpl(BLANK_NODE, "r1");
    private static final TypeValueImpl R2C1 = new TypeValueImpl(BLANK_NODE, "r2");
    private static final TypeValueImpl R1C2 = new TypeValueImpl(URI_REFERENCE, "http://work.example.org/alice/");
    private static final TypeValueImpl R2C2 = new TypeValueImpl(URI_REFERENCE, "http://work.example.org/bob/");
    private static final TypeValueImpl R1C3 = new TypeValueImpl(LITERAL, "Alice");
    private static final TypeValueImpl R2C3 = new TypeValueImpl(LITERAL, "Bob", false, "en");
    private static final TypeValueImpl R1C4 = new TypeValueImpl(LITERAL, "");
    private static final TypeValueImpl R2C4 = new TypeValueImpl(URI_REFERENCE, "mailto:bob@work.example.org");
    private static final TypeValueImpl R1C5 = new TypeValueImpl();
    private static final TypeValueImpl R2C5 = new TypeValueImpl(TYPED_LITERAL, "30", true, "http://www.w3.org/2001/XMLSchema#integer");
    private static final TypeValueImpl R1C6 = new TypeValueImpl(TYPED_LITERAL, "&lt;p xmlns=\"http://www.w3.org/1999/xhtml\"&gt;My name is &lt;b&gt;alice&lt;/b&gt;&lt;/p&gt;", true, "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");
    private static final TypeValueImpl R2C6 = new TypeValueImpl();
    private static final TypeValueImpl R1C7 = new TypeValueImpl(BLANK_NODE, "r2");
    private static final TypeValueImpl R2C7 = new TypeValueImpl(BLANK_NODE, "r1");
    public static final List<TypeValueImpl> ROW_1 = Arrays.asList(R1C1, R1C2, R1C3, R1C4, R1C5, R1C6, R1C7);
    public static final List<TypeValueImpl> ROW_2 = Arrays.asList(R2C1, R2C2, R2C3, R2C4, R2C5, R2C6, R2C7);
    private SparqlAnswerParser parser;

    public void testParse() throws Exception {
        URL resource = getClass().getClassLoader().getResource("org/jrdf/query/answer/xml/data/output.xml");
        InputStream stream = resource.openStream();
        XMLStreamReader streamReader = INPUT_FACTORY.createXMLStreamReader(stream);
        parser = new SparqlAnswerParserImpl(streamReader);
        assertTrue(parser.hasMoreResults());
        Set<String> expectedVariables = asSet("x", "hpage", "name", "mbox", "age", "blurb", "friend");
        assertEquals(expectedVariables, parser.getVariables());
        checkHasMoreAndGetResult(ROW_1);
        checkHasMoreAndGetResult(ROW_2);
        assertFalse(parser.hasMoreResults());
        assertFalse(parser.hasMoreResults());
    }

    private void checkHasMoreAndGetResult(List<TypeValueImpl> row) {
        assertTrue(parser.hasMoreResults());
        assertTrue(parser.hasMoreResults());
        TypeValue[] results = parser.getResults();
        checkRow(results, row);
    }

    private void checkRow(TypeValue[] actualResults, List<TypeValueImpl> execptedResults) {
        for (int i = 0; i < execptedResults.size(); i++) {
            assertEquals(execptedResults.get(i), actualResults[i]);
        }
    }
}
