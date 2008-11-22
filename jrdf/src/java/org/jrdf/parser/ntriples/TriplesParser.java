package org.jrdf.parser.ntriples;

import org.jrdf.parser.StatementHandlerConfiguration;
import org.jrdf.parser.StatementHandlerException;

public interface TriplesParser extends StatementHandlerConfiguration {
    void handleTriple(CharSequence line) throws StatementHandlerException;

    void clear();
}
