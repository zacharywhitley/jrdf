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

package org.jrdf.graph.mem;

import org.jrdf.graph.*;
import org.jrdf.util.ClosableIterator;

import java.io.*;
import java.util.*;

/**
 * A memory based RDF Graph.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class GraphImpl implements Graph, Serializable {

  /**
   * Allow newer compiled version of the stub to operate when changes
   * have not occurred with the class.
   * NOTE : update this serialVersionUID when a method or a public member is
   * deleted.
   */
  private static final long serialVersionUID = -3066836734480153804L;

  // indexes are mapped as:
  // s -> {p -> {set of o}}
  // This is defined in the private add() method

  /**
   * First index.
   */
  private Map<Long, Map<Long, Set<Long>>> index012;

  /**
   * Second index.
   */
  private transient Map<Long, Map<Long, Set<Long>>> index120;

  /**
   * Third index.
   */
  private transient Map<Long, Map<Long, Set<Long>>> index201;

  /**
   * Graph Element Factory.  This caches the node factory.
   */
  private transient GraphElementFactoryImpl elementFactory;

  /**
   * Triple Element Factory.  This caches the element factory.
   */
  private transient TripleFactoryImpl tripleFactory;
  private LongIndexMem longIndex012;
  private LongIndexMem longIndex120;
  private LongIndexMem longIndex201;

  /**
   * Default constructor.
   *
   * @throws GraphException There was an error creating the factory.
   */
  public GraphImpl() throws GraphException {
    init();
  }

  /**
   * Initialization method used by the constructor and the deserializer.
   *
   * @throws GraphException There was an error creating the factory.
   */
  private void init() throws GraphException {

    // protect each field allocation with a test for null
    if (null == index012) {
      index012 = new HashMap<Long, Map<Long, Set<Long>>>();
    }
    if (null == index120) {
      index120 = new HashMap<Long, Map<Long, Set<Long>>>();
    }
    if (null == index201) {
      index201 = new HashMap<Long, Map<Long, Set<Long>>>();
    }

    longIndex012 = new LongIndexMem(index012);
    longIndex120 = new LongIndexMem(index120);
    longIndex201 = new LongIndexMem(index201);

    if (null == elementFactory) {
      try {
        elementFactory = new GraphElementFactoryImpl(this);
      }
      catch (TripleFactoryException e) {
        throw new GraphException(e);
      }
    }

    if (null == tripleFactory) {
      tripleFactory = new TripleFactoryImpl(this, elementFactory);
    }
  }

  /**
   * Test the graph for the occurrence of the triple.  A null value for any
   * of the parts of a triple are treated as unconstrained, any values will be
   * returned.
   *
   * @param triple The triple to find.
   * @return True if the triple is found in the graph, otherwise false.
   * @throws GraphException If there was an error accessing the graph.
   */
  public boolean contains(Triple triple) throws GraphException {
    return contains(triple.getSubject(), triple.getPredicate(),
        triple.getObject());
  }

  /**
   * Test the graph for the occurrence of a statement.  A null value for any
   * of the parts of a triple are treated as unconstrained, any values will be
   * returned.
   *
   * @param subject The subject to find or null to indicate any subject.
   * @param predicate The predicate to find or null to indicate any predicate.
   * @param object The object to find or null to indicate any object.
   * @return True if the statement is found in the model, otherwise false.
   * @throws GraphException If there was an error accessing the graph.
   */
  public boolean contains(SubjectNode subject, PredicateNode predicate,
      ObjectNode object) throws GraphException {

    // Get local node values
    Long[] values;
    try {
      values = localize(subject, predicate, object);
    }
    catch (GraphException ge) {

      // Graph exception on localize implies that the subject, predicate or
      // object did not exist in the graph.
      return false;
    }

    // Return true if all are null and size is greater than zero.
    if (null == subject && null == predicate && null == object) {
      return 0 < index012.size();
    }

    // Subject null.
    if (null == subject) {

      // Predicate null - was null, null obj.
      if (null == predicate) {
        Map<Long, Set<Long>> objIndex = index201.get(values[2]);
        return null != objIndex;
      }
      // Predicate is not null.  Could be null, pred, null or null, pred, obj.
      else {
        Map<Long, Set<Long>> predIndex = index120.get(values[1]);

        // If predicate not found return false.
        if (null == predIndex) {
          return false;
        }

        // If the object is null and we found the predicate return true.
        if (null == object) {
          return true;
        }
        // Was null, pred, obj
        else {
          Set group = predIndex.get(values[2]);
          return null != group;
        }
      }
    }
    // Subject is not null.
    else {
      Map<Long, Set<Long>> subIndex = index012.get(values[0]);

      // If subject not found return false.
      if (null == subIndex) {
        return false;
      }

      // Predicate null.  Could be subj, null, null or subj, null, obj.
      if (null == predicate) {

        // If object null then we've found all we need to find.
        if (null == object) {
          return true;
        }
        // If the object is not null we need to find subj, null, obj
        else {
          Map<Long, Set<Long>> objIndex = index201.get(values[2]);

          if (null == objIndex) {
            return false;
          }

          Set group = objIndex.get(values[0]);
          return null != group;
        }
      }
      // Predicate not null.  Could be subj, pred, obj or subj, pred, null.
      else {

        // look up the predicate
        Set group = subIndex.get(values[1]);
        if (null == group) {
          return false;
        }

        // Object not null.  Must be subj, pred, obj.
        if (null != object) {
          return group.contains(values[2]);
        }
        // Was subj, pred, null - must be true if we get this far.
        else {
          return true;
        }
      }
    }
  }

  /**
   * Returns an iterator to a set of statements that match a given subject,
   * predicate and object.  A null value for any of the parts of a triple are
   * treated as unconstrained, any values will be returned.
   *
   * @param subject The subject to find or null to indicate any subject.
   * @param predicate The predicate to find or null to indicate any predicate.
   * @param object ObjectNode The object to find or null to indicate any object.
   * @throws GraphException If there was an error accessing the graph.
   */
  public ClosableIterator<Triple> find(SubjectNode subject, PredicateNode predicate,
      ObjectNode object) throws GraphException {

    // Get local node values
    Long[] values;
    try {
      values = localize(subject, predicate, object);
    }
    catch (GraphException ge) {

      // A graph exception implies that the subject, predicate or object does
      // not exist in the graph.
      return new EmptyClosableIterator();
    }

    // test which index to use
    if (null != subject) {
      // test for {sp*}
      if (null != predicate) {
        // test for {spo}
        if (null != object) {
          // got {spo}
          return new ThreeFixedIterator(this, subject, predicate, object);
        }
        else {
          // got {sp*}
          return new TwoFixedIterator(index012, 0, values[0], values[1],
              elementFactory, new GraphHandler012(this),
              (Map) index012.get(values[0]));
        }
      }
      else {
        // test for {s**}
        if (null == object) {
          return new OneFixedIterator(index012, 0, values[0], elementFactory,
              new GraphHandler012(this), (Map) index012.get(values[0]));
        }
        // {s*o} so fall through
      }
    }

    if (null != predicate) {
      // test for {*po}
      if (null != object) {
        return new TwoFixedIterator(index120, 2, values[1], values[2],
            elementFactory, new GraphHandler120(this),
            (Map) index120.get(values[1]));
      }
      else {
        // test for {*p*}.  {sp*} should have been picked up above
        assert null == subject;
        return new OneFixedIterator(index120, 2, values[1], elementFactory,
            new GraphHandler120(this), (Map) index120.get(values[1]));
      }
    }

    if (null != object) {
      // test for {s*o}
      if (null != subject) {
        return new TwoFixedIterator(index201, 1, values[2], values[0],
            elementFactory, new GraphHandler201(this),
            (Map) index201.get(values[2]));
      }
      else {
        // test for {**o}.  {*po} should have been picked up above
        assert null == predicate;
        return new OneFixedIterator(index201, 1, values[2], elementFactory,
            new GraphHandler201(this), (Map) index201.get(values[2]));
      }
    }

    // {***} so return entire graph
    return new GraphIterator(elementFactory, new GraphHandler012(this));
  }

  /**
   * Returns an iterator to a set of statements that match a given subject,
   * predicate and object.  A null value for any of the parts of a triple are
   * treated as unconstrained, any values will be returned.
   *
   * @param triple The triple to find.
   * @throws GraphException If there was an error accessing the graph.
   */
  public ClosableIterator<Triple> find(Triple triple) throws GraphException {
    return find(triple.getSubject(), triple.getPredicate(), triple.getObject());
  }

  /**
   * Adds an iterator containing triples into the graph.
   *
   * @param triples The triple iterator.
   * @throws GraphException If the statements can't be made.
   */
  public void add(Iterator triples) throws GraphException {
    while (triples.hasNext()) {
      Triple triple = (Triple) triples.next();
      add(triple);
    }
  }

  /**
   * Adds a triple to the graph.
   *
   * @param triple The triple.
   * @throws GraphException If the statement can't be made.
   */
  public void add(Triple triple) throws GraphException {
    add(triple.getSubject(), triple.getPredicate(), triple.getObject());
  }

  /**
   * Adds a triple to the graph.
   *
   * @param subject The subject.
   * @param predicate The predicate.
   * @param object The object.
   * @throws GraphException If the statement can't be made.
   */
  public void add(SubjectNode subject, PredicateNode predicate,
      ObjectNode object) throws GraphException {

    // Get local node values also tests that it's a valid subject, predicate
    // and object.
    Long[] values = localize(subject, predicate, object);
    addTo012(values[0], values[1], values[2]);
    addTo120(values[1], values[2], values[0]);
    addTo201(values[2], values[0], values[1]);
  }

  /**
   * Removes an iterator containing triples from the graph.
   *
   * @param triples The triple iterator.
   * @throws GraphException If the statements can't be revoked.
   */
  public void remove(Iterator triples) throws GraphException {
    while (triples.hasNext()) {
      Triple triple = (Triple) triples.next();
      remove(triple);
    }
  }

  /**
   * Removes a triple from the graph.
   *
   * @param triple The triple.
   * @throws GraphException If there was an error revoking the statement, for
   *     example if it didn't exist.
   */
  public void remove(Triple triple) throws GraphException {
    remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
  }

  /**
   * Removes a triple from the graph.
   *
   * @param subject The subject.
   * @param predicate The predicate.
   * @param object The object.
   * @throws GraphException If there was an error revoking the statement, for
   *     example if it didn't exist.
   */
  public void remove(SubjectNode subject, PredicateNode predicate,
      ObjectNode object) throws GraphException {

    // Get local node values also tests that it's a valid subject, predicate
    // and object.
    Long[] values = localize(subject, predicate, object);

    removeFrom012(values[0], values[1], values[2]);
    // if the first one succeeded then try and attempt removal on both of the others
    try {
      removeFrom120(values[1], values[2], values[0]);
    }
    finally {
      removeFrom201(values[2], values[0], values[1]);
    }
  }

  /**
   * Returns the node factory for the graph, or creates one.
   *
   * @return the node factory for the graph, or creates one.
   */
  public GraphElementFactory getElementFactory() {
    return elementFactory;
  }

  /**
   * Returns the triple factory for the graph, or creates one.
   *
   * @return the triple factory for the graph, or creates one.
   */
  public TripleFactory getTripleFactory() {
    return tripleFactory;
  }

  /**
   * Returns the number of triples in the graph.
   *
   * @return the number of triples in the graph.
   */
  public long getNumberOfTriples() throws GraphException {
    long size = 0;
    // go over the index map
    Iterator first = index012.values().iterator();
    while (first.hasNext()) {
      // go over the sub indexes
      Iterator second = ((Map) first.next()).values().iterator();
      while (second.hasNext()) {
        // accumulate the sizes of the groups
        size += ((java.util.Collection) second.next()).size();
      }
    }
    return size;
  }

  /**
   * Returns true if the graph is empty i.e. the number of triples is 0.
   *
   * @return true if the graph is empty i.e. the number of triples is 0.
   */
  public boolean isEmpty() throws GraphException {
    return index012.isEmpty();
  }

  /**
   * Closes any underlying resources used by this graph.
   */
  public void close() {
    // no op
  }

  /**
   * Adds a triple to a single index.
   *
   * @param first The first node.
   * @param second The second node.
   * @param third The last node.
   * @throws GraphException If there was an error adding the statement.
   */
  private Long[] localize(Node first, Node second, Node third) throws
      GraphException {

    Long[] localValues = new Long[3];

    // convert the nodes to local memory nodes for convenience
    if (null != first) {
      if (first instanceof BlankNodeImpl) {
        localValues[0] = ((BlankNodeImpl) first).getId();
      }
      else {
        localValues[0] =
            elementFactory.getNodeIdByString(String.valueOf(first));
      }

      if (null == localValues[0]) {
        throw new GraphException("Subject does not exist in graph");
      }
    }

    if (null != second) {
      localValues[1] =
          elementFactory.getNodeIdByString(String.valueOf(second));

      if (null == localValues[1]) {
        throw new GraphException("Predicate does not exist in graph");
      }
    }

    if (null != third) {
      if (third instanceof BlankNodeImpl) {
        localValues[2] = ((BlankNodeImpl) third).getId();
      }
      else if (third instanceof LiteralImpl) {
        localValues[2] = elementFactory.getNodeIdByString(((LiteralImpl)
            third).getEscapedForm());
      }
      else {
        localValues[2] =
            elementFactory.getNodeIdByString(String.valueOf(third));
      }

      if (null == localValues[2]) {
        throw new GraphException("Object does not exist in graph");
      }
    }

    return localValues;
  }

  /**
   * Serializes the current object to a stream.
   *
   * @param out The stream to write to.
   * @throws IOException If an I/O error occurs while writing.
   */
  private void writeObject(ObjectOutputStream out) throws IOException {
    // write out the first index with the default writer
    out.defaultWriteObject();
    // write all the nodes as well
    out.writeObject(elementFactory.getNodePool().toArray());
    // TODO: Consider writing these nodes individually.  Converting to an array
    // may take up unnecessary memory
  }

  /**
   * Deserializes an object from a stream.
   *
   * @param in The stream to read from.
   * @throws IOException If an I/O error occurs while reading.
   */
  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    // read in the first index with the default reader
    in.defaultReadObject();
    // initialize the fields not yet done by the constructor
    try {
      init();
    }
    catch (GraphException e) {
      throw new ClassNotFoundException("Unable to initialize a new graph", e);
    }

    // read all the nodes as well
    Object[] nodes = (Object[]) in.readObject();

    try {
      // test node factory creation in case the constructor did it
      if (null == elementFactory) {
        elementFactory = new GraphElementFactoryImpl(this);
      }
    }
    catch (TripleFactoryException e) {
      throw new ClassNotFoundException("Unable to build NodeFactory", e);
    }
    // populate the node factory with these nodes
    for (int n = 0; n < nodes.length; n++) {
      elementFactory.registerNode((MemNode) nodes[n]);
    }

    // fill in the other indexes
    try {
      GraphHandler012 graphHandler012 = new GraphHandler012(this);
      graphHandler012.reconstructIndices(longIndex012, longIndex120, longIndex201);
    }
    catch (GraphException e) {
      throw new ClassNotFoundException("Unable to add to a graph index", e);
    }
  }

  void removeFrom012(Long first, Long second, Long third)
      throws GraphException {
    longIndex012.remove(first, second, third);
  }

  void removeFrom120(Long first, Long second, Long third)
      throws GraphException {
    longIndex120.remove(first, second, third);
  }

  void removeFrom201(Long first, Long second, Long third)
      throws GraphException {
    longIndex201.remove(first, second, third);
  }

  void addTo012(Long first, Long second, Long third)
      throws GraphException {
    try {
      longIndex012.add(first, second, third);
    }
    catch (GraphException e) {
      removeFrom012(first, second, third);
      throw e;
    }
  }

  void addTo120(Long first, Long second, Long third)
      throws GraphException {
    try {
      longIndex120.add(first, second, third);
    }
    catch (GraphException e) {
      removeFrom120(first, second, third);
      throw e;
    }
  }

  void addTo201(Long first, Long second, Long third)
      throws GraphException {
    longIndex201.add(first, second, third);
  }

  Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator012() {
    return longIndex012.iterator();
  }

  Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator120() {
    return longIndex120.iterator();
  }

  Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator201() {
    return longIndex201.iterator();
  }
}
