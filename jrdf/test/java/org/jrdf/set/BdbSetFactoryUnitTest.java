package org.jrdf.set;

import junit.framework.TestCase;
import org.jrdf.map.DirectoryHandler;
import org.jrdf.map.TempDirectoryHandler;

import java.util.Set;

public class BdbSetFactoryUnitTest extends TestCase {
    private static DirectoryHandler handler = new TempDirectoryHandler();
    private StoredSetHandler storedSetHandler;
    private String databaseName = "dbName" + System.currentTimeMillis();
    private SetFactory factory;
    private Set<String> strSet;

    public void setUp() throws Exception {
        super.setUp();
        handler.removeDir();
        storedSetHandler = new StoredSetHandlerImpl(handler);
    }

    public void testInitialAddingToSet() {
        factory = new BdbSetFactory(storedSetHandler, databaseName);
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
        factory = new BdbSetFactory(storedSetHandler, databaseName);
        strSet = factory.createSet(String.class);
        strSet.clear();
        strSet.add("triples");
        strSet.add("triples");
        assertEquals("set only has 1 entry", 1, strSet.size());

        assertEquals("set only contains \"triples\"", "triples", strSet.iterator().next());
        factory.close();
    }
}
