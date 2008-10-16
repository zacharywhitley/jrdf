package org.jrdf.query.answer.xml;

import java.util.LinkedHashSet;

public interface SparqlAnswerResultsParser {
    TypeValue[] getResults(LinkedHashSet<String> variables);
}
