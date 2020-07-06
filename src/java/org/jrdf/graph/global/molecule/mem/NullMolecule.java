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

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.global.molecule.MergeSubmolecules;
import org.jrdf.graph.global.molecule.Molecule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

public final class NullMolecule implements Molecule {
    /**
     * Null object for molecule.
     */
    public static final Molecule NULL_MOLECULE = new NullMolecule();

    private NullMolecule() {
    }

    public Triple getHeadTriple() {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Molecule molecule) {
        return false;
    }

    public int size() {
        return 0;
    }

    public Molecule add(Triple triple) {
        throw new UnsupportedOperationException();
    }

    public Molecule add(MergeSubmolecules merger, Molecule childMolecule) {
        throw new UnsupportedOperationException();
    }

    public Set<Triple> getRootTriplesAsSet() {
        throw new UnsupportedOperationException();
    }

    public Iterator<Triple> getRootTriples() {
        throw new UnsupportedOperationException();
    }

    public SortedSet<Molecule> getSubMolecules(Triple rootTriple) {
        throw new UnsupportedOperationException();
    }

    public Molecule add(Triple triple, Molecule newMolecule) {
        throw new UnsupportedOperationException();
    }

    public Molecule add(Triple triple, Triple newTriple) {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Triple triple) {
        return false;
    }

    public Iterator<Triple> find(Triple triple) {
        return Collections.unmodifiableSet(new HashSet<Triple>()).iterator();
    }

    public Iterator<Triple> find(SubjectNode subject, PredicateNode predicateNode, ObjectNode object) {
        return Collections.unmodifiableSet(new HashSet<Triple>()).iterator();
    }

    public void remove(Triple triple) {
    }

    public void specialAdd(Molecule molecule) {
    }

    @Override
    public String toString() {
        return "NULL";
    }

    public boolean isTopLevelMolecule() {
        return true;
    }

    public boolean removeMolecule(Triple triple, Molecule molecule) {
        return false;
    }

    public Iterator<Triple> iterator() {
        return null;
    }
}
