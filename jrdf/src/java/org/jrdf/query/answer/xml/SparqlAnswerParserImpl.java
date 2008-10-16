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

// TODO AN/YF Refactor out the getResults, getOneBinding, getOneNode
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
        if (!finishedVariableParsing) {
            try {
                parseAnswerToGetVariables();
            } catch (XMLStreamException e) {
                ;
            }
        }
        return variables;
    }

    public boolean hasMoreResults() {
        try {
            hasMore = getToNextResult();
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

    private void parseAnswerToGetVariables() throws XMLStreamException {
        while (parser.hasNext()) {
            int currentEvent = parser.getEventType();
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
}
