package org.jrdf.util.test.runner;

import junit.framework.Test;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * Run all the unit tests in the project.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class IntegrationTestRunner extends TestCase {
  public static Test suite() throws IOException {
    return new DefaultTestRunner("IntegrationTest");
  }
}

