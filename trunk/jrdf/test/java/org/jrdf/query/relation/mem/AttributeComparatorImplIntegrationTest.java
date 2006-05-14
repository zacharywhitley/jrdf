/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
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

import junit.framework.TestCase;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.mem.NodeComparatorImpl;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.type.BlankNodeType;
import org.jrdf.query.relation.type.LiteralType;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.query.relation.type.Type;
import org.jrdf.query.relation.type.URIReferenceType;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;
import org.jrdf.util.test.AssertThrows;
import org.jrdf.util.test.ClassPropertiesTestUtil;

import java.lang.reflect.Modifier;
import java.util.Comparator;

/**
 * Test for the implementation of NodeComparatorImpl.
 *
 * @author Andrew Newman
 * @version $Id: ClosableIterator.java 436 2005-12-19 13:19:55Z newmana $
 */
public class AttributeComparatorImplIntegrationTest extends TestCase {
    private static final int EQUAL = 0;
    private static final int BEFORE = -1;
    private static final int AFTER = 1;
    private AttributeComparator attComparator;

    private static final AttributeName VARIABLE_NAME_1 = new VariableName("bar");
    private static final AttributeName VARIABLE_NAME_2 = new VariableName("foo");
    private static final AttributeName POSITION_NAME_1 = new PositionName("bar");
    private static final AttributeName POSITION_NAME_2 = new PositionName("foo");
    private static final Type NODE_TYPE_1 = new BlankNodeType();
    private static final Type NODE_TYPE_2 = new URIReferenceType();
    private static final Type NODE_TYPE_3 = new LiteralType();
    private static final Type POSITIONAL_NODE_1 = new SubjectNodeType();
    private static final Type POSITIONAL_NODE_2 = new PredicateNodeType();
    private static final Type POSITIONAL_NODE_3 = new ObjectNodeType();

    public static final Attribute TEST_VAR_BAR_BNODE = new AttributeImpl(VARIABLE_NAME_1, NODE_TYPE_1);
    public static final Attribute TEST_VAR_FOO_BNODE = new AttributeImpl(VARIABLE_NAME_2, NODE_TYPE_1);
    public static final Attribute TEST_POS_BAR_BNODE = new AttributeImpl(POSITION_NAME_1, NODE_TYPE_1);
    public static final Attribute TEST_POS_FOO_BNODE = new AttributeImpl(POSITION_NAME_2, NODE_TYPE_1);
    public static final Attribute TEST_VAR_BAR_URI_REF = new AttributeImpl(VARIABLE_NAME_1, NODE_TYPE_2);
    public static final Attribute TEST_VAR_BAR_LITERAL = new AttributeImpl(VARIABLE_NAME_1, NODE_TYPE_3);


    protected void setUp() throws Exception {
        super.setUp();
        NodeTypeComparator nodeTypeComparator = new NodeTypeComparatorImpl();
        attComparator = new AttributeComparatorImpl(nodeTypeComparator);
    }

    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(NodeComparator.class, NodeComparatorImpl.class);
        ClassPropertiesTestUtil.checkExtensionOf(Comparator.class, NodeComparator.class);
        ClassPropertiesTestUtil .checkConstructor(NodeComparatorImpl.class, Modifier.PUBLIC, NodeTypeComparator.class);
    }

    public void testNullPointerException() {
        checkNullPointerException(attComparator, AttributeImplUnitTest.TEST_ATTRIBUTE_1, null);
        checkNullPointerException(attComparator, null, AttributeImplUnitTest.TEST_ATTRIBUTE_1);
    }

    public void testIdentity() {
        assertEquals(EQUAL, attComparator.compare(TEST_VAR_BAR_BNODE, TEST_VAR_BAR_BNODE));
    }

    public void testAttributeNameTypeOrder() {
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_BNODE, TEST_VAR_FOO_BNODE));
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_BNODE, TEST_POS_BAR_BNODE));
        assertEquals(BEFORE, attComparator.compare(TEST_POS_BAR_BNODE, TEST_POS_FOO_BNODE));
    }

    public void testAttributeNameTypeAntiCommutation() {
        assertEquals(AFTER, attComparator.compare(TEST_VAR_FOO_BNODE, TEST_VAR_BAR_BNODE));
        assertEquals(AFTER, attComparator.compare(TEST_POS_BAR_BNODE, TEST_VAR_BAR_BNODE));
        assertEquals(AFTER, attComparator.compare(TEST_POS_FOO_BNODE, TEST_POS_BAR_BNODE));
    }

    public void testTypeOrder() {
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_BNODE, TEST_VAR_BAR_URI_REF));
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_BNODE, TEST_VAR_BAR_LITERAL));
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_URI_REF, TEST_VAR_BAR_LITERAL));
    }

//    public void testBlankNodeComparison() {
//        assertEquals(AttributeValuePairComparatorImplIntegrationTest.BEFORE, attComparator.compare(AttributeValuePairComparatorImplIntegrationTest.BNODE_1, AttributeValuePairComparatorImplIntegrationTest.BNODE_2));
//        assertEquals(AttributeValuePairComparatorImplIntegrationTest.AFTER, attComparator.compare(AttributeValuePairComparatorImplIntegrationTest.BNODE_2, AttributeValuePairComparatorImplIntegrationTest.BNODE_1));
//        assertEquals(AttributeValuePairComparatorImplIntegrationTest.EQUAL, attComparator.compare(AttributeValuePairComparatorImplIntegrationTest.BNODE_2, new BlankNodeImpl(2l, "b")));
//    }
//
//    public void testURIComparisonByString() {
//        assertEquals(AttributeValuePairComparatorImplIntegrationTest.BEFORE, attComparator.compare(AttributeValuePairComparatorImplIntegrationTest.URI_1, AttributeValuePairComparatorImplIntegrationTest.URI_2));
//        assertEquals(AttributeValuePairComparatorImplIntegrationTest.AFTER, attComparator.compare(AttributeValuePairComparatorImplIntegrationTest.URI_2, AttributeValuePairComparatorImplIntegrationTest.URI_1));
//        assertEquals(AttributeValuePairComparatorImplIntegrationTest.EQUAL, attComparator.compare(AttributeValuePairComparatorImplIntegrationTest.URI_2, new URIReferenceImpl(RDF.BAG, 2l)));
//    }
//
//    public void testLiteralComparisonByString() {
//        assertEquals(AttributeValuePairComparatorImplIntegrationTest.BEFORE, attComparator.compare(AttributeValuePairComparatorImplIntegrationTest.LITERAL_1, AttributeValuePairComparatorImplIntegrationTest.LITERAL_2));
//        assertEquals(AttributeValuePairComparatorImplIntegrationTest.AFTER, attComparator.compare(AttributeValuePairComparatorImplIntegrationTest.LITERAL_2, AttributeValuePairComparatorImplIntegrationTest.LITERAL_1));
//        assertEquals(AttributeValuePairComparatorImplIntegrationTest.EQUAL, attComparator.compare(AttributeValuePairComparatorImplIntegrationTest.LITERAL_2, new LiteralImpl("foo")));
//    }

    private void checkNullPointerException(final AttributeComparator attComparator, final Attribute att,
                                           final Attribute att2) {
        AssertThrows.assertThrows(NullPointerException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                //noinspection unchecked
                attComparator.compare(att, att2);
            }
        });
    }

    private void checkClassCastException(final AttributeComparator attComparator, final Attribute att,
                                         final Attribute att2) {
        AssertThrows.assertThrows(NullPointerException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                //noinspection unchecked
                attComparator.compare(att, att2);
            }
        });
    }
}
