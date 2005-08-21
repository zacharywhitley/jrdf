package org.jrdf.util.test.runner;

import com.gargoylesoftware.base.testing.RecursiveTestSuite;
import com.gargoylesoftware.base.testing.TestFilter;

import java.io.IOException;
import java.io.File;
import java.lang.reflect.Modifier;

/**
 * Recurses the root directory of classes and runs all tests which end with a
 * given suffix.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class DefaultTestRunner extends RecursiveTestSuite {
  public DefaultTestRunner(String testClassNameSuffix) throws IOException {
    super(getLocation(), new ConcreteSuffixFilter(testClassNameSuffix));
    setName(testClassNameSuffix);
  }

  private static File getLocation() {
    return new File(DefaultTestRunner.class.getResource("/").getFile());
  }

  private static class ConcreteSuffixFilter implements TestFilter {
    private String testClassNameSuffix;

    public ConcreteSuffixFilter(String testClassNameSuffix) {
      this.testClassNameSuffix = testClassNameSuffix;
    }

    public boolean accept(Class aClass) {
      if (Modifier.isAbstract(aClass.getModifiers())) {
        return false;
      }
      return (aClass.getName().endsWith(testClassNameSuffix));
    }
  }
}
