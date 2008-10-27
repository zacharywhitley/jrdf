package org.jrdf.query.answer.xml;

import static org.jrdf.query.answer.xml.AnswerXMLWriter.HEAD;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.NAME;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.RESULT;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.VARIABLE;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.LinkedHashSet;

// TODO AN/YF Refactor out the tryGetVariables
public class SparqlAnswerParserImpl implements SparqlAnswerParser {
    private final XMLStreamReader parser;
    private final SparqlAnswerResultsParser resultsParser;
    private boolean hasMore;
    private LinkedHashSet<String> variables = new LinkedHashSet<String>();
    private boolean finishedVariableParsing;

    public SparqlAnswerParserImpl(XMLStreamReader newParser) {
        this.parser = newParser;
        this.resultsParser = new SparqlAnswerResultsParserImpl(parser);
    }

    public LinkedHashSet<String> getVariables() {
        tryGetVariables();
        return variables;
    }

    public boolean hasMoreResults() {
        try {
            tryGetVariables();
            hasMore = hasNextResult();
        } catch (XMLStreamException e) {
            hasMore = false;
        }
        return hasMore;
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
            if (startResultElement(eventType)) {
                return true;
            } else if (!finishedVariableParsing && VARIABLE.equals(parser.getLocalName())) {
                parseAnswerToGetVariables();
            }
            eventType = parser.next();
        }
        return false;
    }

    private boolean startResultElement(int currentEvent) {
        return currentEvent == START_ELEMENT && RESULT.equals(parser.getLocalName());
    }

    private void tryGetVariables() {
        if (!finishedVariableParsing) {
            try {
                parseAnswerToGetVariables();
            } catch (XMLStreamException e) {
                ;
            }
        }
    }

    private void parseAnswerToGetVariables() throws XMLStreamException {
        int currentEvent = parser.getEventType();
        while (parser.hasNext() && !endOfHeadElement(currentEvent)) {
            if (startVariableElement(currentEvent)) {
                String variableName = parser.getAttributeValue(null, NAME);
                variables.add(variableName);
            }
            currentEvent = parser.next();
        }
        finishedVariableParsing = true;
    }

    private boolean startVariableElement(int currentEvent) {
        return currentEvent == START_ELEMENT && VARIABLE.equals(parser.getLocalName());
    }

    private boolean endOfHeadElement(int currentEvent) {
        return currentEvent == END_ELEMENT && HEAD.equals(parser.getLocalName());
    }
}
