package org.jrdf.urql.builder;

import org.jrdf.urql.parser.node.TResource;

import java.net.URI;

public final class TokenHelper {
    private TokenHelper() {
    }

    public static URI getResource(TResource resource) {
        String s = resource.getText();
        return URI.create(s.substring(1, s.length() - 1));
    }
}
