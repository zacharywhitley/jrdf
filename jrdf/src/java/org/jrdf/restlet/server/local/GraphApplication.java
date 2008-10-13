package org.jrdf.restlet.server.local;

import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.query.xml.AnswerXMLWriter;
import org.jrdf.query.Answer;
import org.restlet.resource.ResourceException;

import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.io.IOException;

public interface GraphApplication {

    String getGraphsDir();

    String getFormat();

    void setFormat(String format);

    String getMaxRows();

    void setMaxRows(String rows);

    void close();

    MoleculeGraph getGraph(String name);

    MoleculeGraph getGraph();

    String[] getServers();

    void answerQuery(String graphName, String queryString) throws ResourceException;

    long getTimeTaken();

    boolean isTooManyRows();

    AnswerXMLWriter getAnswerXMLWriter(Writer writer) throws XMLStreamException, IOException;

    Answer answerQuery2(String graphName, String queryString) throws ResourceException;
}
