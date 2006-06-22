package org.jrdf.connection;

import junit.framework.TestCase;
import org.jrdf.query.JrdfQueryExecutorFactory;
import org.jrdf.query.QueryBuilder;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.util.test.MockTestUtil;
import org.jrdf.util.test.ArgumentTestUtil;
import org.jrdf.util.test.ParameterDefinition;

import java.net.URI;
import java.net.URL;

/**
 * Unit test for {@link JrdfConnectionFactory}.
 *
 * @author Tom Adams
 * @version $Id$
 */
public class JrdfConnectionFactoryUnitTest extends TestCase {

    private static final String EXPECTED_NO_SECURITY_DOMAIN = "http://jrdf.sf.net/connection#NO_SECURITY";
    private static final URL NO_SECURITY_DOMAIN = JrdfConnectionFactory.NO_SECURITY_DOMAIN_URL;
    private static final JrdfQueryExecutorFactory QUERY_EXECUTOR_FACTORY
            = MockTestUtil.createMock(JrdfQueryExecutorFactory.class);
    private static final QueryBuilder QUERY_BUILDER = MockTestUtil.createMock(QueryBuilder.class);
    private static final String METHOD_NAME = "createSparqlConnection";
    private static final String[] PARAMETER_NAMES = new String[] {"securityDomain", "builder", "queryExecutorFactory"};
    private static final Class[] PARAMETER_TYPES =
            new Class[] {URL.class, QueryBuilder.class, JrdfQueryExecutorFactory.class};
    private static final ParameterDefinition PARAM_DEFINITION = new ParameterDefinition(PARAMETER_NAMES, PARAMETER_TYPES );

    public void testNoSecurityConstant() {
        assertEquals(URI.create(EXPECTED_NO_SECURITY_DOMAIN), JrdfConnectionFactory.NO_SECURITY_DOMAIN);
    }

    public void testCreateSparqlConnectionNullContract() {
        ArgumentTestUtil.checkMethodNullAssertions(PARAM_DEFINITION, createFactory(), METHOD_NAME);
    }

    public void testGetSparqlConnection() {
        assertNotNull(createConnectionWithBadGraph());
    }

    public void testCreateConnectionReturnsNewConnectionEachTime() {
        SparqlConnection connection1 = createConnectionWithBadGraph();
        SparqlConnection connection2 = createConnectionWithBadGraph();
        assertNotSame(connection1, connection2);
    }

    private SparqlConnection createConnectionWithBadGraph() {
        return createFactory().createSparqlConnection(NO_SECURITY_DOMAIN, QUERY_BUILDER, QUERY_EXECUTOR_FACTORY);
    }

    private JrdfConnectionFactory createFactory() {
        return new JrdfConnectionFactory();
    }
}
