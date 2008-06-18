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

import static org.jrdf.graph.AbstractBlankNode.isBlankNode;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.util.ClosableIterator;

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
}