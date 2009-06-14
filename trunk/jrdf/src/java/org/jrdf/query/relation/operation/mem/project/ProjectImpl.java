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

package org.jrdf.query.relation.operation.mem.project;

import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.operation.Project;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implements restrict by going through the relation and removing the columns.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class ProjectImpl implements Project {
    private final TupleFactory tupleFactory;
    private final RelationFactory relationFactory;

    public ProjectImpl(TupleFactory tupleFactory, RelationFactory relationFactory) {
        this.tupleFactory = tupleFactory;
        this.relationFactory = relationFactory;
    }

    public EvaluatedRelation include(EvaluatedRelation relation, Set<Attribute> attributes) {
        // TODO (AN) Test drive short circuit
        if (relation.getHeading().equals(attributes)) {
            return relation;
        }

        Set<Attribute> newHeading = relation.getHeading();
        newHeading.retainAll(attributes);

        return project(relation, newHeading);
    }

    public EvaluatedRelation exclude(EvaluatedRelation relation, Set<Attribute> attributes) {
        // TODO (AN) Test drive short circuit
        if (attributes.size() == 0) {
            return relation;
        }

        Set<Attribute> newHeading = relation.getHeading();
        newHeading.removeAll(attributes);
        return project(relation, newHeading);
    }

    private EvaluatedRelation project(EvaluatedRelation relation, Set<Attribute> newHeading) {
        Set<Tuple> newTuples = new HashSet<Tuple>();
        Set<Tuple> tuples = relation.getTuples();
        for (Tuple tuple : tuples) {
            Tuple newTuple = createNewTuples(tuple, newHeading);
            // TODO (AN) Only add non empty attributes - this failed.
            if (newTuple.getAttributeValues().size() > 0) {
                newTuples.add(newTuple);
            }
        }

        // TODO (AN) Used to just be getRelation(newHeading) - this failed.
        return relationFactory.getRelation(newHeading, newTuples);
    }

    private Tuple createNewTuples(Tuple tuple, Set<Attribute> newHeading) {
        Map<Attribute, Node> avps = tuple.getAttributeValues();
        Map<Attribute, Node> newAvps = new HashMap<Attribute, Node>();
        for (Attribute attribute : avps.keySet()) {
            if (newHeading.contains(attribute)) {
                newAvps.put(attribute, avps.get(attribute));
            }
        }

        return tupleFactory.getTuple(newAvps);
    }
}