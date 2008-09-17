package org.jrdf.restlet.server;

import org.restlet.Component;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;

public class Server implements Runnable {
    /**
     * The port number of the server(s).
     */
    public static final int PORT = 8182;
    /**
     * The media type for sparql result in XML.
     */
    public static final String SPARQL_XML_RESULT_MEDIA_TYPE_STRING = "application/sparql-results+xml";
    protected Component component;

    private void start() throws Exception {
        MediaType.register(SPARQL_XML_RESULT_MEDIA_TYPE_STRING, "SPARQL Query Results");
        component = new Component();
        component.getServers().add(Protocol.HTTP, PORT);
        component.getDefaultHost().attach(new WebInterfaceApplication());
        component.start();
    }

    public void run() {
        try {
            start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() throws Exception {
        component.stop();
    }

    public static void main(String[] args) {
        try {
            Thread serverThread = new Thread(new Server());
            serverThread.start();
            System.err.println("Server started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
