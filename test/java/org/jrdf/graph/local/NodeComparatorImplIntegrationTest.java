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
package org.jrdf.graph.local;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.datatype.SemanticLiteralComparator;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.ComparatorTestUtil;
import org.jrdf.vocabulary.RDF;

import java.lang.reflect.Modifier;
import java.util.Comparator;

/**
 * Test for the implementation of NodeComparatorImpl.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class NodeComparatorImplIntegrationTest extends TestCase {
    public static final URIReference URI_1 = new URIReferenceImpl(RDF.ALT, 1L);
    public static final URIReference URI_2 = new URIReferenceImpl(RDF.BAG, 2L);
    public static final BlankNode BNODE_1 = new BlankNodeImpl("a", 1L);
    public static final BlankNode BNODE_2 = new BlankNodeImpl("b", 2L);
    public static final Literal LITERAL_1 = new LiteralImpl("bar");
    public static final Literal LITERAL_2 = new LiteralImpl("foo");
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final int EQUAL = 0;
    private static final int BEFORE = -1;
    private static final int AFTER = 1;
    private NodeComparator nodeComparator;

    protected void setUp() throws Exception {
        super.setUp();
        nodeComparator = FACTORY.getNewNodeComparator();
    }

    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(NodeComparator.class, NodeComparatorImpl.class);
        ClassPropertiesTestUtil.checkExtensionOf(Comparator.class, NodeComparator.class);
        ClassPropertiesTestUtil.checkConstructor(NodeComparatorImpl.class, Modifier.PUBLIC, NodeTypeComparator.class,
            SemanticLiteralComparator.class, BlankNodeComparator.class);
    }

    public void testNullPointerException() {
        ComparatorTestUtil.checkNullPointerException(nodeComparator, URI_1, null);
        ComparatorTestUtil.checkNullPointerException(nodeComparator, null, URI_1);
    }

    public void testIdentity() {
        assertEquals(EQUAL, nodeComparator.compare(ANY_SUBJECT_NODE, ANY_SUBJECT_NODE));
        assertEquals(EQUAL, nodeComparator.compare(URI_1, URI_1));
    }

    public void testNodeTypeOrder() {
        assertEquals(BEFORE, nodeComparator.compare(BNODE_1, URI_1));
        assertEquals(BEFORE, nodeComparator.compare(BNODE_1, LITERAL_1));
        assertEquals(BEFORE, nodeComparator.compare(URI_1, LITERAL_1));
    }

    public void testNodeTypeAntiCommutation() {
        assertEquals(AFTER, nodeComparator.compare(URI_1, BNODE_1));
        assertEquals(AFTER, nodeComparator.compare(LITERAL_1, BNODE_1));
        assertEquals(AFTER, nodeComparator.compare(LITERAL_1, URI_1));
    }

    public void testBlankNodeComparison() {
        assertEquals(BEFORE, nodeComparator.compare(BNODE_1, BNODE_2));
        assertEquals(AFTER, nodeComparator.compare(BNODE_2, BNODE_1));
        assertEquals(EQUAL, nodeComparator.compare(BNODE_2, new BlankNodeImpl("b", 2L)));
    }

    public void testURIComparisonByString() {
        assertEquals(BEFORE, nodeComparator.compare(URI_1, URI_2));
        assertEquals(AFTER, nodeComparator.compare(URI_2, URI_1));
        assertEquals(EQUAL, nodeComparator.compare(URI_2, new URIReferenceImpl(RDF.BAG, 2L)));
    }

    public void testLiteralComparisonByString() {
        assertTrue(nodeComparator.compare(LITERAL_1, LITERAL_2) <= BEFORE);
        assertTrue(nodeComparator.compare(LITERAL_2, LITERAL_1) >= AFTER);
        assertEquals(EQUAL, nodeComparator.compare(LITERAL_2, new LiteralImpl("foo")));
    }
}
