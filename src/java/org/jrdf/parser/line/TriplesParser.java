package org.jrdf.parser.line;

import org.jrdf.parser.StatementHandlerConfiguration;

public interface TriplesParser extends StatementHandlerConfiguration {
    void handleTriple(CharSequence line);

    void clear();
}
