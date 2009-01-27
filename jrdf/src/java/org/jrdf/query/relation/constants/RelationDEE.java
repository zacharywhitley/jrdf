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

package org.jrdf.query.relation.constants;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.ValueOperation;
import static org.jrdf.query.relation.constants.NullaryTuple.NULLARY_TUPLE;
import org.jrdf.query.relation.mem.ComparatorFactory;
import org.jrdf.query.relation.mem.ComparatorFactoryImpl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map;

/**
 * Dee is a relation with one tuple and is the base relation for NULLARY_TUPLE.
 * <p>It is also the identity with respect to JOIN i.e. JOIN {r, RelationDEE} is DEE and
 * JOIN {} is RelationDEE.</p>
 * <p>Again, this is going to change when operations are more properly filled out.</p>
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class RelationDEE implements Relation, Serializable {
    private static final ComparatorFactory FACTORY = new ComparatorFactoryImpl();
    private static final long serialVersionUID = -8473232661811978990L;

    /**
     * The singleton version of RelationDEE.
     */
    public static final Relation RELATION_DEE = new RelationDEE();

    /**
     * There can be only one RelationDEE.
     */
    private RelationDEE() {
    }

    public Set<Attribute> getHeading() {
        return Collections.emptySet();
    }

    /**
     * Returns the TUPLE_ZERO.
     *
     * @return the TUPLE_ZERO.
     */
    public Set<Tuple> getTuples() {
        return Collections.singleton(NULLARY_TUPLE);
    }

    public Set<Tuple> getTuples(Map<Attribute, ValueOperation> avo) {
        return Collections.singleton(NULLARY_TUPLE);
    }

    public SortedSet<Tuple> getSortedTuples(Attribute attribute) {
        return getSortedTuples();
    }

    public Set<Tuple> getTuples(Attribute attribute) {
        return Collections.singleton(NULLARY_TUPLE);
    }

    public long getTupleSize() {
        return  NULLARY_TUPLE.getAttributeValues().size();
    }

    // TODO (AN) Test drive me
    public SortedSet<Attribute> getSortedHeading() {
        SortedSet<Attribute> heading = new TreeSet<Attribute>(FACTORY.createAttributeComparator());
        heading.addAll(getHeading());
        return heading;
    }

    // TODO (AN) Test drive me
    public SortedSet<Tuple> getSortedTuples() {
        SortedSet<Tuple> sorted = new TreeSet<Tuple>(FACTORY.createTupleComparator());
        sorted.addAll(getTuples());
        return sorted;
    }
}