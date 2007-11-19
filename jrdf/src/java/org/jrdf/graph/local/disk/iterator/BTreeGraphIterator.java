package org.jrdf.graph.local.disk.iterator;

import org.jrdf.graph.Triple;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.graph.local.index.longindex.sesame.BTree;
import org.jrdf.graph.local.index.longindex.sesame.BTreeIterator;
import static org.jrdf.graph.local.index.longindex.sesame.ByteHandler.fromBytes;
import org.jrdf.util.ClosableIterator;

import java.io.IOException;
import java.util.NoSuchElementException;

public class BTreeGraphIterator implements ClosableIterator<Triple> {
    private final BTree btree;
    private BTreeIterator bTreeIterator;
    private byte[] currentBytes;
    private final GraphHandler handler;
    private static final int TRIPLES = 3;
    private boolean nextCalled;

    public BTreeGraphIterator(BTree newBTree, GraphHandler newHandler) {
        this.btree = newBTree;
        this.handler = newHandler;
        bTreeIterator = btree.iterateAll();
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
        getNextBytes();
        return triple;
    }

    public void remove() {
        try {
            if (nextCalled && null != currentBytes) {
                throw new IllegalStateException("Next not called or beyond end of data");
            } else {
                btree.remove(currentBytes);
            }
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
