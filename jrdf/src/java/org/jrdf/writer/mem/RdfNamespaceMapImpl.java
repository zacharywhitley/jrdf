package org.jrdf.writer.mem;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.util.ClosableIterator;
import org.jrdf.vocabulary.RDF;
import org.jrdf.vocabulary.RDFS;
import org.jrdf.writer.NamespaceException;
import org.jrdf.writer.RdfNamespaceMap;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains mappings between namespaces and partial URIs.
 *
 * @author TurnerRX
 */
public class RdfNamespaceMapImpl implements RdfNamespaceMap {

    private static final String NS_PREFIX = "ns";
    private Map<String, String> names = new HashMap<String, String>();
    private Map<String, String> uris = new HashMap<String, String>();

    public RdfNamespaceMapImpl() {
        // add some well known namespaces
        initCommonNamespaces();
    }

    public void load(Graph graph) throws GraphException {
        // check for blank nodes
        ClosableIterator<Triple> iter = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        Triple triple;
        while (iter.hasNext()) {
            triple = iter.next();
            evaluate(triple.getPredicate());
        }
    }

    public String replaceNamespace(URIReference resource) throws NamespaceException {
        URI uri = resource.getURI();
        String full = uri.toString();
        String partial = getPartialUri(full);
        String ns = uris.get(partial);
        if (ns == null) {
            throw new NamespaceException("Partial uri: " + partial + " is not mapped to a namespace.");
        }
        return full.replaceFirst(partial, ns + ":");
    }

    public String getNamespace(URIReference resource) {
        URI uri = resource.getURI();
        String partial = getPartialUri(uri.toString());
        return uris.get(partial);
    }

    public Set<Map.Entry<String, String>> getNameEntries() {
        return names.entrySet();
    }

    public void reset() {
        names.clear();
        uris.clear();
        initCommonNamespaces();
    }

    public String toString() {
        return names.toString();
    }

    private void initCommonNamespaces() {
        String rdf = getPartialUri(RDF.BASE_URI.toString());
        String rdfs = getPartialUri(RDFS.BASE_URI.toString());
        String owl = "http://www.w3.org/2002/07/owl#";
        String dc = "http://purl.org/dc/elements/1.1/";
        String dcterms = "http://purl.org/dc/terms/";

        add("rdf", rdf);
        add("rdfs", rdfs);
        add("owl", owl);
        add("dc", dc);
        add("dcterms", dcterms);
    }

    /**
     * Creates mappings for a given predicate.
     *
     * @param predicate PredicateNode
     */
    private void evaluate(PredicateNode predicate) {
        if (predicate == null || !(predicate instanceof URIReference)) {
            return;
        }
        // this should always pass
        evaluate(((URIReference) predicate).getURI());
    }

    /**
     * Adds a namespace for the partial resource URI if one does not already
     * exist.
     *
     * @param resource
     */
    private void evaluate(URI resource) {
        String partial = getPartialUri(resource.toString());
        if (!uris.containsKey(partial)) {
            String ns = NS_PREFIX + names.size();
            // map bi-directionally
            add(ns, partial);
        }
    }

    /**
     * Extracts the uri to the last '#' or '/' character.
     *
     * @param uri String URI
     * @return String partial URI
     */
    private String getPartialUri(String uri) {
        int hashIndex = uri.lastIndexOf('#');
        int slashIndex = uri.lastIndexOf('/');
        int index = Math.max(hashIndex, slashIndex);
        // if there is no '#' or '/', return entire uri
        return (index > 0 && index < uri.length()) ? uri.substring(0, ++index) : uri;
    }

    private void add(String name, String uri) {
        // map bi-directionally
        uris.put(uri, name);
        names.put(name, uri);
    }
}
