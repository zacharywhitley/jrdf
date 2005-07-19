package org.jrdf.query;

import java.util.Collections;
import junit.framework.TestCase;

/**
 * Unit test for {@link org.jrdf.query.Variable}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class VariableUnitTest extends TestCase {
    public void testConstants() {
        assertEquals(Collections.emptyList(), Variable.ALL_VARIABLES);
    }
}
