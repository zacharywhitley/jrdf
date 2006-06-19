package org.jrdf.connection;

import junit.framework.TestCase;
import org.jrdf.graph.Graph;
import org.jrdf.query.JrdfQueryExecutor;
import org.jrdf.query.QueryBuilder;
import org.jrdf.util.test.MockTestUtil;

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

    // TODO (AN) !! Come back a fix me!!
    public void testBadMan() {
    }

//    public void testNoSecurityConstant() {
//        assertEquals(URI.create(EXPECTED_NO_SECURITY_DOMAIN), JrdfConnectionFactory.NO_SECURITY_DOMAIN);
//    }
//
//    public void testNullSecurityDomainThrowsException() {
//        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
//            public void execute() throws Throwable {
//                createFactory().createSparqlConnection(null, QUERY_EXECUTOR, QUERY_BUILDER);
//            }
//        });
//    }
//
//    public void testNullQueryExecutorThrowsException() {
//        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
//            public void execute() throws Throwable {
//                createFactory().createSparqlConnection(NO_SECURITY_DOMAIN, null, QUERY_BUILDER);
//            }
//        });
//    }
//
//    public void testNullQueryBuilderThrowsException() {
//        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
//            public void execute() throws Throwable {
//                createFactory().createSparqlConnection(NO_SECURITY_DOMAIN, QUERY_EXECUTOR, null);
//            }
//        });
//    }
//
//    public void testGeSparqlConnection() {
//        assertNotNull(createConnectionWithBadGraph());
//    }
//
//    public void testCreateConnectionReturnsNewConnectionEachTime() {
//        SparqlConnection connection1 = createConnectionWithBadGraph();
//        SparqlConnection connection2 = createConnectionWithBadGraph();
//        assertNotSame(connection1, connection2);
//    }
//
//    private SparqlConnection createConnectionWithBadGraph() {
//        return createFactory().createSparqlConnection(NO_SECURITY_DOMAIN, QUERY_EXECUTOR, QUERY_BUILDER);
//    }
//
//    private JrdfConnectionFactory createFactory() {
//        return new JrdfConnectionFactory();
//    }
}
