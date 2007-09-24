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

package org.jrdf.graph.global.molecule;

import junit.framework.TestCase;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.BlankNodeImpl;
import org.jrdf.graph.global.GlobalizedBlankNodeComparatorImpl;
import org.jrdf.graph.global.GroundedTripleComparatorImpl;
import org.jrdf.graph.global.LiteralImpl;
import org.jrdf.graph.global.TripleImpl;
import org.jrdf.graph.global.URIReferenceImpl;
import org.jrdf.graph.local.mem.BlankNodeComparator;
import org.jrdf.graph.local.mem.NodeComparatorImpl;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MoleculeImplUnitTest extends TestCase {
    private BlankNodeComparator blankNodeComparator = new GlobalizedBlankNodeComparatorImpl();
    private NodeComparator nodeComparator = new NodeComparatorImpl(new NodeTypeComparatorImpl(), blankNodeComparator);
    private TripleComparator tripleComparator = new GroundedTripleComparatorImpl(nodeComparator);
    private MoleculeFactory moleculeFactory = new MoleculeFactoryImpl(tripleComparator);
    private URIReference ref1;
    private URIReference ref2;
    private URIReference ref3;
    private BlankNode blankNode1;
    private BlankNode blankNode2;
    private Molecule molecule1;
    private Molecule molecule2;

    public void setUp() {
        ref1 = new URIReferenceImpl(URI.create("urn:foo"));
        ref2 = new URIReferenceImpl(URI.create("urn:bar"));
        blankNode1 = new BlankNodeImpl();
        blankNode2 = new BlankNodeImpl();
    }

//    public void testMoleculeOrderURIReference() throws Exception {
//        Molecule molecule1 = new MoleculeImpl(tripleComparator);
//        ref1 = new URIReferenceImpl(URI.create("urn:foo"));
//        ref2 = new URIReferenceImpl(URI.create("urn:bar"));
//        Triple triple = new TripleImpl(ref1, ref1, ref1);
//        molecule1.add(triple);
//        molecule1.add(new TripleImpl(ref1, ref1, ref2));
//        assertEquals(triple, molecule1.getHeadTriple());
//    }
//
//    public void testMoleculeOrderBlankNodes() throws Exception {
//        Molecule molecule1 = new MoleculeImpl(tripleComparator);
//        ref1 = new URIReferenceImpl(URI.create("urn:foo"));
//        ref2 = new URIReferenceImpl(URI.create("urn:bar"));
//        BlankNode blankNode = new BlankNodeImpl();
//
//        Triple triple = new TripleImpl(blankNode, ref1, ref1);
//        molecule1.add(new TripleImpl(blankNode, ref1, ref2));
//        molecule1.add(triple);
//        molecule1.add(new TripleImpl(blankNode, ref1, blankNode));
//        assertEquals(triple, molecule1.getHeadTriple());
//    }

    public void testEquals() throws Exception {
        Triple blank1ObjectRef1 = new TripleImpl(ref1, ref1, blankNode1);
        Triple blank1SubjectRef1 = new TripleImpl(blankNode1, ref1, ref1);
        Triple blank2to1Ref1 = new TripleImpl(blankNode2, ref1, blankNode1);
        Triple blank2Ref2Ref1 = new TripleImpl(blankNode2, ref2, ref1);
        molecule1 = moleculeFactory.createMolecule(blank1ObjectRef1, blank1SubjectRef1, blank2to1Ref1, blank2Ref2Ref1);
        molecule2 = moleculeFactory.createMolecule(blank2Ref2Ref1, blank2to1Ref1 , blank1ObjectRef1, blank1SubjectRef1);
        assertEquals(molecule1, molecule2);
    }

    public void testAddAMolecule() throws Exception {
        ref1 = new URIReferenceImpl(URI.create("urn:foo"));
        ref2 = new URIReferenceImpl(URI.create("urn:bar"));
        BlankNode blankNode = new BlankNodeImpl();
        Triple triple = new TripleImpl(ref1, ref1, blankNode);
        molecule1 = moleculeFactory.createMolecule(triple);
        assertEquals(triple, molecule1.getHeadTriple());
        assertEquals(1, molecule1.size());
    }

    public void testAddAMoleculeSecondConstructor() throws Exception {
        ref1 = new URIReferenceImpl(URI.create("urn:foo"));
        ref2 = new URIReferenceImpl(URI.create("urn:bar"));
        BlankNode blankNode = new BlankNodeImpl();
        Triple headTriple = new TripleImpl(ref1, ref1, blankNode);
        SortedSet<Triple> treeSet = new TreeSet<Triple>(tripleComparator);
        treeSet.add(headTriple);
        Molecule molecule = moleculeFactory.createMolecule(treeSet);
        assertEquals(0, molecule.getTailTriples().size());
        assertNotNull(molecule.getHeadTriple());
    }


    public void testAddDuplicateTripleMolecule() throws Exception {
        addMultiLevelTriples();
        Triple headTriple = molecule1.getHeadTriple();
        Triple triple = new TripleImpl(ref3, ref1, (ObjectNode) headTriple.getSubject());
        molecule1.add(triple);
        assertEquals(6, molecule1.getTailTriples().size());
        molecule1.add(triple);
        assertEquals(6, molecule1.getTailTriples().size());
    }


    public void testAddMultiLevelObjectBlank() throws Exception {
        ref1 = new URIReferenceImpl(URI.create("urn:foo"));
        ref2 = new URIReferenceImpl(URI.create("urn:bar"));
        BlankNode blankNode = new BlankNodeImpl();
        Triple triple = new TripleImpl(ref1, ref1, blankNode);

        Molecule molecule = moleculeFactory.createMolecule(triple);
        boolean b = molecule.add(new TripleImpl(blankNode, ref1, ref2));
        assertTrue(b);

        BlankNode blankNode2 = new BlankNodeImpl();
        b = molecule.add(new TripleImpl(blankNode, ref1, blankNode2));
        assertTrue(b);
        BlankNode blankNode3 = new BlankNodeImpl();
        b = molecule.add(new TripleImpl(blankNode2, ref1, new LiteralImpl("foo")));
        assertTrue(b);

        b = molecule.add(new TripleImpl(blankNode2, ref1, blankNode3));
        assertTrue(b);
        b = molecule.add(new TripleImpl(blankNode3, ref1, new LiteralImpl("foo")));
        assertTrue(b);

        assertEquals(triple, molecule.getHeadTriple());

        assertEquals(6, molecule.size());
    }


    public void testAddMultiLevelSubjectBlank() throws Exception {
        Triple triple = addMultiLevelTriples();

        assertEquals(triple, molecule1.getHeadTriple());

        assertEquals(6, molecule1.size());
    }

    public void testGetTailTriples () throws Exception {
        addMultiLevelTriples();
        Set<Triple> tailTriples = molecule1.getTailTriples();
        assertEquals(5, tailTriples.size());
    }


    public void testGetIterator() throws Exception {
        addMultiLevelTriples();
        Iterator<Triple> tailTriples = molecule1.iterator();
        int counter = 0;
        while (tailTriples.hasNext()) {
            tailTriples.next();
            counter++;
        }

        assertEquals(6, counter);
    }

    public void testClear() throws Exception {
        addMultiLevelTriples();
        Set<Triple> tailTriples = molecule1.getTailTriples();
        assertEquals(5, tailTriples.size());

        molecule1.clear();
        assertEquals(0, molecule1.getTailTriples().size());

        assertNotNull(molecule1.getHeadTriple());
    }

    public void testRemove() throws Exception {
        addMultiLevelTriples();
        Set<Triple> tailTriples = molecule1.getTailTriples();
        assertEquals(5, tailTriples.size());


        Triple headTriple = molecule1.getHeadTriple();
        Triple tripleToremove = new TripleImpl(ref3, ref1, (ObjectNode) headTriple.getSubject());
        molecule1.add(tripleToremove);
        assertEquals(6, molecule1.getTailTriples().size());

        molecule1.remove(tripleToremove);

        assertEquals(5, molecule1.getTailTriples().size());
    }

    private Triple addMultiLevelTriples() throws MoleculeInsertionException {
        ref1 = new URIReferenceImpl(URI.create("urn:foo"));
        ref2 = new URIReferenceImpl(URI.create("urn:bar"));
        ref3 = new URIReferenceImpl(URI.create("urn:xyz"));
        BlankNode blankNode = new BlankNodeImpl();
        Triple triple = new TripleImpl(blankNode, ref1, ref1);
        molecule1 = moleculeFactory.createMolecule(triple);
        assertEquals(1, molecule1.size());
        assertTrue(molecule1.add(new TripleImpl(ref2, ref1, blankNode)));
        assertEquals(2, molecule1.size());
        BlankNode blankNode2 = new BlankNodeImpl();
        assertTrue(molecule1.add(new TripleImpl(blankNode, ref1, blankNode2)));
        assertEquals(3, molecule1.size());
        BlankNode blankNode3 = new BlankNodeImpl();
        assertTrue(molecule1.add(new TripleImpl(blankNode2, ref1, new LiteralImpl("foo"))));
        assertEquals(4, molecule1.size());
        assertTrue(molecule1.add(new TripleImpl(blankNode2, ref1, blankNode3)));
        assertEquals(5, molecule1.size());
        assertTrue(molecule1.add(new TripleImpl(blankNode3, ref1, new LiteralImpl("foo"))));
        assertEquals(6, molecule1.size());
        return triple;
    }


}
