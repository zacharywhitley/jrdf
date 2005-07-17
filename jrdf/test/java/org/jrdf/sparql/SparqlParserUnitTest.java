package org.jrdf.sparql;

import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.query.Query;

/**
 * Unit test for {@link SparqlParser}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class SparqlParserUnitTest extends TestCase {

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkSubclassOf(DepthFirstAdapter.class, SparqlParser.class);
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(QueryParser.class, SparqlParser.class);
    }

    // FIXME: Implement...!
    public void xxxTestParseQuery() {
        QueryParser parser = new SparqlParser();
        Query query = parser.parseQuery(SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE);
        // FIXME: assertNotNull(query);
    }
}
