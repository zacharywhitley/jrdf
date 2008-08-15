package org.jrdf.server;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;

public class SampleApplication extends Application {
   public SampleApplication(Context parentContext) {
      super(parentContext);
   }

   @Override
   public synchronized Restlet createRoot() {
      Router router = new Router(getContext());
      router.attachDefault(HelloWorldResource.class);
      return router;
   }
}
