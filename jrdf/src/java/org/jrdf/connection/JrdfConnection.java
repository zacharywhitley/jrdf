package org.jrdf.connection;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.relation.Relation;

/**
 * A connection through which to send textual commands.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public interface JrdfConnection {

    /**
     * Executes a query that returns results.
     *
     * @param queryText The query to execute.
     * @param graph The graph to query.
     * @return The answer to the query, will never be <code>null</code>.
     * @throws org.jrdf.query.InvalidQuerySyntaxException
     *                        If the syntax of the <code>queryText</code> is incorrect.
     * @throws GraphException If an error occurs while executing the query.
     */
    Relation executeQuery(String queryText, Graph graph) throws InvalidQuerySyntaxException, GraphException;
}
