package org.jrdf.query.answer.xml;

import java.util.Set;

public interface SparqlAnswerParser {
    Set<String> getVariables();

    boolean hasMoreResults();

    TypeValue[] getResults();

    void close();
}
