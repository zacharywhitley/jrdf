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

package org.jrdf.query.relation.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.TestJRDFFactory;
import org.jrdf.util.NodeTypeComparator;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterface;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.ComparatorTestUtil.checkNullPointerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Serializable;
import java.lang.reflect.Modifier;

/**
 * Tests the ordering of types - first comes S, P, O and then BNode, URI, Literal.
 */
@RunWith(PowerMockRunner.class)
public class TypeComparatorImplUnitTest {
    private static final int EQUAL = 0;
    private static final int BEFORE = -1;
    private static final int AFTER = 1;
    private static final NodeType BNODE_TYPE = new BlankNodeType();
    private static final NodeType URI_NODE_TYPE = new URIReferenceNodeType();
    private static final NodeType LITERAL_NODE_TYPE = new LiteralNodeType();
    private static final NodeType SUBJECT_POSITIONAL_NODE = new SubjectNodeType();
    private static final NodeType PREDICATE_POSITIONAL_NODE = new PredicateNodeType();
    private static final NodeType OBJECT_POSITIONAL_NODE = new ObjectNodeType();
    @Mock
    private NodeType mockNodeType;
    private TypeComparator typeComparator;

    @Before
    public void setUp() throws Exception {
        typeComparator = TestJRDFFactory.getFactory().getNewTypeComparator();
    }

    @Test
    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(TypeComparator.class, TypeComparatorImpl.class);
        checkImplementationOfInterface(Serializable.class, TypeComparator.class);
        checkConstructor(TypeComparatorImpl.class, Modifier.PRIVATE);
        checkConstructor(TypeComparatorImpl.class, Modifier.PUBLIC, NodeTypeComparator.class);
    }

    @Test
    public void testNullPointerException() {
        checkNullPointerException(typeComparator, mockNodeType, null);
        checkNullPointerException(typeComparator, null, mockNodeType);
    }

    @Test
    public void testCompareEqual() {
        replayAll();
        int result = typeComparator.compare(mockNodeType, mockNodeType);
        verifyAll();
        assertThat(result, is(EQUAL));
    }

    @Test
    public void testIdentity() {
        assertThat(typeComparator.compare(BNODE_TYPE, BNODE_TYPE), is(EQUAL));
    }

    @Test
    public void testNodeTypeOrder() {
        assertThat(typeComparator.compare(BNODE_TYPE, URI_NODE_TYPE), is(BEFORE));
        assertThat(typeComparator.compare(BNODE_TYPE, LITERAL_NODE_TYPE), is(BEFORE));
        assertThat(typeComparator.compare(URI_NODE_TYPE, LITERAL_NODE_TYPE), is(BEFORE));
    }

    @Test
    public void testNodeTypeOrderAntiCommutation() {
        assertThat(typeComparator.compare(URI_NODE_TYPE, BNODE_TYPE), is(AFTER));
        assertThat(typeComparator.compare(LITERAL_NODE_TYPE, BNODE_TYPE), is(AFTER));
        assertThat(typeComparator.compare(LITERAL_NODE_TYPE, URI_NODE_TYPE), is(AFTER));
    }

    @Test
    public void testPositionalNodeOrder() {
        assertThat(typeComparator.compare(SUBJECT_POSITIONAL_NODE, PREDICATE_POSITIONAL_NODE), is(BEFORE));
        assertThat(typeComparator.compare(SUBJECT_POSITIONAL_NODE, OBJECT_POSITIONAL_NODE), is(BEFORE));
        assertThat(typeComparator.compare(PREDICATE_POSITIONAL_NODE, OBJECT_POSITIONAL_NODE), is(BEFORE));
    }

    @Test
    public void testPositionalNodeOrderAntiCommutation() {
        assertThat(typeComparator.compare(PREDICATE_POSITIONAL_NODE, SUBJECT_POSITIONAL_NODE), is(AFTER));
        assertThat(typeComparator.compare(OBJECT_POSITIONAL_NODE, SUBJECT_POSITIONAL_NODE), is(AFTER));
        assertThat(typeComparator.compare(OBJECT_POSITIONAL_NODE, PREDICATE_POSITIONAL_NODE), is(AFTER));
    }

    @Test
    public void testNodeTypeAndPositionalTypeOrder() {
        assertThat(typeComparator.compare(BNODE_TYPE, SUBJECT_POSITIONAL_NODE), is(BEFORE));
        assertThat(typeComparator.compare(URI_NODE_TYPE, PREDICATE_POSITIONAL_NODE), is(BEFORE));
        assertThat(typeComparator.compare(LITERAL_NODE_TYPE, OBJECT_POSITIONAL_NODE), is(BEFORE));
    }

    @Test
    public void testNodeTypeAndPositionalTypeOrderAntiCommutation() {
        assertThat(typeComparator.compare(SUBJECT_POSITIONAL_NODE, BNODE_TYPE), is(AFTER));
        assertThat(typeComparator.compare(PREDICATE_POSITIONAL_NODE, URI_NODE_TYPE), is(AFTER));
        assertThat(typeComparator.compare(OBJECT_POSITIONAL_NODE, LITERAL_NODE_TYPE), is(AFTER));
    }
}
