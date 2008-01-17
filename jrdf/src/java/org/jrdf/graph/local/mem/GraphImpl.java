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

package org.jrdf.graph.local.mem;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Resource;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.graph.local.index.graphhandler.GraphHandler012;
import org.jrdf.graph.local.index.graphhandler.GraphHandler120;
import org.jrdf.graph.local.index.graphhandler.GraphHandler201;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.longindex.mem.LongIndexMem;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.graph.local.index.nodepool.LocalizerImpl;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperFactoryImpl;
import org.jrdf.graph.local.index.nodepool.mem.MemNodePoolFactory;
import org.jrdf.graph.local.iterator.IteratorFactory;
import org.jrdf.graph.local.mem.iterator.AnyResourceIterator;
import org.jrdf.graph.local.mem.iterator.BlankNodeResourceIterator;
import org.jrdf.graph.local.mem.iterator.MemIteratorFactory;
import org.jrdf.graph.local.mem.iterator.URIReferenceResourceIterator;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.RdfNamespaceMap;
import org.jrdf.writer.mem.MemBlankNodeRegistryImpl;
import org.jrdf.writer.mem.RdfNamespaceMapImpl;
import org.jrdf.writer.rdfxml.RdfXmlWriter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A memory based RDF Graph.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public class GraphImpl implements Graph, Serializable {

    private static final String CANT_ADD_NULL_MESSAGE = "Cannot insert null values into the graph";
    private static final String CANT_ADD_ANY_NODE_MESSAGE = "Cannot insert any node values into the graph";
    private static final String CANT_REMOVE_NULL_MESSAGE = "Cannot remove null values into the graph";
    private static final String CANT_REMOVE_ANY_NODE_MESSAGE = "Cannot remove any node values into the graph";
    private static final String CONTAIN_CANT_USE_NULLS = "Cannot use null values for contains";
    private static final String FIND_CANT_USE_NULLS = "Cannot use null values for finds";

    /**
     * Allow newer compiled version of the stub to operate when changes
     * have not occurred with the class.
     * NOTE : update this serialVersionUID when a method or a public member is
     * deleted.
     */
    private static final long serialVersionUID = -3066836734480153804L;

    // indexes are mapped as:
    // s -> {p -> {set of o}}
    // This is defined in the private add() method

    /**
     * First index.
     */
    private LongIndex longIndex012;

    /**
     * Second index.
     */
    private transient LongIndex longIndex120;

    /**
     * Third index.
     */
    private transient LongIndex longIndex201;


    /**
     * Collection of all indexes.
     */
    private transient LongIndex[] indexes;

    /**
     * Graph Element Factory.
     */
    private transient GraphElementFactory elementFactory;

    /**
     * Resource factory.
     */
    private transient ResourceFactory resourceFactory;

    /**
     * The node pool.
     */
    private transient NodePool nodePool;

    /**
     * Triple Element Factory.  This caches the element factory.
     */
    private transient TripleFactory tripleFactory;

    /**
     * Graph handler for the 012 index.
     */
    private transient GraphHandler012 graphHandler012;

    /**
     * Graph Handler container for all 3 graph handlers.
     */
    private transient GraphHandler[] handlers;

    /**
     * Handle changes to the graph's underlying node pool and indexes.
     */
    private transient ReadWriteGraph readWriteGraph;

    /**
     * A way to create iterators.
     */
    private transient IteratorFactory iteratorFactory;

    /**
     * Registry used for the toString method.
     */
    protected transient BlankNodeRegistry bNodeRegistry = new MemBlankNodeRegistryImpl();

    /**
     * Namespace map used for toString method.
     */
    protected transient RdfNamespaceMap nameSpace = new RdfNamespaceMapImpl();

    /**
     * Default constructor.
     */
    public GraphImpl(LongIndex[] longIndexes, NodePool newNodePool, IteratorFactory newIteratorFactory,
        ReadWriteGraph newWritableGraph, ResourceFactory newResourceFactory) {
        this.longIndex012 = longIndexes[0];
        this.longIndex120 = longIndexes[1];
        this.longIndex201 = longIndexes[2];
        this.nodePool = newNodePool;
        this.iteratorFactory = newIteratorFactory;
        this.readWriteGraph = newWritableGraph;
        this.resourceFactory = newResourceFactory;
        LocalizerImpl newLocalizer = new LocalizerImpl(nodePool, new StringNodeMapperFactoryImpl().createMapper());
        this.elementFactory = new GraphElementFactoryImpl(nodePool, resourceFactory, newLocalizer);
        init();
    }

    /**
     * Initialization method used by the constructor and the deserializer.
     */
    private void init() {
        // TODO AN Replace these with IOC!
        // protect each field allocation with a test for null
        initIndexes();

        initHandlers();

        initFactoriesAndGraph(indexes, handlers);
    }

    private void initIndexes() {
        if (null == longIndex012) {
            longIndex012 = new LongIndexMem(new HashMap<Long, Map<Long, Set<Long>>>());
        }
        if (null == longIndex120) {
            longIndex120 = new LongIndexMem(new HashMap<Long, Map<Long, Set<Long>>>());
        }
        if (null == longIndex201) {
            longIndex201 = new LongIndexMem(new HashMap<Long, Map<Long, Set<Long>>>());
        }

        indexes = new LongIndex[]{longIndex012, longIndex120, longIndex201};

        if (null == nodePool) {
            nodePool = new MemNodePoolFactory().createNodePool();
        }
    }

    private void initHandlers() {
        graphHandler012 = new GraphHandler012(indexes, nodePool);
        GraphHandler120 graphHandler120 = new GraphHandler120(indexes, nodePool);
        GraphHandler201 graphHandler201 = new GraphHandler201(indexes, nodePool);
        handlers = new GraphHandler[]{graphHandler012, graphHandler120, graphHandler201};
    }

    private void initFactoriesAndGraph(LongIndex[] indexes, GraphHandler[] handlers) {
        if (null == iteratorFactory) {
            iteratorFactory = new MemIteratorFactory(indexes, handlers, nodePool);
        }

        if (null == readWriteGraph) {
            readWriteGraph = new ReadWriteGraphImpl(indexes, nodePool, iteratorFactory);
        }

        Localizer localizer = new LocalizerImpl(nodePool, new StringNodeMapperFactoryImpl().createMapper());

        if (null == resourceFactory) {
            resourceFactory = new ResourceFactoryImpl(localizer, readWriteGraph);
        }

        if (null == elementFactory) {
            elementFactory = new GraphElementFactoryImpl(nodePool, resourceFactory, localizer);
        }

        if (null == tripleFactory) {
            tripleFactory = new TripleFactoryImpl(this, elementFactory);
        }
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

    public ClosableIterator<Resource> findResources() {
        return new AnyResourceIterator(indexes, handlers, resourceFactory, nodePool);
    }

    public ClosableIterator<BlankNode> findBlankNodes() {
        return new BlankNodeResourceIterator(indexes, handlers, resourceFactory, nodePool);
    }

    public ClosableIterator<URIReference> findURIReferences() {
        return new URIReferenceResourceIterator(indexes, handlers, resourceFactory, nodePool);
    }

    public ClosableIterator<PredicateNode> findUniquePredicates() {
        return readWriteGraph.findUniquePredicates();
    }

    public ClosableIterator<PredicateNode> findUniquePredicates(Resource resource) throws GraphException {
        checkNotNull(resource);
        return readWriteGraph.findUniquePredicates(resource);
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

    public void clear() {
        readWriteGraph.clear();
    }

    public void close() {
        // no op
    }

    // TODO AN Move this to a helper utility perhaps.
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

    /**
     * Serializes the current object to a stream.
     *
     * @param out The stream to write to.
     * @throws IOException If an I/O error occurs while writing.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        // write out the first index with the default writer
        out.defaultWriteObject();
        // write all the nodes as well
        out.writeObject(nodePool.getNodePoolValues());
        // TODO: Consider writing these nodes individually.  Converting to an array
        // may take up unnecessary memory
    }

    /**
     * Deserializes an object from a stream.
     *
     * @param in The stream to read from.
     * @throws IOException If an I/O error occurs while reading.
     */
    @SuppressWarnings({ "unchecked" })
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // read in the first index with the default reader
        in.defaultReadObject();
        // initialize the fields not yet done by the constructor
        init();

        // read all the nodes as well
        List<Map<Long, String>> values = (List<Map<Long, String>>) in.readObject();

        // populate the node factory with these nodes
        nodePool.registerNodePoolValues(values);

        // fill in the other indexes
        try {
            this.graphHandler012.reconstructIndices(longIndex012, longIndex120, longIndex201);
        } catch (GraphException e) {
            throw new ClassNotFoundException("Unable to add to a graph index", e);
        }
    }

    public String toString() {
        RdfXmlWriter writer = new RdfXmlWriter(bNodeRegistry, nameSpace);
        StringWriter sw = new StringWriter();
        try {
            writer.write(this, sw);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get String representation of graph", e);
        }
        return sw.toString();
    }

}
