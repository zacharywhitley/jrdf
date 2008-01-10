package org.jrdf.example;

import junit.framework.TestCase;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import org.jrdf.set.SortedSetFactory;
import org.jrdf.set.BdbSortedSetFactory;

import java.util.Set;

public class BdbSortedSetFactoryPerformance extends TestCase {
    private static final int RUNS = 100000;
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private BdbEnvironmentHandler storedSetHandler;
    private String databaseName = "dbName" + System.currentTimeMillis();
    private SortedSetFactory factory;
    private Set<String> strSet;

    public void setUp() throws Exception {
        super.setUp();
        HANDLER.removeDir();
        storedSetHandler = new BdbEnvironmentHandlerImpl(HANDLER);
    }

    public void testInitialAddingToSet() {
        factory = new BdbSortedSetFactory(storedSetHandler, databaseName);
        strSet = factory.createSet(String.class);
        long start = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++) {
            strSet.add(new String(Integer.toString(i)));
        }
        long end = System.currentTimeMillis();
        System.out.println("Inserting " + RUNS + " strings takes " + (end - start)/(float) 1000 + " seconds");
        assertEquals("Set have " + RUNS + " entries", RUNS, strSet.size());
        factory.close();
    }

    public void testAddDuplicates() {
        factory = new BdbSortedSetFactory(storedSetHandler, databaseName);
        strSet = factory.createSet(String.class);
        strSet.clear();
        strSet.add("triples");
        strSet.add("triples");
        assertEquals("set only has 1 entry", 1, strSet.size());
        assertEquals("set only contains \"triples\"", "triples", strSet.iterator().next());
        factory.close();
    }
}
