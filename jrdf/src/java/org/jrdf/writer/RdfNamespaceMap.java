package org.jrdf.writer;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.URIReference;

import java.util.Map;
import java.util.Set;

/**
 * Contains mappings between namespaces and partial URIs.
 *
 * @author TurnerRX
 */
public interface RdfNamespaceMap {
    /**
     * Loads namespaces from the graph.
     *
     * @param graph Graph containing URIs to load from.
     * @throws org.jrdf.graph.GraphException If the graph cannot be read.
     */
    void load(Graph graph) throws GraphException;

    /**
     * Returns a string representing the resource URI with its URI prefix
     * replaced by the mapped namespace.
     *
     * @param resource URIReference resource URI
     * @return String namespaced representation of resource URI
     * @throws org.jrdf.writer.NamespaceException If there is no mapping for the partial resource URI.
     */
    String replaceNamespace(URIReference resource) throws NamespaceException;

    /**
     * Returns the namespace that is mapped to the resource URI (prefix), or
     * null if the URI is not mapped.
     *
     * @param resource URIReference resource URI to look up.
     * @return String mapped namespace
     */
    String getNamespace(URIReference resource);

    /**
     * Returns the Names mapping entry set.
     *
     * @return name map entries.
     */
    Set<Map.Entry<String, String>> getNameEntries();

    /**
     * Reset the name and uri mappings.
     */
    void reset();
}
