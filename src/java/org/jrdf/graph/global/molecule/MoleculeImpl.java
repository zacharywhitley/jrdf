/*
 * Copyright 2007 BioMANTA Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jrdf.graph.global.molecule;

import org.jrdf.graph.Triple;
import org.jrdf.util.ClosableIterator;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MoleculeImpl implements Molecule {
    private SortedSet<Molecule> subMolecules;
    //private Triple headTriple;
    private static final int THIRTY_ONE = 31;
    private HeadTripleMoleculeComparator headTripleComparator;

    public MoleculeImpl(Triple newHeadTriple, HeadTripleMoleculeComparator newHeadTripleComparator)
        throws MoleculeInsertionException {
        //headTriple = newHeadTriple;
        headTripleComparator = newHeadTripleComparator;
        subMolecules = new TreeSet<Molecule>(headTripleComparator);
        Molecule internalMolecule = new InternalMoleculeImpl(newHeadTriple, headTripleComparator);
        subMolecules.add(internalMolecule);
    }

    public MoleculeImpl(SortedSet<Triple> triples, HeadTripleMoleculeComparator newHeadTripleComparator)
            throws MoleculeInsertionException {
        //headTriple = triples.last();
        headTripleComparator = newHeadTripleComparator;
        subMolecules = new TreeSet<Molecule>(headTripleComparator);
        //triples.remove(headTriple);
        this.add(triples);
    }

    public Triple getHeadTriple() {
        return subMolecules.last().getHeadTriple();
    }

    public boolean remove(Triple triple) throws MoleculeInsertionException {
        boolean res = false;
        Triple headTriple = getHeadTriple();
        if (triple.equals(headTriple) && !(this instanceof InternalMoleculeImpl)) {
            throw new MoleculeInsertionException("Not allowed to remove head triple");
        }

        for (Molecule molecule : subMolecules) {
            Triple headTriple1 = molecule.getHeadTriple();
            if (headTriple1.equals(triple)) {
                res = subMolecules.remove(molecule);
                res = true;
                break;
            } else {
                molecule.remove(triple);
            }
        }

        headTriple = getHeadTriple();

        return res;
    }

    public void clear() {
        subMolecules.clear();
    }

    public ClosableIterator<Triple> find(Triple triple) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    public Iterator<Triple> iterator() {
        return getTriples(true).iterator();
    }

    public Iterator<Triple> tailTriples() {
        return getTailTriples().iterator();
    }

    public Set<Triple> getTailTriples() {
        return getTriples(false);
    }

    private Set<Triple> getTriples(boolean includeHeadTriple) {
        Triple headTriple = getHeadTriple();
        LinkedHashSet<Triple> set = new LinkedHashSet<Triple>();
        if (this instanceof InternalMoleculeImpl || includeHeadTriple) {
            set.add(headTriple);
        }

        for (Molecule molecule : subMolecules) {
            set.addAll(molecule.getTailTriples());
        }
        return set;
    }

    public boolean add(Triple triple) throws MoleculeInsertionException {
        Molecule internalMolecule = new InternalMoleculeImpl(triple, headTripleComparator);
        subMolecules.add(internalMolecule);
        return true;
    }

    public boolean add(Set<Triple> triples) throws MoleculeInsertionException {
        boolean res = false;
        for (Triple triple : triples) {
            res = add(triple);
        }
        return res;
    }

    public int size() {
        int counter = 1;
        Iterator<Molecule> iterator = subMolecules.iterator();
        while (iterator.hasNext()) {
            Molecule molecule = iterator.next();
            counter += molecule.size();
        }

        return counter;
    }


    //TODO fix toString method
    public String toString() {
        final StringBuilder res = new StringBuilder();
        final Iterator<Triple> allTriples = iterator();
        res.append(getClass() + "{\n");
        if (allTriples.hasNext()) {
            while (allTriples.hasNext()) {
                final Triple t = allTriples.next();
                res.append(t.toString());
                res.append('\n');
            }
        } else {
            res.append("EMPTY");
        }
        res.append("}");
        return res.toString();
    }

    public boolean equals(Object obj) {
        // Check equal by reference
        if (this == obj) {
            return true;
        }
        // Check for null and ensure exactly the same class - not subclass.
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // Cast and check for equality by value. (same class)
        try {
            MoleculeImpl tmpMolecule = (MoleculeImpl) obj;
            Set<Triple> tmpMoleculeTriples = tmpMolecule.getTriples(true);
            Set<Triple> triples = getTriples(true);
            return tmpMoleculeTriples.equals(triples);
        } catch (ClassCastException cce) {
            return false;
        }
    }

    public int hashCode() {
        int result;
        result = (subMolecules != null ? subMolecules.hashCode() : 0);
        Triple headTriple = getHeadTriple();
        result = THIRTY_ONE * result + (headTriple != null ? headTriple.hashCode() : 0);
        return result;
    }
}
