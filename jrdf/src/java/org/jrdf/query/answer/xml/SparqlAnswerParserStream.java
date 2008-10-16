package org.jrdf.query.answer.xml;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

public interface SparqlAnswerParserStream extends SparqlAnswerParser {
    void addStream(InputStream stream) throws InterruptedException, XMLStreamException;
}
