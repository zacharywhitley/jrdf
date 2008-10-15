package org.jrdf.query.answer.xml;

import static org.jrdf.query.answer.xml.AnswerXMLWriter.*;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import java.util.Set;
import java.util.HashSet;

public class SparqlAnswerParser {
    private XMLStreamReader parser;
    private boolean hasMore;
    private int currentEvent;
    private Set<String> variables = new HashSet<String>();
    private boolean finishedVariableParsing;

    public SparqlAnswerParser(XMLStreamReader newParser) {
        this.parser = newParser;
    }

    public Set<String> getVariables() throws XMLStreamException {
        if (!finishedVariableParsing) {
            parseAnswerToGetVariables();
        }
        return variables;
    }

    public String getResult() throws XMLStreamException {
        String variableName = parser.getAttributeValue(null, NAME);
        currentEvent = parser.next();
        String tagName = parser.getLocalName();
        if (LITERAL.equals(tagName)) {
            String datatype = parser.getAttributeValue(null, DATATYPE);
            if (datatype != null) {
                // write datatype.
            }
            String language = parser.getAttributeValue(null, XML_LANG);
            if (language != null) {
                // write language.
            }
        }
        final String text = parser.getElementText();
        return text;
    }

    public boolean hasMoreResults() {
        try {
            hasMore = getToNextResult();
        } catch (XMLStreamException e) {
            hasMore = false;
        }
        return hasMore;
    }

    public void close() {
        try {
            parser.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean getToNextResult() throws XMLStreamException {
        while (parser.hasNext()) {
            int eventType = parser.getEventType();
            if (eventType == START_ELEMENT) {
                final String tagName = parser.getLocalName();
                if (RESULT.equals(tagName)) {
                    return true;
                } else if (!finishedVariableParsing && VARIABLE.equals(tagName)) {
                    parseAnswerToGetVariables();
                }
            }
            parser.next();
        }
        return false;
    }

    private void parseAnswerToGetVariables() throws XMLStreamException {
        while (parser.hasNext()) {
            currentEvent = parser.getEventType();
            if (currentEvent == START_ELEMENT && VARIABLE.equals(parser.getLocalName())) {
                variables.add(parser.getAttributeValue(null, NAME));
            } else if (currentEvent == END_ELEMENT) {
                if (HEAD.equals(parser.getLocalName())) {
                    break;
                }
            }
            currentEvent = parser.next();
        }
        finishedVariableParsing = true;
    }
}
