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

package org.jrdf.graph.molecule;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;

import java.util.Iterator;

public class MoleculeComparatorImpl implements MoleculeComparator {

    public boolean compare(Molecule m1, Molecule m2) throws GraphException {
        if (m1.size() != m2.size()) {
            return false;
        } else {
            return compareSameSizeMolecules(m1, m2);
        }
    }

    private boolean compareSameSizeMolecules(Molecule m1, Molecule m2) {
        Iterator<Triple> tripleIterator = m1.iterator();
        while (tripleIterator.hasNext()) {
            Triple triple = tripleIterator.next();
            if (!m2.contains(triple.getSubject(), triple.getPredicate(), triple.getObject())) {
                return false;
            }
        }
        return true;
    }
}
