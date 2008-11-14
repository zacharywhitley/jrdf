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
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.molecule.BlankNodeMapper;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.createMultiLevelMolecule;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1B2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R1B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R1B4;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R2R3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B5R1B6;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B6R1B7;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B7R1B8;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B7R2B8;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.BNODE1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.BNODE2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.BNODE3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.BNODE4;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.BNODE5;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.BNODE6;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.BNODE7;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.BNODE8;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R1R2B1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R1R2B2;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.MoleculeFactory;
import static org.jrdf.util.test.SetUtil.asSet;

import java.util.Collections;
import java.util.Map;

public class BlankNodeMapperImplUnitTest extends TestCase {
    private final TripleComparator comparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private final MoleculeComparator moleculeComparator = new MoleculeHeadTripleComparatorImpl(comparator);
    private final MoleculeFactory moleculeFactory = new MoleculeFactoryImpl(moleculeComparator);
    private BlankNodeMapper mapper;

    public void setUp() throws Exception {
        mapper = new BlankNodeMapperImpl();
    }

    public void testIncompatibleMolecules() {
        Molecule m1 = moleculeFactory.createMolecule(B1R1R1);
        Molecule m2 = moleculeFactory.createMolecule(B2R2B3);
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertTrue(blankNodeMap.isEmpty());
    }

    public void testLevelOneMapping1() {
        Molecule m1 = moleculeFactory.createMolecule(B1R1R1);
        Molecule m2 = moleculeFactory.createMolecule(B2R1R1);
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals(BNODE1, blankNodeMap.get(BNODE2));
    }

    public void testLevelOneMapping2() {
        Molecule m1 = moleculeFactory.createMolecule(R1R2B1);
        Molecule m2 = moleculeFactory.createMolecule(R1R2B2);
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals(BNODE1, blankNodeMap.get(BNODE2));
    }

    public void testConflictingNestedNodes() {
        Molecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R2R2), Collections.<Triple>emptySet());
        Molecule m2 = createMultiLevelMolecule(asSet(B1R1B3), asSet(B3R2R3), Collections.<Triple>emptySet());
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertTrue(blankNodeMap.isEmpty());
    }

    public void testNestedNodes() {
        Molecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R2R2), Collections.<Triple>emptySet());
        Molecule m2 = createMultiLevelMolecule(asSet(B1R1B3), asSet(B3R2R2), Collections.<Triple>emptySet());
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals(BNODE2, blankNodeMap.get(BNODE3));
    }

    public void test3LvlNodes() throws GraphException {
        Molecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R1B3), asSet(B3R1B4));
        Molecule m2 = createMultiLevelMolecule(asSet(B5R1B6), asSet(B6R1B7), asSet(B7R1B8));
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals("size of map", 4, blankNodeMap.size());
        assertEquals(BNODE1, blankNodeMap.get(BNODE5));
        assertEquals(BNODE2, blankNodeMap.get(BNODE6));
        assertEquals(BNODE3, blankNodeMap.get(BNODE7));
        assertEquals(BNODE4, blankNodeMap.get(BNODE8));
    }

    public void testConflicting3Lvl() {
        Molecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R1B3), asSet(B3R1B4));
        Molecule m2 = createMultiLevelMolecule(asSet(B5R1B6), asSet(B6R1B7), asSet(B7R2B8));
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertTrue(blankNodeMap.isEmpty());
    }

    public void testSubsumingDirections() {
        Molecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R1B3), asSet(B3R1B4));
        Molecule m2 = createMultiLevelMolecule(asSet(B5R1B6), asSet(B6R1B7), Collections.<Triple>emptySet());
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m2, m1);
        assertTrue(blankNodeMap.isEmpty());
        blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals(3, blankNodeMap.size());
        assertEquals(BNODE1, blankNodeMap.get(BNODE5));
        assertEquals(BNODE2, blankNodeMap.get(BNODE6));
        assertEquals(BNODE3, blankNodeMap.get(BNODE7));
    }
}
