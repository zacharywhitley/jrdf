package org.jrdf.graph.local.disk.iterator;

import org.jrdf.graph.Triple;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.graph.local.index.longindex.sesame.BTreeIterator;
import org.jrdf.graph.local.index.longindex.sesame.TripleBTree;
import static org.jrdf.graph.local.index.longindex.sesame.ByteHandler.fromBytes;
import org.jrdf.graph.local.iterator.ClosableIterator;

import java.io.IOException;
import java.util.NoSuchElementException;

public class BTreeGraphIterator implements ClosableIterator<Triple> {
    private static final int TRIPLES = 3;
    private final TripleBTree btree;
    private final GraphHandler handler;
    private BTreeIterator bTreeIterator;
    private byte[] bytesToRemove;
    private byte[] currentBytes;
    private boolean nextCalled;

    public BTreeGraphIterator(TripleBTree newBTree, GraphHandler newHandler, Long... nodes) {
        this.btree = newBTree;
        this.handler = newHandler;
        bTreeIterator = btree.getIterator(nodes);
        getNextBytes();
    }

    public boolean close() {
        try {
            bTreeIterator.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean hasNext() {
        return currentBytes != null;
    }

    public Triple next() {
        if (null == currentBytes) {
            throw new NoSuchElementException();
        }
        nextCalled = true;
        Triple triple = handler.createTriple(fromBytes(currentBytes, TRIPLES));
        bytesToRemove = currentBytes;
        getNextBytes();
        return triple;
    }

    public void remove() {
        try {
            if (!nextCalled && null == bytesToRemove) {
                throw new IllegalStateException("Next not called or beyond end of data");
            } else {
                btree.remove(bytesToRemove);
                Long[] longs = fromBytes(bytesToRemove, TRIPLES);
                handler.remove(longs);
            }
        } catch (GraphException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getNextBytes() {
        try {
            currentBytes = bTreeIterator.next();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
