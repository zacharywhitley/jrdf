/*
 * $Header$
 * $Revision$
 * $Date$
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

import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import static org.jrdf.util.EqualsUtil.hasSuperClassOrInterface;
import static org.jrdf.util.EqualsUtil.isNull;
import static org.jrdf.util.EqualsUtil.sameReference;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Implementation of relations containing a set of tuples and a set of attributes.  The attribute constitute a heading
 * the maps to the tuple values.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class RelationImpl implements Relation {
    private Set<Attribute> heading;
    private Set<Tuple> tuples;
    private AttributeComparator attributeComparator;
    private TupleComparator tupleComparator;

    // TODO (AN) This only gets the heading from the first tuple - must iterate through all tuples to ensure that we
    // get the maximum heading values.
    RelationImpl(Set<Tuple> tuples, AttributeComparator attributeComparator, TupleComparator tupleComparator) {
        checkNotNull(tuples, attributeComparator, tupleComparator);
        Set<Attribute> fromTuples = createHeadingFromTuples(tuples);
        create(fromTuples, tuples, attributeComparator, tupleComparator);
    }

    RelationImpl(Set<Attribute> heading, Set<Tuple> tuples, AttributeComparator attributeComparator,
        TupleComparator tupleComparator) {
        checkNotNull(heading, tuples, attributeComparator, tupleComparator);
        create(heading, tuples, attributeComparator, tupleComparator);
    }

    private void create(Set<Attribute> heading, Set<Tuple> tuples, AttributeComparator attributeComparator,
        TupleComparator tupleComparator) {
        this.heading = heading;
        this.tuples = tuples;
        this.attributeComparator = attributeComparator;
        this.tupleComparator = tupleComparator;
    }

    public Set<Attribute> getHeading() {
        return heading;
    }

    public Set<Tuple> getTuples() {
        return tuples;
    }

    public long getTupleSize() {
        return tuples.size();
    }

    public Set<Tuple> getTuples(Map<Attribute, Node> avo) {
        Set<Tuple> set = new HashSet<Tuple>();
        for (Tuple tuple : tuples) {
            final Map<Attribute, Node> map = tuple.getAttributeValues();
            if (contains(map, avo)) {
                set.add(tuple);
            }
        }
        return set;
    }

    public Set<Tuple> getTuples(Attribute attribute) {
        Set<Tuple> set = new HashSet<Tuple>();
        for (Tuple tuple : tuples) {
            if (tuple.getValue(attribute) != null) {
                set.add(tuple);
            }
        }
        return set;
    }

    private boolean contains(Map<Attribute, Node> map, Map<Attribute, Node> avo) {
        final Set<Attribute> keys = avo.keySet();
        for (Attribute attr : keys) {
            Node vo = avo.get(attr);
            if (map.get(attr) == null || !map.get(attr).equals(vo)) {
                return false;
            }
        }
        return true;
    }

    // TODO (AN) Test drive me
    public SortedSet<Attribute> getSortedHeading() {
        if (heading instanceof SortedSet) {
            if (((SortedSet) heading).comparator() != null) {
                return (SortedSet<Attribute>) heading;
            }
        }

        // TODO (AN) Turn this into a sort call instead?
        SortedSet<Attribute> sortedHeading = new TreeSet<Attribute>(attributeComparator);
        sortedHeading.addAll(heading);
        heading = sortedHeading;
        return sortedHeading;
    }

    // TODO (AN) Test drive me
    public SortedSet<Tuple> getSortedTuples() {
        if (tuples instanceof SortedSet) {
            if (((SortedSet) tuples).comparator() != null) {
                return (SortedSet<Tuple>) tuples;
            }
        }
        // TODO (AN) Turn this into a sort call instead?
        SortedSet<Tuple> sortedTuples = new TreeSet<Tuple>(tupleComparator);
        sortedTuples.addAll(tuples);
        tuples = sortedTuples;
        return sortedTuples;
    }

    public SortedSet<Tuple> getSortedTuples(Attribute attribute) {
        ((TupleAttributeValueComparatorImpl) tupleComparator).setAttribute(attribute);
        SortedSet<Tuple> sortedTuples = new TreeSet<Tuple>(tupleComparator);
        sortedTuples.addAll(tuples);
        return sortedTuples;
    }

    public int hashCode() {
        return tuples.hashCode();
    }

    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (sameReference(this, obj)) {
            return true;
        }
        if (hasSuperClassOrInterface(Relation.class, obj)) {
            return determineEqualityFromFields((Relation) obj);
        }
        return false;
    }

    public String toString() {
        return tuples.toString();
    }

    private boolean determineEqualityFromFields(Relation answer) {
        if (answer.getHeading().equals(getHeading())) {
            if (answer.getTuples().equals(getTuples())) {
                return true;
            }
        }
        return false;
    }

    private Set<Attribute> createHeadingFromTuples(Set<Tuple> newTuples) {
        Set<Attribute> heading = new HashSet<Attribute>();
        for (Tuple tuple : newTuples) {
            heading.addAll(tuple.getAttributeValues().keySet());
        }
        return heading;
    }
}
