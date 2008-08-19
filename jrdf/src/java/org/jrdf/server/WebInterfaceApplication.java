package org.jrdf.server;

import org.jrdf.PersistentGlobalJRDFFactory;
import static org.jrdf.PersistentGlobalJRDFFactoryImpl.getFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Resource;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.Router;

import java.net.URI;

public class WebInterfaceApplication extends Application {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final PersistentGlobalJRDFFactory FACTORY = getFactory(HANDLER);

    public WebInterfaceApplication() {
        MoleculeGraph moleculeGraph = FACTORY.getNewGraph("foo");
        try {
            Resource resource = moleculeGraph.getElementFactory().createResource();
            resource.addValue(URI.create("foo:bar"), URI.create("foo:bar"));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public synchronized Restlet createRoot() {
        Router router = new Router(getContext());
        router.attach("/graph/{graph}", GraphResource.class);
        router.attachDefault(GraphResource.class);
        return router;
    }

    public Graph getGraph(String name) {
        return FACTORY.getExistingGraph(name);
    }
}
