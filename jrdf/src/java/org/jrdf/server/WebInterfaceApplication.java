package org.jrdf.server;

import org.jrdf.PersistentGlobalJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactoryImpl;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.Router;

public class WebInterfaceApplication extends Application {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final PersistentGlobalJRDFFactory FACTORY = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);

    @Override
    public synchronized Restlet createRoot() {
        Router router = new Router(getContext());
        router.attach("/graph/{graph}", GraphResource.class);
        router.attachDefault(GraphResource.class);
        return router;
    }

    public MoleculeGraph getGraph(String name) {
        return FACTORY.getGraph(name);
    }

    public MoleculeGraph getGraph() {
        return FACTORY.getGraph();
    }
}
