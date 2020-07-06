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

import org.jrdf.collection.CollectionFactory;
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
import org.jrdf.graph.global.molecule.GraphDecomposer;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.MoleculeFactory;
import org.jrdf.util.ClosableIterable;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.Set;
import java.util.SortedSet;

public class NaiveGraphDecomposerImpl implements GraphDecomposer {
    private final MoleculeFactory moleculeFactory;
    private final SortedSet<Triple> triplesChecked;
    private final SortedSet<Molecule> molecules;
    private TripleComparator tripleComparator;
    private Graph graph;

    public NaiveGraphDecomposerImpl(CollectionFactory newSetFactory, MoleculeFactory newMoleculeFactory,
        MoleculeComparator comparator, TripleComparator newTripleComparator) {
        checkNotNull(newSetFactory, newMoleculeFactory);
        this.triplesChecked = newSetFactory.createSet(Triple.class, newTripleComparator);
        this.molecules = newSetFactory.createSet(Molecule.class, comparator);
        this.moleculeFactory = newMoleculeFactory;
        this.tripleComparator = newTripleComparator;
    }

    public SortedSet<Molecule> decompose(Graph newGraph) throws GraphException {
        triplesChecked.clear();
        molecules.clear();
        graph = newGraph;
        ClosableIterable<Triple> triples = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        for (Triple currentTriple : triples) {
            if (!triplesChecked.contains(currentTriple)) {
                Triple newStartingPoint = new FindEntryNodeImpl().find(graph, currentTriple);
                if (tripleComparator.compare(newStartingPoint, currentTriple) < 0) {
                    currentTriple = newStartingPoint;
                }
                Molecule molecule = moleculeFactory.createMolecule();
                molecule = molecule.add(currentTriple);
                triplesChecked.add(currentTriple);
                molecule = convertTripleToMolecule(molecule);
                molecules.add(molecule);
            }
        }
        triples.iterator().close();
        return molecules;
    }

    private Molecule convertTripleToMolecule(Molecule molecule) throws GraphException {
        Triple currentTriple = molecule.getHeadTriple();
        boolean blankSubject = isBlankNode(currentTriple.getSubject());
        boolean blankObject = isBlankNode(currentTriple.getObject());
        if (blankObject) {
            molecule = findEnclosedTriples(molecule, graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE,
                currentTriple.getObject()));
            molecule = findEnclosedTriples(molecule, graph.find((SubjectNode) currentTriple.getObject(),
                ANY_PREDICATE_NODE, ANY_OBJECT_NODE));
        }
        if (blankSubject) {
            molecule = findEnclosedTriples(molecule, graph.find(currentTriple.getSubject(), ANY_PREDICATE_NODE,
                ANY_OBJECT_NODE));
            molecule = findEnclosedTriples(molecule, graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE,
                (ObjectNode) currentTriple.getSubject()));
        }
        return molecule;
    }

    private Molecule findEnclosedTriples(Molecule molecule, ClosableIterable<Triple> triples)
        throws GraphException {
        for (Triple triple : triples) {
            if (!triplesChecked.contains(triple)) {
                if (isDoubleLinkedTriple(triple) && isDoubleLinkedTriple(molecule.getHeadTriple()) &&
                        triple.getSubject().equals(molecule.getHeadTriple().getSubject())) {
                    return molecule;
                }
                if (isDoubleLinkedTriple(triple)) {
                    molecule = addLinkedTriple(molecule, triple);
                } else {
                    triplesChecked.add(triple);
                    if (isDoubleLinkedTriple(molecule.getHeadTriple())) {
                        molecule.add(molecule.getHeadTriple(), triple);
                    } else {
                        molecule.add(triple);
                    }
                }
            }
        }
        triples.iterator().close();
        return molecule;
    }

    private Molecule addLinkedTriple(Molecule molecule, Triple triple) throws GraphException {
        Molecule subMolecule = moleculeFactory.createMolecule();
        subMolecule.add(triple);
        triplesChecked.add(triple);
        // Put submolecule inside molecule's head triple
        if (isDoubleLinkedTriple(molecule.getHeadTriple()) &&
            molecule.getHeadTriple().getObject().equals(triple.getSubject())) {
            getSubMolecule(subMolecule, triple);
            return molecule.add(molecule.getHeadTriple(), subMolecule);
            // Put molecule inside submolecule
        } else if (triple.getObject().equals(molecule.getHeadTriple().getSubject())) {
            getSubMolecule(subMolecule, triple);
            return subMolecule.add(triple, molecule);
            // Put submolecule inside molecule
        } else {
            getSubMolecule(subMolecule, triple);
            if (isDoubleLinkedTriple(triple)) {
                Set<Molecule> subMolecules = subMolecule.getSubMolecules(triple);
                for (Molecule newMole : subMolecules) {
                    molecule.add(triple, newMole);
                }
            }
            subMolecule.remove(triple);
            getNewRootTriples(molecule, triple);
            return molecule.add(triple, subMolecule);
        }
    }

    private boolean isDoubleLinkedTriple(Triple triple) {
        return isBlankNode(triple.getSubject()) && isBlankNode(triple.getObject());
    }

    private void getNewRootTriples(Molecule molecule, Triple triple) throws GraphException {
        addTriplesToMolecule(molecule, triple.getSubject(), ANY_OBJECT_NODE);
        addTriplesToMolecule(molecule, ANY_SUBJECT_NODE, (ObjectNode) triple.getSubject());
    }

    private void addTriplesToMolecule(Molecule molecule, SubjectNode subject, ObjectNode object)
        throws GraphException {
        ClosableIterable<Triple> triples = graph.find(subject, ANY_PREDICATE_NODE, object);
        for (Triple currentTriple : triples) {
            if (!triplesChecked.contains(currentTriple)) {
                molecule.add(currentTriple);
                triplesChecked.add(currentTriple);
            }
        }
        triples.iterator().close();
    }

    private void getSubMolecule(Molecule subMolecule, Triple triple) throws GraphException {
        findEnclosedTriples(subMolecule, graph.find((SubjectNode) triple.getObject(), ANY_PREDICATE_NODE,
            ANY_OBJECT_NODE));
        findEnclosedTriples(subMolecule, graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, triple.getObject()));
    }
}