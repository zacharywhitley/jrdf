package org.jrdf.parser.ntriples.parser;

public interface LiteralMatcher {
    boolean matches(String s);

    String[] parse(String s);
}
