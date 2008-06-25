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
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.molecule.GlobalGraphTestUtil;
import org.jrdf.graph.global.molecule.LocalGraphTestUtil;
import org.jrdf.graph.local.BlankNodeComparator;
import org.jrdf.graph.local.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.LocalizedNodeComparator;
import org.jrdf.graph.local.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.NodeComparatorImpl;
import org.jrdf.graph.local.TripleComparatorImpl;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;
import org.jrdf.util.test.SetUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LocalMergeSubmoleculesImplUnitTest extends TestCase {
    private final NodeTypeComparator typeComparator = new NodeTypeComparatorImpl();
    private final LocalizedNodeComparator localNodeComparator = new LocalizedNodeComparatorImpl();
    private final BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localNodeComparator);
    private final NodeComparator nodeComparator = new NodeComparatorImpl(typeComparator, blankNodeComparator);
    private final TripleComparator tripleComparator = new TripleComparatorImpl(nodeComparator);
    private LocalMergeSubmolecules mergeSubmolecules;
    private Map<BlankNode, BlankNode> map;

    public void setUp() {
        MoleculeComparator moleculeComparator = new MoleculeComparatorImpl(tripleComparator);
        MoleculeSubsumption subsumption = new MoleculeSubsumptionImpl();
        NewMoleculeFactory factory = new NewMoleculeFactoryImpl(moleculeComparator);
        MergeSubmolecules globalMerger = new MergeSubmoleculesImpl(tripleComparator, moleculeComparator, factory,
            subsumption);
        mergeSubmolecules = new LocalMergeSubmoleculesImpl(globalMerger, factory);
        map = new HashMap<BlankNode, BlankNode>();
    }

    public void testMergeMoleculesSameBlankNodes() {
        Molecule molecule1 = GlobalGraphTestUtil.createMolecule(LocalGraphTestUtil.B1R1R1, LocalGraphTestUtil.B1R2R2);
        Molecule molecule2 = GlobalGraphTestUtil.createMolecule(LocalGraphTestUtil.B1R1R1, LocalGraphTestUtil.B1R3R2);
        map.put(LocalGraphTestUtil.BNODE1, LocalGraphTestUtil.BNODE1);
        Molecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2, map);
        GlobalGraphTestUtil.checkMoluculeContainsRootTriples(newMolecule, GlobalGraphTestUtil.b1r1r1,
            GlobalGraphTestUtil.b1r2r2, GlobalGraphTestUtil.b1r3r2);
    }

    public void testMergeMoleculesDifferentBlankNodes() {
        Molecule molecule1 = GlobalGraphTestUtil.createMolecule(LocalGraphTestUtil.B1R1R1, LocalGraphTestUtil.B1R2R2);
        Molecule molecule2 = GlobalGraphTestUtil.createMolecule(LocalGraphTestUtil.B2R1R1, LocalGraphTestUtil.B2R2R1);
        map.put(LocalGraphTestUtil.BNODE2, LocalGraphTestUtil.BNODE1);
        Molecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2, map);
        GlobalGraphTestUtil.checkMoluculeContainsRootTriples(newMolecule, GlobalGraphTestUtil.b1r1r1,
            GlobalGraphTestUtil.b1r2r2, GlobalGraphTestUtil.b1r1r1);
    }

    public void testMergeHeadMoleculesWithSubMolecules() {
        Molecule molecule1 = GlobalGraphTestUtil.createMoleculeWithSubmolecule(GlobalGraphTestUtil.b1r1r1,
            GlobalGraphTestUtil.b1r2r2);
        Molecule molecule2 = GlobalGraphTestUtil.createMoleculeWithSubmolecule(GlobalGraphTestUtil.b1r1r1,
            GlobalGraphTestUtil.b1r3r3);
        map.put(LocalGraphTestUtil.BNODE1, LocalGraphTestUtil.BNODE1);
        Molecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2, map);
        GlobalGraphTestUtil.checkSubmoleculesContainsHeadTriples(newMolecule.getSubMolecules(GlobalGraphTestUtil.b1r1r1),
            GlobalGraphTestUtil.b1r2r2, GlobalGraphTestUtil.b1r3r3);
    }

    public void testMergeTwoMolecules() {
        Molecule molecule1 = GlobalGraphTestUtil.createMoleculeWithSubmolecule(LocalGraphTestUtil.B1R1B2,
            LocalGraphTestUtil.B2R2R2);
        Molecule molecule2 = GlobalGraphTestUtil.createMoleculeWithSubmolecule(LocalGraphTestUtil.B1R1B3,
            LocalGraphTestUtil.B3R1R1);
        map.put(LocalGraphTestUtil.BNODE1, GlobalGraphTestUtil.BNODE1);
        map.put(LocalGraphTestUtil.BNODE2, GlobalGraphTestUtil.BNODE2);
        map.put(LocalGraphTestUtil.BNODE3, GlobalGraphTestUtil.BNODE2);
        Molecule expectedMolecule = GlobalGraphTestUtil.createMultiLevelMolecule(SetUtil.asSet(GlobalGraphTestUtil.b1r1b2,
            GlobalGraphTestUtil.b1r1b3), Collections.<Triple>emptySet(),
            Collections.<Triple>emptySet());
        expectedMolecule.add(GlobalGraphTestUtil.b1r1b2, GlobalGraphTestUtil.createMolecule(GlobalGraphTestUtil.b2r2r2));
        expectedMolecule.add(GlobalGraphTestUtil.b1r1b2, GlobalGraphTestUtil.createMolecule(GlobalGraphTestUtil.b2r1r1));
        Molecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2, map);
        assertEquals(expectedMolecule, newMolecule);
    }

    public void testMergeM1SubsumesM2() {
        Molecule molecule1 = GlobalGraphTestUtil.createMultiLevelMolecule(SetUtil.asSet(GlobalGraphTestUtil.b1r1r1,
            GlobalGraphTestUtil.b1r2b2, GlobalGraphTestUtil.b1r2r2), SetUtil.asSet(GlobalGraphTestUtil.b2r1r2,
            GlobalGraphTestUtil.b2r2r2, GlobalGraphTestUtil.b2r3b3),
            SetUtil.asSet(GlobalGraphTestUtil.b3r1r3, GlobalGraphTestUtil.b3r2r3));
        Molecule molecule2 = GlobalGraphTestUtil.createMultiLevelMolecule(SetUtil.asSet(GlobalGraphTestUtil.b1r1r1,
            GlobalGraphTestUtil.b1r2b2), SetUtil.asSet(GlobalGraphTestUtil.b2r1r2, GlobalGraphTestUtil.b2r3b3), SetUtil.asSet(
            GlobalGraphTestUtil.b3r1r3));
        map.put(LocalGraphTestUtil.BNODE1, LocalGraphTestUtil.BNODE1);
        map.put(LocalGraphTestUtil.BNODE2, LocalGraphTestUtil.BNODE2);
        Molecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2, map);
        assertEquals(molecule1, newMolecule);
    }

//    public void testMerge() {
//        Molecule molecule1 = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2, b1r2r2), asSet(b2r1r2, b2r2r2, b2r3b3),
//            asSet(b3r1r3, b3r2r3));
//        Molecule molecule2 = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2, b1r3r3), asSet(b2r1r2, b2r3b3, b2r3r1),
//            asSet(b3r1r3, b3r3r3));
//        Molecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2, map);
//        assertEquals(createMultiLevelMolecule(asSet(b1r1r1, b1r2b2, b1r2r2, b1r3r3), asSet(b2r1r2, b2r2r2, b2r3b3,
//            b2r3r1), asSet(b3r1r3, b3r2r3, b3r3r3)), newMolecule);
//    }

//    public void testMergeUnmatchedHeadTriples() {
//        final Molecule molecule1 = createMolecule(B1R1R1);
//        final Molecule molecule2 = createMolecule(B1R1B2);
//        assertThrows(IllegalArgumentException.class, "Molecule 1 does not subsume Molecule 2.",
//            new AssertThrows.Block() {
//                public void execute() throws Throwable {
//                    mergeSubmolecules.merge(molecule1, molecule2, map);
//                }
//            });
//    }

//    public void testMergeUnmatchedSecondLevelTriples() {
//        Molecule molecule1 = createMultiLevelMolecule(triplesAsSet(b1r1r1, b1r2b2), triplesAsSet(b2r3r1),
//                Collections.<Triple>emptySet());
//        Molecule molecule2 = createMultiLevelMolecule(triplesAsSet(b1r1r1, b1r2b2), triplesAsSet(b2r1r2),
//                Collections.<Triple>emptySet());
//        Molecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2);
//        Molecule expectedMolecule = createMultiLevelMolecule(triplesAsSet(b1r1r1, b1r2b2),
//            triplesAsSet(b2r1r2, b2r3r1), Collections.<Triple>emptySet());
//        assertEquals(expectedMolecule, newMolecule);
//    }

//    public void testMergeUnmatchedLevelTriples() {
//        Molecule molecule1 = createMultiLevelMolecule(triplesAsSet(b1r1r1, b1r2b2), triplesAsSet(b2r3r1),
//                Collections.<Triple>emptySet());
//        Molecule molecule2 = createMultiLevelMolecule(triplesAsSet(b1r1r1, b1r2b2), triplesAsSet(b2r1r2, b2r2r2, b2r3b3),
//            asSet(b3r1r3));
//        Molecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2);
//        Molecule expectedMolecule = createMultiLevelMolecule(triplesAsSet(b1r1r1, b1r2b2),
//            triplesAsSet(b2r3r1, b2r1r2, b2r2r2, b2r3b3), triplesAsSet(b3r1r3));
//        assertEquals(expectedMolecule, newMolecule);
//    }

//    public void testMoleculeSubsumption() {
//        Molecule molecule1 = createMultiLevelMolecule(asSet(b2r2b3), Collections.<Triple>emptySet(),
//                Collections.<Triple>emptySet());
//        Molecule molecule2 = createMultiLevelMolecule(asSet(b2r2b3, b2r1r2), Collections.<Triple>emptySet(),
//                Collections.<Triple>emptySet());
//        Molecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2, map);
//        Molecule expectedMolecule = createMultiLevelMolecule(asSet(b2r2b3, b2r2b3, b2r1r2),
//                Collections.<Triple>emptySet(), Collections.<Triple>emptySet());
//        assertEquals(expectedMolecule, newMolecule);
//    }

//    public void testMoleculeSubsumptionLevel2() {
//        Molecule molecule1 = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2), asSet(b2r2b3, b2r1r2), asSet(b3r1r3));
//        Molecule molecule2 = createMultiLevelMolecule(asSet(b1r2b2), asSet(b2r2b3), asSet(b3r1r2));
//        Molecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2);
//        Molecule expectedMolecule = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2), asSet(b2r2b3, b2r1r2),
//            asSet(b3r1r3, b3r1r2));
//        assertEquals(expectedMolecule, newMolecule);
//    }

//    public void testMergeSubmolecules() {
//        Molecule molecule1 = createMoleculeWithSubmolecule(b1r1r1, b1r2r2);
//        Molecule molecule2 = createMoleculeWithSubmolecule(b1r1r1, b1r3r3);
//        Molecule newMolecule = mergeSubmolecules.merge(b1r1r1, molecule1, molecule2, map);
//        assertEquals("Expected head triple to be", b1r1r1, newMolecule.getHeadTriple());
//        checkSubmoleculesContainsHeadTriples(newMolecule.getSubMolecules(b1r1r1), b1r2r2, b1r3r3);
//    }

    // TODO AN Support this and other more complicated merge examples!
//    public void testMergeDifferentMolecules() {
//        Molecule molecule1 = createMoleculeWithSubmolecule(b1r1b2, b2r1r1);
//        Molecule molecule2 = createMoleculeWithSubmolecule(b1r1b3, b3r1r2);
//        Molecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2);
//        assertEquals("Expected head triple to be", b1r1r1, newMolecule.getHeadTriple());
//        checkSubmoleculesContainsHeadTriples(newMolecule.getSubMolecules(b1r1r1), b1r2r2, b1r3r3);
//    }
}
