package org.jrdf.parser.ntriples;

import org.jrdf.parser.StatementHandlerConfiguration;

public interface TriplesParser extends StatementHandlerConfiguration {
    void handleTriple(CharSequence line);

    void clear();
}
