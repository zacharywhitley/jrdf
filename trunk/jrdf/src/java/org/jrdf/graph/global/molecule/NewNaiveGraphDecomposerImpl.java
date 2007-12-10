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

import static org.jrdf.graph.AbstractBlankNode.isBlankNode;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.local.iterator.ClosableIterator;
import org.jrdf.set.SortedSetFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.Iterator;
import java.util.SortedSet;

public class NewNaiveGraphDecomposerImpl implements NewGraphDecomposer {
    private final NewMoleculeFactory moleculeFactory;
    private final SortedSet<Triple> triplesChecked;
    private final SortedSet<NewMolecule> molecules;
    private Graph graph;

    public NewNaiveGraphDecomposerImpl(SortedSetFactory newSetFactory, NewMoleculeFactory newMoleculeFactory,
        NewMoleculeComparator comparator, TripleComparator tripleComparator) {
        checkNotNull(newSetFactory, newMoleculeFactory);
        this.triplesChecked = newSetFactory.createSet(Triple.class, tripleComparator);
        this.molecules = newSetFactory.createSet(NewMolecule.class, comparator);
        this.moleculeFactory = newMoleculeFactory;
    }

    public SortedSet<NewMolecule> decompose(Graph newGraph) throws GraphException {
        triplesChecked.clear();
        molecules.clear();
        graph = newGraph;
        ClosableIterator<Triple> iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        while (iterator.hasNext()) {
            Triple currentTriple = iterator.next();
            if (!triplesChecked.contains(currentTriple)) {
                molecules.add(convertTripleToMolecule(currentTriple));
            }
        }
        return molecules;
    }

    private NewMolecule convertTripleToMolecule(Triple currentTriple) throws GraphException {
        boolean blankSubject = isBlankNode(currentTriple.getSubject());
        boolean blankObject = isBlankNode(currentTriple.getObject());
        NewMolecule molecule = moleculeFactory.createMolecue();
        molecule = molecule.add(currentTriple);
        if (blankSubject) {
            findEnclosedTriples(molecule, currentTriple.getSubject(), ANY_OBJECT_NODE);
            findEnclosedTriples(molecule, ANY_SUBJECT_NODE, (ObjectNode) currentTriple.getSubject());
        }
        if (blankObject) {
            findEnclosedTriples(molecule, ANY_SUBJECT_NODE, currentTriple.getObject());
            findEnclosedTriples(molecule, (SubjectNode) currentTriple.getObject(), ANY_OBJECT_NODE);
        }
        addMoleculeTriplesToCheckedTriples(molecule);
        return molecule;
    }

    private void findEnclosedTriples(NewMolecule molecule, SubjectNode subject, ObjectNode object)
        throws GraphException {
        ClosableIterator<Triple> closableIterator = graph.find(subject, ANY_PREDICATE_NODE, object);
        while (closableIterator.hasNext()) {
            Triple triple = closableIterator.next();
            if (!triplesChecked.contains(triple)) {
                if (isLinkTriple(subject, object, triple) && !isEdge(subject, triple)) {
                    NewMolecule subMolecule = getSubMolecule(triple);
                    molecule.add(triple, subMolecule);
                } else {
                    molecule.add(triple);
                }
            }
        }
    }

    private boolean isLinkTriple(SubjectNode subject, ObjectNode object, Triple triple) {
        return subject == ANY_SUBJECT_NODE && isBlankNode(triple.getSubject()) ||
            object == ANY_OBJECT_NODE && isBlankNode(triple.getObject());
    }

    private boolean isEdge(SubjectNode subject, Triple triple) {
        return triple.getSubject().equals(subject) && isBlankNode(triple.getObject());
    }

    private NewMolecule getSubMolecule(Triple triple) throws GraphException {
        NewMolecule subMolecule = moleculeFactory.createMolecue();
        triplesChecked.add(triple);
        findEnclosedTriples(subMolecule, ANY_SUBJECT_NODE, triple.getObject());
        findEnclosedTriples(subMolecule, (SubjectNode) triple.getObject(), ANY_OBJECT_NODE);
        addMoleculeTriplesToCheckedTriples(subMolecule);
        return subMolecule;
    }

    private void addMoleculeTriplesToCheckedTriples(NewMolecule molecule) {
        Iterator<Triple> tripleIterator = molecule.getRootTriples();
        while (tripleIterator.hasNext()) {
            Triple t = tripleIterator.next();
            triplesChecked.add(t);
        }
    }
}