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

package org.jrdf.graph.global;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;

import java.util.Iterator;
import java.util.Set;

/**
 * A Collection all the statements of a particular group.
 *
 * @author Imran Khan
 * @version $Revision: 1226 $
 */
public interface Molecule {

    /**
     * Returns an iterator for any triples matching the given
     * triple.
     * @param triple
     * @return
     */
    Iterator<Triple> find(Triple triple);

    /**
     * Checks to see if the given triple exists within the
     * molecule.
     * @param triple
     * @return
     */
    boolean contains(Triple triple);

    /**
     * Returns an iterator for the set of triples which make up this
     * molecule.
     * @return
     */
    Iterator<Triple> iterator();

    /**
     * Adds the given triple to the molecule.
     * @param triple
     */
    void add(Triple triple);

    /**
     * Adds a set of triples to this molecule.
     * @param triples
     */
    void add(Set<Triple> triples);

    /**
     * Number of triples contained in the molecule.
     *
     * @return
     */
    int size();

    /**
     * Returns the head triple of the molecule.
     * @return
     */
    Triple getHeadTriple();

    boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object);

    /**
     * Iterate through the tail triples.
     * @return
     */
    Iterator<Triple> tailTriples();
}
