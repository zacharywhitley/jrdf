package org.jrdf.parser.n3;

import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.parser.NamespaceListener;

import java.util.regex.Pattern;

public class PrefixParserImpl implements PrefixParser {
    private static final Pattern PREFIX_REGEX = Pattern.compile("\\p{Blank}*" +
                    "@prefix\\p{Blank}+" +
                    "([a-zA-Z][\\x20-\\x7E]*)?:" +
                    "\\p{Blank}+\\<(([\\x20-\\x7E]+?))\\>\\p{Blank}*\\.\\p{Blank}*");
    private static final int PREFIX_GROUP = 1;
    private static final int URI_GROUP = 2;
    private final RegexMatcherFactory regexMatcherFactory;
    private NamespaceListener listener;

    public PrefixParserImpl(RegexMatcherFactory newRegexMatcherFactory, final NamespaceListener newListener) {
        regexMatcherFactory = newRegexMatcherFactory;
        listener = newListener;
    }

    public boolean handlePrefix(final CharSequence line) {
        final RegexMatcher prefixMatcher = regexMatcherFactory.createMatcher(PREFIX_REGEX, line);
        final boolean matched = prefixMatcher.matches();
        final String prefix = prefixMatcher.group(PREFIX_GROUP);
        final String uri = prefixMatcher.group(URI_GROUP);
        listener.handleNamespace(prefix, uri);
        return matched;
    }
}
