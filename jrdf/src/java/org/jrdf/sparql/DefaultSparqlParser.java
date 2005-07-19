package org.jrdf.sparql;

import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.query.Query;
import org.jrdf.query.DefaultQuery;

/**
 * Default implementation of a {@link SparqlParser}.
 * @author Tom Adams
 * @version $Revision$
 */
final class DefaultSparqlParser extends DepthFirstAdapter implements SparqlParser {

    /**
     * Parses a textual query into a {@link org.jrdf.query.Query} object.
     *
     * @param queryText The textual query to parse.
     * @return A query object representing the <var>queryText</var>, will never be <code>null</code>.
     */
    public Query parseQuery(String queryText) {
        // FIXME: Breadcrumb - Triangulate to force a parsing of the query.
        return new DefaultQuery();
    }
}
