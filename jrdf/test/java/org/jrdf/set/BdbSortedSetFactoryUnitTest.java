package org.jrdf.set;

import junit.framework.TestCase;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;
import org.jrdf.util.bdb.BdbEnvironmentHandler;

import java.util.Set;

public class BdbSortedSetFactoryUnitTest extends TestCase {
    private static DirectoryHandler handler = new TempDirectoryHandler();
    private BdbEnvironmentHandler storedSetHandler;
    private String databaseName = "dbName" + System.currentTimeMillis();
    private SortedSetFactory factory;
    private Set<String> strSet;

    public void setUp() throws Exception {
        super.setUp();
        handler.removeDir();
        storedSetHandler = new BdbEnvironmentHandlerImpl(handler);
    }

    public void testInitialAddingToSet() {
        factory = new BdbSortedSetFactory(storedSetHandler, databaseName);
        strSet = factory.createSet(String.class);
        final int runs = 100000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < runs; i++) {
            strSet.add(new String(Integer.toString(i)));
        }
        long end = System.currentTimeMillis();
        System.out.println("Inserting " + runs + " strings takes " + (float) ((end - start)/(float) 1000) + " seconds");
        assertEquals("Set have " + runs + " entries", runs, strSet.size());
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
