package org.jrdf.util.test;

import java.util.Comparator;

@SuppressWarnings({ "unchecked" })
public class ComparatorTestUtil {
    public static void checkNullPointerException(final Comparator attComparator, final Object object1,
            final Object object2) {
        AssertThrows.assertThrows(NullPointerException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                attComparator.compare(object1, object2);
            }
        });
    }


}
