package org.jrdf.server;

import org.jrdf.PersistentJRDFFactory;
import org.jrdf.PersistentJRDFFactoryImpl;
import org.jrdf.graph.Graph;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.Router;

public class WebInterfaceApplication extends Application {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final PersistentJRDFFactory FACTORY = PersistentJRDFFactoryImpl.getFactory(HANDLER);

    @Override
    public synchronized Restlet createRoot() {
        Router router = new Router(getContext());
        router.attach("/graph/{graph}", GraphResource.class);
        router.attachDefault(GraphResource.class);
        return router;
    }

    public Graph getGraph(String name) {
        return FACTORY.getGraph(name);
    }
}
