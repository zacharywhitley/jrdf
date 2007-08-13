/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph;

import junit.framework.TestCase;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import org.jrdf.graph.mem.BlankNodeResourceImpl;
import org.jrdf.graph.mem.ImmutableGraph;
import org.jrdf.graph.mem.MutableGraph;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.assertThrows;

import java.net.URI;
import java.util.NoSuchElementException;

public abstract class AbstractResourceUnitTest extends TestCase {
    private ImmutableGraph immutableGraph;
    private GraphElementFactory elementFactory;
    private MutableGraph mutableGraph;
    private PredicateNode predicate1;
    private PredicateNode predicate2;
    private PredicateNode predicate3;
    private ObjectNode object1;
    private ObjectNode object2;
    private ObjectNode object3;
    private Resource blankNode1;
    private Resource uriRef1;

    public abstract ImmutableGraph getImmutableGraph();
    public abstract MutableGraph getMutableGraph();
    public abstract GraphElementFactory getElementFactory();
    public abstract Resource createBlankNodeResource(BlankNode node, MutableGraph mutableGraph,
        ImmutableGraph immutableGraph);
    public abstract Resource createURIReferenceResource(URIReference node, MutableGraph mutableGraph,
        ImmutableGraph immutableGraph);

    public void setUp() throws Exception {
        immutableGraph = getImmutableGraph();
        elementFactory = getElementFactory();
        mutableGraph = getMutableGraph();
        mutableGraph.clear();
        URI uri1 = new URI("http://namespace#pred1");
        URI uri2 = new URI("http://namespace#pred2");
        URI uri3 = new URI("http://namespace#pred3");
        predicate1 = elementFactory.createURIReference(uri1);
        predicate2 = elementFactory.createURIReference(uri2);
        predicate3 = elementFactory.createURIReference(uri3);
        object1 = elementFactory.createLiteral("SomeValue1");
        object2 = elementFactory.createLiteral("SomeValue2");
        object3 = elementFactory.createLiteral("SomeValue3");
        blankNode1 = createBlankNodeResource(elementFactory.createBlankNode(), mutableGraph, immutableGraph);
        uriRef1 = createURIReferenceResource(elementFactory.createURIReference(uri1), mutableGraph, immutableGraph);
    }

    public void testCreateResource() throws Exception {
        assertFalse("I did not get a BlankNode :( ", blankNode1.isURIReference());
        assertTrue("I did not get URIReference :( ", uriRef1.isURIReference());
    }

    public void testBlankNodeAddValue() throws Exception {
        checkAddValue(blankNode1, predicate1, object1);
    }

    public void testURIReferenceAddValue() throws Exception {
        checkAddValue(uriRef1, predicate1, object1);
    }

    public void testBlankNodeMultipleAddValue() throws Exception {
        blankNode1.addValue(predicate1, object1);
        checkAddValue(blankNode1, predicate1, object1);
    }

    public void testURIReferenceMultipleAddValue() throws Exception {
        uriRef1.addValue(predicate1, object1);
        checkAddValue(uriRef1, predicate1, object1);
    }

    private void checkAddValue(Resource node, PredicateNode predicate, ObjectNode object) throws GraphException {
        node.addValue(predicate, object);
        assertTrue("Cannot find triple with the BlankNode", immutableGraph.contains(node, predicate, object));
        assertEquals("Size should be 1 but I got " + immutableGraph.getSize(), 1, immutableGraph.getSize());
    }

    public void testAddValue() throws Exception {
        Resource blankNode2 = createBlankNodeResource(elementFactory.createBlankNode(), mutableGraph, immutableGraph);
        Resource blankNode3 = createBlankNodeResource(elementFactory.createBlankNode(), mutableGraph, immutableGraph);
        Resource blankNode4 = createBlankNodeResource(elementFactory.createBlankNode(), mutableGraph, immutableGraph);
        blankNode1.addValue(predicate1, object2);
        blankNode2.addValue(predicate1, object2);
        blankNode3.addValue(predicate2, object3);
        blankNode4.addValue(predicate3, object3);
        assertTrue("Size should be 4 but I got " + immutableGraph.getSize(), immutableGraph.getSize() == 4);
    }

    public void testBlankNodeOverrideSetValue() throws Exception {
        checkSetValueOverride(blankNode1, predicate1, new ObjectNode[] {object1, object2, object3});
    }

    public void testURIReferenceOverrideSetValue() throws Exception {
        checkSetValueOverride(uriRef1, predicate2, new ObjectNode[] {object1, object2, object3});
    }

    private void checkSetValueOverride(Resource node, PredicateNode predicate, ObjectNode[] objects) throws GraphException {
        node.addValue(predicate, objects[0]);
        node.addValue(predicate, objects[1]);
        node.setValue(predicate, objects[2]);
        assertEquals("Size should be 1 but I got " + immutableGraph.getSize(), 1, immutableGraph.getSize());
        assertFalse(immutableGraph.contains(node, predicate, objects[0]));
        assertFalse(immutableGraph.contains(node, predicate, objects[1]));
        assertTrue(immutableGraph.contains(node, predicate, objects[2]));
    }

    public void testRemoveValue() throws Exception {
        blankNode1.addValue(predicate1, object1);
        blankNode1.addValue(predicate2, object2);
        assertTrue(immutableGraph.contains(blankNode1, predicate1, object1));
        assertTrue(immutableGraph.contains(blankNode1, predicate2, object2));
        assertEquals("Size should be 2 but I got " + immutableGraph.getSize(), 2, immutableGraph.getSize());

        blankNode1.removeValue(predicate1, object1);
        assertFalse(immutableGraph.contains(blankNode1, predicate1, object1));
        assertEquals("Size should be 1 but I got " + immutableGraph.getSize(), 1, immutableGraph.getSize());
        blankNode1.removeValue(predicate2, object2);
        assertFalse(immutableGraph.contains(blankNode1, predicate2, object2));
        assertEquals("Size should be 0 but I got " + immutableGraph.getSize(), 0, immutableGraph.getSize());
    }

    public void testRemoveValues() throws Exception {
        addThreeDifferentObjects(predicate1, 3);
        addThreeDifferentObjects(predicate2, 6);
        addThreeDifferentObjects(predicate3, 9);
        checkRemoveAllFromPredicate(6, predicate1);
        checkRemoveAllFromPredicate(3, predicate3);
        checkRemoveAllFromPredicate(0, predicate2);
    }

    private void addThreeDifferentObjects(PredicateNode predicate, int expectedSize) throws GraphException {
        blankNode1.addValue(predicate, object1);
        blankNode1.addValue(predicate, object2);
        blankNode1.addValue(predicate, object3);
        assertTrue(immutableGraph.contains(blankNode1, predicate, object1));
        assertTrue(immutableGraph.contains(blankNode1, predicate, object2));
        assertTrue(immutableGraph.contains(blankNode1, predicate, object3));
        assertEquals("Size should be " + expectedSize + " but I got " + immutableGraph.getSize(), expectedSize,
            immutableGraph.getSize());
    }

    private void checkRemoveAllFromPredicate(int expectedValue, PredicateNode predicate) throws GraphException {
        blankNode1.removeValues(predicate);
        assertEquals("Size should be " + expectedValue + "  but I got " + immutableGraph.getSize(), expectedValue,
            immutableGraph.getSize());
    }

    public void testRemoveSubject() throws Exception {
        Resource blankNode2 = createBlankNodeResource(elementFactory.createBlankNode(), mutableGraph, immutableGraph);
        blankNode1.addValue(predicate1, uriRef1);
        blankNode1.addValue(predicate1, blankNode2);
        assertTrue(immutableGraph.contains(blankNode1, predicate1, uriRef1));
        assertTrue(immutableGraph.contains(blankNode1, predicate1, blankNode2));
        blankNode2.removeSubject(blankNode1, predicate1);
        assertFalse(immutableGraph.contains(blankNode1, predicate1, blankNode2));
        uriRef1.removeSubject(blankNode1, predicate1);
        assertFalse(immutableGraph.contains(blankNode1, predicate1, uriRef1));
    }

    public void testGetObjects() throws Exception {
        blankNode1.addValue(predicate1, object1);
        blankNode1.addValue(predicate1, object2);
        blankNode1.addValue(predicate1, object3);
        blankNode1.addValue(predicate2, object1);
        blankNode1.addValue(predicate2, object2);
        ClosableIterator<ObjectNode> itr = blankNode1.getObjects(predicate1);
        int objects = 0;
        while (itr.hasNext()) {
            itr.next();
            objects++;
        }
        assertEquals("There should be only 3 objects", 3, objects);
        ClosableIterator<ObjectNode> itr2 = blankNode1.getObjects(predicate2);
        assertTrue(immutableGraph.contains(blankNode1, predicate2, itr2.next()));
        assertTrue(immutableGraph.contains(blankNode1, predicate2, itr2.next()));
    }

    public void testGetSubjects () throws Exception {
        Resource blankNode2 = createBlankNodeResource(elementFactory.createBlankNode(), mutableGraph, immutableGraph);
        Resource blankNode3 = createBlankNodeResource(elementFactory.createBlankNode(), mutableGraph, immutableGraph);
        Resource object = new BlankNodeResourceImpl(elementFactory.createBlankNode(), mutableGraph, immutableGraph);
        blankNode1.addValue(predicate1, object);
        blankNode2.addValue(predicate1, object);
        blankNode3.addValue(predicate1, object);
        uriRef1.addValue(predicate1, object);
        ClosableIterator<SubjectNode> itr = object.getSubjects(predicate1);
        int objects = 0;
        while (itr.hasNext()) {
            itr.next();
            objects++;
        }
        final ClosableIterator<SubjectNode> itr2 = object.getSubjects(predicate1);
        assertEquals("There should be 4 subjects in the iterator", 4, objects);
        for (int i = 0; i < 4; i++) {
            assertTrue(immutableGraph.contains(itr2.next(), ANY_PREDICATE_NODE, ANY_OBJECT_NODE));
        }
        assertThrows(NoSuchElementException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                immutableGraph.contains(itr2.next(), ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
            }
        });
    }
}
