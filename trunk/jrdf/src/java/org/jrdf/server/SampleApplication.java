package org.jrdf.server;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.PersistentGlobalJRDFFactory;
import static org.jrdf.PersistentGlobalJRDFFactoryImpl.getFactory;

public class SampleApplication extends Application {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final PersistentGlobalJRDFFactory FACTORY = getFactory(HANDLER);

    public SampleApplication(Context parentContext) {
        super(parentContext);
    }

    @Override
    public synchronized Restlet createRoot() {
        Router router = new Router(getContext());
        router.attachDefault(GraphResource.class);
        return router;
    }
}
