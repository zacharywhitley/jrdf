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

import java.io.*;
import java.util.*;

import org.jrdf.graph.*;
import org.jrdf.util.ClosableIterator;

/**
 * A memory based RDF Graph.
 *
 * @author Paul Gearon
 *
 * @version $Revision$
 */
public class GraphImpl implements Graph, Serializable {

  // indexes are mapped as:
  // s -> {p -> {set of o}}
  // This is defined in the private add() method

  /**
   * First index.
   */
  protected HashMap index012;

  /**
   * Second index.
   */
  protected transient HashMap index120;

  /**
   * Third index.
   */
  protected transient HashMap index201;

  /**
   * Graph Element Factory.  This caches the node factory.
   */
  protected transient GraphElementFactoryImpl elementFactory;

  /**
   * Triple Element Factory.  This caches the element factory.
   */
  protected transient TripleFactoryImpl tripleFactory;

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
    if (index012 == null) {
      index012 = new HashMap();
    }
    if (index120 == null) {
      index120 = new HashMap();
    }
    if (index201 == null) {
      index201 = new HashMap();
    }

    if (elementFactory == null) {
      try {
        elementFactory = new GraphElementFactoryImpl(this);
      } catch (TripleFactoryException e) {
        throw new GraphException(e);
      }
    }

    if (tripleFactory == null) {
      try {
        tripleFactory = new TripleFactoryImpl(this, elementFactory);
      }
      catch (TripleFactoryException e) {
        throw new GraphException(e);
      }
    }
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
  public boolean contains(
      SubjectNode subject, PredicateNode predicate, ObjectNode object
  ) throws GraphException {

    // Return true if all are null and size is greater than zero.
    if ((subject == null) && (predicate == null) && (object == null)) {
      return index012.size() > 0;
    }

    // Subject null.
    if (subject == null) {

      // Predicate null - was null, null obj.
      if (predicate == null) {
        Map objIndex = (Map) this.index201.get(((MemNode) object).getId());
        return objIndex != null;
      }
      // Predicate is not null.  Could be null, pred, null or null, pred, obj.
      else {
        Map predIndex = (Map) index120.get(((MemNode) predicate).getId());

        // If predicate not found return false.
        if (predIndex == null) {
          return false;
        }

        // If the object is null and we found the predicate return true.
        if (object == null) {
          return true;
        }
        // Was null, pred, obj
        else {
          java.util.Collection group = (java.util.Collection)
              predIndex.get(((MemNode) object).getId());
          return group != null;
        }
      }
    }
    // Subject is not null.
    else {
      Map subIndex = (Map) index012.get(((MemNode) subject).getId());

      // If subject not found return false.
      if (subIndex == null) {
        return false;
      }

      // Predicate null.  Could be subj, null, null or subj, null, obj.
      if (predicate == null) {

        // If object null then we've found all we need to find.
        if (object == null) {
          return true;
        }
        // If the object is not null we need to find subj, null, obj
        else {
          Map objIndex = (Map) index201.get(((MemNode) object).getId());

          if (objIndex == null) {
            return false;
          }

          java.util.Collection group = (java.util.Collection)
              objIndex.get(((MemNode) subject).getId());
          return group != null;
        }
      }
      // Predicate not null.  Could be subj, pred, obj or subj, pred, null.
      else {

        // look up the predicate
        java.util.Collection group = (java.util.Collection)
            subIndex.get(((MemNode) predicate).getId());
        if (group == null) return false;

        // Object not null.  Must be subj, pred, obj.
        if (object != null) {
          return group.contains(((MemNode) object).getId());
        }
        // Was subj, pred, null - must be true if we get this far.
        else {
          return true;
        }
      }
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
    return contains(triple.getSubject(), triple.getPredicate(), triple.getObject());
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
  public ClosableIterator find(
      SubjectNode subject, PredicateNode predicate, ObjectNode object
  ) throws GraphException {
    // test which index to use
    if (subject != null) {
      // test for {sp*}
      if (predicate != null) {
        // test for {spo}
        if (object != null) {
          // got {spo}
          return new ThreeFixedIterator(this, subject, predicate, object);
        } else {
          // got {sp*}
          return new TwoFixedIterator(index012, 0, subject, predicate, elementFactory);
        }
      } else {
        // test for {s**}
        if (object == null) {
          return new OneFixedIterator(index012, 0, subject, elementFactory);
        }
        // {s*o} so fall through
      }
    }

    if (predicate != null) {
      // test for {*po}
      if (object != null) {
        return new TwoFixedIterator(index120, 2, predicate, object, elementFactory);
      } else {
        // test for {*p*}.  {sp*} should have been picked up above
        assert subject == null;
        return new OneFixedIterator(index120, 2, predicate, elementFactory);
      }
    }

    if (object != null) {
      // test for {s*o}
      if (subject != null) {
        return new TwoFixedIterator(index201, 1, object, subject, elementFactory);
      } else {
        // test for {**o}.  {*po} should have been picked up above
        assert predicate == null;
        return new OneFixedIterator(index201, 1, object, elementFactory);
      }
    }

    // {***} so return entire graph
    return new GraphIterator(index012, elementFactory);
  }

  /**
   * Returns an iterator to a set of statements that match a given subject,
   * predicate and object.  A null value for any of the parts of a triple are
   * treated as unconstrained, any values will be returned.
   *
   * @param triple The triple to find.
   * @throws GraphException If there was an error accessing the graph.
   */
  public ClosableIterator find(Triple triple) throws GraphException {
    return find(triple.getSubject(), triple.getPredicate(), triple.getObject());
  }

  /**
   * Adds a triple to the graph.
   *
   * @param subject The subject.
   * @param predicate The predicate.
   * @param object The object.
   * @throws GraphException If the statement can't be made.
   */
  public void add(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {

    // Test that it's a valid subject, predicate and object.
    isValidTriple(subject, predicate, object);

    // add to the first index
    add(index012, subject, predicate, object);
    // try and back out changes if an insertion fails
    try {
      // add to the second index
      add(index120, predicate, object, subject);
      try {
        // add to the third index
        add(index201, object, subject, predicate);
      } catch (GraphException e) {
        remove(index120, predicate, object, subject);
        throw e;
      }
    } catch (GraphException e) {
      remove(index012, subject, predicate, object);
      throw e;
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
   * Adds an iterator containing triples into the graph.
   *
   * @param triples The triple iterator.
   * @throws GraphExcepotion If the statements can't be made.
   */
  public void add(Iterator triples) throws GraphException {
    while (triples.hasNext()) {

      Triple triple = (Triple) triples.next();
      add(triple);
    }
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

    // Test that it's a valid subject, predicate and object.
    isValidTriple(subject, predicate, object);

    remove(index012, subject, predicate, object);
    // if the first one succeeded then try and attempt removal on both of the others
    try {
      remove(index120, predicate, object, subject);
    } finally {
      remove(index201, object, subject, predicate);
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
   * Removes an iterator containing triples from the graph.
   *
   * @param triples The triple iterator.
   * @throws GraphExcepotion If the statements can't be revoked.
   */
  public void remove(Iterator triples) throws GraphException {
    while (triples.hasNext()) {
      Triple triple = (Triple) triples.next();
      remove(triple);
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
      Iterator second = ((Map)first.next()).values().iterator();
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
   *
   * @throws GraphException If there was a problem closing off an underlying data store.
   */
  public void close() throws GraphException {
    // no op
  }


  /**
   * Adds a triple to a single index.
   *
   * @param index The index to add the statement to.
   * @param first The first node.
   * @param second The second node.
   * @param third The last node.
   * @throws GraphException If there was an error adding the statement.
   */
  private void add(Map index, Node first, Node second, Node third) throws GraphException {
    // convert the nodes to local memory nodes for convenience
    MemNode mfirst = (MemNode)first;
    MemNode msecond = (MemNode)second;
    MemNode mthird = (MemNode)third;
    // now pass the ids off to the add method which accepts IDs
    add(index, mfirst.getId(), msecond.getId(), mthird.getId());
  }


  /**
   * Adds a triple to a single index.  This method defines the internal structure.
   *
   * @param index The index to add the statement to.
   * @param first The first node id.
   * @param second The second node id.
   * @param third The last node id.
   * @throws GraphException If there was an error adding the statement.
   */
  private void add(Map index, Long first, Long second, Long third) throws GraphException {
    // find the sub index
    Map subIndex = (Map)index.get(first);
    // check that the subindex exists
    if (subIndex == null) {
      // no, so create it and add it to the index
      subIndex = new HashMap();
      index.put(first, subIndex);
    }

    // find the final group
    java.util.Collection group = (java.util.Collection) subIndex.get(second);
    // check that the group exists
    if (group == null) {
      // no, so create it and add it to the subindex
      group = new HashSet();
      subIndex.put(second, group);
    }

    // Add the final node to the group
    group.add(third);
  }


  /**
   * Removes a triple from a single index.
   *
   * @param index The index to remove the statement from.
   * @param first The first node.
   * @param second The second node.
   * @param third The last node.
   * @throws GraphException If there was an error revoking the statement, for
   *     example if it didn't exist.
   */
  private void remove(Map index, Node first, Node second, Node third) throws GraphException {
    // convert the nodes to local memory nodes for convenience
    MemNode mfirst = (MemNode)first;
    MemNode msecond = (MemNode)second;
    MemNode mthird = (MemNode)third;

    // find the sub index
    Map subIndex = (Map)index.get(mfirst.getId());
    // check that the subindex exists
    if (subIndex == null) {
      throw new GraphException("Unable to remove nonexistent statement");
    }
    // find the final group
    java.util.Collection group = (java.util.Collection) subIndex.get(msecond.getId());
    // check that the group exists
    if (group == null) {
      throw new GraphException("Unable to remove nonexistent statement");
    }
    // remove from the group, report error if it didn't exist
    if (!group.remove(mthird.getId())) {
      throw new GraphException("Unable to remove nonexistent statement");
    }
    // clean up the graph
    if (group.isEmpty()) {
      subIndex.remove(msecond.getId());
      if (subIndex.isEmpty()) {
        index.remove(mfirst.getId());
      }
    }
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
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    // read in the first index with the default reader
    in.defaultReadObject();
    // initialize the fields not yet done by the constructor
    try {
      init();
    } catch (GraphException e) {
      throw new ClassNotFoundException("Unable to initialize a new graph", e);
    }

    // read all the nodes as well
    Object[] nodes = (Object[])in.readObject();

    try {
      // test node factory creation in case the constructor did it
      if (elementFactory == null) {
        elementFactory = new GraphElementFactoryImpl(this);
      }
    } catch (TripleFactoryException e) {
      throw new ClassNotFoundException("Unable to build NodeFactory", e);
    }
    // populate the node factory with these nodes
    for (int n = 0; n < nodes.length; n++) {
      elementFactory.registerNode((MemNode)nodes[n]);
    }

    // fill in the other indexes
    try {
      // iterate over the first column
      Iterator firstEntries = index012.entrySet().iterator();
      while (firstEntries.hasNext()) {
        Map.Entry firstEntry = (Map.Entry)firstEntries.next();
        Long first = (Long)firstEntry.getKey();
        // now iterate over the second column
        Iterator secondEntries = ((Map)firstEntry.getValue()).entrySet().iterator();
        while (secondEntries.hasNext()) {
          Map.Entry secondEntry = (Map.Entry)secondEntries.next();
          Long second = (Long)secondEntry.getKey();
          // now iterate over the third column
          Iterator thirdValues = ((Set)secondEntry.getValue()).iterator();
          while (thirdValues.hasNext()) {
            Long third = (Long)thirdValues.next();
            // now add the row to the other two indexes
            add(index120, first, second, third);
            add(index201, first, second, third);
          }
        }
      }
    } catch (GraphException e) {
      throw new ClassNotFoundException("Unable to add to a graph index", e);
    }
  }

  private void isValidTriple(SubjectNode subject, PredicateNode predicate,
      ObjectNode object) throws GraphException {

    // Test valid subject
    if (subject instanceof URIReference) {
      if (elementFactory.getNodeIdByString(((URIReference) subject).getURI().
          toString()) == null) {
        throw new GraphException("Subject does not exist in graph");
      }
    }
    else if (subject instanceof BlankNodeImpl) {
      Node node = elementFactory.getNodeById(((BlankNodeImpl) subject).getId());
      if ((node == null) || (!node.equals(subject))) {
        throw new GraphException("Subject does not exist in graph");
      }
    }

    // Test valid predicate
    if (predicate instanceof URIReference) {
      if (elementFactory.getNodeIdByString(((URIReference) predicate).getURI().
          toString()) == null) {
        throw new GraphException("Predicate does not exist in graph");
      }
    }

    // Test valid object
    if (object instanceof URIReference) {
      if (elementFactory.getNodeIdByString(((URIReference) object).getURI().
          toString()) == null) {
        throw new GraphException("Object does not exist in graph");
      }
    }
    else if (object instanceof Literal) {
      if (elementFactory.getNodeIdByString(((Literal) object).getEscapedForm().
          toString()) == null) {
        throw new GraphException("Object does not exist in graph");
      }
    }
    else if (subject instanceof BlankNodeImpl) {
      Node node = elementFactory.getNodeById(((BlankNodeImpl) object).getId());
      if ((node == null) || (!node.equals(object))) {
        throw new GraphException("Object does not exist in graph");
      }
    }
  }
}
