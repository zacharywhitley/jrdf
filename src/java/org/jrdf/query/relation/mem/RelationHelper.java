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

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.graph.Node;

import java.util.SortedSet;
import java.util.Map;

/**
 * Methods on a collection of relations.
 */
public interface RelationHelper {
    /**
     * Returns the set of attribute that are found in any of the given relations.
     *
     * @param relations The relations to iterator over to get a complete set of attributes.
     * @return a set of attributes that are found in any of the given relations.
     */
    SortedSet<Attribute> getHeadingUnions(EvaluatedRelation... relations);

    /**
     * Returns a set of attributes that are found in all of the given relations.
     *
     * @param relations The relations to iterate over to extract common attributes.
     * @return a set of attributes that are found in all of the given relations.
     */
    SortedSet<Attribute> getHeadingIntersections(EvaluatedRelation... relations);

    /**
     * Returns true if the two tuples have values that around bound to the same attribute but have different value.
     *
     * @param headings The shared headings between tuple1 and tuple2.
     * @param tuple1 The first tuple to compare.
     * @param tuple2 The second tuple to compare.
     * @return true if the tuples have incompatible values.
     */
    boolean areIncompatible(SortedSet<Attribute> headings, Tuple tuple1, Tuple tuple2);

    /**
     * Adds to the result map if the first and second tuples are equal to one another.  If the tuples are unequal
     * then returns true - indicating two unequal values within the tuples - a contradiction - no value was added to
     * the result.
     *
     * @param headings The shared headings to get between tuple1 and tupl2.
     * @param tuple1 The first tuple to compare.
     * @param tuple2 The second tuple to compare.
     * @param mapResult If the tuples are equal the attribute and nodes are added.
     * @return true if the tuples have incompatible values.
     */
    boolean addTuplesIfEqual(SortedSet<Attribute> headings, Tuple tuple1, Tuple tuple2,
        Map<Attribute, Node> mapResult);
}
