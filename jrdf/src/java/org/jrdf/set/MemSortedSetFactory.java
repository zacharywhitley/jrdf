package org.jrdf.set;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.global.ReverseGroundedTripleComparatorImpl;
import org.jrdf.graph.local.mem.BlankNodeComparator;
import org.jrdf.graph.local.mem.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.mem.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.mem.NodeComparatorImpl;
import org.jrdf.graph.local.mem.TripleComparatorImpl;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An in memory implementation that uses TreeSets and a small number of known types: Triples and PredicateNodes.
 */
public class MemSortedSetFactory implements SortedSetFactory {
    private Map<Class<?>, Comparator<?>> defaultComparators = new HashMap<Class<?>, Comparator<?>>();

    public MemSortedSetFactory() {
        NodeTypeComparator nodeTypeComparator = new NodeTypeComparatorImpl();
        BlankNodeComparator comparator = new LocalizedBlankNodeComparatorImpl(new LocalizedNodeComparatorImpl());
        NodeComparator newNodeComparator = new NodeComparatorImpl(nodeTypeComparator,
            comparator);
        TripleComparatorImpl tripleComparator = new TripleComparatorImpl(newNodeComparator);
        defaultComparators.put(Triple.class, new ReverseGroundedTripleComparatorImpl(tripleComparator));
        defaultComparators.put(PredicateNode.class, newNodeComparator);
        defaultComparators.put(BlankNode.class, comparator);
    }

    @SuppressWarnings({ "unchecked" })
    public <T> SortedSet<T> createSet(Class<T> clazz) {
        if (defaultComparators.containsKey(clazz)) {
            Comparator<T> comparator = (Comparator<T>) defaultComparators.get(clazz);
            return new TreeSet<T>(comparator);
        } else {
            return new TreeSet<T>();
        }
    }

    public <T> SortedSet<T> createSet(Class<T> clazz, Comparator<? super T> comparator) {
        return new TreeSet<T>(comparator);
    }

    public void close() {
    }
}
