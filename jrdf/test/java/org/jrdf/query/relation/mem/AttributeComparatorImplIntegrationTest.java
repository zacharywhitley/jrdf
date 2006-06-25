/*
 * $Header$
 * $Revision$
 * $Date$
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
import org.jrdf.TestJRDFFactory;
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
import org.jrdf.util.test.AssertThrows;

/**
 * Test for the implementation of NodeComparatorImpl.
 *
 * @author Andrew Newman
 * @version $Id$
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
    private static final AttributeName POSITION_NAME_3 = new PositionName("subject");
    private static final AttributeName POSITION_NAME_4 = new PositionName("predicate");
    private static final AttributeName POSITION_NAME_5 = new PositionName("object");
    private static final Type BNODE_TYPE = new BlankNodeType();
    private static final Type URI_NODE_TYPE = new URIReferenceType();
    private static final Type LITERAL_NODE_TYPE = new LiteralType();
    private static final Type SUBJECT_POSITIONAL_NODE = new SubjectNodeType();
    private static final Type PREDICATE_POSITIONAL_NODE = new PredicateNodeType();
    private static final Type OBJECT_POSITIONAL_NODE = new ObjectNodeType();

    public static final Attribute TEST_VAR_BAR_BNODE = new AttributeImpl(VARIABLE_NAME_1, BNODE_TYPE);
    public static final Attribute TEST_VAR_FOO_BNODE = new AttributeImpl(VARIABLE_NAME_2, BNODE_TYPE);
    public static final Attribute TEST_POS_BAR_BNODE = new AttributeImpl(POSITION_NAME_1, BNODE_TYPE);
    public static final Attribute TEST_POS_FOO_BNODE = new AttributeImpl(POSITION_NAME_2, BNODE_TYPE);
    public static final Attribute TEST_VAR_BAR_URI_REF = new AttributeImpl(VARIABLE_NAME_1, URI_NODE_TYPE);
    public static final Attribute TEST_VAR_FOO_URI_REF = new AttributeImpl(VARIABLE_NAME_2, URI_NODE_TYPE);
    public static final Attribute TEST_VAR_BAR_LITERAL = new AttributeImpl(VARIABLE_NAME_1, LITERAL_NODE_TYPE);
    public static final Attribute TEST_VAR_FOO_LITERAL = new AttributeImpl(VARIABLE_NAME_2, LITERAL_NODE_TYPE);
    public static final Attribute TEST_VAR_BAR_SNODE = new AttributeImpl(VARIABLE_NAME_1, SUBJECT_POSITIONAL_NODE);
    public static final Attribute TEST_VAR_BAR_PNODE = new AttributeImpl(VARIABLE_NAME_1, PREDICATE_POSITIONAL_NODE);
    public static final Attribute TEST_VAR_BAR_ONODE = new AttributeImpl(VARIABLE_NAME_1, OBJECT_POSITIONAL_NODE);
    public static final Attribute TEST_POS_BAR_SNODE = new AttributeImpl(POSITION_NAME_3, SUBJECT_POSITIONAL_NODE);
    public static final Attribute TEST_POS_FOO_SNODE = new AttributeImpl(POSITION_NAME_2, SUBJECT_POSITIONAL_NODE);
    public static final Attribute TEST_POS_BAR_PNODE = new AttributeImpl(POSITION_NAME_4, PREDICATE_POSITIONAL_NODE);
    public static final Attribute TEST_POS_BAR_ONODE = new AttributeImpl(POSITION_NAME_5, OBJECT_POSITIONAL_NODE);

    protected void setUp() throws Exception {
        super.setUp();
        attComparator = TestJRDFFactory.getFactory().getNewAttributeComparator();
    }

    public void testNullPointerException() {
        checkNullPointerException(attComparator, AttributeImplUnitTest.TEST_ATTRIBUTE_FOO_POS, null);
        checkNullPointerException(attComparator, null, AttributeImplUnitTest.TEST_ATTRIBUTE_FOO_POS);
    }

    public void testIdentity() {
        assertEquals(EQUAL, attComparator.compare(TEST_VAR_BAR_BNODE, TEST_VAR_BAR_BNODE));
    }

    public void testAttributeNameTypeOrder() {
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_BNODE, TEST_VAR_FOO_BNODE));
        assertEquals(BEFORE, attComparator.compare(TEST_POS_BAR_BNODE, TEST_VAR_BAR_BNODE));
        assertEquals(BEFORE, attComparator.compare(TEST_POS_FOO_SNODE, TEST_VAR_BAR_SNODE));
        assertEquals(BEFORE, attComparator.compare(TEST_POS_BAR_BNODE, TEST_POS_FOO_BNODE));
    }

    public void testAttributeNameTypeAntiCommutation() {
        assertEquals(AFTER, attComparator.compare(TEST_VAR_FOO_BNODE, TEST_VAR_BAR_BNODE));
        assertEquals(AFTER, attComparator.compare(TEST_VAR_BAR_BNODE, TEST_POS_BAR_BNODE));
        assertEquals(AFTER, attComparator.compare(TEST_VAR_BAR_SNODE, TEST_POS_FOO_SNODE));
        assertEquals(AFTER, attComparator.compare(TEST_POS_FOO_BNODE, TEST_POS_BAR_BNODE));
    }

    public void testNodeTypeOrder() {
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_BNODE, TEST_VAR_BAR_URI_REF));
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_BNODE, TEST_VAR_BAR_LITERAL));
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_URI_REF, TEST_VAR_BAR_LITERAL));
    }

    public void testNodeTypeOrderAntiCommutation() {
        assertEquals(AFTER, attComparator.compare(TEST_VAR_BAR_URI_REF, TEST_VAR_BAR_BNODE));
        assertEquals(AFTER, attComparator.compare(TEST_VAR_BAR_LITERAL, TEST_VAR_BAR_BNODE));
        assertEquals(AFTER, attComparator.compare(TEST_VAR_BAR_LITERAL, TEST_VAR_BAR_URI_REF));
    }

    public void testPositionalNodeOrder() {
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_SNODE, TEST_VAR_BAR_PNODE));
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_SNODE, TEST_VAR_BAR_ONODE));
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_PNODE, TEST_VAR_BAR_ONODE));
    }

    public void testPositionalNodeOrderAntiCommutation() {
        assertEquals(AFTER, attComparator.compare(TEST_VAR_BAR_PNODE, TEST_VAR_BAR_SNODE));
        assertEquals(AFTER, attComparator.compare(TEST_VAR_BAR_ONODE, TEST_VAR_BAR_SNODE));
        assertEquals(AFTER, attComparator.compare(TEST_VAR_BAR_ONODE, TEST_VAR_BAR_PNODE));
    }

    public void testNodeTypeAndPositionalTypeOrder() {
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_BNODE, TEST_VAR_BAR_SNODE));
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_URI_REF, TEST_VAR_BAR_PNODE));
        assertEquals(BEFORE, attComparator.compare(TEST_VAR_BAR_LITERAL, TEST_VAR_BAR_ONODE));
    }

    public void testNodeTypeAndPositionalTypeOrderAntiCommutation() {
        assertEquals(AFTER, attComparator.compare(TEST_VAR_BAR_SNODE, TEST_VAR_BAR_BNODE));
        assertEquals(AFTER, attComparator.compare(TEST_VAR_BAR_PNODE, TEST_VAR_BAR_URI_REF));
        assertEquals(AFTER, attComparator.compare(TEST_VAR_BAR_ONODE, TEST_VAR_BAR_LITERAL));
    }

    public void testSameAttributeNameDifferentNodeType() {
        assertEquals(BEFORE, attComparator.compare(TEST_POS_BAR_SNODE, TEST_POS_BAR_PNODE));
        assertEquals(BEFORE, attComparator.compare(TEST_POS_BAR_SNODE, TEST_POS_BAR_ONODE));
        assertEquals(BEFORE, attComparator.compare(TEST_POS_BAR_PNODE, TEST_POS_BAR_ONODE));
    }

    public void testSameAttributeNameDifferentNodeTypeAntiCommutation() {
        assertEquals(AFTER, attComparator.compare(TEST_POS_BAR_PNODE, TEST_POS_BAR_SNODE));
        assertEquals(AFTER, attComparator.compare(TEST_POS_BAR_ONODE, TEST_POS_BAR_PNODE));
        assertEquals(AFTER, attComparator.compare(TEST_POS_BAR_ONODE, TEST_POS_BAR_SNODE));
    }

    // TODO (AN) Duplication with other comparator tests
    private void checkNullPointerException(final AttributeComparator attComparator, final Attribute att,
                                           final Attribute att2) {
        AssertThrows.assertThrows(NullPointerException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                //noinspection unchecked
                attComparator.compare(att, att2);
            }
        });
    }
}
