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
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.Triple;
import org.jrdf.graph.global.GroundedTripleComparatorFactory;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b1r1r1;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b1r2b2;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b1r2r2;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b1r2r3;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b1r3r2;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b1r3r3;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b2r1r2;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b2r2r2;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b2r3b3;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b2r3r1;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b3r1r3;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b3r2r3;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b3r3r3;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.checkMoluculeContainsRootTriples;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.checkSubmoleculesContainsHeadTriples;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.createMolecule;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.createMoleculeWithSubmolecule;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.createMultiLevelMolecule;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.SetUtil.asSet;

import java.util.Collections;
import static java.util.Collections.EMPTY_SET;

public class MergeSubmoleculesImplUnitTest extends TestCase {
    private static final GroundedTripleComparatorFactory TRIPLE_COMPARATOR_FACTORY = new GroundedTripleComparatorFactoryImpl();
    private MergeSubmolecules mergeSubmolecules;


    public void setUp() {
        TripleComparator tripleComparator = TRIPLE_COMPARATOR_FACTORY.newComparator();
        NewMoleculeComparator moleculeComparator = new NewMoleculeComparatorImpl(tripleComparator);
        NewMoleculeFactory factory = new NewMoleculeFactoryImpl(tripleComparator, moleculeComparator);
        mergeSubmolecules = new MergeSubmoleculesImpl(tripleComparator, moleculeComparator, factory);
    }

    public void testMergeMolecules() {
        NewMolecule molecule1 = createMolecule(b1r1r1, b1r2r2, b1r3r3);
        NewMolecule molecule2 = createMolecule(b1r1r1, b1r2r3, b1r3r2);
        NewMolecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2);
        checkMoluculeContainsRootTriples(newMolecule, b1r1r1, b1r2r2, b1r3r3, b1r2r3, b1r3r2);
    }

    public void testMergeHeadMoleculesWithSubMolecules() {
        NewMolecule molecule1 = createMoleculeWithSubmolecule(b1r1r1, b1r2r2);
        NewMolecule molecule2 = createMoleculeWithSubmolecule(b1r1r1, b1r3r3);
        NewMolecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2);
        checkSubmoleculesContainsHeadTriples(newMolecule.getSubMolecules(b1r1r1), b1r2r2, b1r3r3);
    }

    public void testMergeM1SubsumesM2() {
        NewMolecule molecule1 = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2, b1r2r2), asSet(b2r1r2, b2r2r2, b2r3b3),
            asSet(b3r1r3, b3r2r3));
        NewMolecule molecule2 = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2), asSet(b2r1r2, b2r3b3), asSet(b3r1r3));
        NewMolecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2);
        assertEquals(molecule1, newMolecule);
    }

    public void testMerge() {
        NewMolecule molecule1 = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2, b1r2r2), asSet(b2r1r2, b2r2r2, b2r3b3),
            asSet(b3r1r3, b3r2r3));
        NewMolecule molecule2 = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2, b1r3r3), asSet(b2r1r2, b2r3b3, b2r3r1),
            asSet(b3r1r3, b3r3r3));
        NewMolecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2);
        assertEquals(createMultiLevelMolecule(asSet(b1r1r1, b1r2b2, b1r2r2, b1r3r3), asSet(b2r1r2, b2r2r2, b2r3b3,
            b2r3r1), asSet(b3r1r3, b3r2r3, b3r3r3)), newMolecule);
    }

    public void testMergeUnmatchedHeadTriples() {
        final NewMolecule molecule1 = createMolecule(b1r1r1, b1r2b2, b1r2r2);
        final NewMolecule molecule2 = createMolecule(b1r2b2, b1r3r3);
        AssertThrows.assertThrows(IllegalArgumentException.class, "Cannot merge molecules with different head triples.",
            new AssertThrows.Block() {
                public void execute() throws Throwable {
                    mergeSubmolecules.merge(molecule1, molecule2);
                }
            });
    }

    public void testMergeUnmatchedSecondLevelTriples() {
        NewMolecule molecule1 = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2), asSet(b2r3r1),
                Collections.<Triple>emptySet());
        NewMolecule molecule2 = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2), asSet(b2r1r2),
                Collections.<Triple>emptySet());
        NewMolecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2);
        NewMolecule expectedMolecule = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2), asSet(b2r3r1, b2r1r2),
            Collections.<Triple>emptySet());
        assertEquals(expectedMolecule, newMolecule);
    }

    public void testMergeUnmatchedLevelTriples() {
        NewMolecule molecule1 = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2), asSet(b2r3r1),
                Collections.<Triple>emptySet());
        NewMolecule molecule2 = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2), asSet(b2r1r2, b2r2r2, b2r3b3),
            asSet(b3r1r3));
        NewMolecule newMolecule = mergeSubmolecules.merge(molecule1, molecule2);
        NewMolecule expectedMolecule = createMultiLevelMolecule(asSet(b1r1r1, b1r2b2), asSet(b2r3r1, b2r1r2, b2r2r2,
            b2r3b3), asSet(b3r1r3));
        assertEquals(expectedMolecule, newMolecule);
    }

    public void testMergeSubmolecules() {
        NewMolecule molecule1 = createMoleculeWithSubmolecule(b1r1r1, b1r2r2);
        NewMolecule molecule2 = createMoleculeWithSubmolecule(b1r1r1, b1r3r3);
        NewMolecule newMolecule = mergeSubmolecules.merge(b1r1r1, molecule1, molecule2);
        assertEquals("Expected head triple to be", b1r1r1, newMolecule.getHeadTriple());
        checkSubmoleculesContainsHeadTriples(newMolecule.getSubMolecules(b1r1r1), b1r2r2, b1r3r3);
    }
}