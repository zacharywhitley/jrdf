package org.jrdf.connection;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.query.Answer;
import org.jrdf.query.InvalidQuerySyntaxException;

/**
 * A connection through which to send textual commands.
 *
 * @version $Revision$
 */
public interface JrdfConnection {

    /**
     * Executes a query that returns results.
     *
     * @param graph The graph to query.
     * @param queryText The query to execute.
     * @return The answer to the query, will never be <code>null</code>.
     * @throws org.jrdf.query.InvalidQuerySyntaxException
     *                        If the syntax of the <code>queryText</code> is incorrect.
     * @throws GraphException If an error occurs while executing the query.
     */
    Answer executeQuery(Graph graph, String queryText) throws InvalidQuerySyntaxException, GraphException;
}
