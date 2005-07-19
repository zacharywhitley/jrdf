package org.jrdf.sparql;

import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;

/**
 * Unit test for {@link org.jrdf.sparql.SparqlParser}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class SparqlParserUnitTest extends TestCase {
    public void testClassProperties() {
        ClassPropertiesTestUtil.checkExtensionOf(QueryParser.class, SparqlParser.class);
    }
}
