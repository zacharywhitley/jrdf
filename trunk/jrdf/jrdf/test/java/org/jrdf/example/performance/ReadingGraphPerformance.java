package org.jrdf.example.performance;

import org.jrdf.SortedDiskJRDFFactory;
import org.jrdf.collection.BdbMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.collection.MapFactory;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;
import org.jrdf.writer.BlankNodeRegistry;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: Apr 7, 2008
 * Time: 11:30:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReadingGraphPerformance extends AbstractGraphPerformance {
    private DirectoryHandler handler = new TempDirectoryHandler();
    private BdbEnvironmentHandler newHandler;
    private MapFactory mapFactory;

    public Graph getGraph() {
        return SortedDiskJRDFFactory.getFactory().getGraph();
    }

    public MapFactory getMapFactory() {
        handler.removeDir();
        handler.makeDir();
        newHandler = new BdbEnvironmentHandlerImpl(handler);
        mapFactory = new BdbMapFactory(newHandler, "map");
        return mapFactory;
    }

    public BlankNodeRegistry getBlankNodeRegistry() {
        return null;
    }

    public static void main(String [] args) throws Exception {
        ReadingGraphPerformance perf = new ReadingGraphPerformance();
        perf.testPerformance(new String[] {"0", "0", "0"});
    }
}
