package org.jrdf.connection;

import junit.framework.TestCase;
import org.jrdf.graph.Graph;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.util.test.AssertThrows;
import org.jrdf.util.test.MockTestUtil;
import org.jrdf.query.JrdfQueryExecutor;
import org.jrdf.query.QueryBuilder;

import java.net.URI;

/**
 * Unit test for {@link JrdfConnectionFactory}.
 *
 * @author Tom Adams
 * @version $Id$
 */
public class JrdfConnectionFactoryUnitTest extends TestCase {

    private static final String EXPECTED_NO_SECURITY_DOMAIN = "http://jrdf.sf.net/connection#NO_SECURITY";
    private static final Graph MOCK_GRAPH = MockTestUtil.createFromInterface(Graph.class);
    private static final URI NO_SECURITY_DOMAIN = JrdfConnectionFactory.NO_SECURITY_DOMAIN;
    private static final JrdfQueryExecutor QUERY_EXECUTOR = MockTestUtil.createFromInterface(JrdfQueryExecutor.class);
    private static final QueryBuilder QUERY_BUILDER = MockTestUtil.createFromInterface(QueryBuilder.class);

    public void testNoSecurityConstant() {
        assertEquals(URI.create(EXPECTED_NO_SECURITY_DOMAIN), JrdfConnectionFactory.NO_SECURITY_DOMAIN);
    }

    public void testNullSessionThrowsException() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                createFactory().createSparqlConnection(null, NO_SECURITY_DOMAIN, QUERY_EXECUTOR, QUERY_BUILDER);
            }
        });
    }

    public void testNullSecurityDomainThrowsException() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                createFactory().createSparqlConnection(MOCK_GRAPH, null, QUERY_EXECUTOR, QUERY_BUILDER);
            }
        });
    }

    public void testNullQueryExecutorThrowsException() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                createFactory().createSparqlConnection(MOCK_GRAPH, NO_SECURITY_DOMAIN, null, QUERY_BUILDER);
            }
        });
    }

    public void testNullQueryBuilderThrowsException() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                createFactory().createSparqlConnection(MOCK_GRAPH, NO_SECURITY_DOMAIN, QUERY_EXECUTOR, null);
            }
        });
    }

    public void testGeSparqlConnection() {
        assertNotNull(createConnectionWithBadGraph());
    }

    public void testCreateConnectionReturnsNewConnectionEachTime() {
        SparqlConnection connection1 = createConnectionWithBadGraph();
        SparqlConnection connection2 = createConnectionWithBadGraph();
        assertNotSame(connection1, connection2);
    }

    private SparqlConnection createConnectionWithBadGraph() {
        return createFactory().createSparqlConnection(MOCK_GRAPH, NO_SECURITY_DOMAIN, QUERY_EXECUTOR, QUERY_BUILDER);
    }

    private JrdfConnectionFactory createFactory() {
        return new JrdfConnectionFactory();
    }
}
