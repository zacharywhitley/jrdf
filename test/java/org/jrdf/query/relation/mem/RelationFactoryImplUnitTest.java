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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructorSetsFieldsAndFieldsPrivateFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

@RunWith(PowerMockRunner.class)
public class RelationFactoryImplUnitTest {
    private static final Class[] CONSTRUCTOR_TYPES = {AttributeComparator.class, TupleComparator.class};
    private static final String[] CONSTRUCTOR_NAMES = {"attributeComparator", "tupleComparator"};
    private static final Set<Tuple> TUPLES = new HashSet<Tuple>();
    private static final Set<Attribute> HEADING = new HashSet<Attribute>();
    @Mock private AttributeComparator attributeComparator;
    @Mock private TupleComparator tupleComparator;

    @Test
    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(RelationFactory.class, RelationFactoryImpl.class);
        checkConstructor(RelationFactoryImpl.class, Modifier.PUBLIC, CONSTRUCTOR_TYPES);
        checkConstructNullAssertion(RelationFactoryImpl.class, CONSTRUCTOR_TYPES);
        checkConstructorSetsFieldsAndFieldsPrivateFinal(RelationFactoryImpl.class, CONSTRUCTOR_TYPES,
            CONSTRUCTOR_NAMES);
    }

    @Test
    public void testGetRelation() {
        RelationFactory relationFactory = new RelationFactoryImpl(attributeComparator, tupleComparator);
        EvaluatedRelation relation = relationFactory.getRelation(TUPLES);
        checkRelation(relation);
    }

    @Test
    public void testGetRelationWithHeading() {
        RelationFactory relationFactory = new RelationFactoryImpl(attributeComparator, tupleComparator);
        EvaluatedRelation relation = relationFactory.getRelation(HEADING, TUPLES);
        checkRelation(relation);
        assertThat(relation.getHeading(), is(HEADING));
    }

    private void checkRelation(EvaluatedRelation relation) {
        assertThat(relation.getTuples(), is(TUPLES));
        assertThat(relation, instanceOf(RelationImpl.class));
    }
}
