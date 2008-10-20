package org.jrdf.query.server;

import org.jrdf.query.answer.xml.AnswerXMLWriter;
import static org.restlet.data.CharacterSet.UTF_8;
import org.restlet.data.MediaType;
import org.restlet.resource.WriterRepresentation;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;

public class SparqlRepresentation extends WriterRepresentation {
    private AnswerXMLWriter answerWriter;

    public SparqlRepresentation(MediaType mediaType, AnswerXMLWriter newAnswerWriter) {
        super(mediaType);
        this.answerWriter = newAnswerWriter;
        setCharacterSet(UTF_8);
    }

    @Override
    public void write(Writer writer) throws IOException {
        try {
            answerWriter.setWriter(writer);
            answerWriter.write();
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage());
        }
    }
}
