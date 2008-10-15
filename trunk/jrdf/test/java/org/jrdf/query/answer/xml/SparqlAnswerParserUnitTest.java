package org.jrdf.query.answer.xml;

import junit.framework.TestCase;
import static org.jrdf.util.test.SetUtil.asSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

public class SparqlAnswerParserUnitTest extends TestCase {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();

    public void testParse() throws Exception {
        URL resource = getClass().getClassLoader().getResource("org/jrdf/query/answer/xml/data/output.xml");
        InputStream stream = resource.openStream();
        XMLStreamReader streamReader = INPUT_FACTORY.createXMLStreamReader(stream);
        SparqlAnswerParser parser = new SparqlAnswerParser(streamReader);
        assertTrue(parser.hasMoreResults());
        Set<String> expectedVariables = asSet("x", "hpage", "name", "mbox", "age", "blurb", "friend");
        assertEquals(expectedVariables, parser.getVariables());
        assertTrue(parser.hasMoreResults());
        assertTrue(parser.hasMoreResults());
        TypeValue[] results = parser.getResults();
    }
}
