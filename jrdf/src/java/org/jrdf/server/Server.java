package org.jrdf.server;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class Server {
    public static void main(String[] args) {
        try {
            Component component = new Component();
            component.getServers().add(Protocol.HTTP, 8182);
            component.getDefaultHost().attach(new SampleApplication(component.getContext()));
            component.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
