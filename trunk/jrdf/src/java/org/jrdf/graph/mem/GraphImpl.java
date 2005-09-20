/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The JRDF Project.  All rights reserved.
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
import org.jrdf.graph.mem.index.GraphHandler012;
import org.jrdf.graph.mem.index.GraphHandler120;
import org.jrdf.graph.mem.index.GraphHandler201;
import org.jrdf.graph.mem.index.LongIndexMem;
import org.jrdf.graph.index.LongIndex;
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
    private transient GraphElementFactoryImpl elementFactory;

    /**
     * Triple Element Factory.  This caches the element factory.
     */
    private transient TripleFactoryImpl tripleFactory;
    private static final String CANT_ADD_NULL_MESSAGE = "Cannot insert null values into the graph";
    private static final String CANT_ADD_ANY_NODE_MESSAGE = "Cannot insert any node values into the graph";
    private static final String CANT_REMOVE_NULL_MESSAGE = "Cannot remove null values into the graph";
    private static final String CANT_REMOVE_ANY_NODE_MESSAGE = "Cannot remove any node values into the graph";
    private static final String CONTAIN_CANT_USE_NULLS = "Cannot use null values for contains";
    private static final String FIND_CANT_USE_NULLS = "Cannot use null values for finds";

    /**
     * Default constructor.
     */
    public GraphImpl() {
        init();
    }

    /**
     * Initialization method used by the constructor and the deserializer.
     */
    private void init() {

        // TODO AN Replace these with IOC!
        // protect each field allocation with a test for null
        if (null == longIndex012) {
            longIndex012 = new LongIndexMem(new HashMap<Long, Map<Long, Set<Long>>>());
        }
        if (null == longIndex120) {
            longIndex120 = new LongIndexMem(new HashMap<Long, Map<Long, Set<Long>>>());
        }
        if (null == longIndex201) {
            longIndex201 = new LongIndexMem(new HashMap<Long, Map<Long, Set<Long>>>());
        }

        if (null == elementFactory) {
            elementFactory = new GraphElementFactoryImpl();
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

        // Return true if all are any AnyNodes and size is greater than zero.
        if (ANY_SUBJECT_NODE == subject && ANY_PREDICATE_NODE == predicate && ANY_OBJECT_NODE == object) {
            return 0 < longIndex012.getSize();
        }

        // Get local node values
        Long[] values;
        try {
            values = elementFactory.localize(subject, predicate, object);
        } catch (GraphException ge) {

            // Graph exception on localize implies that the subject, predicate or
            // object did not exist in the graph.
            return false;
        }

        // AnySubjectNode.
        if (ANY_SUBJECT_NODE == subject) {
            return containsAnySubject(predicate, values, object);
        } else {
            // Subject is not anything.
            return containsFixedSubject(values, predicate, object);

        }
    }

    private boolean containsFixedSubject(Long[] values, PredicateNode predicate, ObjectNode object) {
        Map<Long, Set<Long>> subIndex = longIndex012.getSubIndex(values[0]);

        // If subject not found return false.
        if (null == subIndex) {
            return false;
        }

        // AnyPredicateNode.  Could be subj, AnyPredicateNode, AnyObjectNode or subj, AnyPredicateNode, obj.
        if (ANY_PREDICATE_NODE == predicate) {
            return containsAnyPredicate(object, values);
        } else {
            // Predicate not any node.  Could be subj, pred, obj or subj, pred, AnyObjectNode.
            // look up the predicate
            Set group = subIndex.get(values[1]);
            if (null == group) {
                return false;
            }

            // Object not null.  Must be subj, pred, obj.
            if (ANY_OBJECT_NODE != object) {
                return group.contains(values[2]);
            } else {
                // Was subj, pred, AnyObjectNode - must be true if we get this far.
                return true;
            }
        }
    }

    private boolean containsAnySubject(PredicateNode predicate, Long[] values, ObjectNode object) {
        // AnySubjectNode, AnyPredicateNode, obj.
        if (ANY_PREDICATE_NODE == predicate) {
            Map<Long, Set<Long>> objIndex = longIndex201.getSubIndex(values[2]);
            return null != objIndex;
        } else {
            // Predicate is not null.  Could be null, pred, null or null, pred, obj.
            Map<Long, Set<Long>> predIndex = longIndex120.getSubIndex(values[1]);

            // If predicate not found return false.
            if (null == predIndex) {
                return false;
            }

            // If the object is any object node and we found the predicate return true.
            if (ANY_OBJECT_NODE == object) {
                return true;
            } else {
                // Was null, pred, obj
                Set group = predIndex.get(values[2]);
                return null != group;
            }
        }
    }

    private boolean containsAnyPredicate(ObjectNode object, Long[] values) {
        // If its AnyObjectNode then we've found all we need to find.
        if (ANY_OBJECT_NODE == object) {
            return true;
        } else {
            // If the object is not any node we need to find subj, AnyObjectNode, obj
            Map<Long, Set<Long>> objIndex = longIndex201.getSubIndex(values[2]);

            if (null == objIndex) {
                return false;
            }

            Set group = objIndex.get(values[0]);
            return null != group;
        }
    }

    /**
     * Returns an iterator to a set of statements that match a given subject,
     * predicate and object.  A null value for any of the parts of a triple are
     * treated as unconstrained, any values will be returned.
     *
     * @param subject   The subject to find or null to indicate any subject.
     * @param predicate The predicate to find or null to indicate any predicate.
     * @param object    ObjectNode The object to find or null to indicate any object.
     * @throws GraphException If there was an error accessing the graph.
     */
    public ClosableIterator<Triple> find(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws
            GraphException {

        // Check that the parameters are not nulls
        checkForNulls(subject, predicate, object, FIND_CANT_USE_NULLS);

        // Get local node values
        Long[] values;
        try {
            values = elementFactory.localize(subject, predicate, object);
        } catch (GraphException ge) {
            // A graph exception implies that the subject, predicate or object does
            // not exist in the graph.
            return new EmptyClosableIterator();
        }

        return findNonEmptyIterator(subject, predicate, values, object);
    }

    private ClosableIterator<Triple> findNonEmptyIterator(SubjectNode subject, PredicateNode predicate, Long[] values,
            ObjectNode object) {
        // test which index to use
        if (ANY_SUBJECT_NODE != subject) {
            // test for {sp*}
            if (ANY_PREDICATE_NODE != predicate) {
                // test for {spo}
                return fixedSubjectAndPredicate(values, object);
            } else {
                // test for {s**}
                if (ANY_OBJECT_NODE == object) {
                    return new OneFixedIterator(values[0], longIndex012, tripleFactory,
                            new GraphHandler012(longIndex012, longIndex120, longIndex201, elementFactory));
                }
                // {s*o} so fall through
            }
        }

        if (ANY_PREDICATE_NODE != predicate) {
            return fixedPredicateIterator(values, object, subject);
        }

        if (ANY_OBJECT_NODE != object) {
            return fixedObjectIterator(values, subject, predicate);
        }

        // {***} so return entire graph
        return new GraphIterator(tripleFactory, new GraphHandler012(longIndex012, longIndex120, longIndex201,
                elementFactory));
    }

    private ClosableIterator<Triple> fixedSubjectAndPredicate(Long[] values, ObjectNode object) {
        if (ANY_OBJECT_NODE != object) {
            // got {spo}
            return new ThreeFixedIterator(values, longIndex012, tripleFactory,
                    new GraphHandler012(longIndex012, longIndex120, longIndex201, elementFactory));
        } else {
            // got {sp*}
            return new TwoFixedIterator(values[0], values[1], longIndex012, tripleFactory,
                    new GraphHandler012(longIndex012, longIndex120, longIndex201, elementFactory));
        }
    }

    private ClosableIterator<Triple> fixedObjectIterator(Long[] values, SubjectNode subject, PredicateNode predicate) {
        // test for {s*o}
        if (ANY_SUBJECT_NODE != subject) {
            return new TwoFixedIterator(values[2], values[0], longIndex201, tripleFactory,
                    new GraphHandler201(longIndex012, longIndex120, longIndex201, elementFactory));
        } else {
            // test for {**o}.  {*po} should have been picked up above
            assert ANY_PREDICATE_NODE == predicate;
            return new OneFixedIterator(values[2], longIndex201, tripleFactory,
                    new GraphHandler201(longIndex012, longIndex120, longIndex201, elementFactory));
        }
    }

    private ClosableIterator<Triple> fixedPredicateIterator(Long[] values, ObjectNode object, SubjectNode subject) {
        // test for {*po}
        if (ANY_OBJECT_NODE != object) {
            return new TwoFixedIterator(values[1], values[2], longIndex120, tripleFactory,
                    new GraphHandler120(longIndex012, longIndex120, longIndex201, elementFactory));
        } else {
            // test for {*p*}.  {sp*} should have been picked up above
            assert ANY_SUBJECT_NODE == subject;
            return new OneFixedIterator(values[1], longIndex120, tripleFactory,
                    new GraphHandler120(longIndex012, longIndex120, longIndex201, elementFactory));
        }
    }

    /**
     * Returns an iterator to a set of statements that match a given subject,
     * predicate and object.  A null value for any of the parts of a triple are
     * treated as unconstrained, any values will be returned.
     *
     * @param triple The triple to find.
     * @throws GraphException If there was an error accessing the graph.
     */
    public ClosableIterator<Triple> find(Triple triple) throws GraphException {
        return find(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    /**
     * Adds an iterator containing triples into the graph.
     *
     * @param triples The triple iterator.
     * @throws GraphException If the statements can't be made.
     */
    public void add(Iterator triples) throws GraphException {
        while (triples.hasNext()) {
            Triple triple = (Triple) triples.next();
            add(triple);
        }
    }

    /**
     * Adds a triple to the graph.
     *
     * @param triple The triple.
     * @throws GraphException If the statement can't be made.
     */
    public void add(Triple triple) throws GraphException {
        add(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    /**
     * Adds a triple to the graph.
     *
     * @param subject   The subject.
     * @param predicate The predicate.
     * @param object    The object.
     * @throws GraphException If the statement can't be made.
     */
    public void add(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {

        // Check that the parameters are not nulls or any nodes
        checkForNullsAndAnyNodes(subject, predicate, object, CANT_ADD_NULL_MESSAGE, CANT_ADD_ANY_NODE_MESSAGE);

        // Get local node values also tests that it's a valid subject, predicate
        // and object.
        Long[] values = elementFactory.localize(subject, predicate, object);
        longIndex012.add(values);
        longIndex120.add(values[1], values[2], values[0]);
        longIndex201.add(values[2], values[0], values[1]);
    }

    /**
     * Removes an iterator containing triples from the graph.
     *
     * @param triples The triple iterator.
     * @throws GraphException If the statements can't be revoked.
     */
    public void remove(Iterator triples) throws GraphException {
        while (triples.hasNext()) {
            Triple triple = (Triple) triples.next();
            remove(triple);
        }
    }

    /**
     * Removes a triple from the graph.
     *
     * @param triple The triple.
     * @throws GraphException If there was an error revoking the statement, for
     *                        example if it didn't exist.
     */
    public void remove(Triple triple) throws GraphException {
        remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    /**
     * Removes a triple from the graph.
     *
     * @param subject   The subject.
     * @param predicate The predicate.
     * @param object    The object.
     * @throws GraphException If there was an error revoking the statement, for
     *                        example if it didn't exist.
     */
    public void remove(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {

        // Check that the parameters are not nulls or any nodes
        checkForNullsAndAnyNodes(subject, predicate, object, CANT_REMOVE_NULL_MESSAGE, CANT_REMOVE_ANY_NODE_MESSAGE);

        // Get local node values also tests that it's a valid subject, predicate and object.
        Long[] values = elementFactory.localize(subject, predicate, object);

        longIndex012.remove(values[0], values[1], values[2]);
        // if the first one succeeded then try and attempt removal on both of the others
        try {
            longIndex120.remove(values[1], values[2], values[0]);
        } finally {
            longIndex201.remove(values[2], values[0], values[1]);
        }
    }

    /**
     * Returns the node factory for the graph, or creates one.
     *
     * @return the node factory for the graph, or creates one.
     */
    public GraphElementFactory getElementFactory() {
        return elementFactory;
    }

    /**
     * Returns the triple factory for the graph, or creates one.
     *
     * @return the triple factory for the graph, or creates one.
     */
    public TripleFactory getTripleFactory() {
        return tripleFactory;
    }

    /**
     * Returns the number of triples in the graph.
     *
     * @return the number of triples in the graph.
     */
    public long getNumberOfTriples() throws GraphException {
        return longIndex012.getSize();
    }

    /**
     * Returns true if the graph is empty i.e. the number of triples is 0.
     *
     * @return true if the graph is empty i.e. the number of triples is 0.
     */
    public boolean isEmpty() throws GraphException {
        return longIndex012.getSize() == 0;
    }

    /**
     * Closes any underlying resources used by this graph.
     */
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
        out.writeObject(elementFactory.getNodePoolValues().toArray());
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

        // test node factory creation in case the constructor did it
        if (null == elementFactory) {
            elementFactory = new GraphElementFactoryImpl();
        }

        // populate the node factory with these nodes
        for (Object node : nodes) {
            elementFactory.registerNode((MemNode) node);
        }

        // fill in the other indexes
        try {
            GraphHandler012 graphHandler012 = new GraphHandler012(longIndex012, longIndex120, longIndex201,
                    elementFactory);
            graphHandler012.reconstructIndices(longIndex012, longIndex120, longIndex201);
        } catch (GraphException e) {
            throw new ClassNotFoundException("Unable to add to a graph index", e);
        }
    }
}
