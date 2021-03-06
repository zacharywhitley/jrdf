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

package org.jrdf.query.relation.mem;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleImpl;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeTupleComparator;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.util.ClosableIterable;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.ClosableIteratorImpl;
import static org.jrdf.util.ClosableIterators.with;
import org.jrdf.util.EqualsUtil;
import org.jrdf.util.Function;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Implementation of relations containing 3 column heading (subject, predicate, object).
 *
 * @author Andrew Newman
 * @version $Id: RelationImpl.java 556 2006-06-13 06:38:55Z newmana $
 */
// TODO (AN) Come back and add unit tests and integration tests!!!!!
public final class GraphRelationImpl implements EvaluatedRelation {
    private static final Triple ALL_TRIPLE = new TripleImpl(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
    private static final int TRIPLE = 3;
    private final Graph graph;
    private final AttributeTupleComparator tupleComparator;
    private final TupleFactory tupleFactory;
    private final SortedAttributeFactory attributeFactory;
    private final AttributeValuePairHelper avpHelper;

    public GraphRelationImpl(Graph newGraph, SortedAttributeFactory newAttributeFactory,
        AttributeValuePairHelper newAvpHelper, AttributeTupleComparator newTupleComparator,
        TupleFactory newTupleFactory) {
        this.graph = newGraph;
        this.attributeFactory = newAttributeFactory;
        this.avpHelper = newAvpHelper;
        this.tupleComparator = newTupleComparator;
        this.tupleFactory = newTupleFactory;
    }

    public Set<Attribute> getHeading() {
        return attributeFactory.createHeading();
    }

    public SortedSet<Attribute> getSortedHeading() {
        return attributeFactory.createHeading();
    }

    public Set<Tuple> getTuples() {
        SortedSet<Attribute> heading = attributeFactory.createHeading();
        Attribute[] attributes = heading.toArray(new Attribute[heading.size()]);
        return getUnsortedTuplesFromGraph(ALL_TRIPLE, attributes);
    }

    public Set<Tuple> getTuples(Map<Attribute, Node> nameValues) {
        Attribute[] attributes = nameValues.keySet().toArray(new Attribute[TRIPLE]);
        Triple searchTriple = new TripleImpl((SubjectNode) nameValues.get(attributes[0]),
            (PredicateNode) nameValues.get(attributes[1]),
            (ObjectNode) nameValues.get(attributes[2]));
        return getUnsortedTuplesFromGraph(searchTriple, attributes);
    }

    public ClosableIterator<Tuple> iterator() {
        return new ClosableIteratorImpl<Tuple>(getTuples().iterator());
    }

    public long getTupleSize() {
        return graph.getNumberOfTriples();
    }

    public boolean isEmpty() {
        return graph.getNumberOfTriples() != 0;
    }

    @Override
    public int hashCode() {
        return graph.hashCode() ^ tupleComparator.hashCode() ^ attributeFactory.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (EqualsUtil.isNull(obj)) {
            return false;
        }
        if (EqualsUtil.sameReference(this, obj)) {
            return true;
        }
        if (!EqualsUtil.hasSuperClassOrInterface(GraphRelationImpl.class, obj)) {
            return false;
        }
        return determineEqualityFromFields((GraphRelationImpl) obj);
    }

    //TODO YF change this to a disk-based set to make it more scalable
    private Set<Tuple> getUnsortedTuplesFromGraph(final Triple searchTriple, final Attribute[] attributes) {
        return with(graph.find(searchTriple), new Function<Set<Tuple>, ClosableIterable<Triple>>() {
            public Set<Tuple> apply(ClosableIterable<Triple> object) {
                Set<Tuple> tuples = new HashSet<Tuple>();
                for (Triple triple : object) {
                    Map<Attribute, Node> avo = avpHelper.createAvo(triple, attributes);
                    tuples.add(tupleFactory.getTuple(avo));
                }
                return tuples;
            }
        });
    }

    private boolean determineEqualityFromFields(GraphRelationImpl graphRelation) {
        if (graphRelation.getHeading().equals(getHeading())) {
            if (graphRelation.getTuples().equals(getTuples())) {
                return true;
            }
        }
        return false;
    }
}
