package org.jrdf.set;

import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
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

public class MemSetFactory implements SetFactory {
    private Map<Class<?>, Comparator<?>> defaultComparators = new HashMap<Class<?>, Comparator<?>>();

    public MemSetFactory() {
        NodeTypeComparator nodeTypeComparator = new NodeTypeComparatorImpl();
        BlankNodeComparator comparator = new LocalizedBlankNodeComparatorImpl(new LocalizedNodeComparatorImpl());
        NodeComparator newNodeComparator = new NodeComparatorImpl(nodeTypeComparator,
            comparator);
        defaultComparators.put(Triple.class, new TripleComparatorImpl(newNodeComparator));
        defaultComparators.put(PredicateNode.class, newNodeComparator);
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
