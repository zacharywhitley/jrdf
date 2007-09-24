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
     * Returns an iterator for the set of triples which make up this
     * molecule.
     * @return
     */
    Iterator<Triple> iterator();

    /**
     * Adds the given triple to the molecul. This is immutable, meaning
     * this method will return a new Molecule in the case that the
     * given triple can indeed be added to the molecule.
     * @param triple
     */
    boolean add(Triple triple) throws MoleculeInsertionException;

    /**
     * Adds a set of triples to this molecule. Note that this is immutable,
     * that is calling, this will either return the original Molecule,
     * in which case the set of triples could not be added, otherwise
     * it will return a new Molecule, which contains the original triples,
     * as well as the new triples.
     * @param triples
     */
    boolean add(Set<Triple> triples) throws MoleculeInsertionException;

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

    /**
     * Iterate through the tail triples.
     * @return
     */
    Iterator<Triple> tailTriples();


    /**
     * Checks to see if this molecule is used internal to another molecule.
     * @param molecule
     * @return
     */
    boolean equals(Object molecule);


    Set<Triple> getTailTriples();


    void clear();


    boolean remove(Triple triple) throws MoleculeInsertionException;
}
