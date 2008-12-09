package org.jrdf.query.answer;

import javax.xml.stream.XMLStreamException;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */

public interface AskAnswer extends Answer {
    /**
     * The name of the boolean result.
     */
    String ASK_VARIABLE_NAME = "Boolean Result";

    boolean getResult() throws XMLStreamException;
}
