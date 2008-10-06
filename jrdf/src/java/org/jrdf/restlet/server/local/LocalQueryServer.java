package org.jrdf.restlet.server.local;

import org.restlet.Component;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.jrdf.restlet.server.BaseGraphApplication;

public class LocalQueryServer {
    /**
     * The port number of the server(s).
     */
    public static final int PORT = 8182;
    /**
     * The media type for sparql result in XML.
     */
    public static final String SPARQL_XML_RESULT_MEDIA_TYPE_STRING = "application/sparql-results+xml";
    protected Component component;

    public void start() throws Exception {
        MediaType.register(SPARQL_XML_RESULT_MEDIA_TYPE_STRING, "SPARQL Query Results");
        final BaseGraphApplication graphApplication = new WebInterfaceGraphApplication();
        component = new Component();
        component.getServers().add(Protocol.HTTP, PORT);
        component.getDefaultHost().attach(graphApplication);
        component.start();
        System.out.println("Local server started.");
    }

    public void stop() throws Exception {
        component.stop();
        System.out.println("Local server stopped");
    }

    public static void main(String[] args) throws Exception {
        final LocalQueryServer server = new LocalQueryServer();
        try {
            server.start();
        } catch (Exception e) {
            if (server != null) {
                server.stop();
            }
            throw new RuntimeException(e);
        }
    }
}
