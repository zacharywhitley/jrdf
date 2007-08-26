package org.jrdf.graph.datatype;

import junit.framework.TestCase;
import org.jrdf.graph.AbstractLiteral;
import org.jrdf.graph.Literal;
import org.jrdf.vocabulary.XSD;

import java.net.URI;

public class SemanticComparatorImplUnitTest extends TestCase {
    private SemanticComparator semanticComparator;

    public void setUp() {
        semanticComparator = new SemanticComparatorImpl(new LexicalComparatorImpl());
    }

    public void testSemanticallyEqualNumbers() {
        final Literal literal1 = new TestLiteral("4", XSD.INT);
        final Literal literal2 = new TestLiteral("04", XSD.INTEGER);
        final Literal literal3 = new TestLiteral("4", XSD.LONG);
        final Literal literal4 = new TestLiteral("4", XSD.FLOAT);
        final Literal literal5 = new TestLiteral("4.0", XSD.FLOAT);
        assertEquals(0, semanticComparator.compare(literal1, literal2));
        assertEquals(0, semanticComparator.compare(literal2, literal3));
        assertEquals(0, semanticComparator.compare(literal4, literal5));
    }

    public class TestLiteral extends AbstractLiteral {
        protected TestLiteral(String newLexicalForm, URI newDatatypeURI) {
            super(newLexicalForm, newDatatypeURI);
        }
    }
}
