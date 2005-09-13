package org.jrdf.connection;

import java.net.URI;
import junit.framework.TestCase;
import org.jrdf.query.MockBadGraph;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.util.test.AssertThrows;

/**
 * Unit test for {@link JrdfConnectionFactory}.
 * @author Tom Adams
 * @version $Id$
 */
public class JrdfConnectionFactoryUnitTest extends TestCase {

    private static final String EXPECTED_NO_SECURITY_DOMAIN = "http://jrdf.sf.net/connection#NO_SECURITY";
    private static final MockBadGraph BAD_GRAPH = new MockBadGraph();
    private static final URI NO_SECURITY_DOMAIN = JrdfConnectionFactory.NO_SECURITY_DOMAIN;

    public void testNoSecurityConstant() {
        assertEquals(URI.create(EXPECTED_NO_SECURITY_DOMAIN), JrdfConnectionFactory.NO_SECURITY_DOMAIN);
    }

    public void testNullSessionThrowsException() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                createFactory().createSparqlConnection(null, NO_SECURITY_DOMAIN);
            }
        });
    }

    public void testNullSecurityDomainThrowsException() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                createFactory().createSparqlConnection(BAD_GRAPH, null);
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
        return createFactory().createSparqlConnection(BAD_GRAPH, NO_SECURITY_DOMAIN);
    }

    private JrdfConnectionFactory createFactory() {
        return new JrdfConnectionFactory();
    }
}
