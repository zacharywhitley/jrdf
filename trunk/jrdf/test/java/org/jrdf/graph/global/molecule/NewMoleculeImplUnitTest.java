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
import org.jrdf.graph.Triple;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.moleculeComparator;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.r1r1r1;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.r2r1r1;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.r3r1r1;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.r1r1b1;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b2r2b1;
import static org.jrdf.graph.global.molecule.NewMoleculeTestUtil.b1r3r3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NewMoleculeImplUnitTest extends TestCase {

    public void testMoleculeCreation() {
        NewMolecule newMolecule = new NewMoleculeImpl(moleculeComparator, r1r1r1, r2r1r1, r3r1r1, r1r1b1);
        assertEquals(r1r1r1, newMolecule.getHeadTriple());
    }

    public void testMoleculeCreation2() {
        NewMolecule newMolecule = new NewMoleculeImpl(moleculeComparator);
        newMolecule = newMolecule.add(r2r1r1);
        newMolecule = newMolecule.add(r3r1r1);
        newMolecule = newMolecule.add(r1r1r1);
        newMolecule = newMolecule.add(r1r1b1);
        assertEquals(r1r1r1, newMolecule.getHeadTriple());
    }

    public void testAddVsConstructor() {
        NewMolecule newMolecule1 = new NewMoleculeImpl(moleculeComparator);
        newMolecule1.add(r1r1r1);
        newMolecule1.add(r2r1r1);
        newMolecule1.add(r3r1r1);
        newMolecule1.add(r1r1b1);
        NewMolecule newMolecule2 = new NewMoleculeImpl(moleculeComparator, r1r1r1, r2r1r1, r3r1r1, r1r1b1);
        assertEquals(newMolecule1, newMolecule2);
    }

    public void testMergeHeadMolecules() {
        NewMolecule newMolecule = new NewMoleculeImpl(moleculeComparator, b1r3r3);
        NewMolecule internalMolecule = new NewMoleculeImpl(moleculeComparator, b1r3r3, b2r2b1);
        assertEquals(b1r3r3, newMolecule.getHeadTriple());
        assertEquals(b1r3r3, internalMolecule.getHeadTriple());
        newMolecule = newMolecule.add(internalMolecule);
        assertEquals(b1r3r3, newMolecule.getHeadTriple());
        assertEquals(2, newMolecule.size());
        checkHasHeadMolecules(newMolecule, b2r2b1, b1r3r3);
    }
    

    public void testCombineSubMolecules() {
        NewMolecule newMolecule = new NewMoleculeImpl(moleculeComparator, r1r1b1);
        NewMolecule internalMolecule = new NewMoleculeImpl(moleculeComparator, b2r2b1, b1r3r3);
        newMolecule = newMolecule.add(internalMolecule);
        assertEquals(r1r1b1, newMolecule.getHeadTriple());
        assertEquals(1, newMolecule.size());
        HashMap<Triple, NewMolecule> expectedSubMolecules = new HashMap<Triple, NewMolecule>();
        expectedSubMolecules.put(r1r1b1, internalMolecule);
        checkHasSubMolecule(newMolecule, expectedSubMolecules);
    }

    private void checkHasHeadMolecules(NewMolecule actualMolecule, Triple... triples) {
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

    private void checkHasSubMolecule(NewMolecule actualMolecule, HashMap<Triple, NewMolecule> expectedSubMolecules) {
        for (Map.Entry<Triple,NewMolecule> entry : expectedSubMolecules.entrySet()) {
            Set<NewMolecule> newMolecule = actualMolecule.getSubMolecules(entry.getKey());
            HashSet<NewMolecule> actualMolecules = new HashSet<NewMolecule>();
            actualMolecules.add(entry.getValue());
            assertEquals("Trying to find molecule for triple: " + entry.getKey(), actualMolecules, newMolecule);
        }
    }
}
