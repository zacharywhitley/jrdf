package org.jrdf.query.answer.xml;

public interface TypeValue {
    SparqlResultType getType();

    String getValue();

    DatatypeType getSuffixType();

    String getSuffix();
}
