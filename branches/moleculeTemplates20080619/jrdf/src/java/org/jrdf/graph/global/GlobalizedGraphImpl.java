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

package org.jrdf.graph.global;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.index.MoleculeIndex;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeIteratorFactory;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.Iterator;

/**
 * In memory implementation of Globalized Graph.
 *
 * @author Imran Khan
 * @version $Revision: 1317 $
 */
public class GlobalizedGraphImpl extends AbstractGlobalizedGraph {

    /**
     * Default constructor.
     * @param newIndexes
     * @param newIteratorFactory
     */
    public GlobalizedGraphImpl(MoleculeIndex[] newIndexes, MoleculeIteratorFactory newIteratorFactory,
        TripleComparator newTripleComparator) {
        super(newIndexes, newIteratorFactory, newTripleComparator);
    }

    public ClosableIterator<Molecule> find(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        checkNotNull(subject, predicate, object);
        return findValue(subject, predicate, object);
    }

    public boolean contains(Molecule molecule) {
        Triple headTriple = molecule.getHeadTriple();
        SubjectNode subj = headTriple.getSubject();
        PredicateNode pred = headTriple.getPredicate();
        ObjectNode obj = headTriple.getObject();
        return contains(subj, pred, obj);
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        checkNotNull(subject, predicate, object);
        return containsValue(subject, predicate, object);
    }

    public boolean contains(Triple triple) {
        checkNotNull(triple);
        return containsValue(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    public void add(Molecule molecule) {
        Triple headTriple = molecule.getHeadTriple();
//        if (!isSubMolecule(molecule)) {
        SubjectNode subj = headTriple.getSubject();
        PredicateNode pred = headTriple.getPredicate();
        ObjectNode obj = headTriple.getObject();
        indexes[0].add(subj, pred, obj, molecule);
        indexes[1].add(pred, obj, subj, molecule);
        indexes[2].add(obj, subj, pred, molecule);
//        }
    }

    // TODO Re-enable when we have a sub-molecule contains that works correctly.
    private boolean isSubMolecule(Molecule molecule) {
        Triple headTriple = molecule.getHeadTriple();
        ClosableIterator<Molecule> closableIterator = findValue(headTriple.getSubject(), headTriple.getPredicate(),
            headTriple.getObject());
        while (closableIterator.hasNext()) {
            Molecule molecule1 = closableIterator.next();
            if (compareInternalTriples(molecule, molecule1)) {
                return true;
            }
        }
        return false;
    }

    private boolean compareInternalTriples(Molecule molecule, Molecule molecule1) {
        Iterator<Triple> iterator = molecule.iterator();
        while (iterator.hasNext()) {
            Triple triple = iterator.next();
            if (!molecule1.contains(triple)) {
                return false;
            }
        }
        return true;
    }

    public void remove(Molecule molecule) throws GraphException {
        Triple headTriple = molecule.getHeadTriple();
        SubjectNode subj = headTriple.getSubject();
        PredicateNode pred = headTriple.getPredicate();
        ObjectNode obj = headTriple.getObject();
        indexes[0].remove(subj, pred, obj);
        indexes[1].remove(pred, obj, subj);
        indexes[2].remove(obj, subj, pred);
    }

    public Molecule getMolecule(Triple headTriple) {
        return indexes[0].getMolecule(headTriple);
    }

    public void clear() {
        indexes[0].clear();
        indexes[1].clear();
        indexes[2].clear();
    }

    public boolean isEmpty() {
        return indexes[0].getNumberOfMolecules() == 0L;
    }

    public long getNumberOfMolecules() {
        return indexes[0].getNumberOfMolecules();
    }

    public long getNumberOfTriples() {
        return indexes[0].getNumberOfTriples();
    }

    public MoleculeIteratorFactory getMoleculeIteratorFactory() {
        return iteratorFactory;
    }

    public void close() {
        //do nothing
    }
}
