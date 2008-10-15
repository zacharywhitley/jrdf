package org.jrdf.query.answer.xml;

public enum SparqlResultType {
    /**
     * Result is unbound.
     */
    UNBOUND("unbound"),

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
    TYPED_LITERAL("typed-literal", "literal");

    private static final long serialVersionUID = 1L;
    private String jsonRepresentation;
    private String xmlRepresentation;

    SparqlResultType(String newJsonRepresentation) {
        this.jsonRepresentation = newJsonRepresentation;
        this.xmlRepresentation = newJsonRepresentation;
    }

    SparqlResultType(String newJsonRepresentation, String newXmlRepresentation) {
        this.jsonRepresentation = newJsonRepresentation;
        this.xmlRepresentation = newXmlRepresentation;
    }

    public String getXmlElementName() {
        return xmlRepresentation;
    }

    @Override
    public String toString() {
        return this.jsonRepresentation;
    }
}
