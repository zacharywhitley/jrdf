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

import java.net.*;

// Third party packages
import junit.framework.*;

/**
 * Abstract test case for {@link org.jrdfmem.graph.GraphImpl}.
 * Unfortunately this class is monolithic.  This is because all classes
 * in the org.jrdfmem.graph package are accessed through the Graph interface
 * which acts as a factory in many instances.  There is very tight coupling
 * thoughout the whole package.
 *
 * @author Paul Gearon
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public abstract class AbstractTripleFactoryUnitTest extends TestCase {

  /**
   * Instance of a graph object.
   */
  protected Graph graph;

  /**
   * Instance of a factory for the graph
   */
  protected GraphElementFactory elementFactory;

  /**
   * Instance of the triple factory for the graph.
   */
  protected TripleFactory tripleFactory;

  // The following are interally used "constants"
  protected static BlankNode blank1;
  protected static BlankNode blank2;

  protected static URI uri1;
  protected static URI uri2;
  protected static URI uri3;
  protected static URIReference ref1;
  protected static URIReference ref2;
  protected static URIReference ref3;

  protected static final String TEST_STR1 = "A test string";
  protected static final String TEST_STR2 = "Another test string";
  protected static Literal l1;
  protected static Literal l2;

  /**
   * Constructs a new test with the given name.
   *
   * @param name the name of the test
   */
  public AbstractTripleFactoryUnitTest(String name) {
    super(name);
  }

  /**
   * Create test instance.
   */
  public void setUp() throws Exception {
    graph = newGraph();
    elementFactory = graph.getElementFactory();
    tripleFactory = graph.getTripleFactory();

    blank1 = elementFactory.createResource();
    blank2 = elementFactory.createResource();

    uri1 = new URI("http://namespace#somevalue");
    uri2 = new URI("http://namespace#someothervalue");
    uri3 = new URI("http://namespace#yetanothervalue");
    ref1 = elementFactory.createResource(uri1);
    ref2 = elementFactory.createResource(uri2);
    ref3 = elementFactory.createResource(uri3);

    l1 = elementFactory.createLiteral(TEST_STR1);
    l2 = elementFactory.createLiteral(TEST_STR2);
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
  // implementation interfaces
  //

  /**
   * Create a graph implementation.
   *
   * @return A new GraphImpl.
   */
  public abstract Graph newGraph() throws Exception;

  /**
   * Get the node used for subject reification.
   *
   * @return The subject reification node.
   */
  public abstract PredicateNode getReifySubject() throws TripleFactoryException;

  /**
   * Get the node used for predicate reification.
   *
   * @return The predicate reification node.
   */
  public abstract PredicateNode getReifyPredicate() throws TripleFactoryException;

  /**
   * Get the node used for object reification.
   *
   * @return The object reification node.
   */
  public abstract PredicateNode getReifyObject() throws TripleFactoryException;

  //
  // Test cases
  //

  /**
   * Tests reification.
   */
  public void reification() throws Exception {
    PredicateNode reifySubject = getReifySubject();
    PredicateNode reifyPredicate = getReifyPredicate();
    PredicateNode reifyObject = getReifyObject();
    assertTrue(graph.isEmpty());

    URIReference u = this.elementFactory.createResource(uri1);
    tripleFactory.reifyTriple(blank1, ref1, blank2, u);
    assertEquals(uri1, u.getURI());
    assertEquals(graph.getNumberOfTriples(), 4);
    assertTrue(graph.contains(u, reifySubject, blank1));
    assertTrue(graph.contains(u, reifyPredicate, ref1));
    assertTrue(graph.contains(u, reifyObject, blank2));
    assertTrue(graph.contains(blank1, ref1, blank2));

    Triple t = elementFactory.createTriple(blank1, ref2, blank2);
    u = this.elementFactory.createResource(uri2);
    tripleFactory.reifyTriple(t, u);
    assertEquals(uri2, u.getURI());
    assertEquals(graph.getNumberOfTriples(), 8);
    assertTrue(graph.contains(u, reifySubject, blank1));
    assertTrue(graph.contains(u, reifyPredicate, ref2));
    assertTrue(graph.contains(u, reifyObject, blank2));
    assertTrue(graph.contains(blank1, ref2, blank2));

    // test for double insertion
    testCantInsert(blank1, ref1, blank2, this.elementFactory.createResource(uri1));
    // test for insertion with a different reference
    testCantInsert(blank1, ref1, blank2, this.elementFactory.createResource(uri3));
    // test for insertion of a new triple with an existing reference
    testCantInsert(blank2, ref1, blank1, this.elementFactory.createResource(uri1));
    // test that the graph did not change with the invalid insertions
    assertEquals(graph.getNumberOfTriples(), 8);

    // test for double insertion
    testCantInsert(t, uri2);
    // test for insertion with a different reference
    testCantInsert(t, uri3);
    // test for insertion of a new triple with an existing reference
    testCantInsert(elementFactory.createTriple(blank2, ref2, blank2), uri2);
    // test that the graph did not change with the invalid insertions
    assertEquals(graph.getNumberOfTriples(), 8);

    // do it all again for blank nodes

    BlankNode b = this.elementFactory.createResource();
    tripleFactory.reifyTriple(blank1, ref1, l1, b);
    assertEquals(graph.getNumberOfTriples(), 12);
    assertTrue(graph.contains(b, reifySubject, blank1));
    assertTrue(graph.contains(b, reifyPredicate, ref1));
    assertTrue(graph.contains(b, reifyObject, l1));
    assertTrue(graph.contains(blank1, ref1, l1));

    t = elementFactory.createTriple(blank1, ref2, l2);
    b = this.elementFactory.createResource();
    tripleFactory.reifyTriple(t, b);
    assertEquals(graph.getNumberOfTriples(), 16);
    assertTrue(graph.contains(b, reifySubject, blank1));
    assertTrue(graph.contains(b, reifyPredicate, ref2));
    assertTrue(graph.contains(b, reifyObject, l2));
    assertTrue(graph.contains(blank1, ref2, l2));

    // test for double insertion
    testCantInsert(blank1, ref1, blank2);
    // test for insertion with a a used blank reference
    testCantInsert(blank1, ref3, blank2, u);
    // test that the graph did not change with the invalid insertions
    assertEquals(graph.getNumberOfTriples(), 16);

    // test for double insertion
    testCantInsert(t);
    // test for insertion with a a used blank reference
    testCantInsert(elementFactory.createTriple(blank1, ref3, blank2), u.getURI());
    // test that the graph did not change with the invalid insertions
    assertEquals(graph.getNumberOfTriples(), 16);
  }


  /**
   * Utility method to check that a triple cannot be reified.
   *
   * @param subject The subject for the triple.
   * @param predicate The predicate for the triple.
   * @param object The object for the triple.
   * @param r The reification node for the triple.
   * @throws Exception The triple could be reified.
   */
  private void testCantInsert(SubjectNode subject, PredicateNode predicate,
      ObjectNode object, SubjectNode r) throws Exception {
    try {
      tripleFactory.reifyTriple(subject, predicate, object, r);
      assertTrue(false);
    } catch (AlreadyReifiedException e) {
    }
  }


  /**
   * Utility method to check that a triple cannot be reified.
   *
   * @param triple The triple to reify.
   * @param r The reification node for the triple.
   * @throws Exception The triple could be reified.
   */
  private void testCantInsert(Triple triple, URI r) throws Exception {
    try {
      tripleFactory.reifyTriple(triple, elementFactory.createResource(r));
      assertTrue(false);
    } catch (AlreadyReifiedException e) {
    }
  }


  /**
   * Utility method to check that a triple cannot be reified with a blank node.
   *
   * @param subject The subject for the triple.
   * @param predicate The predicate for the triple.
   * @param object The object for the triple.
   * @throws Exception The triple could be reified.
   */
  private void testCantInsert(SubjectNode subject, PredicateNode predicate,
      ObjectNode object) throws Exception {
    try {
      tripleFactory.reifyTriple(subject, predicate, object,
          elementFactory.createResource());
      assertTrue(false);
    } catch (AlreadyReifiedException e) {
    }
  }


  /**
   * Utility method to check that a triple cannot be reified with a blank node.
   *
   * @param triple The triple to reify.
   * @throws Exception The triple could be reified.
   */
  private void testCantInsert(Triple triple) throws Exception {
    try {
      tripleFactory.reifyTriple(triple, elementFactory.createResource());
      assertTrue(false);
    } catch (AlreadyReifiedException e) {
    }
  }
}
