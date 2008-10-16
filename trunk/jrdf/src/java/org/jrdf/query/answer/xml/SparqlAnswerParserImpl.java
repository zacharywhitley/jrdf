package org.jrdf.query.answer.xml;

import static org.jrdf.query.answer.xml.AnswerXMLWriter.BINDING;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.HEAD;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.NAME;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.RESULT;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.VARIABLE;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

// TODO AN/YF Refactor out the getResults, getOneBinding, getOneNode
public class SparqlAnswerParserImpl implements SparqlAnswerParser {
    private XMLStreamReader parser;
    private boolean hasMore;
    private LinkedHashSet<String> variables = new LinkedHashSet<String>();
    private boolean finishedVariableParsing;

    public SparqlAnswerParserImpl(XMLStreamReader newParser) {
        this.parser = newParser;
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
        Map<String, TypeValue> variableToValue;
        try {
            variableToValue = tryGetResults();
        } catch (XMLStreamException e) {
            variableToValue = new HashMap<String, TypeValue>();
        }
        return getResults(variableToValue);
    }

    private Map<String, TypeValue> tryGetResults() throws XMLStreamException {
        Map<String, TypeValue> variableToValue = new HashMap<String, TypeValue>();
        while (parser.hasNext()) {
            int currentEvent = parser.getEventType();
            if (currentEvent == START_ELEMENT && BINDING.equals(parser.getLocalName())) {
                new SparqlAnswerResultParserImpl(parser).getOneBinding(variableToValue);
            } else if (currentEvent == END_ELEMENT && RESULT.equals(parser.getLocalName())) {
                break;
            }
            parser.next();
        }
        hasMore = getToNextResult();
        return variableToValue;
    }

    private TypeValue[] getResults(Map<String, TypeValue> variableToValue) {
        TypeValue[] result = new TypeValue[variables.size()];
        int index = 0;
        for (String variable : variables) {
            TypeValue value = variableToValue.get(variable);
            if (value == null) {
                result [index] = new TypeValueImpl();
            } else {
                result[index] = value;
            }
            index++;
        }
        return result;
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
