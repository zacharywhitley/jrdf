/*
 * $Header$
 * $Revision$
 * $Date$
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
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.AttributeValuePair;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Implementation of relations containing a set of tuples and a set of attributes.  The attribute constitute a heading
 * the maps to the tuple values.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class RelationImpl extends Primordial implements Relation {
    private Set<Attribute> heading;
    private Set<Tuple> tuples;
    private final AttributeComparator attributeComparator;
    private final TupleComparator tupleComparator;

    // TODO (AN) Headings can be gleaned from tuples
    public RelationImpl(Set<Tuple> newTuples, AttributeComparator attributeComparator,
            TupleComparator tupleComparator) {
        heading = createHeadingFromTuples(newTuples);
        tuples = newTuples;
        this.attributeComparator = attributeComparator;
        this.tupleComparator = tupleComparator;
    }

    public Set<Attribute> getHeading() {
        return heading;
    }

    public Set<Tuple> getTuples() {
        return tuples;
    }

    // TODO (AN) Test drive me
    public SortedSet<Attribute> getSortedHeading() {
        if (heading instanceof SortedSet) {
            if (((SortedSet) heading).comparator() != null) {
                //noinspection unchecked
                return (SortedSet) heading;
            }
        }

        // TODO (AN) Turn this into a sort call instead?
        Set<Attribute> sortedHeading = new TreeSet<Attribute>(attributeComparator);
        sortedHeading.addAll(heading);
        heading = sortedHeading;
        //noinspection unchecked
        return (SortedSet) sortedHeading;
    }

    // TODO (AN) Test drive me
    public SortedSet<Tuple> getSortedTuples() {
        if (tuples instanceof SortedSet) {
            if (((SortedSet) tuples).comparator() != null) {
                //noinspection unchecked
                return (SortedSet) tuples;
            }
        }

        // TODO (AN) Turn this into a sort call instead?
        Set<Tuple> sortedTuples = new TreeSet<Tuple>(tupleComparator);
        sortedTuples.addAll(tuples);
        tuples = sortedTuples;
        //noinspection unchecked
        return (SortedSet) sortedTuples;
    }

    private Set<Attribute> createHeadingFromTuples(Set<Tuple> newTuples) {
        Set<Attribute> heading = new HashSet<Attribute>();
        Iterator<Tuple> iterator = newTuples.iterator();
        if (iterator.hasNext()) {
            Tuple tuple = iterator.next();
            addHeading(tuple, heading);
        }
        return heading;
    }

    private void addHeading(Tuple tuple, Set<Attribute> heading) {
        Set<AttributeValuePair> attributeValues = tuple.getAttributeValues();
        for (AttributeValuePair attributeValue : attributeValues) {
            heading.add(attributeValue.getAttribute());
        }
    }
}
