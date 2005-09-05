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

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.TripleFactoryException;
import org.jrdf.util.ClosableIterator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;

/**
 * A memory based RDF Graph.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 *
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
    private Map<Long, Map<Long, Set<Long>>> index012;

    /**
     * Second index.
     */
    private transient Map<Long, Map<Long, Set<Long>>> index120;

    /**
     * Third index.
     */
    private transient Map<Long, Map<Long, Set<Long>>> index201;

    /**
     * Graph Element Factory.  This caches the node factory.
     */
    private transient GraphElementFactoryImpl elementFactory;

    /**
     * Triple Element Factory.  This caches the element factory.
     */
    private transient TripleFactoryImpl tripleFactory;
    private LongIndexMem longIndex012;
    private LongIndexMem longIndex120;
    private LongIndexMem longIndex201;
    private static final String CANT_ADD_NULL_MESSAGE = "Cannot insert null values into the graph";
    private static final String CANT_ADD_ANY_NODE_MESSAGE = "Cannot insert any node values into the graph";
    private static final String CANT_REMOVE_NULL_MESSAGE = "Cannot remove null values into the graph";
    private static final String CANT_REMOVE_ANY_NODE_MESSAGE = "Cannot remove any node values into the graph";
    private static final String CONTAIN_CANT_USE_NULLS = "Cannot use null values for contains";
    private static final String FIND_CANT_USE_NULLS = "Cannot use null values for finds";
    private static final int TRIPLE = 3;

    /**
     * Default constructor.
     *
     * @throws GraphException There was an error creating the factory.
     */
    public GraphImpl() throws GraphException {
        init();
    }

    /**
     * Initialization method used by the constructor and the deserializer.
     *
     * @throws GraphException There was an error creating the factory.
     */
    private void init() throws GraphException {

        // protect each field allocation with a test for null
        if (null == index012) {
            index012 = new HashMap<Long, Map<Long, Set<Long>>>();
        }
        if (null == index120) {
            index120 = new HashMap<Long, Map<Long, Set<Long>>>();
        }
        if (null == index201) {
            index201 = new HashMap<Long, Map<Long, Set<Long>>>();
        }

        longIndex012 = new LongIndexMem(index012);
        longIndex120 = new LongIndexMem(index120);
        longIndex201 = new LongIndexMem(index201);

        if (null == elementFactory) {
            try {
                elementFactory = new GraphElementFactoryImpl();
            }
            catch (TripleFactoryException e) {
                throw new GraphException(e);
            }
        }

        if (null == tripleFactory) {
            tripleFactory = new TripleFactoryImpl(this, elementFactory);
        }
    }

    public boolean contains(Triple triple) throws GraphException {
        return contains(triple.getSubject(), triple.getPredicate(),
            triple.getObject());
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {

        // Check that the parameters are not nulls
        checkForNulls(subject, predicate, object, CONTAIN_CANT_USE_NULLS);

        // Return true if all are any AnyNodes and size is greater than zero.
        if (ANY_SUBJECT_NODE == subject && ANY_PREDICATE_NODE == predicate && ANY_OBJECT_NODE == object) {
            return 0 < index012.size();
        }

        // Get local node values
        Long[] values;
        try {
            values = localize(subject, predicate, object);
        }
        catch (GraphException ge) {

            // Graph exception on localize implies that the subject, predicate or
            // object did not exist in the graph.
            return false;
        }

        // AnySubjectNode.
        if (ANY_SUBJECT_NODE == subject) {
            return containsAnySubject(predicate, values, object);
        }
        // Subject is not anything.
        else {
            return containsFixedSubject(values, predicate, object);

        }
    }

    private boolean containsFixedSubject(Long[] values, PredicateNode predicate, ObjectNode object) {
        Map<Long, Set<Long>> subIndex = index012.get(values[0]);

        // If subject not found return false.
        if (null == subIndex) {
            return false;
        }

        // AnyPredicateNode.  Could be subj, AnyPredicateNode, AnyObjectNode or subj, AnyPredicateNode, obj.
        if (ANY_PREDICATE_NODE == predicate) {
            return containsAnyPredicate(object, values);
        }
        // Predicate not any node.  Could be subj, pred, obj or subj, pred, AnyObjectNode.
        else {

            // look up the predicate
            Set group = subIndex.get(values[1]);
            if (null == group) {
                return false;
            }

            // Object not null.  Must be subj, pred, obj.
            if (ANY_OBJECT_NODE != object) {
                return group.contains(values[2]);
            }
            // Was subj, pred, AnyObjectNode - must be true if we get this far.
            else {
                return true;
            }
        }
    }

    private boolean containsAnySubject(PredicateNode predicate, Long[] values, ObjectNode object) {
        // AnySubjectNode, AnyPredicateNode, obj.
        if (ANY_PREDICATE_NODE == predicate) {
            Map<Long, Set<Long>> objIndex = index201.get(values[2]);
            return null != objIndex;
        }
        // Predicate is not null.  Could be null, pred, null or null, pred, obj.
        else {
            Map<Long, Set<Long>> predIndex = index120.get(values[1]);

            // If predicate not found return false.
            if (null == predIndex) {
                return false;
            }

            // If the object is any object node and we found the predicate return true.
            if (ANY_OBJECT_NODE == object) {
                return true;
            }
            // Was null, pred, obj
            else {
                Set group = predIndex.get(values[2]);
                return null != group;
            }
        }
    }

    private boolean containsAnyPredicate(ObjectNode object, Long[] values) {
        // If its AnyObjectNode then we've found all we need to find.
        if (ANY_OBJECT_NODE == object) {
            return true;
        }
        // If the object is not any node we need to find subj, AnyObjectNode, obj
        else {
            Map<Long, Set<Long>> objIndex = index201.get(values[2]);

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
     * @param subject The subject to find or null to indicate any subject.
     * @param predicate The predicate to find or null to indicate any predicate.
     * @param object ObjectNode The object to find or null to indicate any object.
     * @throws GraphException If there was an error accessing the graph.
     */
    public ClosableIterator<Triple> find(SubjectNode subject, PredicateNode predicate, ObjectNode object)
        throws GraphException {

        // Check that the parameters are not nulls
        checkForNulls(subject, predicate, object, FIND_CANT_USE_NULLS);

        // Get local node values
        Long[] values;
        try {
            values = localize(subject, predicate, object);
        }
        catch (GraphException ge) {

            // A graph exception implies that the subject, predicate or object does
            // not exist in the graph.
            return new EmptyClosableIterator();
        }

        // test which index to use
        if (ANY_SUBJECT_NODE != subject) {
            // test for {sp*}
            if (ANY_PREDICATE_NODE != predicate) {
                // test for {spo}
                return fixedSubjectAndPredicate(values, object, subject, predicate);
            }
            else {
                // test for {s**}
                if (ANY_OBJECT_NODE == object) {
                    return new OneFixedIterator(index012, 0, values[0], elementFactory,
                        new GraphHandler012(this), (Map) index012.get(values[0]));
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
        return new GraphIterator(elementFactory, new GraphHandler012(this));
    }

    private ClosableIterator<Triple> fixedSubjectAndPredicate(Long[] values, ObjectNode object, SubjectNode subject,
        PredicateNode predicate) throws GraphException {
        if (ANY_OBJECT_NODE != object) {
            // got {spo}
            return new ThreeFixedIterator(this, subject, predicate, object);
        }
        else {
            // got {sp*}
            return new TwoFixedIterator(index012, 0, values[0], values[1],
                elementFactory, new GraphHandler012(this),
                (Map) index012.get(values[0]));
        }
    }

    private ClosableIterator<Triple> fixedObjectIterator(Long[] values, SubjectNode subject, PredicateNode predicate) {
        // test for {s*o}
        if (ANY_SUBJECT_NODE != subject) {
            return new TwoFixedIterator(index201, 1, values[2], values[0],
                elementFactory, new GraphHandler201(this),
                (Map) index201.get(values[2]));
        }
        else {
            // test for {**o}.  {*po} should have been picked up above
            assert ANY_PREDICATE_NODE == predicate;
            return new OneFixedIterator(index201, 1, values[2], elementFactory,
                new GraphHandler201(this), (Map) index201.get(values[2]));
        }
    }

    private ClosableIterator<Triple> fixedPredicateIterator(Long[] values, ObjectNode object, SubjectNode subject) {
        // test for {*po}
        if (ANY_OBJECT_NODE != object) {
            return new TwoFixedIterator(index120, 2, values[1], values[2],
                elementFactory, new GraphHandler120(this),
                (Map) index120.get(values[1]));
        }
        else {
            // test for {*p*}.  {sp*} should have been picked up above
            assert ANY_SUBJECT_NODE == subject;
            return new OneFixedIterator(index120, 2, values[1], elementFactory,
                new GraphHandler120(this), (Map) index120.get(values[1]));
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
     * @param subject The subject.
     * @param predicate The predicate.
     * @param object The object.
     * @throws GraphException If the statement can't be made.
     */
    public void add(SubjectNode subject, PredicateNode predicate,
        ObjectNode object) throws GraphException {

        // Check that the parameters are not nulls or any nodes
        checkForNullsAndAnyNodes(subject, predicate, object, CANT_ADD_NULL_MESSAGE, CANT_ADD_ANY_NODE_MESSAGE);

        // Get local node values also tests that it's a valid subject, predicate
        // and object.
        Long[] values = localize(subject, predicate, object);
        addTo012(values[0], values[1], values[2]);
        addTo120(values[1], values[2], values[0]);
        addTo201(values[2], values[0], values[1]);
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
     *     example if it didn't exist.
     */
    public void remove(Triple triple) throws GraphException {
        remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    /**
     * Removes a triple from the graph.
     *
     * @param subject The subject.
     * @param predicate The predicate.
     * @param object The object.
     * @throws GraphException If there was an error revoking the statement, for
     *     example if it didn't exist.
     */
    public void remove(SubjectNode subject, PredicateNode predicate,
        ObjectNode object) throws GraphException {

        // Check that the parameters are not nulls or any nodes
        checkForNullsAndAnyNodes(subject, predicate, object, CANT_REMOVE_NULL_MESSAGE, CANT_REMOVE_ANY_NODE_MESSAGE);

        // Get local node values also tests that it's a valid subject, predicate
        // and object.
        Long[] values = localize(subject, predicate, object);

        removeFrom012(values[0], values[1], values[2]);
        // if the first one succeeded then try and attempt removal on both of the others
        try {
            removeFrom120(values[1], values[2], values[0]);
        }
        finally {
            removeFrom201(values[2], values[0], values[1]);
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
        // TODO Move this into the LongIndex implementations.
        long size = 0;
        // go over the index map
        Iterator first = index012.values().iterator();
        while (first.hasNext()) {
            // go over the sub indexes
            Iterator second = ((Map) first.next()).values().iterator();
            while (second.hasNext()) {
                // accumulate the sizes of the groups
                size += ((java.util.Collection) second.next()).size();
            }
        }
        return size;
    }

    /**
     * Returns true if the graph is empty i.e. the number of triples is 0.
     *
     * @return true if the graph is empty i.e. the number of triples is 0.
     */
    public boolean isEmpty() throws GraphException {
        return index012.isEmpty();
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

    // TODO AN Break this out into its own class and test drive.
    /**
     * Adds a triple to a single index.
     *
     * @param first The first node.
     * @param second The second node.
     * @param third The last node.
     * @throws GraphException If there was an error adding the statement.
     */
    private Long[] localize(Node first, Node second, Node third) throws GraphException {

        Long[] localValues = new Long[TRIPLE];

        // TODO AN See if we can remove these guard clauses now that we throw an Illegalargumentexception if they
        // are invalid first.

        // convert the nodes to local memory nodes for convenience
        localValues[0] = convertSubject(first);
        localValues[1] = convertPredicate(second);
        localValues[2] = convertObject(third);
        return localValues;
    }

    private Long convertSubject(Node first) throws GraphException {
        Long subjectValue = null;
        if (ANY_SUBJECT_NODE != first) {
            if (first instanceof BlankNodeImpl) {
                subjectValue = ((BlankNodeImpl) first).getId();
            }
            else {
                subjectValue = elementFactory.getNodeIdByString(String.valueOf(first));
            }

            if (null == subjectValue) {
                throw new GraphException("Subject does not exist in graph");
            }
        }

        return subjectValue;
    }

    private Long convertPredicate(Node second) throws GraphException {
        Long predicateValue = null;
        if (ANY_PREDICATE_NODE != second) {
            predicateValue = elementFactory.getNodeIdByString(String.valueOf(second));

            if (null == predicateValue) {
                throw new GraphException("Predicate does not exist in graph");
            }
        }

        return predicateValue;
    }

    private Long convertObject(Node third) throws GraphException {
        Long objectValue = null;
        if (ANY_OBJECT_NODE != third) {
            if (third instanceof BlankNodeImpl) {
                objectValue = ((BlankNodeImpl) third).getId();
            }
            else if (third instanceof LiteralImpl) {
                objectValue = elementFactory.getNodeIdByString(((LiteralImpl) third).getEscapedForm());
            }
            else {
                objectValue = elementFactory.getNodeIdByString(String.valueOf(third));
            }

            if (null == objectValue) {
                throw new GraphException("Object does not exist in graph");
            }
        }

        return objectValue;
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
        out.writeObject(elementFactory.getNodePool().toArray());
        // TODO: Consider writing these nodes individually.  Converting to an array
        // may take up unnecessary memory
    }

    /**
     * Deserializes an object from a stream.
     *
     * @param in The stream to read from.
     * @throws IOException If an I/O error occurs while reading.
     */
    private void readObject(ObjectInputStream in) throws IOException,
        ClassNotFoundException {
        // read in the first index with the default reader
        in.defaultReadObject();
        // initialize the fields not yet done by the constructor
        try {
            init();
        }
        catch (GraphException e) {
            throw new ClassNotFoundException("Unable to initialize a new graph", e);
        }

        // read all the nodes as well
        Object[] nodes = (Object[]) in.readObject();

        try {
            // test node factory creation in case the constructor did it
            if (null == elementFactory) {
                elementFactory = new GraphElementFactoryImpl();
            }
        }
        catch (TripleFactoryException e) {
            throw new ClassNotFoundException("Unable to build NodeFactory", e);
        }
        // populate the node factory with these nodes
        for (int n = 0; n < nodes.length; n++) {
            elementFactory.registerNode((MemNode) nodes[n]);
        }

        // fill in the other indexes
        try {
            GraphHandler012 graphHandler012 = new GraphHandler012(this);
            graphHandler012.reconstructIndices(longIndex012, longIndex120, longIndex201);
        }
        catch (GraphException e) {
            throw new ClassNotFoundException("Unable to add to a graph index", e);
        }
    }

    void removeFrom012(Long first, Long second, Long third)
        throws GraphException {
        longIndex012.remove(first, second, third);
    }

    void removeFrom120(Long first, Long second, Long third)
        throws GraphException {
        longIndex120.remove(first, second, third);
    }

    void removeFrom201(Long first, Long second, Long third)
        throws GraphException {
        longIndex201.remove(first, second, third);
    }

    void addTo012(Long first, Long second, Long third)
        throws GraphException {
        try {
            longIndex012.add(first, second, third);
        }
        catch (GraphException e) {
            removeFrom012(first, second, third);
            throw e;
        }
    }

    void addTo120(Long first, Long second, Long third)
        throws GraphException {
        try {
            longIndex120.add(first, second, third);
        }
        catch (GraphException e) {
            removeFrom120(first, second, third);
            throw e;
        }
    }

    void addTo201(Long first, Long second, Long third)
        throws GraphException {
        longIndex201.add(first, second, third);
    }

    Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator012() {
        return longIndex012.iterator();
    }

    Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator120() {
        return longIndex120.iterator();
    }

    Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator201() {
        return longIndex201.iterator();
    }
}
