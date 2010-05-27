/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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
 *
 */

package org.jrdf.writer.mem;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import org.jrdf.util.ClosableIterable;
import org.jrdf.vocabulary.RDF;
import org.jrdf.writer.NamespaceException;
import org.jrdf.writer.RdfNamespaceMap;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.jrdf.query.relation.type.PredicateNodeType.PREDICATE_TYPE;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

/**
 * Contains mappings between namespaces and partial URIs.
 *
 * @author TurnerRX
 */
public final class MemRdfNamespaceMap implements RdfNamespaceMap {
    private static final String NS_PREFIX = "ns";
    private Map<String, String> prefixToNamespaceUri = new HashMap<String, String>();

    public MemRdfNamespaceMap() {
        createDefaultNamespace();
    }

    public void load(Graph graph) throws GraphException {
        checkNotNull(graph);
        ClosableIterable<? extends Node> predicates = graph.findNodes(PREDICATE_TYPE);
        try {
            for (Node node : predicates) {
                URIReference uriReference = (URIReference) node;
                QName qname = getQName(uriReference.getURI().toString());
                if (!prefixToNamespaceUri.containsValue(qname.getNamespaceURI())) {
                    String ns = NS_PREFIX + prefixToNamespaceUri.size();
                    addNamespace(ns, qname.getNamespaceURI());
                }
            }
        } finally {
            predicates.iterator().close();
        }
    }

    public void addNamespace(String namespace, String partialUri) throws NamespaceException {
        checkNotNull(namespace, partialUri);
        if (prefixToNamespaceUri.containsKey(namespace)) {
            throw new NamespaceException("Namespace: " + namespace + " already mapped to " + partialUri);
        }
        prefixToNamespaceUri.put(namespace, partialUri);
    }

    public Set<Map.Entry<String, String>> getNameEntries() {
        return prefixToNamespaceUri.entrySet();
    }

    public QName getQName(String uri) {
        checkNotNull(uri);
        int hashIndex = uri.lastIndexOf('#');
        int slashIndex = uri.lastIndexOf('/');
        int index = Math.max(hashIndex, slashIndex);
        // if there is no '#' or '/', return entire uri
        if (index > 0 && index < uri.length()) {
            String prefix = uri.substring(0, ++index);
            String suffix = uri.substring(index, uri.length());
            return new QName(prefix, suffix);
        } else {
            return new QName(uri, "");
        }
    }

    public void reset() {
        prefixToNamespaceUri.clear();
        createDefaultNamespace();
    }

    public String toString() {
        return prefixToNamespaceUri.toString();
    }

    private void createDefaultNamespace() {
        addNamespace("rdf", getQName(RDF.BASE_URI.toString()).getNamespaceURI());
    }
}
