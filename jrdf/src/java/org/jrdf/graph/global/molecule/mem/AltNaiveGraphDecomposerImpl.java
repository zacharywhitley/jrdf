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

import org.jrdf.graph.AbstractBlankNode;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.local.util.TripleUtil;
import org.jrdf.graph.local.util.TripleUtilImpl;
import org.jrdf.set.SortedSetFactory;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: 2008-5-12
 * Time: 17:33:17
 * To change this template use File | Settings | File Templates.
 */
public class AltNaiveGraphDecomposerImpl implements NewGraphDecomposer {
    private final NewMoleculeFactory moleculeFactory;
    private final SortedSet<Triple> triplesChecked;
    private final SortedSet<NewMolecule> molecules;
    private TripleComparator tripleComparator;
    private TripleUtil tripleUtil;
    private Graph graph;

    public AltNaiveGraphDecomposerImpl(SortedSetFactory newSetFactory, NewMoleculeFactory newMoleculeFactory,
                                       NewMoleculeComparator comparator, TripleComparator newTripleComparator) {
        checkNotNull(newSetFactory, newMoleculeFactory);
        this.triplesChecked = newSetFactory.createSet(Triple.class, newTripleComparator);
        this.molecules = newSetFactory.createSet(NewMolecule.class, comparator);
        this.moleculeFactory = newMoleculeFactory;
        this.tripleComparator = newTripleComparator;
        this.tripleUtil = new TripleUtilImpl(newSetFactory, tripleComparator);
    }

    public SortedSet<NewMolecule> decompose(Graph newGraph) throws GraphException {
        ClosableIterator<Triple> iterator = preprocess(newGraph);
        while (iterator.hasNext()) {
            Triple currentTriple = iterator.next();
            if (!triplesChecked.contains(currentTriple)) {
                // return all the triples that are connected by blank nodes (essentially a flat molecule as a set)
                Set<Triple> triples = tripleUtil.getAllTriplesForTriple(currentTriple, graph);
                NewMolecule molecule = moleculeFactory.createMolecule();
                molecule = molecule.add(currentTriple);
                triplesChecked.add(currentTriple);
                molecule = addAllLinkedTriples(triples, molecule);
                molecules.add(molecule);
                addTriplesToCheckedSet(triples);
            }
        }
        return molecules;
    }

    private NewMolecule addAllLinkedTriples(Set<Triple> triples, NewMolecule molecule) {
        for (Triple triple : triples) {
            NewMolecule curMolecule = placeTripleInMolecule(molecule, triple);
            // Can't find a place for triple anywhere in the molecule,
            // so just add it as a root triple for the molecule.
            if (curMolecule == null) {
                molecule.add(triple);
            } else {
                molecule = curMolecule;
            }
            triplesChecked.add(triple);
        }
        return molecule;
    }

    private ClosableIterator<Triple> preprocess(Graph newGraph) throws GraphException {
        triplesChecked.clear();
        molecules.clear();
        graph = newGraph;
        ClosableIterator<Triple> iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        return iterator;
    }

    /**
     * This method searches through the molecule in the depth-first style to find a place to place the current triple.
     *
     * @param molecule
     * @param triple
     * @return
     */
    private NewMolecule placeTripleInMolecule(NewMolecule molecule, Triple triple) {
        Iterator<Triple> rootTriples = molecule.getRootTriples();
        NewMolecule curMolecule = null;
        // need to search through all triples of the molecule to find a matching
        // search through the rest of the root triples first
        while (rootTriples.hasNext() && curMolecule == null) {
            Triple currentRoot = rootTriples.next();
            curMolecule = addTripleUnderRoot(molecule, triple, currentRoot);
        }
        return curMolecule;
    }

    /**
     * Try to add <p>triple</p> somewhere under the current root triple, in a depth-first manner.
     * @param molecule
     * @param triple
     * @param currentRoot
     * @return curMolecule The new molecule as the result of adding this triple.
     */
    private NewMolecule addTripleUnderRoot(NewMolecule molecule, Triple triple, Triple currentRoot) {
        NewMolecule curMolecule = null;
        curMolecule = addTripleToCurrentRootTriple(molecule, triple, currentRoot, curMolecule);
        if (curMolecule == null) {
            // cannot add triple to the current root triple, so go down the submolecules of this root triple
            SortedSet<NewMolecule> subMolecules = molecule.getSubMolecules(currentRoot);
            for (NewMolecule subMolecule : subMolecules) {
                curMolecule = placeTripleInMolecule(subMolecule, triple);
                if (curMolecule != null) {
                    molecule.add(currentRoot, curMolecule);
                    curMolecule = molecule;
                    break;
                }
            }
        }
        //triplesChecked.add(triple);
        return curMolecule;
    }

    private NewMolecule addTripleToCurrentRootTriple(NewMolecule molecule,
                                                     Triple triple, Triple currentRoot, NewMolecule curMolecule) {
        if (isObjSubMatching(currentRoot, triple) && isSubObjMatching(currentRoot, triple)) {
            int compare = tripleComparator.compare(currentRoot, triple);
            curMolecule = addDoublyLinkedTriple(molecule, triple, currentRoot, curMolecule, compare);
        } else if (isObjSubMatching(currentRoot, triple)) {
            // check with the subject of triple is the same blank of the object of currentRoot
            // add the new molecuel as a submolecule
            curMolecule = addTripleAsSub(molecule, currentRoot, triple);
        } else if (isSubObjMatching(currentRoot, triple)) {
            // add the current molecuel as a submolecule of the new molecule
            if (molecule.isTopLevelMolecule()) {
                curMolecule = createMoleculeWithSub(currentRoot, triple, molecule);
            }
        }
        return curMolecule;
    }

    private NewMolecule addDoublyLinkedTriple(NewMolecule molecule, Triple triple, Triple currentRoot,
                                              NewMolecule curMolecule, int compare) {
        if (compare < 0) {
            // currentRoot is smaller
            curMolecule = addTripleAsSub(molecule, currentRoot, triple);
        } else {
            // currentRoot is larger, if the current root triple is at top level, then
            // add the current molecule as the sub molecule of the new molecule
            if (molecule.isTopLevelMolecule()) {
                curMolecule = createMoleculeWithSub(currentRoot, triple, molecule);
            }
        }
        return curMolecule;
    }

    private NewMolecule addTripleAsSub(NewMolecule molecule, Triple currentRoot, Triple triple) {
        NewMolecule curMolecule = moleculeFactory.createMolecule(triple);
        molecule.add(currentRoot, curMolecule);
        return molecule;
    }

    private NewMolecule createMoleculeWithSub(Triple currentHead, Triple headTriple, NewMolecule subMolecule) {
        Set<NewMolecule> subs = subMolecule.getSubMolecules(currentHead);
        subMolecule.remove(currentHead);
        final NewMolecule newSubMolecule = moleculeFactory.createMolecule(currentHead);
        for (NewMolecule sub : subs) {
            newSubMolecule.add(currentHead, sub);
        }
        subMolecule.add(headTriple, newSubMolecule);
        //NewMolecule molecule = moleculeFactory.createMolecule(headTriple);
        //molecule.add(headTriple, subMolecule);
        return subMolecule;
    }

    private boolean isObjSubMatching(Triple currentRoot, Triple triple) {
        final ObjectNode rootObj = currentRoot.getObject();
        final SubjectNode curSbj = triple.getSubject();
        return AbstractBlankNode.isBlankNode(rootObj) && AbstractBlankNode.isBlankNode(curSbj) &&
                rootObj.equals(curSbj);
    }

    private boolean isSubObjMatching(Triple currentRoot, Triple triple) {
        final SubjectNode rootSbj = currentRoot.getSubject();
        final ObjectNode curObj = triple.getObject();
        return AbstractBlankNode.isBlankNode(rootSbj) && AbstractBlankNode.isBlankNode(curObj) &&
                rootSbj.equals(curObj);
    }

    private void addTriplesToCheckedSet(Set<Triple> triples) {
        for (Triple triple : triples) {
            triplesChecked.add(triple);
        }
    }
}
