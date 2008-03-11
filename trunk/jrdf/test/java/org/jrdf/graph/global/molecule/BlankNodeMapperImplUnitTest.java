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

package org.jrdf.graph.global.molecule;

import junit.framework.TestCase;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorImpl;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.createMultiLevelMolecule;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1B2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R2R3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.BNODE1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.BNODE2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.BNODE3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R1R2B1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R1R2B2;
import org.jrdf.graph.global.molecule.mem.NewMolecule;
import org.jrdf.graph.global.molecule.mem.NewMoleculeComparator;
import org.jrdf.graph.global.molecule.mem.NewMoleculeFactory;
import org.jrdf.graph.global.molecule.mem.NewMoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.NewMoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.local.BlankNodeComparator;
import org.jrdf.graph.local.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.LocalizedNodeComparator;
import org.jrdf.graph.local.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.NodeComparatorImpl;
import org.jrdf.graph.local.TripleComparatorImpl;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;
import static org.jrdf.util.test.SetUtil.asSet;

import java.util.Collections;
import java.util.Map;

public class BlankNodeMapperImplUnitTest extends TestCase {
    private final NodeTypeComparator typeComparator = new NodeTypeComparatorImpl();
    private final LocalizedNodeComparator localNodeComparator = new LocalizedNodeComparatorImpl();
    private final BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localNodeComparator);
    private final NodeComparator nodeComparator = new NodeComparatorImpl(typeComparator, blankNodeComparator);
    private final TripleComparator tripleComparator = new TripleComparatorImpl(nodeComparator);
    private final TripleComparator comparator = new GroundedTripleComparatorImpl(tripleComparator);
    private final NewMoleculeComparator moleculeComparator = new NewMoleculeHeadTripleComparatorImpl(comparator);
    private final NewMoleculeFactory moleculeFactory = new NewMoleculeFactoryImpl(moleculeComparator
    );
    private BlankNodeMapper mapper;

    public void setUp() throws Exception {
        mapper = new BlankNodeMapperImpl();
    }

    public void testIncompatibleMolecules() {
        NewMolecule m1 = moleculeFactory.createMolecule(B1R1R1);
        NewMolecule m2 = moleculeFactory.createMolecule(B2R2B3);
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertTrue(blankNodeMap.isEmpty());
    }

    public void testLevelOneMapping1() {
        NewMolecule m1 = moleculeFactory.createMolecule(B1R1R1);
        NewMolecule m2 = moleculeFactory.createMolecule(B2R1R1);
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals(BNODE1, blankNodeMap.get(BNODE2));
    }

    public void testLevelOneMapping2() {
        NewMolecule m1 = moleculeFactory.createMolecule(R1R2B1);
        NewMolecule m2 = moleculeFactory.createMolecule(R1R2B2);
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals(BNODE1, blankNodeMap.get(BNODE2));
    }

    public void testConflictingNestedNodes() {
        NewMolecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R2R2), Collections.<Triple>emptySet());
        NewMolecule m2 = createMultiLevelMolecule(asSet(B1R1B3), asSet(B3R2R3), Collections.<Triple>emptySet());
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertTrue(blankNodeMap.isEmpty());
    }

    public void testNestedNodes() {
        NewMolecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R2R2), Collections.<Triple>emptySet());
        NewMolecule m2 = createMultiLevelMolecule(asSet(B1R1B3), asSet(B3R2R2), Collections.<Triple>emptySet());
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals(BNODE2, blankNodeMap.get(BNODE3));
    }
}
