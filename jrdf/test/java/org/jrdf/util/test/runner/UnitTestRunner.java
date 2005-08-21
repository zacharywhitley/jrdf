package org.jrdf.util.test.runner;

import com.gargoylesoftware.base.testing.RecursiveTestSuite;
import com.gargoylesoftware.base.testing.TestFilter;
import com.gargoylesoftware.base.testing.AcceptAllTestFilter;

import java.io.IOException;
import java.io.File;
import java.lang.reflect.Modifier;

import junit.framework.TestSuite;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestCase;

/**
 * Run all the unit tests in the project.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class UnitTestRunner extends TestCase {
  public static Test suite() throws IOException {
    return new DefaultTestRunner("UnitTest");
  }
}

