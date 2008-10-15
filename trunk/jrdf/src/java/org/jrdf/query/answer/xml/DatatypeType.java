package org.jrdf.query.answer.xml;

public enum DatatypeType {
    /**
     *  Indicates that a literal has no type (language or literal).
     */
    NONE("none"),

    /**
     * Indicates that a literal has a datatype.
     */
    DATATYPE("datatype"),

    /**
     * Indicates that the literal has language.
     */
    XML_LANG("xml:lang");

    private static final long serialVersionUID = 1L;
    private String representation;

    DatatypeType(String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation;
    }
}
