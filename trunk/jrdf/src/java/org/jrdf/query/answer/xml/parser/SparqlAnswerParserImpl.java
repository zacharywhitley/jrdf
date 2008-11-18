package org.jrdf.query.answer.xml.parser;

import org.jrdf.query.answer.AnswerType;
import static org.jrdf.query.answer.AnswerType.ASK;
import static org.jrdf.query.answer.AnswerType.SELECT;
import static org.jrdf.query.answer.AnswerType.UNKNOWN;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.BOOLEAN;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.HEAD;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.NAME;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.RESULT;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.RESULTS;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.VARIABLE;
import org.jrdf.query.answer.xml.TypeValue;

import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.LinkedHashSet;

// TODO AN/YF Refactor out the tryGetVariables
public class SparqlAnswerParserImpl implements SparqlAnswerParser {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private final XMLStreamReader parser;
    private final SparqlAnswerResultsParser resultsParser;
    private boolean hasMore;
    private LinkedHashSet<String> variables = new LinkedHashSet<String>();
    private boolean finishedVariableParsing;
    private AnswerType answerType = UNKNOWN;

    public SparqlAnswerParserImpl(InputStream stream) throws XMLStreamException {
        this.parser = INPUT_FACTORY.createXMLStreamReader(stream);
        this.resultsParser = new SparqlAnswerResultsParserImpl(parser);
        this.finishedVariableParsing = false;
        this.hasMore = false;
        parseHeadElement();
    }

    public LinkedHashSet<String> getVariables() {
        if (answerType == SELECT) {
            return variables;
        } else {
            throw new UnsupportedOperationException("Cannot get variables for non-SLECT queries.");
        }
    }

    public AnswerType getAnswerType() throws XMLStreamException {
        return answerType;
    }

    /**
     * Parses the HEAD element of the XML stream, get answer type and variable list (for SELECT queries).
     *
     * @return Answer type.
     * @throws XMLStreamException
     */
    private void parseHeadElement() throws XMLStreamException {
        int eventType = parser.getEventType();
        while (parser.hasNext()) {
            if (startOfElement(eventType, BOOLEAN)) {
                finishedVariableParsing = true;
                hasMore = true;
                answerType = ASK;
                break;
            } else if (startOfElement(eventType, VARIABLE) || endOfElement(eventType, HEAD)) {
                tryGetVariables();
                answerType = SELECT;
                break;
            }
            eventType = parser.next();
        }
    }

    public boolean hasMoreResults() {
        try {
            hasMore = hasNextResult();
        } catch (XMLStreamException e) {
            hasMore = false;
        }
        return hasMore;
    }

    public boolean getAskResult() throws XMLStreamException {
        if (answerType != ASK) {
            throw new UnsupportedOperationException("Cannot get boolean result for non-ASK queries.");
        }
        int eventType = parser.getEventType();
        while (parser.hasNext()) {
            if (startOfElement(eventType, BOOLEAN)) {
                eventType = parser.next();
                if (eventType == CHARACTERS) {
                    hasMore = false;
                    return Boolean.parseBoolean(parser.getText());
                }
            }
            eventType = parser.next();
        }
        throw new XMLStreamException("Cannot find boolean result value.");
    }

    public TypeValue[] getResults() {
        return resultsParser.getResults(variables);
    }

    public void close() {
        try {
            parser.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasNextResult() throws XMLStreamException {
        int eventType = parser.getEventType();
        while (parser.hasNext()) {
            if (startOfElement(eventType, RESULT)) {
                return true;
            }
            eventType = parser.next();
        }
        return false;
    }

    private void tryGetVariables() throws XMLStreamException {
        if (!finishedVariableParsing) {
            int currentEvent = parser.getEventType();
            while (parser.hasNext()) {
                if (startOfElement(currentEvent, VARIABLE)) {
                    String variableName = parser.getAttributeValue(null, NAME);
                    variables.add(variableName);
                } else if (endOfElement(currentEvent, HEAD) || startOfElement(currentEvent, RESULTS)) {
                    break;
                }
                currentEvent = parser.next();
            }
            finishedVariableParsing = true;
        }
    }

    private boolean startOfElement(int currentEvent, String tagName) {
        return currentEvent == START_ELEMENT && tagName.equals(parser.getLocalName());
    }

    private boolean endOfElement(int currentEvent, String tagName) {
        return currentEvent == END_ELEMENT && tagName.equals(parser.getLocalName());
    }
}
