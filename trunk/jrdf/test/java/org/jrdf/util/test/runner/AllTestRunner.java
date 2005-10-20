package org.jrdf.util.test.runner;

import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 * Run all the tests in the project.
 * @author Tom Adams
 * @version $Id$
 */
public class AllTestRunner extends TestCase {

    public static Test suite() throws IOException {
        return new DefaultTestRunner("Test");
    }
}
