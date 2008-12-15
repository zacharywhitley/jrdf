/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import static org.jrdf.TestJRDFFactory.getFactory;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.Relation;
import static org.jrdf.query.relation.mem.AttributeImplUnitTest.TEST_ATTRIBUTE_BAR_VAR;
import static org.jrdf.query.relation.mem.AttributeImplUnitTest.TEST_ATTRIBUTE_BAZ_VAR;
import static org.jrdf.query.relation.mem.AttributeImplUnitTest.TEST_ATTRIBUTE_FOO_POS;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructorSetsFieldsAndFieldsPrivate;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterface;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.MockFactory;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class RelationHelperImplUnitTest extends TestCase {
    private static final Class[] PARAMETERS = {AttributeComparator.class};
    private static final String[] PARAMETER_NAMES = {"attributeComparator"};
    private MockFactory factory;
    private AttributeComparator mockAttributeComparator;
    private AttributeComparator realAttributeComparator;

    public void setUp() {
        factory = new MockFactory();
        mockAttributeComparator = factory.createMock(AttributeComparator.class);
        realAttributeComparator = getFactory().getNewAttributeComparator();
    }

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(RelationHelper.class, RelationHelperImpl.class);
        checkImplementationOfInterface(Serializable.class, RelationHelperImpl.class);
        checkConstructor(RelationHelperImpl.class, Modifier.PUBLIC, PARAMETERS);
        checkConstructorSetsFieldsAndFieldsPrivate(RelationHelperImpl.class, PARAMETERS, PARAMETER_NAMES);
        checkConstructNullAssertion(RelationHelperImpl.class, PARAMETERS);
    }

    public void testGetMockHeading() {
        RelationHelper relationHelper = new RelationHelperImpl(mockAttributeComparator);
        Relation relation = createRelation(new HashSet<Attribute>());
        factory.replay();
        Set<Attribute> headingUnions = relationHelper.getHeadingUnions(relation);
        checkIsSorted(headingUnions);
        factory.verify();
    }

    public void testGetHeading() {
        RelationHelper relationHelper = new RelationHelperImpl(realAttributeComparator);
        Set<Attribute> set1 = new HashSet<Attribute>();
        set1.add(TEST_ATTRIBUTE_BAR_VAR);
        Set<Attribute> set2 = new HashSet<Attribute>();
        set2.add(TEST_ATTRIBUTE_FOO_POS);
        Relation relation1 = createRelation(set1);
        Relation relation2 = createRelation(set2);
        factory.replay();
        Set<Attribute> headingUnions = relationHelper.getHeadingUnions(relation1, relation2);
        factory.verify();
        assertTrue(headingUnions.contains(TEST_ATTRIBUTE_BAR_VAR));
        assertTrue(headingUnions.contains(TEST_ATTRIBUTE_FOO_POS));
    }

    public void testGetHeadingIntersections() {
        RelationHelper relationHelper = new RelationHelperImpl(realAttributeComparator);
        SortedSet<Attribute> set1 = new TreeSet<Attribute>(realAttributeComparator);
        set1.add(TEST_ATTRIBUTE_BAR_VAR);
        set1.add(TEST_ATTRIBUTE_BAZ_VAR);
        SortedSet<Attribute> set2 = new TreeSet<Attribute>(realAttributeComparator);
        set2.add(TEST_ATTRIBUTE_FOO_POS);
        set2.add(TEST_ATTRIBUTE_BAZ_VAR);
        Relation relation1 = createSortedRelation(set1);
        Relation relation2 = createSortedRelation(set2);
        factory.replay();
        Set<Attribute> headingIntersections = relationHelper.getHeadingIntersections(relation1, relation2);
        factory.verify();
        assertTrue(headingIntersections.contains(TEST_ATTRIBUTE_BAZ_VAR));
        assertFalse(headingIntersections.contains(TEST_ATTRIBUTE_BAR_VAR));
        assertFalse(headingIntersections.contains(TEST_ATTRIBUTE_FOO_POS));
    }

    private void checkIsSorted(Set<Attribute> headingUnions) {
        assertTrue(headingUnions instanceof SortedSet);
        SortedSet<Attribute> sorted = (SortedSet<Attribute>) headingUnions;
        Comparator<? super Attribute> comparator = sorted.comparator();
        assertTrue(comparator instanceof AttributeComparator);
    }

    @SuppressWarnings({ "unchecked" })
    private Relation createRelation(Set<Attribute> set) {
        Relation relation = factory.createMock(Relation.class);
        expect(relation.getHeading()).andReturn(set);
        return relation;
    }

    @SuppressWarnings({ "unchecked" })
    private Relation createSortedRelation(SortedSet<Attribute> set) {
        Relation relation = factory.createMock(Relation.class);
        expect(relation.getSortedHeading()).andReturn(set);
        return relation;
    }
}
