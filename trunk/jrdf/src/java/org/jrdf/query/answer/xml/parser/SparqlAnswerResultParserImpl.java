package org.jrdf.query.answer.xml.parser;

import static org.jrdf.query.answer.xml.AnswerXMLWriter.BNODE;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.DATATYPE;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.LITERAL;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.NAME;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.URI;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.XML_LANG;
import static org.jrdf.query.answer.xml.SparqlResultType.BLANK_NODE;
import static org.jrdf.query.answer.xml.SparqlResultType.TYPED_LITERAL;
import static org.jrdf.query.answer.xml.SparqlResultType.URI_REFERENCE;
import org.jrdf.query.answer.xml.TypeValue;
import org.jrdf.query.answer.xml.TypeValueImpl;
import org.jrdf.query.answer.xml.SparqlResultType;

import static javax.xml.XMLConstants.XML_NS_URI;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Map;

public class SparqlAnswerResultParserImpl implements SparqlAnswerResultParser {
    private XMLStreamReader parser;

    public SparqlAnswerResultParserImpl(XMLStreamReader newParser) {
        this.parser = newParser;
    }

    public void getOneBinding(Map<String, TypeValue> variableToValue) throws XMLStreamException {
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
        String language = parser.getAttributeValue(XML_NS_URI, XML_LANG);
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
