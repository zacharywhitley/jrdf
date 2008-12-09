package org.jrdf.query.answer.xml.parser;

import org.jrdf.query.answer.xml.TypeValue;

import javax.xml.stream.XMLStreamException;
import java.util.Map;

public interface SparqlAnswerResultParser {
    void getOneBinding(Map<String, TypeValue> variableToValue) throws XMLStreamException;
}
