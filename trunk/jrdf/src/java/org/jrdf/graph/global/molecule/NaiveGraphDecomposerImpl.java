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
import org.jrdf.graph.local.mem.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.mem.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.mem.NodeComparatorImpl;
import org.jrdf.graph.local.mem.TripleComparatorImpl;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Collection all the statements of a particular group.
 *
 * @author Imran Khan
 * @version $Revision: 1226 $
 */
public class NaiveGraphDecomposerImpl implements GraphDecomposer {
    // TODO AN Change blank node comparator????
    private final TripleComparator comparator = new TripleComparatorImpl(new NodeComparatorImpl(
        new NodeTypeComparatorImpl(), new LocalizedBlankNodeComparatorImpl(new LocalizedNodeComparatorImpl())));
    private Graph graph;

    public Set<Molecule> decompose(Graph graph) throws GraphException {
        this.graph = graph;
        ClosableIterator<Triple> iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        Set<Molecule> molecules = new HashSet<Molecule>();          //set of molecules created by decompose process
        Set<Triple> triplesChecked = new HashSet<Triple>();       //set of triples which have been added to a molecule)

        //move through triples
        while (iterator.hasNext()) {
            Triple triple = iterator.next();
            //check triple has not already been added to molecule
            if (!triplesChecked.contains(triple)) {
                boolean blankSubject = isBlankNode(triple.getSubject());
                boolean blankObject = isBlankNode(triple.getObject());
                if (!blankSubject || !blankObject) {
                    Molecule molecule = new MoleculeImpl(comparator);
                    molecule.add(triple);
                    if (blankSubject || blankObject) {
                        Set<Triple> hangingTripleSet = getHangingTriples(triple);
                        molecule.add(hangingTripleSet);
                    }
                    Iterator<Triple> tripleIterator = molecule.iterator();
                    while (tripleIterator.hasNext()) {
                        Triple t = tripleIterator.next();
                        triplesChecked.add(t);
                    }
                    molecules.add(molecule);
                }
            }
        }
        return molecules;
    }

    /**
     * Given the specified triple, this method will return all
     * other triples which hang off blank nodes which occur
     * either as the subject or object of the triple.
     *
     * @param triple
     * @return
     * @throws GraphException
     */
    protected Set<Triple> getHangingTriples(Triple triple) throws GraphException {
        Set<Triple> hangingTriples = new HashSet<Triple>();
        getHangingTriples(hangingTriples, triple);
        return hangingTriples;
    }

    /**
     * Given the specified triple, this method will check to see if the
     * subject and then object node to check if they are blank nodes.
     * In the case they are this method will then call handleBlankNode method
     * which adds the hanging triples to the hangingTriples set. In the case
     *
     * @param hangingTriples
     * @param triple
     * @throws GraphException
     */
    protected void getHangingTriples(Set<Triple> hangingTriples, Triple triple) throws GraphException {
        addBlankNode(hangingTriples, triple, triple.getSubject());
        addBlankNode(hangingTriples, triple, triple.getObject());
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
                getHangingTriples(hangingTriples, nextTriple);
            }
        }
    }

    private boolean isBlankNode(Node node) {
        return BlankNode.class.isAssignableFrom(node.getClass());
    }
}