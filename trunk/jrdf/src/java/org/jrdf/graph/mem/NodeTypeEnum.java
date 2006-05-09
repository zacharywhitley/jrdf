package org.jrdf.graph.mem;

/**
 * Enumeration for node types.
 *
 * @author Andrew Newman
 * @version $Id: ClosableIterator.java 436 2005-12-19 13:19:55Z newmana $
 */
// TODO (AN) Test drive me.
public enum NodeTypeEnum {
    /**
     * The order of the three different types of RDF nodes.
     */
    BLANK_NODE, URI_REFERENCE, LITERAL;

    public boolean isBlankNode() {
        return equals(BLANK_NODE);
    }

    public boolean isURIReferenceNode() {
        return equals(URI_REFERENCE);
    }

    public boolean isLiteralNode() {
        return equals(LITERAL);
    }
}
