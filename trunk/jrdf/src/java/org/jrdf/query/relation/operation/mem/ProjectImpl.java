/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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

package org.jrdf.query.relation.operation.mem;

import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.AttributeValuePairImpl;
import org.jrdf.query.relation.operation.Project;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.query.relation.type.SubjectObjectNodeType;
import org.jrdf.query.relation.type.SubjectPredicateNodeType;
import org.jrdf.query.relation.type.PredicateObjectNodeType;
import org.jrdf.query.relation.type.SubjectPredicateObjectNodeType;

import java.util.HashSet;
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

    public Relation include(Relation relation, Set<Attribute> attributes) {
        // TODO (AN) Test drive short circuit
        if (relation.getHeading().equals(attributes)) {
            return relation;
        }

        if (requiresMerge(attributes)) {
            relation = mergeRelationHeading(relation, attributes);
        }
        Set<Attribute> newHeading = relation.getHeading();
        newHeading.retainAll(attributes);
        return project(relation, newHeading);
    }

    // TODO (AN) Piece of bollocks.
    private boolean requiresMerge(Set<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            if (attribute.getType().getClass().equals(SubjectPredicateNodeType.class) ||
                    attribute.getType().getClass().equals(SubjectObjectNodeType.class) ||
                    attribute.getType().getClass().equals(PredicateObjectNodeType.class)) {
                return true;
            }
        }
        return false;
    }

    // TODO (AN) Piece of bollocks.
    private Relation mergeRelationHeading(Relation relation, Set<Attribute> attributes) {
        Set<Tuple> newTuples = new HashSet<Tuple>();
        Set<Tuple> tuples = relation.getTuples();
        for (Tuple tuple : tuples) {
            Set<AttributeValuePair> avps = tuple.getAttributeValues();
            Set<AttributeValuePair> newAvps = new HashSet<AttributeValuePair>();
            for (Attribute attribute : attributes) {
                Set<Node> matchedValues = new HashSet<Node>();
                Set<Attribute> attributesToMatch = new HashSet<Attribute>();
                if (attribute.getType().getClass().equals(SubjectPredicateObjectNodeType.class)) {
                    attributesToMatch.add(new AttributeImpl(attribute.getAttributeName(), new SubjectNodeType()));
                    attributesToMatch.add(new AttributeImpl(attribute.getAttributeName(), new PredicateNodeType()));
                    attributesToMatch.add(new AttributeImpl(attribute.getAttributeName(), new ObjectNodeType()));
                }
                else if (attribute.getType().getClass().equals(SubjectPredicateNodeType.class)) {
                    attributesToMatch.add(new AttributeImpl(attribute.getAttributeName(), new SubjectNodeType()));
                    attributesToMatch.add(new AttributeImpl(attribute.getAttributeName(), new PredicateNodeType()));
                } else if (attribute.getType().getClass().equals(SubjectObjectNodeType.class)) {
                    attributesToMatch.add(new AttributeImpl(attribute.getAttributeName(), new SubjectNodeType()));
                    attributesToMatch.add(new AttributeImpl(attribute.getAttributeName(), new ObjectNodeType()));
                } else if (attribute.getType().getClass().equals(PredicateObjectNodeType.class)) {
                    attributesToMatch.add(new AttributeImpl(attribute.getAttributeName(), new PredicateNodeType()));
                    attributesToMatch.add(new AttributeImpl(attribute.getAttributeName(), new ObjectNodeType()));
                } else {
                    attributesToMatch.add(attribute);
                }

                for (AttributeValuePair avp : avps) {
                    if (attributesToMatch.contains(avp.getAttribute())) {
                        matchedValues.add(avp.getValue());
                    }
                }
                if (matchedValues.size() == 1) {
                    Node matchedValue = matchedValues.iterator().next();
                    AttributeValuePair newAvp = new AttributeValuePairImpl(attribute, matchedValue);
                    newAvps.add(newAvp);
                }
            }
            if (newAvps.size() == attributes.size()) {
                newTuples.add(tupleFactory.getTuple(newAvps));
            }
        }
        return relationFactory.getRelation(newTuples);
    }

    public Relation exclude(Relation relation, Set<Attribute> attributes) {
        // TODO (AN) Test drive short circuit
        if (attributes.size() == 0) {
            return relation;
        }

        Set<Attribute> newHeading = relation.getHeading();
        newHeading.removeAll(attributes);
        return project(relation, newHeading);
    }

    private Relation project(Relation relation, Set<Attribute> newHeading) {
        Set<Tuple> newTuples = new HashSet<Tuple>();
        Set<Tuple> tuples = relation.getTuples();
        for (Tuple tuple : tuples) {
            Tuple newTuple = createNewTuples(tuple, newHeading);
            newTuples.add(newTuple);
        }
        return relationFactory.getRelation(newTuples);
    }

    private Tuple createNewTuples(Tuple tuple, Set<Attribute> newHeading) {
        Set<AttributeValuePair> avps = tuple.getAttributeValues();
        Set<AttributeValuePair> newAvps = new HashSet<AttributeValuePair>();
        for (AttributeValuePair avp : avps) {
            if (newHeading.contains(avp.getAttribute())) {
                newAvps.add(avp);
            }
        }
        return tupleFactory.getTuple(newAvps);
    }
}