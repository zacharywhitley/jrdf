package org.jrdf.query.answer.xml;

import javax.xml.stream.XMLStreamException;
import java.util.Map;

public interface SparqlAnswerResultParser {
    void getOneBinding(Map<String, TypeValue> variableToValue) throws XMLStreamException;
}
