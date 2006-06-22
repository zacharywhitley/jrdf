package org.jrdf.connection;

/**
 * Indicates that a connection error occured while connected/ing to a graph.
 *
 * @author Tom Adams
 * @version $Id$
 */
public final class JrdfConnectionException extends Exception {

    private static final long serialVersionUID = 8620551289077269764L;

    public JrdfConnectionException(String message) {
        super(message);
    }

    public JrdfConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
