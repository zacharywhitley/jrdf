package org.jrdf.sparql;

import junit.framework.TestCase;
import org.jrdf.query.Query;
import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.util.test.ClassPropertiesTestUtil;

/**
 * Unit test for {@link DefaultSparqlParser}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class DefaultSparqlParserUnitTest extends TestCase {
    // FIXME: Make sure that empty variable projection lists don't make it past the parser, as the Variable.ALL_VARIABLES is the empty list.

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkExtensionOf(DepthFirstAdapter.class, DefaultSparqlParser.class);
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(SparqlParser.class, DefaultSparqlParser.class);
    }

    // FIXME: Breadcrumb - Triangulate to force a parsing of the query.
    public void testParseQuery() {
        QueryParser parser = new DefaultSparqlParser();
        Query query = parser.parseQuery(SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE);
        //List<Variable> variables = query.getProjectedVariables();
        assertNotNull(query);
    }
}
