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

package org.jrdf.graph.global.molecule.mem;

import junit.framework.TestCase;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.molecule.GlobalGraphTestUtil;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.MOLECULE_FACTORY;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.b1r2b2;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.b1r3r3;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.b2r2b1;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.r1r1b1;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.r1r1r1;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.r2r1r1;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.r3r1r1;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.MoleculeFactory;
import org.jrdf.graph.global.molecule.MoleculeSubsumption;
import org.jrdf.graph.local.TripleComparatorFactoryImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MoleculeImplUnitTest extends TestCase {
    private final TripleComparator comparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private final TripleComparator tripleComparator = new TripleComparatorFactoryImpl().newComparator();
    private final MoleculeComparator moleculeComparator = new MoleculeHeadTripleComparatorImpl(tripleComparator);
    private MoleculeFactory moleculeFactory = new MoleculeFactoryImpl(moleculeComparator);
    private MoleculeSubsumption subsumption = new MoleculeSubsumptionImpl();
    private MergeSubmoleculesImpl merger;

    public void setUp() {
        merger = new MergeSubmoleculesImpl(comparator, moleculeComparator, moleculeFactory, subsumption);
    }

    public void testMoleculeCreation() {
        Molecule newMolecule = MOLECULE_FACTORY.createMolecule(r1r1r1, r2r1r1, r3r1r1, r1r1b1);
        assertEquals(r1r1r1, newMolecule.getHeadTriple());
    }

    public void testMoleculeCreation2() {
        Molecule newMolecule = MOLECULE_FACTORY.createMolecule();
        newMolecule = newMolecule.add(r2r1r1);
        newMolecule = newMolecule.add(r3r1r1);
        newMolecule = newMolecule.add(r1r1r1);
        newMolecule = newMolecule.add(r1r1b1);
        assertEquals(r1r1r1, newMolecule.getHeadTriple());
    }

    public void testAddVsConstructor() {
        Molecule newMolecule1 = MOLECULE_FACTORY.createMolecule();
        newMolecule1.add(r1r1r1);
        newMolecule1.add(r2r1r1);
        newMolecule1.add(r3r1r1);
        newMolecule1.add(r1r1b1);
        Molecule newMolecule2 = MOLECULE_FACTORY.createMolecule(r1r1r1, r2r1r1, r3r1r1, r1r1b1);
        assertEquals(newMolecule1, newMolecule2);
    }

    public void testMergeHeadMolecules() {
        Molecule newMolecule = MOLECULE_FACTORY.createMolecule(b1r3r3);
        Molecule internalMolecule = MOLECULE_FACTORY.createMolecule(b1r3r3, b2r2b1);
        assertEquals(b1r3r3, newMolecule.getHeadTriple());
        assertEquals(b1r3r3, internalMolecule.getHeadTriple());
        newMolecule = newMolecule.add(merger, internalMolecule);
        assertEquals(b1r3r3, newMolecule.getHeadTriple());
        assertEquals(2, newMolecule.size());
        checkHasHeadMolecules(newMolecule, b2r2b1, b1r3r3);
    }

    public void testCombineSubMolecules() {
        Molecule newMolecule = MOLECULE_FACTORY.createMolecule(r1r1b1);
        Molecule internalMolecule = MOLECULE_FACTORY.createMolecule(b2r2b1, b1r3r3);
        newMolecule = newMolecule.add(merger, internalMolecule);
        assertEquals(r1r1b1, newMolecule.getHeadTriple());
        assertEquals(3, newMolecule.size());
        HashMap<Triple, Molecule> expectedSubMolecules = new HashMap<Triple, Molecule>();
        expectedSubMolecules.put(r1r1b1, internalMolecule);
        checkHasSubMolecule(newMolecule, expectedSubMolecules);
    }

    public void testAddSubMolecules() {
        Molecule internalMolecule = MOLECULE_FACTORY.createMolecule(r1r1r1);
        Molecule newMolecule = MOLECULE_FACTORY.createMolecule(b1r3r3, b1r2b2);
        newMolecule.add(b1r2b2, internalMolecule);
        Set<Triple> triples = newMolecule.getRootTriplesAsSet();
        assertTrue(triples.contains(b1r3r3));
        assertTrue(triples.contains(b1r2b2));
        Set<Molecule> molecules = newMolecule.getSubMolecules(b1r2b2);
        assertTrue(molecules.size() == 1);
        Set<Triple> triplesAsSet = molecules.iterator().next().getRootTriplesAsSet();
        assertTrue(triplesAsSet.size() == 1);
        assertTrue(triplesAsSet.contains((r1r1r1)));
    }

    public void testSimpleTopLevel() {
        Molecule molecule = MOLECULE_FACTORY.createMolecule(r1r1r1);
        Molecule newMolecule = MOLECULE_FACTORY.createMolecule(b1r3r3, b1r2b2);
        molecule.add(b1r2b2, newMolecule);
        assertTrue("Is top level", molecule.isTopLevelMolecule());
        assertTrue("Is top level", !newMolecule.isTopLevelMolecule());
    }

    public void testIterator() {
        Molecule newMolecule = MOLECULE_FACTORY.createMolecule();
        newMolecule = newMolecule.add(r2r1r1);
        newMolecule = newMolecule.add(r3r1r1);
        newMolecule = newMolecule.add(r1r1r1);
        newMolecule = newMolecule.add(r1r1b1);
        Set<Triple> set = new HashSet<Triple>();
        set.add(r2r1r1);
        set.add(r3r1r1);
        set.add(r1r1r1);
        set.add(r1r1b1);
        Iterator<Triple> iterator = newMolecule.iterator();
        int size = 0;
        while (iterator.hasNext()) {
            size++;
            Triple triple = iterator.next();
            assertTrue(set.contains(triple));
        }
        assertEquals(set.size(), size);
    }

    public void testFindTriple() {
        Molecule newMolecule = MOLECULE_FACTORY.createMolecule();
        Molecule m1 = MOLECULE_FACTORY.createMolecule(r3r1r1);
        newMolecule = newMolecule.add(r2r1r1, m1);
        Triple triple = r3r1r1;
        Iterator<Triple> triples = newMolecule.find(triple);
        assertTrue(triples.hasNext());
        Triple triple1 = triples.next();
        assertEquals(0, tripleComparator.compare(triple, triple1));
        assertFalse(triples.hasNext());

        triple = new TripleImpl(AnySubjectNode.ANY_SUBJECT_NODE, GlobalGraphTestUtil.REF1, GlobalGraphTestUtil.REF1);
        triples = newMolecule.find(triple);
        assertTrue(triples.hasNext());
        triple1 = triples.next();
        assertEquals(0, tripleComparator.compare(r3r1r1, triple1));
        assertTrue(triples.hasNext());
        triple1 = triples.next();
        assertEquals(0, tripleComparator.compare(r2r1r1, triple1));
    }

    private void checkHasHeadMolecules(Molecule actualMolecule, Triple... triples) {
        Set<Triple> moleculeContents = new HashSet<Triple>();
        for (Triple triple : triples) {
            moleculeContents.add(triple);
        }
        Iterator<Triple> rootTriples = actualMolecule.getRootTriples();
        while (rootTriples.hasNext()) {
            Triple tmpTriple = rootTriples.next();
            assertTrue("Could not find: " + tmpTriple + " in " + moleculeContents,
                moleculeContents.contains(tmpTriple));
        }
    }

    private void checkHasSubMolecule(Molecule actualMolecule, HashMap<Triple, Molecule> expectedSubMolecules) {
        for (Map.Entry<Triple, Molecule> entry : expectedSubMolecules.entrySet()) {
            Set<Molecule> newMolecule = actualMolecule.getSubMolecules(entry.getKey());
            HashSet<Molecule> actualMolecules = new HashSet<Molecule>();
            actualMolecules.add(entry.getValue());
            assertEquals("Trying to find molecule for triple: " + entry.getKey(), actualMolecules, newMolecule);
        }
    }
}
