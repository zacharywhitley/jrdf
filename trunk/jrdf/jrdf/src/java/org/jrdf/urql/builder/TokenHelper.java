package org.jrdf.urql.builder;

import org.jrdf.urql.parser.node.Token;

import java.util.List;

public final class TokenHelper {
    private TokenHelper() {

    }

    public static String getStringFromTokens(List<? extends Token> tokens) {
        StringBuilder resourceStr = new StringBuilder(tokens.size());
        for (Token urlChar : tokens) {
            resourceStr.append(urlChar.getText());
        }
        return resourceStr.toString();
    }
}
