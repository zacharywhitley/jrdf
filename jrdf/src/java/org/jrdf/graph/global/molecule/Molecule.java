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

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

/**
 * A Collection all the statements of a particular group.
 *
 * @author Imran Khan
 * @version $Revision: 1226 $
 */
public interface Molecule {

    /**
     * Checks to see if the given triple exists within the molecule.
     *
     * @param triple the triple to search for - does not currently support ANY_SUBJECT, etc.
     *
     * @return true if found.
     */
    boolean contains(Triple triple);

    /**
     * Checks to see if the given triple exists within the molecule.
     *
     * @param subject the subject to search for - does not currently support ANY_SUBJECT.
     * @param predicate the predicate to search for - does not currently support ANY_PREDICATE.
     * @param object the object to search for - does not currently support ANY_OBJECT.
     *
     * @return true if found.
     */
    boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object);

    /**
     * Returns an iterator for the set of triples which make up this molecule.
     *
     * @return all the triples in the molecule.
     */
    Iterator<Triple> iterator();

    /**
     * Adds the given triple to the molecule.
     *
     * @param triple the triple to add.
     * @return a new molecule based on the current one plus the new triple.
     */
    Molecule add(Triple triple);

    /**
     * Adds a set of triples to this molecule.
     *
     * @param triples the set of triples.
     * @return a new molecule based on the current one plus the new triples.
     */
    Molecule add(Set<Triple> triples);

    /**
     * Number of triples contained in the molecule.
     *
     * @return the number of triples contains in the molecule.
     */
    int size();

    /**
     * Returns the head triple of the molecule.
     *
     * @return the head triple of the molecule.
     */
    Triple getHeadTriple();


    /**
     * An iterator that contains tail triples i.e. all triples except head triple.
     *
     * @return the iterator of tail triples.
     */
    Iterator<Triple> tailTriples();

    /**
     * Removes a triple from the molecule.
     *
     * @param triple the triple to remove.
     * @return a new Molecule that contains all triples except the one removed.
     */
    Molecule remove(Triple triple);

    /**
     * Returns all triples in the molecule.
     *
     * @return all triples in the molecule.
     */
    SortedSet<Triple> getTriples();
}
