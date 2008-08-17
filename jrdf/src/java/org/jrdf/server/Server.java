package org.jrdf.server;

import org.restlet.Component;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;

public class Server {
    private static final int PORT = 8182;

    public static void main(String[] args) {
        try {
            MediaType.register("application/sparql-results+xml", "SPARQL Query Results");
            Component component = new Component();
            component.getServers().add(Protocol.HTTP, PORT);
            component.getDefaultHost().attach(new SampleApplication(component.getContext()));
            component.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
