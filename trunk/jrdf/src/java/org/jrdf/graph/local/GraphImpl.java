/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.graph.local;

import static org.jrdf.graph.AnyObjectNode.*;
import static org.jrdf.graph.AnyPredicateNode.*;
import static org.jrdf.graph.AnySubjectNode.*;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.GraphValueFactory;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Resource;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.graph.local.index.nodepool.LocalizerImpl;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperFactoryImpl;
import org.jrdf.graph.local.iterator.ResourceIteratorFactory;
import org.jrdf.graph.local.iterator.ResourceIteratorFactoryImpl;
import org.jrdf.query.relation.GraphRelation;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import static org.jrdf.query.relation.type.BlankNodeType.*;
import org.jrdf.query.relation.type.NodeType;
import static org.jrdf.query.relation.type.PredicateNodeType.*;
import static org.jrdf.query.relation.type.ResourceNodeType.*;
import static org.jrdf.query.relation.type.URIReferenceNodeType.*;
import org.jrdf.query.relation.type.ValueNodeType;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.*;
import org.jrdf.writer.RdfWriter;
import org.jrdf.writer.rdfxml.MemRdfXmlWriter;

import java.io.StringWriter;
import static java.util.Arrays.*;
import java.util.Iterator;

/**
 * A memory based RDF Graph.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public class GraphImpl implements Graph {

    private static final String CANT_ADD_NULL_MESSAGE = "Cannot insert null values into the graph";
    private static final String CANT_ADD_ANY_NODE_MESSAGE = "Cannot insert any node values into the graph";
    private static final String CANT_REMOVE_NULL_MESSAGE = "Cannot remove null values into the graph";
    private static final String CANT_REMOVE_ANY_NODE_MESSAGE = "Cannot remove any node values into the graph";
    private static final String CONTAIN_CANT_USE_NULLS = "Cannot use null values for contains";
    private static final String FIND_CANT_USE_NULLS = "Cannot use null values for finds";

    // indexes are mapped as:
    // s -> {p -> {collection of o}}
    // This is defined in the private add() method

    /**
     * Collection of all indexes.
     */
    private LongIndex[] indexes;

    /**
     * Graph Element Factory.
     */
    private GraphElementFactory elementFactory;

    /**
     * The node pool.
     */
    private NodePool nodePool;

    /**
     * Handle changes to the graph's underlying node pool and indexes.
     */
    private ReadWriteGraph readWriteGraph;

    /**
     * Triple Element Factory.  This caches the element factory.
     */
    private TripleFactory tripleFactory;

    /**
     * Creates resource iterators.
     */
    private ResourceIteratorFactory resourceIteratorFactory;

    /**
     * Default constructor.
     */
    public GraphImpl(LongIndex[] longIndexes, NodePool newNodePool, ReadWriteGraph newWritableGraph) {
        this.indexes = longIndexes;
        this.nodePool = newNodePool;
        this.readWriteGraph = newWritableGraph;
        Localizer localizer = new LocalizerImpl(nodePool, new StringNodeMapperFactoryImpl().createMapper());
        GraphValueFactory valueFactory = new GraphValueFactoryImpl(nodePool, localizer);
        ResourceFactory resourceFactory = new ResourceFactoryImpl(readWriteGraph, valueFactory);
        this.elementFactory = new GraphElementFactoryImpl(resourceFactory, localizer, valueFactory);
        this.tripleFactory = new TripleFactoryImpl(this, elementFactory);
        this.resourceIteratorFactory = new ResourceIteratorFactoryImpl(indexes, elementFactory, nodePool);
    }

    public boolean contains(Triple triple) throws GraphException {
        return contains(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
        // Check that the parameters are not nulls
        checkForNulls(subject, predicate, object, CONTAIN_CANT_USE_NULLS);
        if (ANY_SUBJECT_NODE == subject && ANY_PREDICATE_NODE == predicate && ANY_OBJECT_NODE == object) {
            // Return true if all are any AnyNodes and size is greater than zero.
            return (0L < readWriteGraph.getSize());
        } else {
            return readWriteGraph.contains(subject, predicate, object);
        }
    }

    public ClosableIterator<Triple> find(Triple triple) throws GraphException {
        return find(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    public ClosableIterator<Triple> find(SubjectNode subject, PredicateNode predicate, ObjectNode object)
        throws GraphException {
        checkForNulls(subject, predicate, object, FIND_CANT_USE_NULLS);
        return readWriteGraph.find(subject, predicate, object);
    }

    // TODO AN Missing Literal type and s, o and combinations.
    public ClosableIterator<? extends Node> findNodes(NodeType type) {
        ClosableIterator<? extends Node> result;
        if (type.equals(BNODE_TYPE)) {
            result = nodePool.getBlankNodeIterator();
        } else if (type.equals(URI_REFERENCE_TYPE)) {
            result = nodePool.getURIReferenceIterator();
        } else if (type.equals(RESOURCE_TYPE)) {
            result = resourceIteratorFactory.newAnyResourceIterator();
        } else if (type.equals(PREDICATE_TYPE)) {
            result = readWriteGraph.findUniquePredicates();
        } else {
            throw new UnsupportedOperationException("Cannot find with node type: " + type);
        }
        return result;
    }

    public ClosableIterator<PredicateNode> findPredicates(Resource resource) throws GraphException {
        checkNotNull(resource);
        return readWriteGraph.findUniquePredicates(resource);
    }

    public ClosableIterator<? super Resource> findResources(ValueNodeType type) {
        if (type.equals(URI_REFERENCE_TYPE)) {
            return resourceIteratorFactory.newURIReferenceResourceIterator();
        } else if (type.equals(BNODE_TYPE)) {
            return resourceIteratorFactory.newBlankNodeResourceIterator();
        } else {
            throw new UnsupportedOperationException("Cannot find resource with node type: " + type);
        }
    }

    public void add(Iterator<Triple> triples) throws GraphException {
        while (triples.hasNext()) {
            add(triples.next());
        }
    }

    public void add(Triple... triples) throws GraphException {
        for (Triple triple : triples) {
            add(triple);
        }
    }

    public void add(Triple triple) throws GraphException {
        add(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    public void add(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
        checkForNullsAndAnyNodes(subject, predicate, object, CANT_ADD_NULL_MESSAGE, CANT_ADD_ANY_NODE_MESSAGE);
        readWriteGraph.localizeAndAdd(subject, predicate, object);
    }

    public void remove(Iterator<Triple> triples) throws GraphException {
        readWriteGraph.removeIterator(triples);
    }

    public void remove(Triple... triples) throws GraphException {
        remove(asList(triples).iterator());
    }

    public void remove(Triple triple) throws GraphException {
        remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    public void remove(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
        checkForNullsAndAnyNodes(subject, predicate, object, CANT_REMOVE_NULL_MESSAGE, CANT_REMOVE_ANY_NODE_MESSAGE);
        readWriteGraph.localizeAndRemove(subject, predicate, object);
    }

    public GraphElementFactory getElementFactory() {
        return elementFactory;
    }

    public TripleFactory getTripleFactory() {
        return tripleFactory;
    }

    public long getNumberOfTriples() throws GraphException {
        return readWriteGraph.getSize();
    }

    public boolean isEmpty() throws GraphException {
        return readWriteGraph.getSize() == 0L;
    }

    public GraphRelation createRelation(GraphRelationFactory graphRelationFactory) {
        return readWriteGraph.createRelation(graphRelationFactory);
    }

    public void clear() {
        readWriteGraph.clear();
    }

    public void close() {
        // no op
    }

    private void checkForNullsAndAnyNodes(SubjectNode subject, PredicateNode predicate, ObjectNode object,
        String nullMessage, String anyNodeMessage) {
        checkForNulls(subject, predicate, object, nullMessage);
        checkForAnyNodes(subject, predicate, object, anyNodeMessage);
    }

    private void checkForAnyNodes(SubjectNode subject, PredicateNode predicate, ObjectNode object, String message) {
        if (ANY_SUBJECT_NODE == subject || ANY_PREDICATE_NODE == predicate || ANY_OBJECT_NODE == object) {
            throw new IllegalArgumentException(message);
        }
    }

    private void checkForNulls(SubjectNode subject, PredicateNode predicate, ObjectNode object, String message) {
        if (null == subject || null == predicate || null == object) {
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public String toString() {
        RdfWriter writer = new MemRdfXmlWriter();
        StringWriter sw = new StringWriter();
        try {
            writer.write(this, sw);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get String representation of graph", e);
        }
        return sw.toString();
    }
}
