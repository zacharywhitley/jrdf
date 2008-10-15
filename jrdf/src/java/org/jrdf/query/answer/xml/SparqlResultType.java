package org.jrdf.query.answer.xml;

public enum SparqlResultType {
    /**
     * Result is a blank node.
     */
    BLANK_NODE("bnode"),

    /**
     * Result is a URI reference.
     */
    URI_REFERENCE("uri"),

    /**
     * Result is an untyped literal.
     */
    LITERAL("literal"),

    /**
     * Result is a typed literal.
     */
    TYPED_LITERAL("typed-literal");

    private static final long serialVersionUID = 1L;
    private String representation;

    SparqlResultType(String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return this.representation;
    }
}
