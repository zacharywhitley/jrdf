package org.jrdf.query.answer.xml.parser;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

public interface SparqlAnswerStreamParser extends SparqlAnswerParser {
    void addStream(InputStream stream) throws InterruptedException, XMLStreamException;
}
