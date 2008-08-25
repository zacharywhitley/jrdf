/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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

package org.jrdf.util;

import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Resource;
import org.jrdf.graph.Triple;
import org.jrdf.vocabulary.RDF;

import java.net.URI;
import static java.net.URI.create;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModelsImpl implements Models {
    /**
     * Default name space for triples.
     */
    public static final String JRDF_NAMESPACE = "http://jrdf.sf.net/";
    private static final URI GRAPH = create(JRDF_NAMESPACE + "graph");
    private static final URI NAME = create(JRDF_NAMESPACE + "name");
    private static final URI ID = create(JRDF_NAMESPACE + "id");
    private Set<Resource> graphs = new HashSet<Resource>();
    private Map<String, Long> graphNameToId = new HashMap<String, Long>();
    private long highestId;
    private Graph graph;

    public ModelsImpl(Graph newGraph) {
        this.graph = newGraph;
        init();
    }

    private void init() {
        try {
            GraphElementFactory elementFactory = graph.getElementFactory();
            PredicateNode type = elementFactory.createURIReference(RDF.TYPE);
            ObjectNode graphName = elementFactory.createURIReference(GRAPH);
            ClosableIterable<Triple> triples = graph.find(ANY_SUBJECT_NODE, type, graphName);
            try {
                for (Triple triple : triples) {
                    addResource(elementFactory, triple);
                }
            } finally {
                triples.iterator().close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addResource(GraphElementFactory elementFactory, Triple triple) throws GraphElementFactoryException {
        Resource resource = elementFactory.createResource(triple.getSubject());
        graphs.add(resource);
        long id = getId(resource);
        graphNameToId.put(getName(resource), id);
        if (id > highestId) {
            highestId = id;
        }
    }

    public boolean hasGraph(String name) {
        return graphNameToId.keySet().contains(name);
    }

    public long addGraph(String name) {
        GraphElementFactory graphElementFactory = graph.getElementFactory();
        highestId++;
        Resource resource = tryAddGraph(name, highestId, graphElementFactory);
        graphs.add(resource);
        return highestId;
    }

    public String getName(Resource resource) {
        ClosableIterator<ObjectNode> nodeClosableIterator = tryGetObjects(resource, NAME);
        if (nodeClosableIterator.hasNext()) {
            return (String) ((Literal) nodeClosableIterator.next()).getValue();
        } else {
            return "";
        }
    }

    public long getId(String graphName) {
        Long id = graphNameToId.get(graphName);
        if (id != null) {
            return id;
        } else {
            return 0;
        }
    }

    // TODO Remove this method (used on in testing)
    public Set<Resource> getResources() {
        return graphs;
    }

    // TODO Remove this method (used on in testing)
    public long getId(Resource resource) {
        ClosableIterator<ObjectNode> nodeClosableIterator = tryGetObjects(resource, ID);
        if (nodeClosableIterator.hasNext()) {
            return (Long) ((Literal) nodeClosableIterator.next()).getValue();
        } else {
            return 0;
        }
    }

    private ClosableIterator<ObjectNode> tryGetObjects(Resource resource, URI predicate) {
        try {
            return resource.getObjects(predicate);
        } catch (GraphException e) {
            return new ObjectNodeEmptyClosableIterator();
        }
    }

    private Resource tryAddGraph(String name, Long id, GraphElementFactory graphElementFactory) {
        try {
            Resource resource = graphElementFactory.createResource();
            resource.addValue(RDF.TYPE, GRAPH);
            resource.addValue(NAME, name);
            resource.addValue(ID, id);
            graphNameToId.put(name, id);
            return resource;
        } catch (GraphException e) {
            throw new RuntimeException(e);
        }
    }
}
