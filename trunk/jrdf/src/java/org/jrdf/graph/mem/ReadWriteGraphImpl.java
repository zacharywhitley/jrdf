package org.jrdf.graph.mem;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.mem.iterator.IteratorFactory;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.index.nodepool.NodePool;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.Iterator;

public class ReadWriteGraphImpl implements ReadWriteGraph {
    private final ReadableGraph readableGraph;
    private final WritableGraph writableGraph;

    public ReadWriteGraphImpl(LongIndex[] newIndexes, NodePool newNodePool, IteratorFactory newIteratorFactory) {
        checkNotNull(newIndexes, newNodePool, newIteratorFactory);
        this.readableGraph = new ReadableGraphImpl(newIndexes, newNodePool, newIteratorFactory);
        this.writableGraph = new WritableGraphImpl(newIndexes, newNodePool);
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        return readableGraph.contains(subject, predicate, object);
    }

    public ClosableIterator<Triple> find(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        return readableGraph.find(subject, predicate, object);
    }

    public long getSize() {
        return readableGraph.getSize();
    }

    public void localizeAndAdd(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
        writableGraph.localizeAndAdd(subject, predicate, object);
    }

    public void localizeAndRemove(SubjectNode subject, PredicateNode predicate, ObjectNode object)
        throws GraphException {
        writableGraph.localizeAndRemove(subject, predicate, object);
    }

    public void removeIterator(Iterator<Triple> triples) throws GraphException {
        writableGraph.removeIterator(triples);
    }

    public void clear() {
        writableGraph.clear();
    }
}
