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

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.TripleImpl;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.HashSet;

public class NewMoleculeImpl implements NewMolecule {
    private TripleComparator comparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    // This should be a set of molecules for the values.
    private final SortedMap<Triple, Set<NewMolecule>> subMolecules;
    private final NewMoleculeComparator moleculeComparator;

    private NewMoleculeImpl(NewMoleculeComparator newComparator, SortedMap<Triple, Set<NewMolecule>> newSubMolecules) {
        checkNotNull(newComparator, newSubMolecules);
        moleculeComparator = newComparator;
        subMolecules = newSubMolecules;
    }

    public NewMoleculeImpl(NewMoleculeComparator newComparator) {
        checkNotNull(newComparator);
        moleculeComparator = newComparator;
        subMolecules = new TreeMap<Triple, Set<NewMolecule>>(comparator);
    }

    public NewMoleculeImpl(NewMoleculeComparator newComparator, Triple... rootTriples) {
        this(newComparator);
        for (Triple rootTriple : rootTriples) {
            subMolecules.put(rootTriple, null);
        }
    }

    public NewMoleculeImpl(NewMoleculeComparator newComparator, NewMolecule... childMolecules) {
        this(newComparator);
        for (NewMolecule molecule : childMolecules) {
            Triple headTriple = molecule.getHeadTriple();
            HashSet<NewMolecule> submolecules = new HashSet<NewMolecule>();
            submolecules.add(molecule);
            subMolecules.put(headTriple, submolecules);
        }
    }

    public Triple getHeadTriple() {
        return subMolecules.lastKey();
    }

    public NewMolecule remove(Triple triple) {
        NewMolecule headMolecule = new HeadMoleculeImpl(triple);
        subMolecules.remove(headMolecule);
        return new NewMoleculeImpl(moleculeComparator,
            subMolecules.keySet().toArray(new NewMolecule[subMolecules.size()]));
    }

    public SortedSet<Triple> getTriples() {
        return null;
    }

    public NewMolecule add(Triple triple) {
        subMolecules.put(triple, null);
        return new NewMoleculeImpl(moleculeComparator, subMolecules.keySet().toArray(new Triple[subMolecules.size()]));
    }

    public NewMolecule add(Triple triple, NewMolecule newMolecule) {
        Set<NewMolecule> moleculeSet = subMolecules.get(triple);
        if (moleculeSet == null) {
            moleculeSet = new HashSet<NewMolecule>();
        }
        moleculeSet.add(newMolecule);
        subMolecules.put(triple, moleculeSet);
        return new NewMoleculeImpl(moleculeComparator, subMolecules.keySet().toArray(new Triple[subMolecules.size()]));
    }

    public NewMolecule add(NewMolecule childMolecule) {
        Triple headTriple = getHeadTriple();
        Triple childHeadTriple = childMolecule.getHeadTriple();
        if (childHeadTriple.equals(headTriple)) {
            // Assume that there are no molecules hanging off the any of the triples.
            return mergeHeadMatchingMolecule(childMolecule);
        } else {
            // For now assume there is a match with the least grounded to the head triple and therefore we are adding
            // a child molecule onto the head triple.
            // Also we can currently on store one submolecule off of any single triple.
            subMolecules.remove(headTriple);
            HashSet<NewMolecule> containedMolecules = new HashSet<NewMolecule>();
            containedMolecules.add(childMolecule);
            subMolecules.put(headTriple, containedMolecules);
            return new NewMoleculeImpl(moleculeComparator, subMolecules);
        }
    }

    private NewMolecule mergeHeadMatchingMolecule(NewMolecule childMolecule) {
        SortedSet<Triple> newRootTriples = new TreeSet<Triple>(comparator);
        newRootTriples.addAll(subMolecules.keySet());
        Iterator<Triple> subMoleculeIter = childMolecule.getRootTriples();
        while (subMoleculeIter.hasNext()) {
            newRootTriples.add(subMoleculeIter.next());
        }
        return new NewMoleculeImpl(moleculeComparator, newRootTriples.toArray(new Triple[newRootTriples.size()]));
    }

    public boolean contains(Triple triple) {
        NewMolecule moleculeToFind = new HeadMoleculeImpl(triple);
        return subMolecules.keySet().contains(moleculeToFind);
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        return contains(new TripleImpl(subject, predicate, object));
    }

    public boolean contains(Molecule molecule) {
        // Head triple comparison
        if (subMolecules.keySet().contains(molecule)) {
            return true;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Iterator<Triple> getRootTriples() {
        return subMolecules.keySet().iterator();
    }

    public Set<NewMolecule> getSubMolecules(Triple rootTriple) {
        return subMolecules.get(rootTriple);
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
        return subMolecules.size();
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
        return subMolecules != null ? subMolecules.hashCode() : 0;
    }

    @Override
    public String toString() {
        return subMolecules.toString();
    }

    private boolean isBlankNode(Node node) {
        return BlankNode.class.isAssignableFrom(node.getClass());
    }
}
