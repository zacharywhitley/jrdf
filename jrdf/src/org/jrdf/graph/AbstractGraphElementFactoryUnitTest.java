/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 */

package org.jrdf.graph;

import org.jrdf.graph.*;

import java.net.URI;
import java.net.URISyntaxException;

// Third party packages
import junit.framework.*;

/**
 * Abstract Test case for {@link org.jrdfmem.graph.NodeFactoryImpl}.
 * Implementing packages should extend this class and implement the
 * {@link #newGraph}, {@link #getDefaultLiteralType} and
 * {@link #getDefaultLiteralLanguage} methods.
 * Unfortunately this class is monolithic.  This is because all classes
 * in the org.jrdfmem.graph package are accessed through the Graph interface
 * which acts as a factory in many instances.  There is very tight coupling
 * thoughout the whole package.
 *
 * @author Paul Gearon
 *
 * @version $Revision$
 */
public abstract class AbstractGraphElementFactoryUnitTest extends TestCase {

  /**
   * Instance of a graph element factory.
   */
  private GraphElementFactory elementFactory;

  /**
   * Constructs a new test with the given name.
   *
   * @param name the name of the test
   */
  public AbstractGraphElementFactoryUnitTest(String name) {
    super(name);
  }

  /**
   * Create test instance.
   */
  public void setUp() throws Exception {
    Graph graph = newGraph();
    elementFactory = graph.getElementFactory();
  }

  /**
   * Hook for test runner to obtain a test suite from.
   * Override in derived class.
   *
   * @return The test suite
   */
  public static Test suite() {
    return null;
  }

  //
  // abstract methods specific to the implementation.
  //

  /**
   * Create a new graph of the appropriate type.
   *
   * @return A new graph implementation object.
   */
  public abstract Graph newGraph() throws Exception;

  /**
   * Return the default literal type from the implementation.
   *
   * @return The default Literal type.
   */
  public abstract URI getDefaultLiteralType();

  /**
   * Get the default literal language from the implementation.
   *
   * @return The default Literal language.
   */
  public abstract String getDefaultLiteralLanguage();

  //
  // Test cases
  //

  /**
   * Tests that each of the createLiteral methods work as expected.
   *
   * @throws Exception if query fails when it should have succeeded
   */
  public void createLiterals() throws Exception {
    final String TEST_STR1 = "A test string";
    final String TEST_STR2 = "Another test string";

    // createLiteral(String lexicalValue)
    Literal l1 = elementFactory.createLiteral(TEST_STR1);
    Literal l2 = elementFactory.createLiteral(TEST_STR2);
    Literal l3 = elementFactory.createLiteral(TEST_STR1);
    assertFalse(l1.equals(l2));
    assertEquals(l1, l3);
    assertEquals(getDefaultLiteralType(), l1.getDatatypeURI());
    assertEquals(getDefaultLiteralLanguage(), l1.getLanguage());
    assertEquals(TEST_STR1, l1.getLexicalForm());

    // createLiteral(String lexicalValue, String languageType)
    l1 = elementFactory.createLiteral(TEST_STR1, "it");
    l2 = elementFactory.createLiteral(TEST_STR2, "it");
    l3 = elementFactory.createLiteral(TEST_STR1, "it");
    Literal l4 = elementFactory.createLiteral(TEST_STR1);
    assertFalse(l1.equals(l2));
    assertFalse(l1.equals(l4));
    assertEquals(l1, l3);
    assertEquals(getDefaultLiteralType(), l1.getDatatypeURI());
    assertEquals("it", l1.getLanguage());
    assertEquals(TEST_STR1, l1.getLexicalForm());

    // createLiteral(String lexicalValue, URI datatypeURI)
    URI type = new URI("xsd:long");
    l1 = elementFactory.createLiteral("42", type);
    l2 = elementFactory.createLiteral("0", type);
    l3 = elementFactory.createLiteral("42", type);
    l4 = elementFactory.createLiteral("42");
    assertFalse(l1.equals(l2));
    assertFalse(l1.equals(l4));
    assertEquals(l1, l3);
    assertEquals(type, l1.getDatatypeURI());
    assertEquals(getDefaultLiteralLanguage(), l1.getLanguage());
    assertEquals("42", l1.getLexicalForm());

  }

  /**
   * Tests that each of the createResource methods work as expected.
   *
   * @throws Exception if query fails when it should have succeeded
   */
  public void createResources() throws Exception {
    // test blank node creation
    BlankNode blank1 = elementFactory.createResource();
    BlankNode blank2 = elementFactory.createResource();
    assertFalse(blank1.equals(blank2));

    // test named node creation
    URI uri1 = new URI("http://namespace#somevalue");
    URI uri2 = new URI("http://namespace#someothervalue");
    URIReference ref1 = elementFactory.createResource(uri1);
    URIReference ref2 = elementFactory.createResource(uri2);
    URIReference ref3 = elementFactory.createResource(uri1);
    assertFalse(ref1.equals(ref2));
    assertEquals(ref1, ref3);
    assertEquals(ref1.getURI(), uri1);
  }


  /**
   * Tests that each of the createResource methods work as expected.
   *
   * @throws Exception if query fails when it should have succeeded
   */
  public void createTriples() throws Exception {
    BlankNode blank1 = elementFactory.createResource();
    BlankNode blank2 = elementFactory.createResource();

    URI uri1 = new URI("http://namespace#somevalue");
    URI uri2 = new URI("http://namespace#someothervalue");
    URI uri3 = new URI("http://namespace#yetanothervalue");
    URIReference ref1 = elementFactory.createResource(uri1);
    URIReference ref2 = elementFactory.createResource(uri2);
    URIReference ref3 = elementFactory.createResource(uri3);

    final String TEST_STR1 = "A test string";
    final String TEST_STR2 = "Another test string";
    Literal l1 = elementFactory.createLiteral(TEST_STR1);
    Literal l2 = elementFactory.createLiteral(TEST_STR2);

    // test ordinary creation
    Triple triple = elementFactory.createTriple(blank1, ref1, blank2);
    assertEquals(blank1, triple.getSubject());
    assertEquals(ref1, triple.getPredicate());
    assertEquals(blank2, triple.getObject());

    // test inequality, particularly against differing blank nodes
    Triple triple2 = elementFactory.createTriple(blank2, ref1, blank2);
    assertFalse(triple.equals(triple2));

    // test equality
    triple2 = elementFactory.createTriple(blank1, ref1, blank2);
    assertEquals(triple, triple2);

    // test all types of statement creation
    triple = elementFactory.createTriple(blank1, ref1, l1);
    triple = elementFactory.createTriple(blank1, ref1, l1);
    triple = elementFactory.createTriple(ref1, ref2, l1);
    triple = elementFactory.createTriple(ref1, ref2, blank1);
    triple = elementFactory.createTriple(ref1, ref2, ref3);
  }


  // reification tests are not done here until their location within
  // the JRDF package is properly resolved
}
