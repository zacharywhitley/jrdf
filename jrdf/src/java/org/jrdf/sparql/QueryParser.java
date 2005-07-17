package org.jrdf.sparql;

import org.jrdf.query.Query;

/**
 * Parses {@link String}s into {@linkplain org.jrdf.query.Query queries}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
interface QueryParser {

    /**
     * Parses a textual query into a {@link Query} object.
     * @param queryText The textual query to parse.
     * @return A query object representing the <var>queryText</var>, will never be <code>null</code>.
     */
    Query parseQuery(String queryText);
}
