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

import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.EmptyClosableIterator;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Dum is a relation with no tuples and is the base relation for FALSE.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class RelationDUM implements EvaluatedRelation, Serializable {
    private static final long serialVersionUID = -6414359849752251621L;

    /**
     * The singleton version of RelationDUM.
     */
    public static final EvaluatedRelation RELATION_DUM = new RelationDUM();

    /**
     * There can be only one RelationDUM.
     */
    private RelationDUM() {
    }

    /**
     * This Relation has no attributes.
     *
     * @return an empty set.
     */
    public Set<Attribute> getHeading() {
        return Collections.emptySet();
    }

    /**
     * Returns an empty set of tuples.
     *
     * @return an empty set of tuples.
     */
    public Set<Tuple> getTuples() {
        return Collections.emptySet();
    }

    public long getTupleSize() {
        return 0;
    }

    public boolean isEmpty() {
        return true;
    }

    public Set<Tuple> getTuples(Map<Attribute, Node> attributeValue) {
        return Collections.emptySet();
    }

    public SortedSet<Attribute> getSortedHeading() {
        return new TreeSet<Attribute>(getHeading());
    }

    public ClosableIterator<Tuple> iterator() {
        return new EmptyClosableIterator<Tuple>();
    }

    // TODO (AN) Test drive me
    public SortedSet<Tuple> getSortedTuples() {
        return new TreeSet<Tuple>(getTuples());
    }
}