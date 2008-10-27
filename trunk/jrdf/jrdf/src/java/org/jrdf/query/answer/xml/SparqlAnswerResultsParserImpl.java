package org.jrdf.query.answer.xml;

import static org.jrdf.query.answer.xml.AnswerXMLWriter.BINDING;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.RESULT;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class SparqlAnswerResultsParserImpl implements SparqlAnswerResultsParser {
    private XMLStreamReader parser;
    private SparqlAnswerResultParser resultParser;

    public SparqlAnswerResultsParserImpl(XMLStreamReader newParser) {
        this.parser = newParser;
        this.resultParser = new SparqlAnswerResultParserImpl(parser);
    }

    public TypeValue[] getResults(LinkedHashSet<String> variables) {
        Map<String, TypeValue> variableToValue;
        try {
            variableToValue = parseAllResults();
        } catch (XMLStreamException e) {
            variableToValue = new HashMap<String, TypeValue>();
        }
        return mapToArray(variables, variableToValue);
    }

    public Map<String, TypeValue> parseAllResults() throws XMLStreamException {
        Map<String, TypeValue> variableToValue = new HashMap<String, TypeValue>();
        int currentEvent = parser.getEventType();
        while (parser.hasNext() && !endOfResult(currentEvent)) {
            if (startOfBinding(currentEvent)) {
                resultParser.getOneBinding(variableToValue);
            }
            currentEvent = parser.next();
        }
        return variableToValue;
    }

    private boolean startOfBinding(int currentEvent) {
        return currentEvent == START_ELEMENT && BINDING.equals(parser.getLocalName());
    }

    private boolean endOfResult(int currentEvent) {
        return currentEvent == END_ELEMENT && RESULT.equals(parser.getLocalName());
    }

    private TypeValue[] mapToArray(LinkedHashSet<String> variables, Map<String, TypeValue> variableToValue) {
        TypeValue[] result = new TypeValue[variables.size()];
        int index = 0;
        for (String variable : variables) {
            getValueOrUnbound(variableToValue, result, index, variable);
            index++;
        }
        return result;
    }

    private void getValueOrUnbound(Map<String, TypeValue> variableToValue, TypeValue[] result, int index,
        String variable) {
        TypeValue value = variableToValue.get(variable);
        if (value == null) {
            result [index] = new TypeValueImpl();
        } else {
            result[index] = value;
        }
    }
}
