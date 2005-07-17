package org.jrdf.sparql;

import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.query.Query;

/**
 * Parses <a href="http://www.w3.org/TR/rdf-sparql-query/">SPARQL</a> textual queries into
 * {@linkplain org.jrdf.query.Query queries}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
final class SparqlParser extends DepthFirstAdapter implements QueryParser {

    /**
     * Parses a textual query into a {@link org.jrdf.query.Query} object.
     *
     * @param queryText The textual query to parse.
     * @return A query object representing the <var>queryText</var>, will never be <code>null</code>.
     */
    public Query parseQuery(String queryText) {
        throw new UnsupportedOperationException("Implement me...");
    }
}
