package org.jrdf.urql.builder;

import org.jrdf.urql.parser.node.AVariable;
import org.jrdf.urql.parser.node.TResource;
import org.jrdf.urql.parser.node.TVariablename;
import org.jrdf.urql.parser.node.TVariableprefix;

import java.net.URI;

public final class TripleSpecHelper {
    public static AVariable createAVariable(String variableNameTitle) {
        return new AVariable(new TVariableprefix("$"), new TVariablename(variableNameTitle));
    }

    public static TResource createResource(URI uri) {
        return new TResource("<" + uri.toString() + ">");
    }
}
