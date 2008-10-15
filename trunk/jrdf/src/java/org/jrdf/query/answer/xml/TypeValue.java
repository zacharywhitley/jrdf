package org.jrdf.query.answer.xml;

/**
 * Created by IntelliJ IDEA.
 * User: anewman
 * Date: 16/10/2008
 * Time: 08:23:48
 * To change this template use File | Settings | File Templates.
 */
public interface TypeValue {
    SparqlResultType getType();

    String getValue();

    DatatypeType getSuffixType();

    String getSuffix();
}
