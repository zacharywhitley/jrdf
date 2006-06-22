package org.jrdf.connection;

import org.jrdf.query.JrdfQueryExecutorFactory;
import org.jrdf.query.QueryBuilder;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.sparql.SparqlConnectionImpl;
import org.jrdf.util.param.ParameterUtil;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Returns queriable connections to a graph.
 *
 * @author Tom Adams
 * @version $Id$
 */
public final class JrdfConnectionFactory {

    private static final String JRDF_NAMESPACE = "http://jrdf.sf.net/";
    private static final String JRDF_CONNECTION_NAMESPACE = JRDF_NAMESPACE + "connection";

    /**
     * Indicates that no security is enabled on the graph being queried.
     */
    public static final URI NO_SECURITY_DOMAIN = URI.create(JRDF_CONNECTION_NAMESPACE + "#NO_SECURITY");

    /**
     * URL version of NO_SECURITY_DOMAIN - for Spring wiring (it doesn't have a URI PropertyEditor).
     */
    public static final URL NO_SECURITY_DOMAIN_URL = tryConvertUri();

    /**
     * Returns a connection to through which to send SPARQL queries.
     * <p>Note. A new connection is returned for each call, they are not pooled. Clients should ensure that they call
     * close on the connection once it is no longer required, the system will not clean up the connection
     * automatically.</p>
     *
     * @param securityDomain The security domain.
     * @return A connection through which to issue SPARQL queries.
     */
    public SparqlConnection createSparqlConnection(URL securityDomain, QueryBuilder builder,
            JrdfQueryExecutorFactory queryExecutorFactory) {
        ParameterUtil.checkNotNull("securityDomain", securityDomain);
        ParameterUtil.checkNotNull("builder", builder);
        ParameterUtil.checkNotNull("queryExecutorFactory", queryExecutorFactory);
        return new SparqlConnectionImpl(securityDomain, builder, queryExecutorFactory);
    }

    private static URL tryConvertUri() {
        try {
            return NO_SECURITY_DOMAIN.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
