package org.jrdf.query.answer.xml;

import javax.xml.stream.XMLStreamException;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: anewman
 * Date: 16/10/2008
 * Time: 08:21:35
 * To change this template use File | Settings | File Templates.
 */
public interface SparqlAnswerParser {
    Set<String> getVariables() throws XMLStreamException;

    boolean hasMoreResults();

    TypeValue[] getResults() throws XMLStreamException;

    void close();
}
