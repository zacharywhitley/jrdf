package org.jrdf.graph.local.index.longindex.sesame;

import junit.framework.TestCase;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.GraphException;
import org.jrdf.map.TempDirectoryHandler;

public class LongIndexSesameUnitTest extends TestCase {
    private LongIndex longIndex;

    public void setUp() {
        longIndex = new LongIndexSesame(new TempDirectoryHandler(), "sesTestDb");
        longIndex.clear();
    }

    public void testNoTest() {
        assertTrue(true);
    }

//    public void testAddAndContains() throws Exception {
//        longIndex.add(1L, 2L, 3L);
//        longIndex.add(1L, 2L, 4L);
//        longIndex.add(2L, 2L, 4L);
//        assertTrue(longIndex.contains(2L));
//    }
}
