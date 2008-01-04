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
import org.jrdf.graph.local.iterator.ClosableIterator;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeIteratorFactory;

/**
 * Represents a globalized graph, which maintains molecules. This allows us to better handle blank nodes.
 */
public interface GlobalizedGraph {
    /**
     * Position in the index for SPO.
     */
    int SUBJECT_INDEX = 0;

    /**
     * Position in the index for POS.
     */
    int PREDICATE_INDEX = 1;

    /**
     * Position in the index for OSP.
     */
    int OBJECT_INDEX = 2;

    // TODO Make sure we don't get false positives i.e. a blank node that's in a molecule being found here - should
    // only be found with the contains(molecule) call.
    // TODO Remove this when we're finished - it becomes a subset of the next method?  Where the context/set of triples
    // is an empty set.
    /**
     * Check to see if the given triple exists within the graph.  If it contains blank nodes will only check ones
     * that are not in a molecule.
     *
     * @param subject   The subject to find or AnySubjectNode to indicate any subject.
     * @param predicate The predicate to find or AnyPredicateNode to indicate any predicate.
     * @param object    The object to find or AnyObjectNode to indicate any object.
     * @return True if the statement is found in the model, otherwise false.
     */
    boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object);

    /**
     * Check to see if the given triple exists within the graph.  If it contains blank nodes will only check ones
     * that are not in a molecule.
     *
     * @param triple The triple to find.
     * @return True if the triple is found in the graph, otherwise false.
     */
    boolean contains(Triple triple);

    // TODO implement this by searching inside the given molecule for matching molecules in the store.
    // TODO Probably should be contains(subject, predicate, object, Set<Triple>).  Like *, *, o, ...
    /**
     * Check to see if a given molecule exists - or part thereof.  So if there are more statements in the molecule it
     * will still match.
     *
     * @param molecule The triple to find.
     * @return True if the triple is found in the graph, otherwise false.
     */
    boolean contains(Molecule molecule);

    // TODO Make sure we don't get false positives i.e. a blank node that's in a molecule being found here - should
    // only be found with the find(molecule) call.
    // TODO Remove this when we're finished - it becomes a subset of the next method?  Where the context/set of triples
    // is an empty set.
    /**
     * Find a triple that may contain wildcards - any subject, any predicate, and any object.
     *
     * @param subject   The subject to find or AnySubjectNode to indicate any subject.
     * @param predicate The predicate to find or AnyPredicateNode to indicate any predicate.
     * @param object    The object to find or AnyObjectNode to indicate any object.
     * @return an iterator of molecules.
     */
    ClosableIterator<Molecule> find(SubjectNode subject, PredicateNode predicate, ObjectNode object);

    // TODO find(molecule) - one idea maybe find(subject, predicate, object, Set<Triple>).
    // ClosableIterator<Molecule> find(SubjectNode subject, PredicateNode predicate, ObjectNode object,
    // Set<Triple> triples).
    // The first three parameters can either be a triple or a search condition.  Like *, p, o, ...
    // TODO Do we want addIntersect()/addUnion()/addMinus().
    /**
     * Adds the given molecule and its associated triples to the graph.  If a molecule already exists new tail triples
     * will be added in place.  A molecule with a single grounded node is equivalent to add(subject, predicate, object)
     * or add(triple).
     *
     * @param molecule the molecule to add.
     */
    void add(Molecule molecule);

    /**
     * This will find the given molecule and remove it and any statements it contains. It will first search
     * for the molecules head triple, and then progressively remove the tail triples from the main index.   A molecule
     * with a single grounded node is equivalent to remove(subject, predicate, object) or remove(triple).
     *
     * @param molecule the molecule to remove.
     */
    void remove(Molecule molecule) throws GraphException;

    /**
     * Retrieves the molecule with the given head triple. Returns null in case the head triple does not exist.
     *
     * @param headTriple the head triple to use to get the molecule.
     * @return the moelcule.
     */
    Molecule getMolecule(Triple headTriple);

    /**
     * Closes any open resources.
     */
    void close();

    /**
     * Remove all molecules from the graph.
     */
    void clear();

    /**
     * Returns true if the graph is empty i.e. the number of triples is 0.
     *
     * @return true if the graph is empty i.e. the number of triples is 0.
     * @throws GraphException If the statements number of statements in the graph fails to be found.
     */
    boolean isEmpty();

    /**
     * Returns number of molecules in the graph.
     *
     * @return the number of molecules in the graph.
     */
    long getNumberOfMolecules();


    /**
     * Returns the number of triples in the graph.
     *
     * @return the number of triples in the graph.
     */
    long getNumberOfTriples();

    /**
     * Returns a iterator factory for generating the various types of iterators required.
     *
     * @return a iterator factory for generating the various types of iterators required.
     */
    MoleculeIteratorFactory getMoleculeIteratorFactory();

}

