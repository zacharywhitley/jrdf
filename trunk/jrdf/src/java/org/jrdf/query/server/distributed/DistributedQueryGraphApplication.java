package org.jrdf.query.server.distributed;

import org.restlet.resource.ResourceException;
import org.jrdf.query.xml.AnswerXMLWriter;

import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.io.IOException;

public interface DistributedQueryGraphApplication {
    void addServers(String... servers);

    void removeServers(String... servers);

    String[] getServers();

    void answerQuery(String graphName, String queryString) throws ResourceException;

    void setPort(int port);

    int getPort();

    AnswerXMLWriter getAnswerXMLWriter(Writer writer) throws XMLStreamException, IOException;

    long getTimeTaken();
}
