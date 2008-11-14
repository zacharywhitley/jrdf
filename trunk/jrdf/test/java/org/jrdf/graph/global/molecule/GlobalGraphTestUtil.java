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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.BlankNodeImpl;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.URIReferenceImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeHeadTripleComparatorImpl;
import static org.jrdf.util.test.SetUtil.asSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class GlobalGraphTestUtil {
    private static final TripleComparator COMPARATOR = new GroundedTripleComparatorFactoryImpl().newComparator();
    public static final URIReference REF1 = new URIReferenceImpl("urn:foo");
    public static final URIReference REF2 = new URIReferenceImpl("urn:bar");
    public static final URIReference REF3 = new URIReferenceImpl("urn:baz");
    public static final BlankNode BNODE1 = new BlankNodeImpl();
    public static final BlankNode BNODE2 = new BlankNodeImpl();
    public static final BlankNode BNODE3 = new BlankNodeImpl();
    public static final Triple R1R1R1 = new TripleImpl(REF1, REF1, REF1);
    public static final Triple R1R1B1 = new TripleImpl(REF1, REF1, BNODE1);
    public static final Triple R1R1B2 = new TripleImpl(REF1, REF1, BNODE2);
    public static final Triple R1R2B2 = new TripleImpl(REF1, REF2, BNODE2);
    public static final Triple R2R1R1 = new TripleImpl(REF2, REF1, REF1);
    public static final Triple R2R2B2 = new TripleImpl(REF2, REF2, BNODE2);
    public static final Triple R2R1R2 = new TripleImpl(REF2, REF1, REF2);
    public static final Triple R2R1B1 = new TripleImpl(REF2, REF1, BNODE1);
    public static final Triple R3R1R1 = new TripleImpl(REF3, REF1, REF1);
    public static final Triple B1R1R1 = new TripleImpl(BNODE1, REF1, REF1);
    public static final Triple B1R1B2 = new TripleImpl(BNODE1, REF1, BNODE2);
    public static final Triple B1R1B3 = new TripleImpl(BNODE1, REF1, BNODE3);
    public static final Triple B1R2R2 = new TripleImpl(BNODE1, REF2, REF2);
    public static final Triple B1R2R1 = new TripleImpl(BNODE1, REF2, REF1);
    public static final Triple B1R1B1 = new TripleImpl(BNODE1, REF1, BNODE1);
    public static final Triple B1R2B2 = new TripleImpl(BNODE1, REF2, BNODE2);
    public static final Triple B1R3R3 = new TripleImpl(BNODE1, REF3, REF3);
    public static final Triple B1R2R3 = new TripleImpl(BNODE1, REF2, REF3);
    public static final Triple B1R3R2 = new TripleImpl(BNODE1, REF3, REF2);
    public static final Triple B2R1R1 = new TripleImpl(BNODE2, REF1, REF1);
    public static final Triple B2R1B1 = new TripleImpl(BNODE2, REF1, BNODE1);
    public static final Triple B2R2R1 = new TripleImpl(BNODE2, REF2, REF1);
    public static final Triple B2R1R2 = new TripleImpl(BNODE2, REF1, REF2);
    public static final Triple B2R1B3 = new TripleImpl(BNODE2, REF1, BNODE3);
    public static final Triple B2R3R1 = new TripleImpl(BNODE2, REF3, REF1);
    public static final Triple B2R2B1 = new TripleImpl(BNODE2, REF2, BNODE1);
    public static final Triple B2R3B3 = new TripleImpl(BNODE2, REF3, BNODE3);
    public static final Triple B2R2R2 = new TripleImpl(BNODE2, REF2, REF2);
    public static final Triple B2R2B3 = new TripleImpl(BNODE2, REF2, BNODE3);
    public static final Triple B3R1R1 = new TripleImpl(BNODE3, REF1, REF1);
    public static final Triple B3R1R2 = new TripleImpl(BNODE3, REF1, REF2);
    public static final Triple B3R1R3 = new TripleImpl(BNODE3, REF1, REF3);
    public static final Triple B3R2R2 = new TripleImpl(BNODE3, REF2, REF2);
    public static final Triple B3R2R3 = new TripleImpl(BNODE3, REF2, REF3);
    public static final Triple B3R3R3 = new TripleImpl(BNODE3, REF3, REF3);
    public static final MoleculeComparator MOLECULE_COMPARATOR = new MoleculeHeadTripleComparatorImpl(COMPARATOR);
    private static final TripleComparator TRIPLE_COMPARATOR = new GroundedTripleComparatorFactoryImpl().newComparator();
    public static final MoleculeFactory MOLECULE_FACTORY = new MoleculeFactoryImpl(MOLECULE_COMPARATOR);
    private static final MergeMolecules MERGE_MOLECULES = new MergeMoleculesImpl();

    private GlobalGraphTestUtil() {
    }

    public static Set<Triple> triplesAsSet(Triple... triples) {
        return asSet(TRIPLE_COMPARATOR, triples);
    }

    public static Molecule createMolecule(Triple... triples) {
        Molecule newMolecule = MOLECULE_FACTORY.createMolecule();
        for (Triple triple : triples) {
            newMolecule.add(triple);
        }
        return newMolecule;
    }

    public static Molecule createMultiLevelMolecule(Set<Triple> level1Triples, Set<Triple> level2Triples,
        Set<Triple> level3Triples) {
        Molecule level3 = createMolecule(level3Triples.toArray(new Triple[level3Triples.size()]));
        Molecule level2 = createMolecule(level2Triples.toArray(new Triple[level2Triples.size()]));
        Molecule level1 = createMolecule(level1Triples.toArray(new Triple[level1Triples.size()]));
        Molecule level2And3 = mergeMolecules(level2, level3);
        return mergeMolecules(level1, level2And3);
    }

    public static Molecule mergeMolecules(Molecule m1, Molecule m2) {
        return MERGE_MOLECULES.merge(m1, m2);
    }

    public static Molecule createMolecule(Triple rootTriple, Molecule molecule) {
        Molecule newMolecule = MOLECULE_FACTORY.createMolecule();
        newMolecule.add(rootTriple, molecule);
        return newMolecule;
    }

    public static Molecule createMoleculeWithSubmolecule(Triple headTriple, Triple submoleculeTriple) {
        Molecule submolecule = createMolecule(submoleculeTriple);
        return createMolecule(headTriple, submolecule);
    }

    public static void checkMoluculeContainsRootTriples(Molecule molecule, Triple... expectedTriples) {
        Iterator<Triple> actualRootTriplesIterator = molecule.getRootTriples();
        int count = 0;
        while (actualRootTriplesIterator.hasNext()) {
            actualRootTriplesIterator.next();
            count++;
        }
        assertEquals("Unexpected size of root triples in molecule", expectedTriples.length, count);
        Iterator<Triple> iter = molecule.getRootTriples();
        Set<Triple> rootTriples = new TreeSet<Triple>(TRIPLE_COMPARATOR);
        while (iter.hasNext()) {
            rootTriples.add(iter.next());
        }
        for (Triple expectedTriple : expectedTriples) {
            assertTrue("Expected triple: " + expectedTriple + " but got: " + rootTriples,
                rootTriples.contains(expectedTriple));
        }
    }

    public static void checkSubmoleculesContainsHeadTriples(Set<Molecule> subMolecules, Triple... expectedTriples) {
        assertEquals("Expected submolecules", expectedTriples.length, subMolecules.size());
        Iterator<Molecule> iter = subMolecules.iterator();
        Set<Triple> headTriples = new HashSet<Triple>();
        while (iter.hasNext()) {
            Molecule tmpMolecule = iter.next();
            headTriples.add(tmpMolecule.getHeadTriple());
        }
        for (Triple headTriple : expectedTriples) {
            assertTrue("Looking for: " + headTriple + " in: " + headTriples, headTriples.contains(headTriple));
        }
    }
}
