package org.jrdf.query;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.util.ClosableIterator;

import java.util.Iterator;

/**
 * Mock {@link Graph} for unit testing.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class MockGraph implements Graph {

    private ClosableIterator<Triple> triples;

    public MockGraph(ClosableIterator<Triple> expectedIterator) {
        this.triples = expectedIterator;
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
        throw new UnsupportedOperationException("Implement me...");
    }

    public boolean contains(Triple triple) throws GraphException {
        throw new UnsupportedOperationException("Implement me...");
    }

    public ClosableIterator<Triple> find(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        return triples;
    }

    public ClosableIterator<Triple> find(Triple triple) throws GraphException {
        return triples;
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
        // do nothing
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