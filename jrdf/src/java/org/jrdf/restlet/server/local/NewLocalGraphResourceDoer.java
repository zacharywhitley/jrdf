package org.jrdf.restlet.server.local;

import org.jrdf.query.Answer;
import org.restlet.resource.ResourceException;

public interface NewLocalGraphResourceDoer {
    String getMaxRows();

    long getTimeTaken();

    boolean isTooManyRows();

    Answer answerQuery(String graphName, String queryString) throws ResourceException;
}
