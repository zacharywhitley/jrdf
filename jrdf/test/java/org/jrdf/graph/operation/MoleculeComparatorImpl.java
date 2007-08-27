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
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.util.ClosableIterator;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: imrank
 * Date: 8/08/2007
 * Time: 17:48:20
 * To change this template use File | Settings | File Templates.
 */
public class MoleculeComparatorImpl implements MoleculeComparator {

    public boolean compare(Molecule m1, Molecule m2) throws GraphException {
        boolean res = true;

        if (m1.size() != m2.size()) {
            res = false;
        } else {
            //go through m1
            Iterator<Triple> tripleIterator = m1.getTripleIterator();
            while (tripleIterator.hasNext()) {
                Triple triple = tripleIterator.next();


                if (triple.getSubject() instanceof BlankNode && triple.getObject() instanceof BlankNode) {
                    ClosableIterator<Triple> it = m2.find(ANY_SUBJECT_NODE, triple.getPredicate(), ANY_OBJECT_NODE);

                    if (!it.hasNext()) {
                        res = false;
                        break;
                    }
                } else if (triple.getSubject() instanceof BlankNode) {
                    //is there a triple in the other molecule which is in the same context
                    ClosableIterator<Triple> it = m2.find(ANY_SUBJECT_NODE, triple.getPredicate(), triple.getObject());

                    if (!it.hasNext()) {
                        res = false;
                        break;
                    }

                } else if (triple.getObject() instanceof BlankNode) {
                    ClosableIterator<Triple> it = m2.find(triple.getSubject(), triple.getPredicate(), ANY_OBJECT_NODE);

                    if (!it.hasNext()) {
                        res = false;
                        break;
                    }
                } else if (!(triple.getSubject() instanceof BlankNode) && !(triple.getObject() instanceof BlankNode)) {
                    if (!m2.contains(triple)) {
                        res = false;
                        break;
                    }
                }
            }
        }

        return res;
    }
}
