package org.jrdf.query.answer.xml.parser;

import org.jrdf.query.answer.xml.TypeValue;

import java.util.LinkedHashSet;

public interface SparqlAnswerResultsParser {
    TypeValue[] getResults(LinkedHashSet<String> variables);
}
