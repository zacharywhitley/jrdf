package org.jrdf.graph.local.index.longindex.sesame;

import junit.framework.TestCase;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.map.TempDirectoryHandler;
import static org.jrdf.util.test.SetUtil.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LongIndexSesameUnitTest extends TestCase {
    private LongIndex longIndex;

    public void setUp() {
        longIndex = new LongIndexSesame(new TempDirectoryHandler(), "sesTestDb");
        longIndex.clear();
    }

    public void testNoTest() {
        assertTrue(true);
    }

    public void testAddAndContains() throws Exception {
        longIndex.add(1L, 2L, 3L);
        longIndex.add(1L, 2L, 4L);
        longIndex.add(2L, 2L, 4L);
        longIndex.add(2L, 2L, 5L);
        assertTrue(longIndex.contains(2L));
    }

    public void testAddAndSubIndex() throws Exception {
        longIndex.add(1L, 2L, 3L);
        longIndex.add(1L, 2L, 4L);
        longIndex.add(2L, 2L, 4L);
        longIndex.add(2L, 2L, 5L);
        Set<Long> set1 = asSet(3L, 4L);
        Set<Long> set2 = asSet(4L, 5L);
        checkSubIndexResult(1L, 2L, set1);
        checkSubIndexResult(2L, 2L, set2);
    }

    public void testAddition() throws Exception {
        longIndex.add(1L, 2L, 3L);
        checkNumberOfTriples(1, longIndex.getSize());
        longIndex.add(1L, 2L, 3L);
        checkNumberOfTriples(1, longIndex.getSize());
        longIndex.add(4L, 5L, 6L);
        checkNumberOfTriples(2, longIndex.getSize());
    }

    public void testRemove() throws Exception {
        longIndex.add(1L, 2L, 3L);
        longIndex.add(3L, 4L, 3L);
        checkNumberOfTriples(2, longIndex.getSize());
        longIndex.remove(1L, 2L, 3L);
        checkNumberOfTriples(1, longIndex.getSize());
        longIndex.remove(3L, 4L, 3L);
        checkNumberOfTriples(0, longIndex.getSize());
    }

    public void testClear() throws Exception {
        longIndex.add(1L, 2L, 3L);
        longIndex.add(3L, 4L, 3L);
        longIndex.clear();
        checkNumberOfTriples(0, longIndex.getSize());
    }

    private void checkSubIndexResult(long subject, long predicate, Set<Long> objects) {
        Map<Long,Set<Long>> result = longIndex.getSubIndex(subject);
        Map<Long, Set<Long>> expectedResult = new HashMap<Long, Set<Long>>();
        expectedResult.put(predicate, objects);
        assertEquals(expectedResult, result);
    }

    private void checkNumberOfTriples(final int expectedNumber, final long actualSize) {
        assertEquals("Incorrect numober of triples: ", expectedNumber, actualSize);
    }
}
