package org.jrdf.connection;

import org.jrdf.graph.*;
import org.jrdf.util.ClosableIterator;

import java.util.Iterator;

/**
 * Mock &quot;bad&quot; {@link Graph} for unit testing.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class MockBadGraph implements Graph {

  public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }

  public boolean contains(Triple triple) throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }

  public ClosableIterator find(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }

  public ClosableIterator find(Triple triple) throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }

  public void add(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }

  public void add(Triple triple) throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }

  public void add(Iterator triples) throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }

  public void close() {
    throw new UnsupportedOperationException("Implement me...");
  }

  public void remove(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }

  public void remove(Triple triple) throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }

  public void remove(Iterator triples) throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }

  public GraphElementFactory getElementFactory() {
    throw new UnsupportedOperationException("Implement me...");
  }

  public TripleFactory getTripleFactory() {
    throw new UnsupportedOperationException("Implement me...");
  }

  public long getNumberOfTriples() throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }

  public boolean isEmpty() throws GraphException {
    throw new UnsupportedOperationException("Implement me...");
  }
}
