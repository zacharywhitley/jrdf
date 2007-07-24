/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.mem;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.index.graphhandler.GraphHandler;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler012;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler120;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler201;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.index.longindex.mem.LongIndexMem;
import org.jrdf.graph.index.nodepool.NodePool;
import org.jrdf.graph.index.nodepool.map.MemNodePoolFactory;
import org.jrdf.graph.mem.iterator.ClosableMemIterator;
import org.jrdf.graph.mem.iterator.EmptyClosableIterator;
import org.jrdf.graph.mem.iterator.IteratorFactory;
import org.jrdf.graph.mem.iterator.IteratorFactoryImpl;
import org.jrdf.util.ClosableIterator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
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

    // FIXME TJA: Test-drive toString()
    // TODO AN: Remove indexes and replace with graphHandlers.  To make different kinds of Graphs - ones that use
    // localization and others that don't.

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
     * Graph Element Factory.  This caches the node factory.
     */
    private transient GraphElementFactory elementFactory;

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
     * A way to create iterators.
     */
    private transient IteratorFactory iteratorFactory;

    private static final String CANT_ADD_NULL_MESSAGE = "Cannot insert null values into the graph";
    private static final String CANT_ADD_ANY_NODE_MESSAGE = "Cannot insert any node values into the graph";
    private static final String CANT_REMOVE_NULL_MESSAGE = "Cannot remove null values into the graph";
    private static final String CANT_REMOVE_ANY_NODE_MESSAGE = "Cannot remove any node values into the graph";
    private static final String CONTAIN_CANT_USE_NULLS = "Cannot use null values for contains";
    private static final String FIND_CANT_USE_NULLS = "Cannot use null values for finds";

    /**
     * Default constructor.
     */
    public GraphImpl(LongIndex[] longIndexes, NodePool nodePool, GraphElementFactory elementFactory,
        GraphHandler012 graphHandler, IteratorFactory newIteratorFactory) {
        this.longIndex012 = longIndexes[0];
        this.longIndex120 = longIndexes[1];
        this.longIndex201 = longIndexes[2];
        this.nodePool = nodePool;
        this.elementFactory = elementFactory;
        this.graphHandler012 = graphHandler;
        this.iteratorFactory = newIteratorFactory;
        init();
    }

    /**
     * Initialization method used by the constructor and the deserializer.
     */
    private void init() {

        // TODO AN Replace these with IOC!
        // protect each field allocation with a test for null
        initIndexes();

        LongIndex[] indexes = {longIndex012, longIndex120, longIndex201};

        if (null == nodePool) {
            nodePool = new MemNodePoolFactory().createNodePool();
        }

        if (null == elementFactory) {
            elementFactory = new GraphElementFactoryImpl(nodePool);
        }

        if (null == tripleFactory) {
            tripleFactory = new TripleFactoryImpl(this, elementFactory);
        }

        if (null == graphHandler012) {
            graphHandler012 = new GraphHandler012(indexes, nodePool);
        }

        initIteratorFactory(indexes);
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
    }

    private void initIteratorFactory(LongIndex[] indexes) {
        if (null == iteratorFactory) {
            GraphHandler201 graphHandler201 = new GraphHandler201(indexes, nodePool);
            GraphHandler120 graphHandler120 = new GraphHandler120(indexes, nodePool);
            iteratorFactory = new IteratorFactoryImpl(indexes,
                new GraphHandler[]{graphHandler012, graphHandler120, graphHandler201});
        }
    }

    public boolean contains(Triple triple) throws GraphException {
        return contains(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
        // Check that the parameters are not nulls
        checkForNulls(subject, predicate, object, CONTAIN_CANT_USE_NULLS);
        boolean returnValue;
        if (ANY_SUBJECT_NODE == subject && ANY_PREDICATE_NODE == predicate && ANY_OBJECT_NODE == object) {
            // Return true if all are any AnyNodes and size is greater than zero.
            returnValue = 0L < longIndex012.getSize();
        } else {
            try {
                // Get local node values
                Long[] values = nodePool.localize(subject, predicate, object);
                returnValue = containsValues(values, subject, predicate, object);
            } catch (GraphException ge) {
                // Graph exception on localize implies that the subject, predicate or
                // object did not exist in the graph.
                returnValue = false;
            }
        }
        return returnValue;
    }

    private boolean containsValues(Long[] values, SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        if (ANY_SUBJECT_NODE != subject) {
            // subj, *, *
            return containsFixedSubject(values, predicate, object);
        } else {
            // AnySubjectNode, *, *
            return containsAnySubject(values, predicate, object);
        }
    }

    private boolean containsFixedSubject(Long[] values, PredicateNode predicate, ObjectNode object) {
        if (longIndex012.contains(values[0])) {
            if (ANY_PREDICATE_NODE != predicate) {
                // subj, pred, AnyObjectNode or subj, pred, obj
                return containsFixedSubjectFixedPredicate(values, object);
            } else {
                // subj, AnyPredicateNode, AnyObjectNode or subj, AnyPredicateNode, obj.
                return containsFixedSubjectAnyPredicate(values, object);
            }
        } else {
            // If subject not found return false.
            return false;
        }
    }

    private boolean containsFixedSubjectFixedPredicate(Long[] values, ObjectNode object) {
        Map<Long, Set<Long>> subjIndex = longIndex012.getSubIndex(values[0]);
        Set<Long> subjPredIndex = subjIndex.get(values[1]);
        if (null != subjPredIndex) {
            if (ANY_OBJECT_NODE != object) {
                // Must be subj, pred, obj.
                return subjPredIndex.contains(values[2]);
            } else {
                // Was subj, pred, AnyObjectNode - must be true if we get this far.
                return true;
            }
        } else {
            // subj, pred not found.
            return false;
        }
    }

    private boolean containsFixedSubjectAnyPredicate(Long[] values, ObjectNode object) {
        if (ANY_OBJECT_NODE != object) {
            // Was subj, AnyPredicateNode, obj
            // Use 201 index to find object and then subject.
            Map<Long, Set<Long>> objIndex = longIndex201.getSubIndex(values[2]);
            if (null != objIndex) {
                // Find object.
                return null != objIndex.get(values[0]);
            } else {
                // Didn't find subject.
                return false;
            }
        } else {
            // Was subj, AnyPredicate, AnyObject
            // If its AnyObjectNode then we've found all we need to find.
            return true;
        }
    }

    private boolean containsAnySubject(Long[] values, PredicateNode predicate, ObjectNode object) {
        if (ANY_PREDICATE_NODE != predicate) {
            return containsAnySubjectAnyPredicate(values, object);
        } else {
            // AnySubjectNode, AnyPredicateNode, obj.
            return longIndex201.contains(values[2]);
        }
    }

    private boolean containsAnySubjectAnyPredicate(Long[] values, ObjectNode object) {
        // AnySubjectNode, pred, AnyObjectNode or AnySubjectNode, pred, obj.
        Map<Long, Set<Long>> predIndex = longIndex120.getSubIndex(values[1]);
        if (null != predIndex) {
            if (ANY_OBJECT_NODE != object) {
                // Was AnySubjectNode, pred, obj
                return null != predIndex.get(values[2]);
            } else {
                // If the object is any object node and we found the predicate return true.
                return true;
            }
        } else {
            // If predicate not found return false.
            return false;
        }
    }

    public ClosableIterator<Triple> find(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws
        GraphException {

        // Check that the parameters are not nulls
        checkForNulls(subject, predicate, object, FIND_CANT_USE_NULLS);

        // Get local node values
        Long[] values;
        try {
            values = nodePool.localize(subject, predicate, object);
        } catch (GraphException ge) {
            // A graph exception implies that the subject, predicate or object does
            // not exist in the graph.
            return new EmptyClosableIterator();
        }

        return findNonEmptyIterator(subject, predicate, object, values);
    }

    private ClosableIterator<Triple> findNonEmptyIterator(SubjectNode subject, PredicateNode predicate,
        ObjectNode object, Long[] values) {

        ClosableIterator<Triple> result;

        if (ANY_SUBJECT_NODE != subject) {
            // {s??} Get fixed subject, fixed or any predicate and object.
            result = fixedSubjectIterator(values, predicate, object);
        } else if (ANY_PREDICATE_NODE != predicate) {
            // {*p?} Get any subject, fixed predicate, fixed or any object.
            result = anySubjectFixedPredicateIterator(values, object);
        } else if (ANY_OBJECT_NODE != object) {
            // {**o} Get any subject and predicate, fixed object.
            result = anySubjectAndPredicateFixedObjectIterator(values);
        } else {
            // {***} Get all.
            result = iteratorFactory.newGraphIterator();
        }
        return result;
    }

    private ClosableIterator<Triple> fixedSubjectIterator(Long[] values, PredicateNode predicate, ObjectNode object) {
        ClosableIterator<Triple> result;

        // test for {s??}
        if (ANY_PREDICATE_NODE != predicate) {
            // test for {sp?}
            if (ANY_OBJECT_NODE != object) {
                // got {spo}
                result = iteratorFactory.newThreeFixedIterator(values);
            } else {
                // got {sp*}
                result = iteratorFactory.newTwoFixedIterator(values[0], values[1], 0);
            }
        } else {
            // test for {s*?}
            if (ANY_OBJECT_NODE != object) {
                // got s*o {}
                result = iteratorFactory.newTwoFixedIterator(values[2], values[0], 2);
            } else {
                // got {s**}
                result = iteratorFactory.newOneFixedIterator(values[0], 0);
            }
        }

        return result;
    }

    private ClosableIterator<Triple> anySubjectFixedPredicateIterator(Long[] values, ObjectNode object) {
        ClosableIterator<Triple> result;

        // test for {*p?}
        if (ANY_OBJECT_NODE != object) {
            // got {*po}
            result = iteratorFactory.newTwoFixedIterator(values[1], values[2], 1);
        } else {
            // got {*p*}.
            result = iteratorFactory.newOneFixedIterator(values[1], 1);
        }

        return result;
    }

    private ClosableIterator<Triple> anySubjectAndPredicateFixedObjectIterator(Long[] values) {
        // got {**o}
        return iteratorFactory.newOneFixedIterator(values[2], 2);
    }

    public ClosableIterator<Triple> find(Triple triple) throws GraphException {
        return find(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    public void add(Iterator<Triple> triples) throws GraphException {
        while (triples.hasNext()) {
            add(triples.next());
        }
    }

    public void add(Triple triple) throws GraphException {
        add(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    public void add(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {

        // Check that the parameters are not nulls or any nodes
        checkForNullsAndAnyNodes(subject, predicate, object, CANT_ADD_NULL_MESSAGE, CANT_ADD_ANY_NODE_MESSAGE);

        // Get local node values also tests that it's a valid subject, predicate
        // and object.
        try {
            Long[] values = nodePool.localize(subject, predicate, object);
            longIndex012.add(values);
            longIndex120.add(values[1], values[2], values[0]);
            longIndex201.add(values[2], values[0], values[1]);
        } catch (GraphException ge) {
            throw new GraphException("Failed to add triple.", ge);
        }
    }

    @SuppressWarnings({
        "unchecked"
        })
    public void remove(Iterator<Triple> triples) throws GraphException {
        if (triples instanceof ClosableMemIterator) {
            localIteratorRemove(triples);
        } else {
            globalIteratorRemove(triples);
        }
    }

    private void localIteratorRemove(Iterator<Triple> triples) {
        ClosableMemIterator<Triple> memIterator = (ClosableMemIterator<Triple>) triples;
        while (memIterator.hasNext()) {
            memIterator.next();
            memIterator.remove();
        }
    }

    private void globalIteratorRemove(Iterator<Triple> triples) throws GraphException {
        while (triples.hasNext()) {
            remove(triples.next());
        }
    }

    public void remove(Triple triple) throws GraphException {
        remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    public void remove(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {

        // Check that the parameters are not nulls or any nodes
        checkForNullsAndAnyNodes(subject, predicate, object, CANT_REMOVE_NULL_MESSAGE, CANT_REMOVE_ANY_NODE_MESSAGE);

        // Get local node values also tests that it's a valid subject, predicate and object.
        Long[] values = nodePool.localize(subject, predicate, object);

        longIndex012.remove(values[0], values[1], values[2]);
        // if the first one succeeded then try and attempt removal on both of the others
        try {
            longIndex120.remove(values[1], values[2], values[0]);
        } finally {
            longIndex201.remove(values[2], values[0], values[1]);
        }
    }

    public GraphElementFactory getElementFactory() {
        return elementFactory;
    }

    public TripleFactory getTripleFactory() {
        return tripleFactory;
    }

    public long getNumberOfTriples() throws GraphException {
        return longIndex012.getSize();
    }

    public boolean isEmpty() throws GraphException {
        return longIndex012.getSize() == 0L;
    }

    public void clear() {
        // TODO AN: Should this work regardless of failure - or rollback?
        // TODO AN: Improve GraphHandler API to do clearing of indexes instead of direct clearing.
        longIndex012.clear();
        longIndex120.clear();
        longIndex201.clear();
        nodePool.clear();
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
        out.writeObject(nodePool.getNodePoolValues().toArray());
        // TODO: Consider writing these nodes individually.  Converting to an array
        // may take up unnecessary memory
    }

    /**
     * Deserializes an object from a stream.
     *
     * @param in The stream to read from.
     * @throws IOException If an I/O error occurs while reading.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // read in the first index with the default reader
        in.defaultReadObject();
        // initialize the fields not yet done by the constructor
        init();

        // read all the nodes as well
        Object[] nodes = (Object[]) in.readObject();

        // populate the node factory with these nodes
        for (Object node : nodes) {
            nodePool.registerNode((LocalizedNode) node);
        }

        // fill in the other indexes
        try {
            GraphHandler012 graphHandler012 = this.graphHandler012;
            graphHandler012.reconstructIndices(longIndex012, longIndex120, longIndex201);
        } catch (GraphException e) {
            throw new ClassNotFoundException("Unable to add to a graph index", e);
        }
    }
}
