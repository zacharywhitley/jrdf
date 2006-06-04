/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 */
package org.jrdf.writer;

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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Contains mappings between namespaces and partial URIs.
 *
 * @author TurnerRX
 */
public class RdfNamespaceMap {

    private static final String NS_PREFIX = "ns";
    private Map<String, String> names = new HashMap<String, String>();
    private Map<String, String> uris = new HashMap<String, String>();

    public RdfNamespaceMap() {
        // add some well known namespaces
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
     * Loads namespaces from the graph.
     *
     * @param graph Graph containing URIs to load from.
     * @throws GraphException If the graph cannot be read.
     */
    public void load(Graph graph) throws GraphException {
        // check for blank nodes
        ClosableIterator<Triple> iter = graph.find(ANY_SUBJECT_NODE,
                ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        Triple triple;
        while (iter.hasNext()) {
            triple = iter.next();
            evaluate(triple.getPredicate());
        }
    }

    /**
     * Returns a string representing the resource URI with its URI prefix
     * replaced by the mapped namespace.
     *
     * @param resource URIReference resource URI
     * @return String namespaced representation of resource URI
     * @throws NamespaceException If there is no mapping for the partial resource URI.
     */
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

    /**
     * Returns the namespace that is mapped to the resource URI (prefix), or
     * null if the URI is not mapped.
     *
     * @param resource URIReference resource URI to look up.
     * @return String mapped namespace
     */
    public String getNamespace(URIReference resource) {
        URI uri = resource.getURI();
        String partial = getPartialUri(uri.toString());
        return uris.get(partial);
    }

    /**
     * Returns the Names mapping entry set.
     *
     * @return name map entries.
     */
    public Set<Entry<String, String>> getNameEntries() {
        return names.entrySet();
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
