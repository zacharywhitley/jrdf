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
                NewMolecule molecule = moleculeFactory.createMolecue();
                molecule = molecule.add(currentTriple);
                triplesChecked.add(currentTriple);
                convertTripleToMolecule(molecule);
                molecules.add(molecule);
            }
        }

        return molecules;
    }

    private NewMolecule convertTripleToMolecule(NewMolecule molecule) throws GraphException {
        Triple currentTriple = molecule.getHeadTriple();
        boolean blankSubject = isBlankNode(currentTriple.getSubject());
        boolean blankObject = isBlankNode(currentTriple.getObject());
        if (blankObject) {
            findEnclosedTriples(molecule, graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, currentTriple.getObject()));
            findEnclosedTriples(molecule, graph.find((SubjectNode) currentTriple.getObject(), ANY_PREDICATE_NODE,
                ANY_OBJECT_NODE));
        }
        if (blankSubject) {
            findEnclosedTriples(molecule, graph.find(currentTriple.getSubject(), ANY_PREDICATE_NODE, ANY_OBJECT_NODE));
            findEnclosedTriples(molecule, graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE,
                (ObjectNode) currentTriple.getSubject()));
        }
        return molecule;
    }

    private void findEnclosedTriples(NewMolecule molecule, ClosableIterator<Triple> closableIterator)
        throws GraphException {
        while (closableIterator.hasNext()) {
            Triple triple = closableIterator.next();
            if (!triplesChecked.contains(triple)) {
                if (doesHeadLinkToTriple(molecule, triple)) {
                    addLinkedTriple(molecule, triple);
                } else {
                    molecule.add(triple);
                }
            }
        }
    }

    private boolean doesHeadLinkToTriple(NewMolecule molecule, Triple triple) {
        ObjectNode headTripleObject = molecule.getHeadTriple().getObject();
        return isBlankNode(headTripleObject) && headTripleObject.equals(triple.getSubject());
    }

    private void addLinkedTriple(NewMolecule molecule, Triple triple) throws GraphException {
        if (isDoubleLinkedTriple(triple)) {
            NewMolecule subMolecule = moleculeFactory.createMolecue();
            subMolecule.add(triple);
            triplesChecked.add(triple);
            getSubMolecule(subMolecule);
            molecule.add(molecule.getHeadTriple(), subMolecule);
        } else {
            molecule.add(molecule.getHeadTriple(), triple);
            triplesChecked.add(triple);
        }
    }

    private boolean isDoubleLinkedTriple(Triple triple) {
        return isBlankNode(triple.getSubject()) && isBlankNode(triple.getObject());
    }

    private void getSubMolecule(NewMolecule subMolecule) throws GraphException {
        Triple triple = subMolecule.getHeadTriple();
        findEnclosedTriples(subMolecule, graph.find((SubjectNode) triple.getObject(), ANY_PREDICATE_NODE,
            ANY_OBJECT_NODE));
        findEnclosedTriples(subMolecule, graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, triple.getObject()));
    }
}