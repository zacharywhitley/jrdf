package org.jrdf.graph;

/**
 * Represents a wrapper of Literal values.  Used for wrapping native values.
 */
public interface Value {
    /**
     * Obtain the text of this literal.
     *
     * @return the text of the literal, never <code>null</code>
     */
    String getLexicalForm();

    /**
     * Object the native Java type for this value.
     *
     * @return the native java type for this value.
     */
    Object getValue();

    /**
     * Whether the literal is well formed XML.
     *
     * @return whether the literal is wll formed XML.
     */
    boolean isWellFormedXML();
}
