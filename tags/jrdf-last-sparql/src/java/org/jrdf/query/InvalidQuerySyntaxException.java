package org.jrdf.query;

/**
 * Indicates that the format of a query does not match the required syntax for its language.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class InvalidQuerySyntaxException extends Exception {

    private static final long serialVersionUID = -4108587331671842402L;

    private InvalidQuerySyntaxException() {
    }

    public InvalidQuerySyntaxException(String message) {
        super(message);
    }

    public InvalidQuerySyntaxException(Throwable cause) {
        super(cause);
    }

    public InvalidQuerySyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
}