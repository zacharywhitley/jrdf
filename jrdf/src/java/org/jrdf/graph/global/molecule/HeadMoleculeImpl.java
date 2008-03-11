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

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.molecule.mem.NewMolecule;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

public class HeadMoleculeImpl implements NewMolecule {
    private final Triple internalTriple;

    public HeadMoleculeImpl(Triple triple) {
        this.internalTriple = triple;
    }

    public Triple getHeadTriple() {
        return internalTriple;
    }

    public Iterator<Triple> find(Triple triple) {
        return null;
    }

    public boolean contains(Triple triple) {
        return false;
    }

    public Iterator<Triple> iterator() {
        return null;
    }

    public Iterator<Molecule> moleculeIterator() {
        // Return null iterator.
        return null;
    }

    public NewMolecule add(Triple triple) {
        TripleComparator comparator = new GroundedTripleComparatorFactoryImpl().newComparator();
        return null;
    }

    public NewMolecule add(MergeSubmolecules merger, NewMolecule childMolecule) {
        return null;
    }

    public Molecule add(Set<Triple> triples) {
        return null;
    }

    public int size() {
        return 1;
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        return false;
    }

    public Iterator<Triple> tailTriples() {
        return null;
    }

    public void remove(Triple triple) {
    }

    public SortedSet<Triple> getTriples() {
        return null;
    }

    public boolean contains(NewMolecule molecule) {
        return false;
    }

    public Iterator<Triple> getRootTriples() {
        return null;
    }

    public Set<NewMolecule> getSubMolecules(Triple rootTriple) {
        return null;
    }

    public NewMolecule add(Triple triple, NewMolecule newMolecule) {
        return null;
    }

    public void specialAdd(NewMolecule molecule) {
    }

    public NewMolecule add(Triple triple, Triple newTriple) {
        return null;
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
            HeadMoleculeImpl tmpMolecule = (HeadMoleculeImpl) obj;
            return tmpMolecule.internalTriple.equals(internalTriple);
        } catch (ClassCastException cce) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return internalTriple != null ? internalTriple.hashCode() : 0;
    }


    public String toString() {
        return internalTriple.toString();
    }
}