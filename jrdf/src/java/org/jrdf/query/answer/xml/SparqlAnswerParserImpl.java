package org.jrdf.query.answer.xml;

import static org.jrdf.query.answer.xml.AnswerXMLWriter.BINDING;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.BNODE;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.DATATYPE;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.HEAD;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.LITERAL;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.NAME;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.RESULT;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.URI;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.VARIABLE;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.XML_LANG;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.XML_NS;
import static org.jrdf.query.answer.xml.SparqlResultType.BLANK_NODE;
import static org.jrdf.query.answer.xml.SparqlResultType.TYPED_LITERAL;
import static org.jrdf.query.answer.xml.SparqlResultType.URI_REFERENCE;

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
                getOneBinding(variableToValue);
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

    private void getOneBinding(Map<String, TypeValue> variableToValue) throws XMLStreamException {
        String variableName = parser.getAttributeValue(null, NAME);
        TypeValue binding = getOneNode();
        variableToValue.put(variableName, binding);
    }

    private TypeValue getOneNode() throws XMLStreamException {
        parser.next();
        String tagName = parser.getLocalName();
        TypeValue typeValue = new TypeValueImpl();
        if (URI.equals(tagName)) {
            typeValue = createURI(parser.getElementText());
        } else if (LITERAL.equals(tagName)) {
            typeValue = createLiteral();
        } else if (BNODE.equals(tagName)) {
            typeValue = createBNode(parser.getElementText());
        }
        return typeValue;
    }

    private TypeValue createURI(String elementText) {
        return new TypeValueImpl(URI_REFERENCE, elementText);
    }

    private TypeValue createLiteral() throws XMLStreamException {
        TypeValue typeValue;
        String datatype = parser.getAttributeValue(null, DATATYPE);
        String language = parser.getAttributeValue(XML_NS, XML_LANG);
        if (datatype != null) {
            typeValue = createDatatypeLiteral(parser.getElementText(), datatype);
        } else if (language != null) {
            typeValue = createLanguageLiteral(parser.getElementText(), language);
        } else {
            typeValue = createLiteral(parser.getElementText());
        }
        return typeValue;
    }

    private TypeValue createLiteral(String elementText) {
        return new TypeValueImpl(SparqlResultType.LITERAL, elementText);
    }

    private TypeValue createLanguageLiteral(String elementText, String language) {
        return new TypeValueImpl(SparqlResultType.LITERAL, elementText, false, language);
    }

    private TypeValue createDatatypeLiteral(String elementText, String datatype) {
        return new TypeValueImpl(TYPED_LITERAL, elementText, true, datatype);
    }

    private TypeValue createBNode(String elementText) {
        return new TypeValueImpl(BLANK_NODE, elementText);
    }

}
