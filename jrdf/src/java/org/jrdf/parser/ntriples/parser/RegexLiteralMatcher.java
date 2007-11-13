package org.jrdf.parser.ntriples.parser;

import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcher;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import static org.jrdf.util.param.ParameterUtil.checkNotEmptyString;

import java.util.regex.Pattern;

public final class RegexLiteralMatcher implements LiteralMatcher {
    private Pattern pattern = Pattern.compile("\\\"([\\x20-\\x7E]*)\\\"" +
        "(" +
        "((\\@(\\p{Lower}+(\\-a-z0-9]+)*))|(\\^\\^\\<([\\x20-\\x7E]+)\\>))?" +
        ").*");
    private static final int LITERAL_INDEX = 1;
    private static final int LANGUAGE_INDEX = 5;
    private static final int DATATYPE_INDEX = 8;
    private final RegexMatcherFactory regexFactory;
    private final NTripleUtil nTripleUtil;
    private static final int LITERAL_VALUES_LENGTH = 3;

    public RegexLiteralMatcher(RegexMatcherFactory newRegexFactory, NTripleUtil newNTripleUtil) {
        checkNotNull(newRegexFactory, newNTripleUtil);
        this.regexFactory = newRegexFactory;
        this.nTripleUtil = newNTripleUtil;
    }

    public void setPattern(String newPattern) {
        pattern = Pattern.compile(newPattern);
    }

    public boolean matches(String s) {
        checkNotEmptyString("s", s);
        RegexMatcher matcher = regexFactory.createMatcher(pattern, s);
        return matcher.matches();
    }

    public String[] parse(String s) {
        checkNotEmptyString("s", s);
        RegexMatcher matcher = regexFactory.createMatcher(pattern, s);
        String[] values = new String[LITERAL_VALUES_LENGTH];
        if (matcher.matches()) {
            String ntriplesLiteral = matcher.group(LITERAL_INDEX);
            values[0] = nTripleUtil.unescapeLiteral(ntriplesLiteral);
            values[1] = matcher.group(LANGUAGE_INDEX);
            values[2] = matcher.group(DATATYPE_INDEX);
        }
        return values;
    }
}
