package org.jrdf.query.server.distributed;

import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.xml.AnswerXMLWriter;
import org.restlet.resource.ResourceException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;

public interface DistributedQueryGraphApplication {
    void addServers(String... servers);

    void removeServers(String... servers);

    String[] getServers();

    void answerQuery(String graphName, String queryString) throws ResourceException;

    void setPort(int port);

    int getPort();

    AnswerXMLWriter getAnswerXMLWriter(Writer writer) throws XMLStreamException, IOException;

    long getTimeTaken();

    boolean isTooManyRows();

    Answer answerQuery2(String graphName, String queryString);
}
