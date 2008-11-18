package org.jrdf.query.answer.xml;

import org.jrdf.query.answer.AskAnswer;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Yuan-Fang Li
 * @version $Id:$
 */

public class AskAnswerXMLStreamWriter extends AbstractXMLStreamWriter {
    private AskAnswer answer;
    private boolean hasMore;

    private AskAnswerXMLStreamWriter() {
    }

    public AskAnswerXMLStreamWriter(AskAnswer answer) {
        this.answer = answer;
        hasMore = true;
    }

    public AskAnswerXMLStreamWriter(AskAnswer answer, Writer writer) throws XMLStreamException {
        this(answer);
        this.streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
    }

    public void setWriter(Writer writer) throws XMLStreamException, IOException {
        close();
        this.streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
    }

    public boolean hasMoreResults() {
        return hasMore;
    }

    public void write() throws XMLStreamException {
        checkNotNull(streamWriter);
        super.write();
    }

    public void write(Writer writer) throws XMLStreamException {
        streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
        super.write();
    }

    public void writeResult() throws XMLStreamException {
        if (hasMoreResults()) {
            streamWriter.writeStartElement(BOOLEAN);
            streamWriter.writeCharacters(Boolean.toString(answer.getResult()));
            streamWriter.writeEndElement();
            streamWriter.flush();
            hasMore = false;
        }
    }

    public void writeVariables() throws XMLStreamException {
    }
}
