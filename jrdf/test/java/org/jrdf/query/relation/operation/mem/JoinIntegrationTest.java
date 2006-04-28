/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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

import junit.framework.TestCase;
import org.jrdf.graph.URIReference;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.constants.RelationDEE;
import org.jrdf.query.relation.constants.RelationDUM;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.AttributeValuePairImpl;
import org.jrdf.query.relation.type.BlankNodeType;
import org.jrdf.query.relation.type.LiteralType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.util.test.NodeTestUtil;
import org.jrdf.vocabulary.RDF;

import java.util.Collections;
import java.util.Set;

/**
 * Tests the integration between join and other classes such as RelationDEE, RelationDUM and other
 * relations.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class JoinIntegrationTest extends TestCase {
    private static final Attribute ATTRIBUTE_1 = new AttributeImpl("foo1", new LiteralType());
    private static final Attribute ATTRIBUTE_2 = new AttributeImpl("foo2", new BlankNodeType());
    private static final Attribute ATTRIBUTE_3 = new AttributeImpl("bar1", new SubjectNodeType());
    private static final Attribute ATTRIBUTE_4 = new AttributeImpl("bar2", new PredicateNodeType());
    private static final URIReference RESOURCE_1 = NodeTestUtil.createResource(RDF.ALT);
    private static final URIReference RESOURCE_2 = NodeTestUtil.createResource(RDF.BAG);
    private static final URIReference RESOURCE_3 = NodeTestUtil.createResource(RDF.FIRST);
    private static final URIReference RESOURCE_4 = NodeTestUtil.createResource(RDF.LI);
    private static final AttributeValuePair ATTRIBUTE_VALUE_PAIR_1 =
        new AttributeValuePairImpl(ATTRIBUTE_1, RESOURCE_1);
    private static final AttributeValuePair ATTRIBUTE_VALUE_PAIR_2 =
        new AttributeValuePairImpl(ATTRIBUTE_2, RESOURCE_2);
    private static final AttributeValuePair ATTRIBUTE_VALUE_PAIR_3 =
        new AttributeValuePairImpl(ATTRIBUTE_3, RESOURCE_3);
    private static final AttributeValuePair ATTRIBUTE_VALUE_PAIR_4 =
        new AttributeValuePairImpl(ATTRIBUTE_4, RESOURCE_4);


    public void testRelationDEEandDUM() {
        // The JOIN of empty is DEE.
        checkRelation(Collections.<Relation>emptySet(), RelationDEE.RELATION_DEE);
        // The JOIN of DEE is DEE.
        checkRelation(Collections.singleton(RelationDEE.RELATION_DEE), RelationDEE.RELATION_DEE);
        // The JOIN of DUM is DUM.
        checkRelation(Collections.singleton(RelationDUM.RELATION_DUM), RelationDUM.RELATION_DUM);
    }

//    public void testCartesianProduct() {
//        Set<Tuple> tuple1 = createASingleTuple(ATTRIBUTE_VALUE_PAIR_1, ATTRIBUTE_VALUE_PAIR_2);
//        Set<Tuple> tuple2 = createASingleTuple(ATTRIBUTE_VALUE_PAIR_3, ATTRIBUTE_VALUE_PAIR_4);
//        Set<Tuple> resultTuple = createASingleTuple(ATTRIBUTE_VALUE_PAIR_1, ATTRIBUTE_VALUE_PAIR_2,
//            ATTRIBUTE_VALUE_PAIR_3, ATTRIBUTE_VALUE_PAIR_4);
//        Set<Attribute> heading1 = createHeading(ATTRIBUTE_1, ATTRIBUTE_2);
//        Set<Attribute> heading2 = createHeading(ATTRIBUTE_3, ATTRIBUTE_4);
//        Set<Attribute> resultHeading = createHeading(ATTRIBUTE_1, ATTRIBUTE_2, ATTRIBUTE_3, ATTRIBUTE_4);
//        Relation relation1 = new RelationImpl(heading1, tuple1);
//        Relation relation2 = new RelationImpl(heading2, tuple2);
//        Relation expectedResult = new RelationImpl(resultHeading, resultTuple);
//
//        Set<Relation> tuples = new TreeSet<Relation>();
//        tuples.add(relation1);
//        tuples.add(relation2);
//
//        checkRelation(tuples, expectedResult);
//    }

//    private Set<Tuple> createASingleTuple(AttributeValuePair... attributeValuePairs) {
//        Set<AttributeValuePair> values = new TreeSet<AttributeValuePair>();
//        for (AttributeValuePair attributeValuePair : attributeValuePairs) {
//            values.add(attributeValuePair);
//        }
//        Set<Tuple> tuples = new TreeSet<Tuple>();
//        tuples.add(new TupleImpl(values));
//        return tuples;
//    }
//
//    private Set<Attribute> createHeading(Attribute... attributes) {
//        Set<Attribute> heading = new TreeSet<Attribute>();
//        for (Attribute attribute: attributes) {
//            heading.add(attribute);
//        }
//        return heading;
//    }

    private void checkRelation(Set<Relation> actual, Relation expected) {
        Relation relation = Join.JOIN.join(actual);
        assertTrue(relation == expected);
    }
}
