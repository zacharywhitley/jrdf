package org.jrdf.sparql.analysis;

import junit.framework.TestCase;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldPublicConstant;
import static org.jrdf.util.test.SerializationTestUtil.checkSerialialVersionUid;
import org.jrdf.util.test.MockFactory;
import org.jrdf.query.Answer;
import org.jrdf.query.EmptyAnswer;
import org.jrdf.query.Query;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.graph.Graph;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;

public class NoQueryUnitTest extends TestCase {
    private static final Class[] PARAM_TYPES = {};
    private MockFactory factory = new MockFactory();

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(Query.class, NoQuery.class);
        checkImplementationOfInterfaceAndFinal(Serializable.class, NoQuery.class);
        checkConstructor(NoQuery.class, Modifier.PRIVATE, PARAM_TYPES);
        checkFieldPublicConstant(NoQuery.class, "NO_QUERY");
        checkSerialialVersionUid(NoQuery.class, -1815852679585213051L);
    }

    public void testNoQueryValues() {
        Query query = NoQuery.NO_QUERY;
        Graph graph = factory.createMock(Graph.class);
        QueryEngine queryEngine = factory.createMock(QueryEngine.class);
        assertNotNull(query);
        assertEquals(EmptyAnswer.EMPTY_ANSWER, query.executeQuery(graph, queryEngine));
    }
}
