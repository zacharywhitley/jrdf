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

package org.biomanta.comparison;

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
import org.jrdf.util.ClosableIterator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: imrank
 * Date: 3/08/2007
 * Time: 15:10:18
 * To change this template use File | Settings | File Templates.
 */
public class NaiveGraphDecomposerImpl implements GraphDecomposer {


    public Set<Molecule> decompose(Graph graph) throws GraphException {
        ClosableIterator<Triple> iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        Set<Molecule> molecules = new HashSet();          //set of molecules created by decompose process
        Set<Triple> triplesChecked = new HashSet();       //set of triples which have been added to a molecule)

        //move through triples
        while (iterator.hasNext()) {
            Triple triple = iterator.next();
            //iterator.remove();

            //check triple has not already been added to molecule
            if (!triplesChecked.contains(triple)) {
                Molecule molecule = null;

                if (!(triple.getObject() instanceof BlankNode) && !(triple.getSubject() instanceof BlankNode)) {
                    molecule = new MoleculeImpl(graph, triple);
                } else if (!(triple.getObject() instanceof BlankNode) || !(triple.getSubject() instanceof BlankNode)) {
                    molecule = new MoleculeImpl(graph, triple);
                    Set hangingTripleSet = getHangingTriples(triple, graph);
                    molecule.addTriples(hangingTripleSet);
                }

                if (molecule != null) {
//                    Iterator<Triple> tripleIterator = molecule.getTripleIterator();
//                    while (tripleIterator.hasNext()) {
//                        Triple t = tripleIterator.next();
//                        if (!t.equals(triple)) {
//                            graph.remove(t);
//                        }
//                    }

                    triplesChecked.addAll(molecule.getTriples());
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
     * @param graph
     * @return
     * @throws GraphException
     */
    protected Set<Triple> getHangingTriples(Triple triple, Graph graph) throws GraphException {
        Set<Triple> hangingTriples = new HashSet<Triple>();
        getHangingTriples(triple, hangingTriples, graph);

        return hangingTriples;
    }

    /**
     * Given the specified triple, this method will check to see if the
     * subject and then object node to check if they are blank nodes.
     * In the case they are this method will then call handleBlankNode method
     * which adds the hanging triples to the hangingTriples set. In the case
     *
     * @param triple
     * @param hangingTriples
     * @param graph
     * @throws GraphException
     */
    protected void getHangingTriples(Triple triple, Set<Triple> hangingTriples, Graph graph)
        throws GraphException {
        //check if object is blank node
        if (triple.getObject() instanceof BlankNode) {
            Node node = triple.getObject();
            handleBlankNode(graph, node, triple, hangingTriples);
        }

        //check to see if the subject is a blank node
        if (triple.getSubject() instanceof BlankNode) {
            Node node = triple.getSubject();
            handleBlankNode(graph, node, triple, hangingTriples);
        }
    }

    /**
     * Given the specified node, this method will search for any triples within the graph
     * which has the node as either its subject or object and add it to the hangingTriple
     * set.
     *
     * @param graph
     * @param node
     * @param triple
     * @param hangingTriples
     * @throws GraphException
     */
    private void handleBlankNode(Graph graph, Node node, Triple triple, Set<Triple> hangingTriples)
        throws GraphException {
        //serach for node in the subject
        findHangingStatements(graph, triple, hangingTriples, (SubjectNode) node, ANY_OBJECT_NODE);
        //search for the node in the object
        findHangingStatements(graph, triple, hangingTriples, ANY_SUBJECT_NODE, (ObjectNode) node);
    }


    /**
     * Given the specified graph, this method will iterate through for the pattern of
     * the given subject node and object node and add any triples matching this pattern
     * to the hangingTriples set.
     * @param graph
     * @param triple
     * @param hangingTriples
     * @param subjNode
     * @param objNode
     * @throws GraphException
     */
    private void findHangingStatements(Graph graph, Triple triple, Set<Triple> hangingTriples, SubjectNode subjNode,
        ObjectNode objNode) throws GraphException {

        //find where blankNode object occurs as the subject in the graph
        ClosableIterator<Triple> blankNodeAsSubjectIterator = graph.find(subjNode, ANY_PREDICATE_NODE, objNode);

        //get all the hanging triples on its object side
        while (blankNodeAsSubjectIterator.hasNext()) {
            Triple nextTriple = blankNodeAsSubjectIterator.next();

            if (!nextTriple.equals(triple) && !hangingTriples.contains(nextTriple)) {
                hangingTriples.add(nextTriple);
                getHangingTriples(nextTriple, hangingTriples, graph);
            }
        }
    }
}
