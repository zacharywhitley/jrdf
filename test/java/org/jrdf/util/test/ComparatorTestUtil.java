package org.jrdf.util.test;

import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.Attribute;

import java.util.Comparator;

public class ComparatorTestUtil {
    public static void checkNullPointerException(final Comparator attComparator, final Object object1,
            final Object object2) {
        AssertThrows.assertThrows(NullPointerException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                //noinspection unchecked
                attComparator.compare(object1, object2);
            }
        });
    }

    
}
