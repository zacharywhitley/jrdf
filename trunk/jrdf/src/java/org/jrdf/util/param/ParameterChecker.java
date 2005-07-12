package org.jrdf.util.param;

/**
 * Checks parameters for conformance against some criteria.
 *
 * @author Tom Adams
 * @version $Revision$
 */
interface ParameterChecker {

  /**
   * Checks if this checker allows a parameter with the given value.
   *
   * @param param The parameter to check the value of.
   * @return <code>true</code> if the parameter is allowed by this checker.
   */
  boolean paramAllowed(Object param);
}
