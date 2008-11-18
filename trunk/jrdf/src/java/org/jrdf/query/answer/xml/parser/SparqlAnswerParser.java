package org.jrdf.query.answer.xml.parser;

import org.jrdf.query.answer.AnswerType;
import org.jrdf.query.answer.xml.TypeValue;

import javax.xml.stream.XMLStreamException;
import java.util.LinkedHashSet;

public interface SparqlAnswerParser {
    LinkedHashSet<String> getVariables();

    boolean hasMoreResults();

    TypeValue[] getResults();

    boolean getAskResult() throws XMLStreamException;

    void close();

    AnswerType getAnswerType() throws XMLStreamException;
}
