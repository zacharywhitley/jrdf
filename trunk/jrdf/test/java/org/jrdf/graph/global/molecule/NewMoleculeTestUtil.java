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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.BlankNodeImpl;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.TripleImpl;
import org.jrdf.graph.global.URIReferenceImpl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NewMoleculeTestUtil {
    private static final TripleComparator comparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    public static final URIReference ref1 = new URIReferenceImpl("urn:foo");
    public static final URIReference ref2 = new URIReferenceImpl("urn:bar");
    public static final URIReference ref3 = new URIReferenceImpl("urn:baz");
    public static final BlankNode bNode1 = new BlankNodeImpl();
    public static final BlankNode bNode2 = new BlankNodeImpl();
    public static final BlankNode bNode3 = new BlankNodeImpl();
    public static final Triple r1r1r1 = new TripleImpl(ref1, ref1, ref1);
    public static final Triple r2r1r1 = new TripleImpl(ref2, ref1, ref1);
    public static final Triple r3r1r1 = new TripleImpl(ref3, ref1, ref1);
    public static final Triple r1r1b1 = new TripleImpl(ref1, ref1, bNode1);
    public static final Triple b1r1r1 = new TripleImpl(bNode1, ref1, ref1);
    public static final Triple b1r2r2 = new TripleImpl(bNode1, ref2, ref2);
    public static final Triple b1r2b2 = new TripleImpl(bNode1, ref2, bNode2);
    public static final Triple b1r3r3 = new TripleImpl(bNode1, ref3, ref3);
    public static final Triple b1r2r3 = new TripleImpl(bNode1, ref2, ref3);
    public static final Triple b1r3r2 = new TripleImpl(bNode1, ref3, ref2);
    public static final Triple b2r1r2 = new TripleImpl(bNode2, ref1, ref2);
    public static final Triple b2r3r1 = new TripleImpl(bNode2, ref3, ref1);
    public static final Triple b2r2b1 = new TripleImpl(bNode2, ref2, bNode1);
    public static final Triple b2r3b3 = new TripleImpl(bNode2, ref3, bNode3);
    public static final Triple b2r2r2 = new TripleImpl(bNode2, ref2, ref2);
    public static final Triple b3r1r3 = new TripleImpl(bNode3, ref1, ref3);
    public static final Triple b3r2r3 = new TripleImpl(bNode3, ref2, ref3);
    public static final Triple b3r3r3 = new TripleImpl(bNode3, ref3, ref3);
    public static final NewMoleculeComparator moleculeComparator = new NewMoleculeComparatorImpl(comparator);

    private NewMoleculeTestUtil() {
    }

    public static NewMolecule createMolecule(Triple... triples) {
        NewMolecule newMolecule = new NewMoleculeImpl(moleculeComparator);
        for (Triple triple : triples) {
            newMolecule.add(triple);
        }
        return newMolecule;
    }

    public static NewMolecule createMultiLevelMolecule(Set<Triple> level1Triples, Set<Triple> level2Triples,
        Set<Triple> level3Triples) {
        NewMolecule level3 = createMolecule(level3Triples.toArray(new Triple[level3Triples.size()]));
        NewMolecule level2 = createMolecule(level2Triples.toArray(new Triple[level2Triples.size()]));
        NewMolecule level1 = createMolecule(level1Triples.toArray(new Triple[level1Triples.size()]));
        MergeMolecules mergeMolecules = new MergeMoleculesImpl();
        NewMolecule level2And3 = mergeMolecules.merge(level2, level3);
        return mergeMolecules.merge(level1, level2And3);
    }

    public static NewMolecule createMolecule(Triple rootTriple, NewMolecule molecule) {
        NewMolecule newMolecule = new NewMoleculeImpl(moleculeComparator);
        newMolecule.add(rootTriple, molecule);
        return newMolecule;
    }

    public static NewMolecule createMoleculeWithSubmolecule(Triple headTriple, Triple submoleculeTriple) {
        NewMolecule submolecule = createMolecule(submoleculeTriple);
        return createMolecule(headTriple, submolecule);
    }

    public static void checkMoluculeContainsRootTriples(NewMolecule molecule, Triple... expectedTriples) {
        assertEquals("Unexpected size of molecule", expectedTriples.length, molecule.size());
        Iterator<Triple> iter = molecule.getRootTriples();
        Set<Triple> rootTriples = new HashSet<Triple>();
        while (iter.hasNext()) {
            rootTriples.add(iter.next());
        }
        for (Triple expectedTriple : expectedTriples) {
            assertTrue(rootTriples.contains(expectedTriple));
        }
    }

    public static void checkSubmoleculesContainsHeadTriples(Set<NewMolecule> subMolecules, Triple... expectedTriples) {
        assertEquals("Expected submolecules", expectedTriples.length, subMolecules.size());
        Iterator<NewMolecule> iter = subMolecules.iterator();
        Set<Triple> headTriples = new HashSet<Triple>();
        while (iter.hasNext()) {
            NewMolecule tmpMolecule = iter.next();
            headTriples.add(tmpMolecule.getHeadTriple());
        }
        for (Triple headTriple : expectedTriples) {
            assertTrue(headTriples.contains(headTriple));
        }
    }
}
