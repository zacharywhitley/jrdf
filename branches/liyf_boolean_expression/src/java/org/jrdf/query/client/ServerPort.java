package org.jrdf.query.client;

public final class ServerPort {
    private int port;
    private String hostname;

    public static ServerPort createServerPort(String serverPort, int defaultPort) {
        return new ServerPort(serverPort, defaultPort);
    }

    private ServerPort(String serverPort, int defaultPort) {
        String[] hostPort = serverPort.split(":");
        if (hostPort.length == 1) {
            this.port = defaultPort;
            this.hostname = serverPort;
        } else if (hostPort.length == 2) {
            this.port = Integer.parseInt(hostPort[1]);
            this.hostname = hostPort[0];
        } else {
            throw new IllegalArgumentException("Unable to parse for hostname and port: " + serverPort);
        }
    }

    public int getPort() {
        return port;
    }

    public String getHostname() {
        return hostname;
    }
}
