package org.jrdf.sparql;

/**
 * Example SPARQL queries used in tests.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class SparqlQueryTestUtil {
    private static final String URI_DC_TITLE = "<http://purl.org/dc/elements/1.1/title>";
    private static final String URI_BOOK_1 = "<http://example.org/book/book1>";
    public static final String QUERY_BOOK_1_DC_TITLE =
            "SELECT * WHERE  { " + URI_BOOK_1 + " " + URI_DC_TITLE + " ?title }";
}
