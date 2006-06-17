/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
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

package org.jrdf.query.relation.mem;

import au.net.netstorm.boost.primordial.Primordial;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.mem.TripleImpl;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.GraphRelation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.util.ClosableIterator;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * Implementation of relations containing 3 column heading (subject, predicate, object).
 *
 * @author Andrew Newman
 * @version $Id: RelationImpl.java 556 2006-06-13 06:38:55Z newmana $
 */
// TODO (AN) Come back and add unit tests and integration tests!!!!!
// TODO (AN) This class is doing too much - given the abstractcoupling failure.
public final class GraphRelationImpl extends Primordial implements GraphRelation {
    private static final PositionName SUBJECT_NAME = new PositionName("SUBJECT1");
    private static final PositionName PREDICATE_NAME = new PositionName("PREDICATE1");
    private static final PositionName OBJECT_NAME = new PositionName("OBJECT1");
    private static final Attribute SUBJECT_ATTRIBUTE = new AttributeImpl(SUBJECT_NAME, new SubjectNodeType());
    private static final Attribute PREDICATE_ATTRIBUTE = new AttributeImpl(PREDICATE_NAME, new PredicateNodeType());
    private static final Attribute OBJECT_ATTRIBUTE = new AttributeImpl(OBJECT_NAME, new ObjectNodeType());

    private final Graph graph;
    private final AttributeComparator attributeComparator;
    private final TupleComparator tupleComparator;
    private final AttributeValuePairComparator attributeValuePairComparator;
    private static final int NUMBER_OF_NODES = 3;

    public GraphRelationImpl(Graph graph, AttributeComparator attributeComparator,
            AttributeValuePairComparator attributeValuePairComparator, TupleComparator tupleComparator) {
        this.graph = graph;
        this.attributeComparator = attributeComparator;
        this.attributeValuePairComparator = attributeValuePairComparator;
        this.tupleComparator = tupleComparator;
    }

    public Set<Attribute> getHeading() {
        return createHeading();
    }

    public Set<Tuple> getTuples() {
        return getTuplesFromGraph(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
    }

    public SortedSet<Attribute> getSortedHeading() {
        return null;
    }

    public SortedSet<Tuple> getSortedTuples() {
        return null;
    }

    public Set<Tuple> getTuples(SortedSet<AttributeValuePair> nameValues) {
        throwIllegalArgumentExceptionIfNotThreeAttributeValuePairs(nameValues);
        Triple triple = getNodes(nameValues);
        return getTuplesFromGraph(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    private Set<Attribute> createHeading() {
        TreeSet<Attribute> attributes = new TreeSet<Attribute>(attributeComparator);
        attributes.add(GraphRelationImpl.SUBJECT_ATTRIBUTE);
        attributes.add(GraphRelationImpl.PREDICATE_ATTRIBUTE);
        attributes.add(GraphRelationImpl.OBJECT_ATTRIBUTE);
        return attributes;
    }

    private Set<Tuple> getTuplesFromGraph(SubjectNode subjectNode, PredicateNode predicateNode, ObjectNode objectNode) {
        ClosableIterator<Triple> closableIterator = tryGetTriples(subjectNode, predicateNode, objectNode);
        Set<Tuple> tuples = new TreeSet<Tuple>(tupleComparator);
        while (closableIterator.hasNext()) {
            Triple triple = closableIterator.next();
            addTripleToTuples(tuples, triple);
        }
        return tuples;
    }

    private ClosableIterator<Triple> tryGetTriples(SubjectNode subjectNode, PredicateNode predicateNode,
          ObjectNode objectNode) {
        try {
            return graph.find(subjectNode, predicateNode, objectNode);
        } catch (GraphException e) {
            throw new RuntimeException(e);
        }
    }

    private void addTripleToTuples(Set<Tuple> tuples, Triple triple) {
        Set<AttributeValuePair> avp = createAvp(triple);
        Tuple tuple = new TupleImpl(avp, attributeValuePairComparator);
        tuples.add(tuple);
    }

    private Set<AttributeValuePair> createAvp(Triple triple) {
        final AttributeValuePair subjectAv = new AttributeValuePairImpl(SUBJECT_ATTRIBUTE, triple.getSubject());
        final AttributeValuePair predicateAv = new AttributeValuePairImpl(PREDICATE_ATTRIBUTE, triple.getPredicate());
        final AttributeValuePair objectAv = new AttributeValuePairImpl(OBJECT_ATTRIBUTE, triple.getObject());
        TreeSet<AttributeValuePair> attributeValuePairs = new TreeSet<AttributeValuePair>(attributeValuePairComparator);
        attributeValuePairs.add(subjectAv);
        attributeValuePairs.add(predicateAv);
        attributeValuePairs.add(objectAv);
        return attributeValuePairs;
    }

    private void throwIllegalArgumentExceptionIfNotThreeAttributeValuePairs(Set<AttributeValuePair> nameValues) {
        if (nameValues.size() != NUMBER_OF_NODES) {
            throw new IllegalArgumentException("Can only get 3 tuples.");
        }
    }

    private Triple getNodes(SortedSet<AttributeValuePair> nameValues) {
        Iterator<AttributeValuePair> iterator = nameValues.iterator();
        SubjectNode subject = (SubjectNode) iterator.next().getValue();
        PredicateNode predicate = (PredicateNode) iterator.next().getValue();
        ObjectNode object = (ObjectNode) iterator.next().getValue();
        return new TripleImpl(subject, predicate, object);
    }
}
