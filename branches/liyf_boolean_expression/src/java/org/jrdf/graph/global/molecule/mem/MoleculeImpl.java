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

import static org.jrdf.graph.AbstractBlankNode.isBlankNode;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.global.molecule.MergeSubmolecules;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.MoleculeToText;
import org.jrdf.graph.global.molecule.MoleculeToTripleIterator;
import org.jrdf.graph.global.molecule.MoleculeTraverser;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class MoleculeImpl implements Molecule {
    // This should be a collection of molecules for the values.
    private final SortedMap<Triple, SortedSet<Molecule>> subMolecules;
    private final MoleculeComparator moleculeComparator;
    private final MoleculeTraverser traverser = new MoleculeTraverserImpl();
    protected boolean isTopLevel;
    private TripleComparator tripleComparator;

    private MoleculeImpl(MoleculeComparator newComparator, SortedMap<Triple, SortedSet<Molecule>> newSubMolecules) {
        checkNotNull(newComparator, newSubMolecules);
        moleculeComparator = newComparator;
        tripleComparator = moleculeComparator.getTripleComparator();
        subMolecules = newSubMolecules;
        isTopLevel = true;
    }

    public MoleculeImpl(MoleculeComparator newComparator) {
        checkNotNull(newComparator);
        moleculeComparator = newComparator;
        tripleComparator = moleculeComparator.getTripleComparator();
        isTopLevel = true;
        subMolecules = new TreeMap<Triple, SortedSet<Molecule>>(moleculeComparator.getTripleComparator());
    }

    public MoleculeImpl(MoleculeComparator newComparator, Triple... rootTriples) {
        this(newComparator);
        for (Triple rootTriple : rootTriples) {
            subMolecules.put(rootTriple, new TreeSet<Molecule>(moleculeComparator));
        }
    }

    public MoleculeImpl(MoleculeComparator newComparator, Molecule... childMolecules) {
        this(newComparator);
        for (Molecule molecule : childMolecules) {
            Triple headTriple = molecule.getHeadTriple();
            SortedSet<Molecule> submolecules = new TreeSet<Molecule>(moleculeComparator);
            submolecules.add(molecule);
            ((MoleculeImpl) molecule).isTopLevel = false;
            subMolecules.put(headTriple, submolecules);
        }
    }

    public Triple getHeadTriple() {
        return subMolecules.lastKey();
    }

    public void remove(Triple triple) {
        subMolecules.remove(triple);
    }

    public Molecule add(Triple triple) {
        if (!subMolecules.keySet().contains(triple)) {
            subMolecules.put(triple, new TreeSet<Molecule>(moleculeComparator));
        }
        return new MoleculeImpl(moleculeComparator, subMolecules);
    }

    public Molecule add(Triple triple, Molecule newMolecule) {
        SortedSet<Molecule> moleculeSet = subMolecules.get(triple);
        if (moleculeSet == null) {
            moleculeSet = new TreeSet<Molecule>(moleculeComparator);
        }
        if (newMolecule.size() > 0) {
            moleculeSet.add(newMolecule);
            ((MoleculeImpl) newMolecule).isTopLevel = false;
        }
        subMolecules.put(triple, moleculeSet);
        return new MoleculeImpl(moleculeComparator, subMolecules);
    }

    public Molecule add(Triple triple, Triple newTriple) {
        SortedSet<Molecule> moleculeSet = subMolecules.get(triple);
        if (moleculeSet == null) {
            moleculeSet = new TreeSet<Molecule>(moleculeComparator);
        }
        if (isDoubleLinkedTriple(newTriple)) {
            Molecule newMolecule = new MoleculeImpl(moleculeComparator, newTriple);
            moleculeSet.add(newMolecule);
            ((MoleculeImpl) newMolecule).isTopLevel = false;
            subMolecules.put(triple, moleculeSet);
        } else {
            if (moleculeSet.isEmpty()) {
                Molecule newMolecule = new MoleculeImpl(moleculeComparator, newTriple);
                ((MoleculeImpl) newMolecule).isTopLevel = false;
                moleculeSet.add(newMolecule);
                subMolecules.put(triple, moleculeSet);
            } else {
                moleculeSet.last().add(newTriple);
            }
        }
        return new MoleculeImpl(moleculeComparator, subMolecules);
    }

    private boolean isDoubleLinkedTriple(Triple triple) {
        return isBlankNode(triple.getSubject()) && isBlankNode(triple.getObject());
    }

    public void specialAdd(Molecule molecule) {
        Iterator<Triple> rootTriples = molecule.getRootTriples();
        while (rootTriples.hasNext()) {
            Triple currentTriple = rootTriples.next();
            final SortedSet<Molecule> newMolecules = molecule.getSubMolecules(currentTriple);
            for (Molecule newMolecule : newMolecules) {
                ((MoleculeImpl) newMolecule).isTopLevel = false;
            }
            subMolecules.put(currentTriple, newMolecules);
        }
    }

    public Molecule add(MergeSubmolecules merger, Molecule childMolecule) {
        Triple headTriple = getHeadTriple();
        Triple childHeadTriple = childMolecule.getHeadTriple();
        if (childHeadTriple.equals(headTriple)) {
            // Assume that there are no molecules hanging off the any of the triples.
            return merger.merge(this, childMolecule);
        } else {
            // For now assume there is a match with the least grounded to the head triple and therefore we are adding
            // a child molecule onto the head triple.
            // Also we can currently only store one submolecule off of any single triple.
            subMolecules.remove(headTriple);
            SortedSet<Molecule> containedMolecules = new TreeSet<Molecule>(moleculeComparator);
            containedMolecules.add(childMolecule);
            ((MoleculeImpl) childMolecule).isTopLevel = false;
            subMolecules.put(headTriple, containedMolecules);
            return new MoleculeImpl(moleculeComparator, subMolecules);
        }
    }

    public boolean contains(Triple triple) {
        return subMolecules.keySet().contains(triple);
    }

    public Iterator<Triple> getRootTriples() {
        return subMolecules.keySet().iterator();
    }

    public Set<Triple> getRootTriplesAsSet() {
        return subMolecules.keySet();
    }

    public SortedSet<Molecule> getSubMolecules(Triple rootTriple) {
        SortedSet<Molecule> molecules = subMolecules.get(rootTriple);
        if (molecules == null) {
            molecules = Collections.unmodifiableSortedSet(new TreeSet<Molecule>());
        }
        return molecules;
    }

    public Iterator<Triple> tailTriples() {
        return null;
    }

    public Iterator<Triple> find(Triple triple) {
        final Set<Molecule> molecules = new HashSet<Molecule>();
        molecules.add(this);
        Set<Triple> set = findTriple(molecules, triple);
        return set.iterator();
    }

    public Iterator<Triple> find(SubjectNode subject, PredicateNode predicateNode, ObjectNode object) {
        Triple triple = new TripleImpl(subject, predicateNode, object);
        return find(triple);
    }

    private Set<Triple> findTriple(Set<Molecule> molecules, Triple triple) {
        Set<Triple> set = new HashSet<Triple>();
        for (Molecule m : molecules) {
            final Iterator<Triple> roots = m.getRootTriples();
            while (roots.hasNext()) {
                Triple root = roots.next();
                if (triplesMatch(triple, root)) {
                    set.add(root);
                }
                set.addAll(findTriple(m.getSubMolecules(root), triple));
            }
        }
        return set;
    }

    private boolean triplesMatch(Triple pattern, Triple triple2) {
        return nodesMatch(pattern.getSubject(), triple2.getSubject()) &&
                nodesMatch(pattern.getPredicate(), triple2.getPredicate()) &&
                nodesMatch(pattern.getObject(), triple2.getObject());
    }

    private boolean nodesMatch(Node pattern, Node node) {
        if (pattern.equals(AnySubjectNode.ANY_SUBJECT_NODE) || pattern.equals(AnyObjectNode.ANY_OBJECT_NODE) ||
                pattern.equals(AnyObjectNode.ANY_OBJECT_NODE)) {
            return true;
        } else {
            return tripleComparator.getNodeComparator().compare(pattern, node) == 0 ? true : false;
        }
    }

    public Iterator<Triple> iterator() {
        HashSet<Triple> set = new HashSet<Triple>();
        MoleculeToTripleIterator mtti = new MoleculeToTripleIterator(set);
        MoleculeTraverser traverser = new MoleculeTraverserImpl();
        traverser.traverse(this, mtti);
        return set.iterator();
    }

    public Molecule add(Set<Triple> set) {
        List<Triple> newMolecules = new ArrayList<Triple>();
        for (Triple triple : set) {
            newMolecules.add(triple);
        }
        return new MoleculeImpl(moleculeComparator, newMolecules.toArray(new Triple[newMolecules.size()]));
    }

    public int size() {
        int size = this.subMolecules.keySet().size();
        Collection<SortedSet<Molecule>> collectionOfMoleculeSets = subMolecules.values();
        for (Set<Molecule> molecules : collectionOfMoleculeSets) {
            size += calcSize(molecules);
        }
        return size;
    }

    private int calcSize(Set<Molecule> molecules) {
        int size = 0;
        if (molecules != null) {
            for (Molecule molecule : molecules) {
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
            MoleculeImpl tmpMolecule = (MoleculeImpl) obj;
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
        traverser.traverse(this, new MoleculeToText(builder));
        return builder.toString();
    }

    public boolean isTopLevelMolecule() {
        return isTopLevel;
    }

    public boolean removeMolecule(Triple triple, Molecule molecule) {
        if (!subMolecules.keySet().contains(triple)) {
            return false;
        }
        SortedSet<Molecule> subs = this.getSubMolecules(triple);
        for (Molecule sub : subs) {
            if (sub.equals(molecule)) {
                subs.remove(sub);
                break;
            }
        }
        subMolecules.put(triple, subs);
        return true;
    }
}
