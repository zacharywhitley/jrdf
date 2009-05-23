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

package org.jrdf.graph;

import static org.hamcrest.CoreMatchers.is;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import org.jrdf.graph.local.ReadWriteGraph;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.assertThrows;
import static org.jrdf.util.test.matcher.ReadWriteGraphContainsMatcher.containsTriple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import static java.net.URI.create;
import java.util.NoSuchElementException;

public abstract class AbstractResourceUnitTest {
    private GraphElementFactory elementFactory;
    private ReadWriteGraph readWriteGraph;
    private PredicateNode predicate1;
    private PredicateNode predicate2;
    private PredicateNode predicate3;
    private ObjectNode object1;
    private ObjectNode object2;
    private ObjectNode object3;
    private Resource blankNode1;
    private Resource blankNode2;
    private Resource blankNode3;
    private Resource blankNode4;
    private Resource uriRef1;

    public abstract ReadWriteGraph getGraph();

    public abstract GraphElementFactory getElementFactory();

    public abstract Resource createBlankNodeResource(ReadWriteGraph graph, GraphElementFactory factory);

    public abstract Resource createURIReferenceResource(ReadWriteGraph graph, GraphElementFactory factory, URI uri);

    @Before
    public void setUp() {
        elementFactory = getElementFactory();
        readWriteGraph = getGraph();
        readWriteGraph.clear();
        URI uri1 = URI.create("http://namespace#pred1");
        URI uri2 = URI.create("http://namespace#pred2");
        URI uri3 = URI.create("http://namespace#pred3");
        predicate1 = elementFactory.createURIReference(uri1);
        predicate2 = elementFactory.createURIReference(uri2);
        predicate3 = elementFactory.createURIReference(uri3);
        object1 = elementFactory.createLiteral("SomeValue1");
        object2 = elementFactory.createLiteral("SomeValue2");
        object3 = elementFactory.createLiteral("SomeValue3");
        blankNode1 = createBlankNodeResource(readWriteGraph, elementFactory);
        blankNode2 = createBlankNodeResource(readWriteGraph, elementFactory);
        blankNode3 = createBlankNodeResource(readWriteGraph, elementFactory);
        blankNode4 = createBlankNodeResource(readWriteGraph, elementFactory);
        uriRef1 = createURIReferenceResource(readWriteGraph, elementFactory, uri1);
    }

    @Test
    public void blankNodeResourcesAreNotURIReferences() {
        assertThat(blankNode1.isURIReference(), is(false));
        assertThat(uriRef1.isURIReference(), is(true));
    }

    @Test
    public void testBlankNodeAddValue() {
        checkAddValue(blankNode1, predicate1, object1);
    }

    @Test
    public void testURIReferenceAddValue() {
        checkAddValue(uriRef1, predicate1, object1);
    }

    @Test
    public void testBlankNodeMultipleAddValue() {
        blankNode1.addValue(predicate1, object1);
        checkAddValue(blankNode1, predicate1, object1);
    }

    @Test
    public void testURIReferenceMultipleAddValue() {
        uriRef1.addValue(predicate1, object1);
        checkAddValue(uriRef1, predicate1, object1);
    }

    private void checkAddValue(Resource node, PredicateNode predicate, ObjectNode object) throws GraphException {
        node.addValue(predicate, object);
        assertThat(readWriteGraph, containsTriple(node, predicate, object));
        assertThat(readWriteGraph.getSize(), is(1L));
    }

    @Test
    public void testAddValue() {
        blankNode1.addValue(predicate1, object2);
        blankNode2.addValue(predicate1, object2);
        blankNode3.addValue(predicate2, object3);
        blankNode4.addValue(predicate3, object3);
        assertThat(readWriteGraph.getSize(), is(4L));
    }

    @Test
    public void testAddValueWithResource() {
        blankNode1.addValue(URI.create("urn:foo"), blankNode2);
        Resource resource = elementFactory.createResource(URI.create("urn:bar"));
        blankNode1.addValue(URI.create("urn:foo"), resource);
        resource.addValue(URI.create("urn:foo"), resource);
    }

    @Test
    public void testBlankNodeOverrideSetValue() {
        checkSetValueOverride(blankNode1, predicate1, new ObjectNode[]{object1, object2, object3});
    }

    @Test
    public void testURIReferenceOverrideSetValue() {
        checkSetValueOverride(uriRef1, predicate2, new ObjectNode[]{object1, object2, object3});
    }

    private void checkSetValueOverride(Resource node, PredicateNode predicate, ObjectNode[] objects)
        throws GraphException {
        node.addValue(predicate, objects[0]);
        node.addValue(predicate, objects[1]);
        node.setValue(predicate, objects[2]);
        assertEquals("Size should be 1 but I got " + readWriteGraph.getSize(), 1, readWriteGraph.getSize());
        assertFalse(readWriteGraph.contains(node, predicate, objects[0]));
        assertFalse(readWriteGraph.contains(node, predicate, objects[1]));
        assertTrue(readWriteGraph.contains(node, predicate, objects[2]));
    }

    @Test
    public void testRemoveValue() {
        blankNode1.addValue(predicate1, object1);
        blankNode1.addValue(predicate2, object2);
        checkRemovedSuccessfully(blankNode1, predicate1, object1, 1);
        checkRemovedSuccessfully(blankNode1, predicate2, object2, 0);
    }

    private void checkRemovedSuccessfully(Resource node, PredicateNode predicate, ObjectNode object,
        int expectedSize) throws GraphException {
        node.removeValue(predicate, object);
        assertFalse(readWriteGraph.contains(node, predicate, object));
        assertEquals("Size should be " + expectedSize + " but I got " + readWriteGraph.getSize(), expectedSize,
            readWriteGraph.getSize());
    }

    @Test
    public void testRemoveValues() {
        addThreeDifferentObjectsToPredicate(predicate1);
        addThreeDifferentObjectsToPredicate(predicate2);
        addThreeDifferentObjectsToPredicate(predicate3);
        checkRemoveAllFromPredicate(6, predicate1);
        checkRemoveAllFromPredicate(3, predicate3);
        checkRemoveAllFromPredicate(0, predicate2);
    }

    private void addThreeDifferentObjectsToPredicate(PredicateNode predicate) throws GraphException {
        blankNode1.addValue(predicate, object1);
        blankNode1.addValue(predicate, object2);
        blankNode1.addValue(predicate, object3);
    }

    private void checkRemoveAllFromPredicate(int expectedValue, PredicateNode predicate) throws GraphException {
        blankNode1.removeValues(predicate);
        assertEquals("Size should be " + expectedValue + "  but I got " + readWriteGraph.getSize(), expectedValue,
            readWriteGraph.getSize());
    }

    @Test
    public void testRemoveSubject() {
        blankNode1.addValue(predicate1, uriRef1);
        blankNode1.addValue(predicate1, blankNode2);
        blankNode2.removeSubject(blankNode1, predicate1);
        assertFalse(readWriteGraph.contains(blankNode1, predicate1, blankNode2));
        uriRef1.removeSubject(blankNode1, predicate1);
        assertFalse(readWriteGraph.contains(blankNode1, predicate1, uriRef1));
    }

    @Test
    public void testGetObjects() {
        addThreeDifferentObjectsToPredicate(predicate1);
        addThreeDifferentObjectsToPredicate(predicate2);
        ClosableIterator<ObjectNode> itr = blankNode1.getObjects(predicate1);
        int objects = 0;
        while (itr.hasNext()) {
            itr.next();
            objects++;
        }
        assertEquals("There should be only 3 objects", 3, objects);
        ClosableIterator<ObjectNode> itr2 = blankNode1.getObjects(predicate2);
        assertTrue(readWriteGraph.contains(blankNode1, predicate2, itr2.next()));
        assertTrue(readWriteGraph.contains(blankNode1, predicate2, itr2.next()));
        assertTrue(readWriteGraph.contains(blankNode1, predicate2, itr2.next()));
        assertFalse("There should only be 3 items in the graph with the given blank node and predicate",
            itr2.hasNext());
    }

    @Test
    public void testGetSubjects() {
        Resource object = createBlankNodeResource(readWriteGraph, elementFactory);
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
            assertTrue(readWriteGraph.contains(itr2.next(), ANY_PREDICATE_NODE, ANY_OBJECT_NODE));
        }
        assertThrows(NoSuchElementException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                readWriteGraph.contains(itr2.next(), ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
            }
        });
    }

    @Test
    public void testContainsTriple() throws GraphException {
        Resource subject = uriRef1;
        subject.addValue(predicate1, object1);
        assertTrue("Contains p1, o1", subject.containsTriple(predicate1, object1));
        assertFalse("Doesn't contain p1, o2", subject.containsTriple(predicate1, object2));
        assertFalse("Doesn't contain p2, o1", subject.containsTriple(predicate2, object1));
        assertFalse("Doesn't contain p2, o2", subject.containsTriple(predicate2, object2));
    }

    @Test
    public void testResourceUsage() {
        final Resource supplier = elementFactory.createResource();
        final URIReference sno = elementFactory.createURIReference(create("urn:sno"));
        supplier.addValue(sno, elementFactory.createLiteral("sno"));
        supplier.addValue(sno, elementFactory.createLiteral(20));
        assertEquals(2, readWriteGraph.getSize());
        final Triple triple = new TripleImpl((SubjectNode) supplier.getUnderlyingNode(), sno,
            elementFactory.createLiteral("sno"));
        assertEquals(true, readWriteGraph.contains(triple.getSubject(), triple.getPredicate(),
                triple.getObject()));
    }
}
