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

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
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

    private boolean compareSameSizeMolecules(Molecule m1, Molecule m2) throws GraphException {
        Iterator<Triple> tripleIterator = m1.getTripleIterator();
        while (tripleIterator.hasNext()) {
            Triple triple = tripleIterator.next();
            if (triple.getSubject() instanceof BlankNode && triple.getObject() instanceof BlankNode) {
                if (!m2.contains(ANY_SUBJECT_NODE, triple.getPredicate(), ANY_OBJECT_NODE)) {
                    return false;
                }
            } else if (triple.getSubject() instanceof BlankNode) {
                if (!m2.contains(ANY_SUBJECT_NODE, triple.getPredicate(), triple.getObject())) {
                    return false;
                }
            } else if (triple.getObject() instanceof BlankNode) {
                if (!m2.contains(triple.getSubject(), triple.getPredicate(), ANY_OBJECT_NODE)) {
                    return false;
                }
            } else if (!(triple.getSubject() instanceof BlankNode) && !(triple.getObject() instanceof BlankNode)) {
                if (!m2.contains(triple.getSubject(), triple.getPredicate(), triple.getObject())) {
                    return false;
                }
            }
        }
        return true;
    }
}
