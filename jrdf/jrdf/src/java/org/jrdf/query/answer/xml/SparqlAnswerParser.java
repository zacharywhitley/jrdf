package org.jrdf.query.answer.xml;

import java.util.LinkedHashSet;

public interface SparqlAnswerParser {
    LinkedHashSet<String> getVariables();

    boolean hasMoreResults();

    TypeValue[] getResults();

    void close();
}
