package org.jrdf.query.server.distributed;

import org.jrdf.query.server.GraphApplication;

public interface DistributedQueryGraphApplication extends GraphApplication {
    void addServers(String... servers);

    void removeServers(String... servers);

    void setPort(int port);

    int getPort();
}
