package org.jrdf.parser.ntriples;

import org.jrdf.util.boundary.RegexMatcherFactory;

import java.util.regex.Pattern;

public class CommentsParserImpl implements CommentsParser {
    private static final Pattern COMMENT_REGEX = Pattern.compile("\\p{Blank}*#([\\x20-\\x7E[^\\n\\r]])*");
    private final RegexMatcherFactory regexMatcherFactory;

    public CommentsParserImpl(RegexMatcherFactory newRegexMatcherFactory) {
        this.regexMatcherFactory = newRegexMatcherFactory;
    }

    public boolean handleComment(final CharSequence line) {
        return regexMatcherFactory.createMatcher(COMMENT_REGEX, line).matches();
    }
}