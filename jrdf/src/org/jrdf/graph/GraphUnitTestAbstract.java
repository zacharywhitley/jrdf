/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The JRDF Project.  All rights reserved.
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
import org.jrdf.util.*;

import java.net.*;
import java.util.*;

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
 *
 * @version $Revision$
 */
public abstract class GraphUnitTestAbstract extends TestCase {

  /** Instance of a graph object. */
  protected Graph graph;

  /** Instance of a factory for the graph */
  protected NodeFactory factory;

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
  public GraphUnitTestAbstract(String name) {
    super(name);
  }

  /**
   * Create test instance.
   */
  public void setUp() throws Exception {
    graph = newGraph();
    factory = graph.getNodeFactory();

    blank1 = factory.createResource();
    blank2 = factory.createResource();

    uri1 = new URI("http://namespace#somevalue");
    uri2 = new URI("http://namespace#someothervalue");
    uri3 = new URI("http://namespace#yetanothervalue");
    ref1 = factory.createResource(uri1);
    ref2 = factory.createResource(uri2);
    ref3 = factory.createResource(uri3);

    l1 = factory.createLiteral(TEST_STR1);
    l2 = factory.createLiteral(TEST_STR2);
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

  /**
   * Default text runner.
   *
   * @param args The command line arguments
   */
  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
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
  public abstract PredicateNode getReifySubject() throws NodeFactoryException;

  /**
   * Get the node used for predicate reification.
   *
   * @return The predicate reification node.
   */
  public abstract PredicateNode getReifyPredicate() throws NodeFactoryException;

  /**
   * Get the node used for object reification.
   *
   * @return The object reification node.
   */
  public abstract PredicateNode getReifyObject() throws NodeFactoryException;

  //
  // Test cases
  //

  /**
   * Tests that a new graph is empty.
   *
   * @throws Exception if query fails when it should have succeeded
   */
  public void empty() throws Exception {
    assertTrue(graph.isEmpty());
    assertEquals(0, graph.getNumberOfTriples());
  }


  /**
   * Tests that it is possible to get a NodeFactory from a graph.
   *
   * @throws Exception if query fails when it should have succeeded
   */
  public void factory() throws Exception {
    NodeFactory f = graph.getNodeFactory();
    assertTrue(f != null);
  }


  /**
   * Tests addition.
   */
  public void addition() throws Exception {

    // add in a triple by nodes
    graph.add(blank1, ref1, blank2);

    assertFalse(graph.isEmpty());
    assertEquals(1, graph.getNumberOfTriples());

    // add in a whole triple
    Triple triple2 = factory.createTriple(blank2, ref1, blank2);
    graph.add(triple2);

    assertFalse(graph.isEmpty());
    assertEquals(2, graph.getNumberOfTriples());

    // add in the first triple again
    graph.add(blank1, ref1, blank2);

    assertFalse(graph.isEmpty());
    assertEquals(2, graph.getNumberOfTriples());

    // add in the second whole triple again
    Triple triple2b = factory.createTriple(blank2, ref1, blank2);
    graph.add(triple2b);
    assertFalse(graph.isEmpty());
    assertEquals(2, graph.getNumberOfTriples());

    // and again
    graph.add(triple2);
    assertFalse(graph.isEmpty());
    assertEquals(2, graph.getNumberOfTriples());

  }

  /**
   * Tests removal.
   */
  public void removal() throws Exception {
    // add some test data
    graph.add(blank1, ref1, blank2);
    graph.add(blank1, ref2, blank2);
    graph.add(ref1, ref2, l2);
    Triple t1 = factory.createTriple(blank2, ref1, blank1);
    graph.add(t1);
    Triple t2 = factory.createTriple(blank2, ref2, blank1);
    graph.add(t2);
    Triple t3 = factory.createTriple(blank2, ref1, l1);
    graph.add(t3);

    // check that all is well
    assertFalse(graph.isEmpty());
    assertEquals(6, graph.getNumberOfTriples());

    // delete the first statement
    graph.remove(blank1, ref1, blank2);
    assertEquals(5, graph.getNumberOfTriples());

    // delete the last statement
    graph.remove(t3);
    assertEquals(4, graph.getNumberOfTriples());

    // delete the next last statement with a new "triple object"
    t2 = factory.createTriple(blank2, ref2, blank1);
    graph.remove(t2);
    assertEquals(3, graph.getNumberOfTriples());

    // delete the next last statement with a triple different to what it was built with
    graph.remove(blank2, ref1, blank1);
    assertEquals(2, graph.getNumberOfTriples());

    // delete the next last statement with a triple different to what it was built with
    graph.remove(ref1, ref2, l2);
    assertEquals(1, graph.getNumberOfTriples());

    // delete the wrong triple
    try {
      graph.remove(blank2, ref1, blank1);
      assertTrue(false);
    } catch (GraphException e) { /* no-op */ }
    assertEquals(1, graph.getNumberOfTriples());

    // delete a triple that never existed
    try {
      graph.remove(blank2, ref2, l2);
      assertTrue(false);
    } catch (GraphException e) { /* no-op */ }
    assertEquals(1, graph.getNumberOfTriples());

    // and delete with a triple object
    t1 = factory.createTriple(blank2, ref1, blank1);
    try {
      graph.remove(t1);
      assertTrue(false);
    } catch (GraphException e) { /* no-op */ }
    assertEquals(1, graph.getNumberOfTriples());

    // now clear out the graph
    assertFalse(graph.isEmpty());
    graph.remove(blank1, ref2, blank2);
    assertTrue(graph.isEmpty());
    assertEquals(0, graph.getNumberOfTriples());

    // check that we can't still remove things
    try {
      graph.remove(blank1, ref2, blank2);
      assertTrue(false);
    } catch (GraphException e) { /* no-op */ }
    assertTrue(graph.isEmpty());
    assertEquals(0, graph.getNumberOfTriples());
  }


  /**
   * Tests containership.
   */
  public void contains() throws Exception {
    // add some test data
    graph.add(blank1, ref1, blank2);
    graph.add(blank1, ref2, blank2);
    graph.add(ref1, ref2, l2);
    Triple t1 = factory.createTriple(blank2, ref1, blank1);
    graph.add(t1);
    Triple t2 = factory.createTriple(blank2, ref2, blank1);
    graph.add(t2);
    Triple t3 = factory.createTriple(blank2, ref1, l1);
    graph.add(t3);

    // test containership
    assertTrue(graph.contains(blank1, ref1, blank2));
    // test with existing and built triples
    assertTrue(graph.contains(t1));
    t1 = factory.createTriple(blank2, ref2, blank1);
    assertTrue(graph.contains(t1));

    // test non containership
    assertFalse(graph.contains(blank1, ref1, blank1));
    t1 = factory.createTriple(blank2, ref2, ref1);
    assertFalse(graph.contains(t1));

    // test containership after removal
    graph.remove(blank1, ref1, blank2);
    assertFalse(graph.contains(blank1, ref1, blank2));
    t1 = factory.createTriple(blank1, ref1, blank2);
    assertFalse(graph.contains(t1));
    // put it back in and test again
    graph.add(blank1, ref1, blank2);
    assertTrue(graph.contains(blank1, ref1, blank2));
    assertTrue(graph.contains(t1));
  }


  /**
   * Tests finding.
   */
  public void finding() throws Exception {
    graph.add(blank1, ref1, blank2);
    graph.add(blank1, ref1, l1);
    graph.add(blank1, ref2, blank2);
    graph.add(blank1, ref1, l2);
    graph.add(blank2, ref1, blank2);
    graph.add(blank2, ref2, blank2);
    graph.add(blank2, ref1, l1);
    graph.add(blank2, ref1, l2);

    // look for the first triple and check that one is returned
    ClosableIterator it = graph.find(blank1, ref1, blank2);
    assertTrue(it.hasNext());
    it.close();

    // look for a non-existent triple
    it = graph.find(ref1, ref1, blank1);
    assertFalse(it.hasNext());
    it.close();

    // look for doubles and check that there is data there
    it = graph.find(blank1, ref1, null);
    assertTrue(it.hasNext());
    it.close();
    it = graph.find(blank1, null, blank2);
    assertTrue(it.hasNext());
    it.close();
    it = graph.find(null, ref1, blank2);
    assertTrue(it.hasNext());
    it.close();

    // look for a non-existent double
    it = graph.find(ref1, ref1, null);
    assertFalse(it.hasNext());
    it.close();
    it = graph.find(ref1, null, blank2);
    assertFalse(it.hasNext());
    it.close();
    it = graph.find(null, ref3, blank2);
    assertFalse(it.hasNext());
    it.close();

    // look for singles
    it = graph.find(blank1, null, null);
    assertTrue(it.hasNext());
    it.close();
    it = graph.find(null, ref1, null);
    assertTrue(it.hasNext());
    it.close();
    it = graph.find(null, null, l1);
    assertTrue(it.hasNext());
    it.close();

    // look for non-existent singles
    it = graph.find(ref1, null, null);
    assertFalse(it.hasNext());
    it.close();
    it = graph.find(null, ref3, null);
    assertFalse(it.hasNext());
    it.close();
    it = graph.find(null, null, ref1);
    assertFalse(it.hasNext());
    it.close();

    // do it all again with triples

    // look for the first triple and check that one is returned
    Triple t = factory.createTriple(blank1, ref1, blank2);
    it = graph.find(t);
    assertTrue(it.hasNext());
    it.close();

    // look for a non-existent triple
    t = factory.createTriple(ref1, ref1, blank1);
    it = graph.find(t);
    assertFalse(it.hasNext());
    it.close();

    // look for doubles and check that there is data there
    t = factory.createTriple(blank1, ref1, null);
    it = graph.find(t);
    assertTrue(it.hasNext());
    it.close();
    t = factory.createTriple(blank1, null, blank2);
    it = graph.find(t);
    assertTrue(it.hasNext());
    it.close();
    t = factory.createTriple(null, ref1, blank2);
    it = graph.find(t);
    assertTrue(it.hasNext());
    it.close();

    // look for a non-existent double
    t = factory.createTriple(ref1, ref1, null);
    it = graph.find(t);
    assertFalse(it.hasNext());
    it.close();
    t = factory.createTriple(ref1, null, blank2);
    it = graph.find(t);
    assertFalse(it.hasNext());
    it.close();
    t = factory.createTriple(null, ref3, blank2);
    it = graph.find(t);
    assertFalse(it.hasNext());
    it.close();

    // look for singles
    t = factory.createTriple(blank1, null, null);
    it = graph.find(t);
    assertTrue(it.hasNext());
    it.close();
    t = factory.createTriple(null, ref1, null);
    it = graph.find(t);
    assertTrue(it.hasNext());
    it.close();
    t = factory.createTriple(null, null, l1);
    it = graph.find(t);
    assertTrue(it.hasNext());
    it.close();

    // look for non-existent singles
    t = factory.createTriple(ref1, null, null);
    it = graph.find(t);
    assertFalse(it.hasNext());
    it.close();
    t = factory.createTriple(null, ref3, null);
    it = graph.find(t);
    assertFalse(it.hasNext());
    it.close();
    t = factory.createTriple(null, null, ref1);
    it = graph.find(t);
    assertFalse(it.hasNext());
    it.close();
  }


  /**
   * Tests iteration over a found set.
   */
  public void iteration() throws Exception {
    Triple t1 = factory.createTriple(blank1, ref1, blank2);
    Triple t2 = factory.createTriple(blank1, ref2, blank2);
    Triple t3 = factory.createTriple(blank1, ref1, l1);
    Triple t4 = factory.createTriple(blank1, ref1, l2);
    Triple t5 = factory.createTriple(blank2, ref1, blank2);
    Triple t6 = factory.createTriple(blank2, ref2, blank2);
    Triple t7 = factory.createTriple(blank2, ref1, l1);
    Triple t8 = factory.createTriple(blank2, ref1, l2);
    graph.add(t1);
    graph.add(t2);
    graph.add(t3);
    graph.add(t4);
    graph.add(t5);
    graph.add(t6);
    graph.add(t7);
    graph.add(t8);

    // look for the first triple and check that there is only one returned
    ClosableIterator it = graph.find(t1);
    assertTrue(it.hasNext());
    Triple t = (Triple)it.next();
    assertTrue(t.equals(t1));
    assertFalse(it.hasNext());

    Set triples = new HashSet();

    // look for doubles and check that there is data there
    t = factory.createTriple(blank1, ref1, null);
    triples.add(t1);
    triples.add(t3);
    triples.add(t4);
    it = graph.find(t);
    checkSet(triples, it);

    t = factory.createTriple(blank1, null, blank2);
    triples.add(t1);
    triples.add(t2);
    it = graph.find(t);
    checkSet(triples, it);

    t = factory.createTriple(null, ref1, blank2);
    triples.add(t1);
    triples.add(t5);
    it = graph.find(t);
    checkSet(triples, it);

    // look for singles
    t = factory.createTriple(blank1, null, null);
    triples.add(t1);
    triples.add(t2);
    triples.add(t3);
    triples.add(t4);
    it = graph.find(t);
    checkSet(triples, it);

    t = factory.createTriple(null, ref1, null);
    triples.add(t1);
    triples.add(t3);
    triples.add(t4);
    triples.add(t5);
    triples.add(t7);
    triples.add(t8);
    it = graph.find(t);
    checkSet(triples, it);

    t = factory.createTriple(null, null, l1);
    triples.add(t3);
    triples.add(t7);
    it = graph.find(t);
    checkSet(triples, it);

    // look for the first triple and check that there is only one returned
    it = graph.find(blank1, ref1, blank2);
    assertTrue(it.hasNext());
    t = (Triple)it.next();
    assertTrue(t.equals(t1));
    assertFalse(it.hasNext());

    // look for doubles and check that there is data there
    it = graph.find(blank1, ref1, null);
    triples.add(t1);
    triples.add(t3);
    triples.add(t4);
    checkSet(triples, it);

    it=graph.find(blank1, null, blank2);
    triples.add(t1);
    triples.add(t2);
    checkSet(triples, it);

    it=graph.find(null, ref1, blank2);
    triples.add(t1);
    triples.add(t5);
    checkSet(triples, it);

    // look for singles
    it=graph.find(blank1, null, null);
    triples.add(t1);
    triples.add(t2);
    triples.add(t3);
    triples.add(t4);
    checkSet(triples, it);

    it=graph.find(null, ref1, null);
    triples.add(t1);
    triples.add(t3);
    triples.add(t4);
    triples.add(t5);
    triples.add(t7);
    triples.add(t8);
    checkSet(triples, it);

    it=graph.find(null, null, l1);
    triples.add(t3);
    triples.add(t7);
    checkSet(triples, it);

  }


  /**
   * Tests reification.
   */
  public void reification() throws Exception {
    PredicateNode reifySubject = getReifySubject();
    PredicateNode reifyPredicate = getReifyPredicate();
    PredicateNode reifyObject = getReifyObject();
    assertTrue(graph.isEmpty());

    URIReference u = factory.reifyTriple(blank1, ref1, blank2, uri1);
    assertEquals(uri1, u.getURI());
    assertEquals(graph.getNumberOfTriples(), 4);
    assertTrue(graph.contains(u, reifySubject, blank1));
    assertTrue(graph.contains(u, reifyPredicate, ref1));
    assertTrue(graph.contains(u, reifyObject, blank2));
    assertTrue(graph.contains(blank1, ref1, blank2));

    Triple t = factory.createTriple(blank1, ref2, blank2);
    u = factory.reifyTriple(t, uri2);
    assertEquals(uri2, u.getURI());
    assertEquals(graph.getNumberOfTriples(), 8);
    assertTrue(graph.contains(u, reifySubject, blank1));
    assertTrue(graph.contains(u, reifyPredicate, ref2));
    assertTrue(graph.contains(u, reifyObject, blank2));
    assertTrue(graph.contains(blank1, ref2, blank2));

    // test for double insertion
    testCantInsert(blank1, ref1, blank2, uri1);
    // test for insertion with a different reference
    testCantInsert(blank1, ref1, blank2, uri3);
    // test for insertion of a new triple with an existing reference
    testCantInsert(blank2, ref1, blank1, uri1);
    // test that the graph did not change with the invalid insertions
    assertEquals(graph.getNumberOfTriples(), 8);

    // test for double insertion
    testCantInsert(t, uri2);
    // test for insertion with a different reference
    testCantInsert(t, uri3);
    // test for insertion of a new triple with an existing reference
    testCantInsert(factory.createTriple(blank2, ref2, blank2), uri2);
    // test that the graph did not change with the invalid insertions
    assertEquals(graph.getNumberOfTriples(), 8);

    // do it all again for blank nodes

    BlankNode b = factory.reifyTriple(blank1, ref1, l1);
    assertEquals(graph.getNumberOfTriples(), 12);
    assertTrue(graph.contains(b, reifySubject, blank1));
    assertTrue(graph.contains(b, reifyPredicate, ref1));
    assertTrue(graph.contains(b, reifyObject, l1));
    assertTrue(graph.contains(blank1, ref1, l1));

    t = factory.createTriple(blank1, ref2, l2);
    b = factory.reifyTriple(t);
    assertEquals(graph.getNumberOfTriples(), 16);
    assertTrue(graph.contains(b, reifySubject, blank1));
    assertTrue(graph.contains(b, reifyPredicate, ref2));
    assertTrue(graph.contains(b, reifyObject, l2));
    assertTrue(graph.contains(blank1, ref2, l2));

    // test for double insertion
    testCantInsert(blank1, ref1, blank2);
    // test for insertion with a a used blank reference
    testCantInsert(blank1, ref3, blank2, u.getURI());
    // test that the graph did not change with the invalid insertions
    assertEquals(graph.getNumberOfTriples(), 16);

    // test for double insertion
    testCantInsert(t);
    // test for insertion with a a used blank reference
    testCantInsert(factory.createTriple(blank1, ref3, blank2), u.getURI());
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
  private void testCantInsert(SubjectNode subject, PredicateNode predicate, ObjectNode object, URI r) throws Exception {
    try {
      factory.reifyTriple(subject, predicate, object, r);
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
      factory.reifyTriple(triple, r);
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
  private void testCantInsert(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws Exception {
    try {
      factory.reifyTriple(subject, predicate, object);
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
      factory.reifyTriple(triple);
      assertTrue(false);
    } catch (AlreadyReifiedException e) {
    }
  }


  /**
   * Checks that an iterator matches a set exactly.
   * The set will be emptied and the iterator will be closed.
   *
   * @throws Exception If the set does not match the iterator.
   */
  private void checkSet(Set triples, ClosableIterator it) throws Exception {
    while (it.hasNext()) {
      Triple t = (Triple)it.next();
      assertTrue(triples.contains(t));
      triples.remove(t);
    }
    assertTrue(triples.isEmpty());
    it.close();
  }

}
