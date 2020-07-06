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

package org.jrdf.graph.global.index;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.global.MoleculeLocalizer;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.createMultiLevelMolecule;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1B2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R2R3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R1R2B2;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeHandler;
import org.jrdf.graph.global.molecule.MoleculeTraverser;
import org.jrdf.graph.global.molecule.mem.MoleculeTraverserImpl;
import static org.jrdf.util.test.SetUtil.asSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

@RunWith(PowerMockRunner.class)
public class AddMoleculeToIndexUnitTest {
    private static final Long MID_0 = 1L;
    private static final Long MID_1 = 2L;
    private static final Long MID_2 = 3L;
    private static final Long MID_3 = 4L;
    // B1R1R1
    private static final Long[] TRIPLE_1 = new Long[]{1L, 2L, 2L};
    // B1R2R2
    private static final Long[] TRIPLE_2 = new Long[]{1L, 3L, 3L};
    // B1R1B2
    private static final Long[] TRIPLE_3 = new Long[]{1L, 2L, 4L};
    // R1R2B2
    private static final Long[] TRIPLE_4 = new Long[]{2L, 3L, 4L};
    // B2R2R1
    private static final Long[] TRIPLE_5 = new Long[]{4L, 3L, 2L};
    // B2R2B3
    private static final Long[] TRIPLE_6 = new Long[]{4L, 3L, 5L};
    // B3R2R3
    private static final Long[] TRIPLE_7 = new Long[]{5L, 3L, 6L};
    // B3R2R2
    private static final Long[] TRIPLE_8 = new Long[]{5L, 3L, 3L};
    private static final Long[] QUAD_1 = new Long[]{1L, 2L, 2L, MID_1, MID_0};
    private static final Long[] QUAD_2 = new Long[]{1L, 3L, 3L, MID_1, MID_0};
    private static final Long[] QUAD_3 = new Long[]{1L, 2L, 4L, MID_1, MID_0};
    private static final Long[] QUAD_4 = new Long[]{2L, 3L, 4L, MID_2, MID_1};
    private static final Long[] QUAD_5 = new Long[]{4L, 3L, 2L, MID_2, MID_1};
    private static final Long[] QUAD_6 = new Long[]{4L, 3L, 5L, MID_2, MID_1};
    private static final Long[] QUAD_7 = new Long[]{5L, 3L, 6L, MID_3, MID_2};
    private static final Long[] QUAD_8 = new Long[]{5L, 3L, 3L, MID_3, MID_2};
    @Mock private WritableIndex<Long> moleculeIndex;
    @Mock private MoleculeLocalizer localizer;
    private MoleculeHandler handler;
    private MoleculeTraverser traverser;

    @Before
    public void setUp() {
        traverser = new MoleculeTraverserImpl();
    }

    @Test
    public void testSingleMolcule() throws GraphException {
        Molecule molecule = createMultiLevelMolecule(asSet(B1R1R1), Collections.<Triple>emptySet(),
            Collections.<Triple>emptySet());
        expect(localizer.getNextMoleculeId()).andReturn(MID_1);
        expect(localizer.localizeTriple(B1R1R1)).andReturn(TRIPLE_1);
        moleculeIndex.add(QUAD_1);
        expectLastCall();
        replayAll();
        handler = new AddMoleculeToIndex(moleculeIndex, localizer);
        traverser.traverse(molecule, handler);
        verifyAll();
    }

    @Test
    public void testMultipleTriplesMolcule() throws GraphException {
        Molecule molecule = createMultiLevelMolecule(asSet(B1R1R1, B1R2R2, B1R1B2), Collections.<Triple>emptySet(),
            Collections.<Triple>emptySet());
        expect(localizer.getNextMoleculeId()).andReturn(MID_1);
        expect(localizer.localizeTriple(B1R1B2)).andReturn(TRIPLE_3);
        expect(localizer.localizeTriple(B1R2R2)).andReturn(TRIPLE_2);
        expect(localizer.localizeTriple(B1R1R1)).andReturn(TRIPLE_1);
        moleculeIndex.add(QUAD_1);
        expectLastCall();
        moleculeIndex.add(QUAD_2);
        expectLastCall();
        moleculeIndex.add(QUAD_3);
        expectLastCall();
        replayAll();
        handler = new AddMoleculeToIndex(moleculeIndex, localizer);
        traverser.traverse(molecule, handler);
        verifyAll();
    }

    @Test
    public void testManyLevelMolecule() throws GraphException {
        Molecule molecule = createMultiLevelMolecule(asSet(B1R1R1, B1R2R2, B1R1B2),
            asSet(R1R2B2, B2R2R1, B2R2B3), asSet(B3R2R3, B3R2R2));
        expect(localizer.getNextMoleculeId()).andReturn(MID_1);
        expect(localizer.localizeTriple(B1R1B2)).andReturn(TRIPLE_3);
        expect(localizer.getNextMoleculeId()).andReturn(MID_2);
        expect(localizer.localizeTriple(B2R2B3)).andReturn(TRIPLE_6);
        expect(localizer.getNextMoleculeId()).andReturn(MID_3);
        expect(localizer.localizeTriple(B3R2R2)).andReturn(TRIPLE_8);
        expect(localizer.localizeTriple(B3R2R3)).andReturn(TRIPLE_7);
        expect(localizer.localizeTriple(B2R2R1)).andReturn(TRIPLE_5);
        expect(localizer.localizeTriple(R1R2B2)).andReturn(TRIPLE_4);
        expect(localizer.localizeTriple(B1R2R2)).andReturn(TRIPLE_2);
        expect(localizer.localizeTriple(B1R1R1)).andReturn(TRIPLE_1);
        moleculeIndex.add(QUAD_1);
        expectLastCall();
        moleculeIndex.add(QUAD_6);
        expectLastCall();
        moleculeIndex.add(QUAD_7);
        expectLastCall();
        moleculeIndex.add(QUAD_8);
        expectLastCall();
        moleculeIndex.add(QUAD_4);
        expectLastCall();
        moleculeIndex.add(QUAD_5);
        expectLastCall();
        moleculeIndex.add(QUAD_2);
        expectLastCall();
        moleculeIndex.add(QUAD_3);
        expectLastCall();
        replayAll();
        handler = new AddMoleculeToIndex(moleculeIndex, localizer);
        traverser.traverse(molecule, handler);
        verifyAll();
    }
}
