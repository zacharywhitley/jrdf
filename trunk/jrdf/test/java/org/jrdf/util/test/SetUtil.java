package org.jrdf.util.test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SetUtil {
    public static <T> Set<T> asSet(final T... o) {
        return new HashSet<T>(Arrays.asList(o));
    }

    public static <T> SortedSet<T> asSet(Comparator<? super T> comparator, final T... o) {
        SortedSet<T> set = new TreeSet<T>(comparator);
        List<T> list = Arrays.asList(o);
        set.addAll(list);
        return set;
    }
}
