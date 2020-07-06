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

package org.jrdf.graph.global.molecule.mem;

import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.molecule.MergeSubmolecules;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.MoleculeFactory;
import org.jrdf.graph.global.molecule.MoleculeSubsumption;
import static org.jrdf.graph.global.molecule.mem.NullMolecule.NULL_MOLECULE;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class MergeSubmoleculesImpl implements MergeSubmolecules {
    private final TripleComparator comparator;
    private final MoleculeComparator moleculeComparator;
    private final MoleculeFactory moleculeFactory;
    private final MoleculeSubsumption subsumption;

    public MergeSubmoleculesImpl(TripleComparator newComparator, MoleculeComparator newMoleculeComparator,
        MoleculeFactory newMoleculeFactory, MoleculeSubsumption newSubsumption) {
        this.comparator = newComparator;
        this.moleculeComparator = newMoleculeComparator;
        this.moleculeFactory = newMoleculeFactory;
        this.subsumption = newSubsumption;
    }

    public Molecule merge(Molecule molecule1, Molecule molecule2) {
        if (subsumption.subsumes(molecule1, molecule2)) {
            SortedSet<Triple> newRootTriples = new TreeSet<Triple>(comparator);
            addRootTriples(molecule1, newRootTriples);
            addRootTriples(molecule2, newRootTriples);
            Molecule newMolecule = moleculeFactory.createMolecule(newRootTriples);
            Iterator<Triple> subMoleculeIter = newMolecule.getRootTriples();
            while (subMoleculeIter.hasNext()) {
                Triple currentTriple = subMoleculeIter.next();
                newMolecule.specialAdd(merge(currentTriple, molecule1, molecule2));
            }
            return newMolecule;
        } else {
            throw new IllegalArgumentException("Cannot merge molecules with different head triples.");
        }
    }

    public Molecule merge(Triple triple, Molecule molecule1, Molecule molecule2) {
        Molecule newMolecule = moleculeFactory.createMolecule();
        Iterator<Molecule> curr1Iterator = molecule1.getSubMolecules(triple).iterator();
        Iterator<Molecule> curr2Iterator = molecule2.getSubMolecules(triple).iterator();
        iterateAndMergeMolecules(newMolecule, triple, curr1Iterator, curr2Iterator);
        return newMolecule;
    }

    private void iterateAndMergeMolecules(Molecule newMolecule, Triple currentTriple,
        Iterator<Molecule> curr1Iterator, Iterator<Molecule> curr2Iterator) {
        boolean endIterator1 = curr1Iterator.hasNext();
        boolean endIterator2 = curr2Iterator.hasNext();
        Molecule currentMolecule1 = getNextFromIterator(curr1Iterator);
        Molecule currentMolecule2 = getNextFromIterator(curr2Iterator);
        while (endIterator1 || endIterator2) {
            int result = moleculeComparator.compare(currentMolecule1, currentMolecule2);
            if (result == 1) {
                endIterator1 = curr1Iterator.hasNext();
                currentMolecule1 = addMolecule(newMolecule, currentTriple, currentMolecule1, curr1Iterator);
            } else if (result == -1) {
                endIterator2 = curr2Iterator.hasNext();
                currentMolecule2 = addMolecule(newMolecule, currentTriple, currentMolecule2, curr2Iterator);
            } else {
                newMolecule.add(currentTriple, merge(currentMolecule1, currentMolecule2));
                endIterator1 = curr1Iterator.hasNext();
                endIterator2 = curr2Iterator.hasNext();
                currentMolecule1 = getNextFromIterator(curr1Iterator);
                currentMolecule2 = getNextFromIterator(curr2Iterator);
            }
        }
    }

    private Molecule addMolecule(Molecule newMolecule, Triple currentTriple, Molecule currentMolecule,
        Iterator<Molecule> moleculeIterator) {
        Molecule tmpMolecule = currentMolecule;
        newMolecule.add(currentTriple, tmpMolecule);
        tmpMolecule = getNextFromIterator(moleculeIterator);
        return tmpMolecule;
    }

    private Molecule getNextFromIterator(Iterator<Molecule> moleculeIterator) {
        Molecule tmpMolecule;
        if (moleculeIterator.hasNext()) {
            tmpMolecule = moleculeIterator.next();
        } else {
            tmpMolecule = NULL_MOLECULE;
        }
        return tmpMolecule;
    }

    private void addRootTriples(Molecule sourceMolecule, SortedSet<Triple> destinationRootTriples) {
        Iterator<Triple> subMoleculeIter = sourceMolecule.getRootTriples();
        while (subMoleculeIter.hasNext()) {
            destinationRootTriples.add(subMoleculeIter.next());
        }
    }
}
