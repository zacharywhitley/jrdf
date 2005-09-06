package org.jrdf.connection;

import org.jrdf.query.Answer;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.graph.GraphException;

/**
 * A connection through which to send textual commands.
 * @author Tom Adams
 * @version $Revision$
 */
public interface JrdfConnection {

    /**
     * Executes a query that returns results.
     * @param queryText The query to execute.
     * @return The answer to the query, will never be <code>null</code>.
     * @throws org.jrdf.query.InvalidQuerySyntaxException If the syntax of the <code>queryText</code> is incorrect.
     * @throws GraphException If an error occurs while executing the query.
     */
    Answer executeQuery(String queryText) throws InvalidQuerySyntaxException,  GraphException;

    /**
     * Closes the connection to the graph.
     * <p>Calling this method will close the underlying {@link org.jrdf.graph.Graph}, making it unusable for future
     * use.</p>
     */
    void close();
}
