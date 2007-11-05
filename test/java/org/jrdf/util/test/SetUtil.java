package org.jrdf.util.test;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class SetUtil {
    public static <T> Set<T> asSet(final T... o) {
        return new HashSet<T>(Arrays.asList(o));
    }
}
