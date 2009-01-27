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

import junit.framework.TestCase;
import static org.jrdf.graph.AnyObjectNode.*;
import static org.jrdf.graph.AnyPredicateNode.*;
import org.jrdf.graph.local.ReadWriteGraph;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.*;

import java.net.URI;
import java.util.NoSuchElementException;

public abstract class AbstractResourceUnitTest extends TestCase {
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

    public abstract Resource createBlankNodeResource(ReadWriteGraph graph, GraphElementFactory factory)
        throws Exception;

    public abstract Resource createURIReferenceResource(ReadWriteGraph graph, GraphElementFactory factory, URI uri)
        throws Exception;

    public void setUp() throws Exception {
        elementFactory = getElementFactory();
        readWriteGraph = getGraph();
        readWriteGraph.clear();
        URI uri1 = new URI("http://namespace#pred1");
        URI uri2 = new URI("http://namespace#pred2");
        URI uri3 = new URI("http://namespace#pred3");
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
        assertTrue("Cannot find triple with the BlankNode", readWriteGraph.contains(node, predicate, object));
        assertEquals("Size should be 1 but I got " + readWriteGraph.getSize(), 1, readWriteGraph.getSize());
    }

    public void testAddValue() throws Exception {
        blankNode1.addValue(predicate1, object2);
        blankNode2.addValue(predicate1, object2);
        blankNode3.addValue(predicate2, object3);
        blankNode4.addValue(predicate3, object3);
        assertTrue("Size should be 4 but I got " + readWriteGraph.getSize(), readWriteGraph.getSize() == 4);
    }

    public void testAddValueWithResource() throws Exception {
        blankNode1.addValue(URI.create("urn:foo"), blankNode2);
        Resource resource = elementFactory.createResource(URI.create("urn:bar"));
        blankNode1.addValue(URI.create("urn:foo"), resource);
        resource.addValue(URI.create("urn:foo"), resource);
    }

    public void testBlankNodeOverrideSetValue() throws Exception {
        checkSetValueOverride(blankNode1, predicate1, new ObjectNode[]{object1, object2, object3});
    }

    public void testURIReferenceOverrideSetValue() throws Exception {
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

    public void testRemoveValue() throws Exception {
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

    public void testRemoveValues() throws Exception {
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

    public void testRemoveSubject() throws Exception {
        blankNode1.addValue(predicate1, uriRef1);
        blankNode1.addValue(predicate1, blankNode2);
        blankNode2.removeSubject(blankNode1, predicate1);
        assertFalse(readWriteGraph.contains(blankNode1, predicate1, blankNode2));
        uriRef1.removeSubject(blankNode1, predicate1);
        assertFalse(readWriteGraph.contains(blankNode1, predicate1, uriRef1));
    }

    public void testGetObjects() throws Exception {
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

    public void testGetSubjects() throws Exception {
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

    public void testContainsTriple() throws GraphException {
        Resource subject = uriRef1;
        subject.addValue(predicate1, object1);
        assertTrue("Contains p1, o1", subject.containsTriple(predicate1, object1));
        assertFalse("Doesn't contain p1, o2", subject.containsTriple(predicate1, object2));
        assertFalse("Doesn't contain p2, o1", subject.containsTriple(predicate2, object1));
        assertFalse("Doesn't contain p2, o2", subject.containsTriple(predicate2, object2));
    }
}
