/*
 * Copyright 2007 BioMANTA Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jrdf.graph.global.molecule;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.local.iterator.ClosableIterator;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A Collection all the statements of a particular group.
 *
 * @author Imran Khan
 * @version $Revision: 1226 $
 */
public class NaiveGraphDecomposerImpl implements GraphDecomposer {
    private final TripleComparator comparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private final Set<Triple> triplesChecked = new HashSet<Triple>();
    private final Set<Molecule> molecules = new HashSet<Molecule>();
    private Graph graph;
    private Triple currentTriple;

    public Set<Molecule> decompose(Graph newGraph) throws GraphException {
        graph = newGraph;
        triplesChecked.clear();
        molecules.clear();
        ClosableIterator<Triple> iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        //move through triples
        while (iterator.hasNext()) {
            currentTriple = iterator.next();
            //check triple has not already been added to molecule
            if (!triplesChecked.contains(currentTriple)) {
                addTriplesWithBlankNodes();
            }
        }
        return molecules;
    }

    private void addTriplesWithBlankNodes() throws GraphException {
        boolean blankSubject = isBlankNode(currentTriple.getSubject());
        boolean blankObject = isBlankNode(currentTriple.getObject());
        Molecule molecule = new MoleculeImpl(comparator);
        molecule = molecule.add(currentTriple);
        if (blankSubject || blankObject) {
            Set<Triple> hangingTriples = getHangingTriples();
            molecule = molecule.add(hangingTriples);
        }
        addMoleculeTriplesToCheckedTriples(molecule);
        molecules.add(molecule);
    }

    /**
     * Given the specified triple, this method will return all other triples which hang off blank nodes which occur
     * either as the subject or object of the triple.
     *
     * @return an interator of all triples with blank nodes.
     * @throws GraphException
     */
    protected Set<Triple> getHangingTriples() throws GraphException {
        SortedSet<Triple> hangingTriples = new TreeSet<Triple>(comparator);
        addBlankNode(hangingTriples, currentTriple, currentTriple.getSubject());
        addBlankNode(hangingTriples, currentTriple, currentTriple.getObject());
        return hangingTriples;
    }

    private void addMoleculeTriplesToCheckedTriples(Molecule molecule) {
        Iterator<Triple> tripleIterator = molecule.iterator();
        while (tripleIterator.hasNext()) {
            Triple t = tripleIterator.next();
            triplesChecked.add(t);
        }
    }

    private void addBlankNode(Set<Triple> hangingTriples, Triple triple, Node resource) throws GraphException {
        if (isBlankNode(resource)) {
            ClosableIterator<Triple> subjectIterator = graph.find((SubjectNode) resource, ANY_PREDICATE_NODE,
                ANY_OBJECT_NODE);
            ClosableIterator<Triple> objectIterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE,
                (ObjectNode) resource);
            findHangingStatements(hangingTriples, triple, subjectIterator);
            findHangingStatements(hangingTriples, triple, objectIterator);
        }
    }

    /**
     * Given the specified graph, this method will iterate through for the pattern of
     * the given subject node and object node and add any triples matching this pattern
     * to the hangingTriples set.
     *
     * @param hangingTriples
     * @param triple
     * @param tripleIterator
     * @throws GraphException
     */
    private void findHangingStatements(Set<Triple> hangingTriples, Triple triple,
        ClosableIterator<Triple> tripleIterator) throws GraphException {

        //get all the hanging triples on its object side
        while (tripleIterator.hasNext()) {
            Triple nextTriple = tripleIterator.next();
            if (!nextTriple.isGrounded() && !nextTriple.equals(triple) && !hangingTriples.contains(nextTriple)) {
                hangingTriples.add(nextTriple);
                addBlankNode(hangingTriples, nextTriple, nextTriple.getSubject());
                addBlankNode(hangingTriples, nextTriple, nextTriple.getObject());
            }
        }
    }

    private boolean isBlankNode(Node node) {
        return BlankNode.class.isAssignableFrom(node.getClass());
    }
}