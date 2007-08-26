package org.jrdf.graph.mem.iterator;

import org.jrdf.graph.PredicateNode;
import org.jrdf.util.ClosableIterator;

import java.util.Iterator;

public class PredicateClosableIterator implements ClosableIterator<PredicateNode> {
    private final Iterator<PredicateNode> iterator;

    public PredicateClosableIterator(Iterator<PredicateNode> iterator) {
        this.iterator = iterator;
    }

    public boolean close() {
        return true;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public PredicateNode next() {
        return iterator.next();
    }

    public void remove() {
        iterator.remove();
    }
}
