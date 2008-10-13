package org.jrdf.restlet.server.local;

import org.jrdf.query.xml.AnswerXMLWriter;

import javax.xml.stream.XMLStreamException;
import java.io.StringWriter;
import java.io.IOException;

public interface NewLocalGraphResourceDoer {
    String getMaxRows();

    long getTimeTaken();

    boolean isTooManyRows();

    AnswerXMLWriter getAnswerXMLWriter(StringWriter writer) throws XMLStreamException, IOException;
}
