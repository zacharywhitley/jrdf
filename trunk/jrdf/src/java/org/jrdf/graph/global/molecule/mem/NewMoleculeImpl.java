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

import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.molecule.MergeSubmolecules;
import org.jrdf.graph.global.molecule.MoleculeTraverser;
import org.jrdf.graph.global.molecule.MoleculeTraverserImpl;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class NewMoleculeImpl implements NewMolecule {
    private TripleComparator tripleComparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    // This should be a set of molecules for the values.
    private final SortedMap<Triple, Set<NewMolecule>> subMolecules;
    private final NewMoleculeComparator moleculeComparator;
    private final MoleculeTraverser traverser = new MoleculeTraverserImpl();

    public NewMoleculeImpl(NewMoleculeComparator newComparator) {
        checkNotNull(newComparator);
        moleculeComparator = newComparator;
        subMolecules = new TreeMap<Triple, Set<NewMolecule>>(tripleComparator);
    }

    private NewMoleculeImpl(NewMoleculeComparator newComparator, SortedMap<Triple, Set<NewMolecule>> newSubMolecules) {
        checkNotNull(newComparator, newSubMolecules);
        moleculeComparator = newComparator;
        subMolecules = newSubMolecules;
    }

    public NewMoleculeImpl(NewMoleculeComparator newComparator, Triple... rootTriples) {
        this(newComparator);
        for (Triple rootTriple : rootTriples) {
            subMolecules.put(rootTriple, new TreeSet<NewMolecule>(moleculeComparator));
        }
    }

    public NewMoleculeImpl(NewMoleculeComparator newComparator, NewMolecule... childMolecules) {
        this(newComparator);
        for (NewMolecule molecule : childMolecules) {
            Triple headTriple = molecule.getHeadTriple();
            Set<NewMolecule> submolecules = new TreeSet<NewMolecule>(moleculeComparator);
            submolecules.add(molecule);
            subMolecules.put(headTriple, submolecules);
        }
    }

    public Triple getHeadTriple() {
        return subMolecules.lastKey();
    }

    public void remove(Triple triple) {
        subMolecules.remove(triple);
    }

    public NewMolecule add(Triple triple) {
        if (!subMolecules.keySet().contains(triple)) {
            subMolecules.put(triple, new TreeSet<NewMolecule>(moleculeComparator));
        }
        return new NewMoleculeImpl(moleculeComparator, subMolecules);
    }

    public NewMolecule add(Triple triple, NewMolecule newMolecule) {
        Set<NewMolecule> moleculeSet = subMolecules.get(triple);
        if (moleculeSet == null) {
            moleculeSet = new TreeSet<NewMolecule>(moleculeComparator);
        }
        if (newMolecule.size() > 0) {
            moleculeSet.add(newMolecule);
        }
        subMolecules.put(triple, moleculeSet);
        return new NewMoleculeImpl(moleculeComparator, subMolecules);
    }

    public NewMolecule add(Triple triple, Triple newTriple) {
        Set<NewMolecule> moleculeSet = subMolecules.get(triple);
        if (moleculeSet == null) {
            moleculeSet = new TreeSet<NewMolecule>(moleculeComparator);
        }
        NewMolecule newMolecule = new NewMoleculeImpl(moleculeComparator, newTriple);
        moleculeSet.add(newMolecule);
        subMolecules.put(triple, moleculeSet);
        return new NewMoleculeImpl(moleculeComparator, subMolecules);
    }

    public void specialAdd(NewMolecule molecule) {
        Iterator<Triple> rootTriples = molecule.getRootTriples();
        while (rootTriples.hasNext()) {
            Triple currentTriple = rootTriples.next();
            subMolecules.put(currentTriple, molecule.getSubMolecules(currentTriple));
        }
    }

    public NewMolecule add(MergeSubmolecules merger, NewMolecule childMolecule) {
        Triple headTriple = getHeadTriple();
        Triple childHeadTriple = childMolecule.getHeadTriple();
        if (childHeadTriple.equals(headTriple)) {
            // Assume that there are no molecules hanging off the any of the triples.
            return merger.merge(this, childMolecule);
        } else {
            // For now assume there is a match with the least grounded to the head triple and therefore we are adding
            // a child molecule onto the head triple.
            // Also we can currently on store one submolecule off of any single triple.
            subMolecules.remove(headTriple);
            Set<NewMolecule> containedMolecules = new TreeSet<NewMolecule>(moleculeComparator);
            containedMolecules.add(childMolecule);
            subMolecules.put(headTriple, containedMolecules);
            return new NewMoleculeImpl(moleculeComparator, subMolecules);
        }
    }

    public boolean contains(Triple triple) {
        return subMolecules.keySet().contains(triple);
    }

    public Iterator<Triple> getRootTriples() {
        return subMolecules.keySet().iterator();
    }

    public Set<NewMolecule> getSubMolecules(Triple rootTriple) {
        Set<NewMolecule> molecules = subMolecules.get(rootTriple);
        if (molecules == null) {
            molecules = Collections.emptySet();
        }
        return molecules;
    }

    public Iterator<Triple> tailTriples() {
        return null;
    }

    public ClosableIterator<Triple> find(Triple triple) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    public Iterator<Triple> iterator() {
        return null;
    }

    public NewMolecule add(Set<Triple> set) {
        List<Triple> newMolecules = new ArrayList<Triple>();
        for (Triple triple : set) {
            newMolecules.add(triple);
        }
        return new NewMoleculeImpl(moleculeComparator, newMolecules.toArray(new Triple[newMolecules.size()]));
    }

    public int size() {
        int size = this.subMolecules.keySet().size();
        Collection<Set<NewMolecule>> collectionOfMoleculeSets = subMolecules.values();
        for (Set<NewMolecule> molecules : collectionOfMoleculeSets) {
            size += calcSize(molecules);
        }
        return size;
    }

    private int calcSize(Set<NewMolecule> molecules) {
        int size = 0;
        if (molecules != null) {
            for (NewMolecule molecule : molecules) {
                Iterator<Triple> rootTriples = molecule.getRootTriples();
                while (rootTriples.hasNext()) {
                    size += calcSize(molecule.getSubMolecules(rootTriples.next()));
                    size++;
                }
            }
        }
        return size;
    }

    @Override
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
            NewMoleculeImpl tmpMolecule = (NewMoleculeImpl) obj;
            return tmpMolecule.subMolecules.equals(subMolecules);
        } catch (ClassCastException cce) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return subMolecules == null ? 0 : subMolecules.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        traverser.traverse(this, new NewMoleculeToString(builder));
        return builder.toString();
    }
}
