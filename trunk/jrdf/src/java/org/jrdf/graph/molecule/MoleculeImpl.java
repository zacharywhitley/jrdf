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

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MoleculeImpl implements Molecule {
    private SortedSet<Triple> triples;
    private Map<PredicateNode, SubjectNode> predicateSubjectMap = new HashMap<PredicateNode, SubjectNode>();
    private Map<PredicateNode, ObjectNode> predicateObjectMap = new HashMap<PredicateNode, ObjectNode>();

    public MoleculeImpl(TripleComparator tripleComparator) {
        this.triples = new TreeSet<Triple>(tripleComparator);
    }

    public boolean containsTriple(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        if (isBlankNode(subject)) {
            if (isBlankNode(object)) {
                return predicateSubjectMap.containsKey(predicate);
            } else {
                return predicateSubjectMap.containsKey(predicate) && predicateObjectMap.containsValue(object);
            }
        } else {
            if (isBlankNode(object)) {
                return predicateSubjectMap.containsValue(subject) && predicateObjectMap.containsKey(predicate);
            } else {
                return predicateSubjectMap.containsKey(predicate) && predicateSubjectMap.containsValue(subject) &&
                    predicateObjectMap.containsValue(object);
            }
        }
    }

    public Iterator<Triple> getTripleIterator() {
        return triples.iterator();
    }

    public Set<Triple> getTriples() {
        return triples;
    }

    public void addTriple(Triple triple) {
        triples.add(triple);
    }

    public void addTriples(Set<Triple> triples) {
        for (Triple triple : triples) {
            addTriple(triple);
        }
    }

    public int size() {
        return triples.size();
    }

    public String toString() {
        final StringBuilder res = new StringBuilder();
        final Iterator<Triple> allTriples = getTripleIterator();
        res.append("{\n");
        if (allTriples.hasNext()) {
            while (allTriples.hasNext()) {
                final Triple t = allTriples.next();
                res.append(t.toString());
                res.append('\n');
            }
        } else {
            res.append("EMPTY");
        }
        res.append("}");
        return res.toString();
    }

    private boolean isBlankNode(Node node) {
        return node.getClass().isAssignableFrom(BlankNode.class);
    }
}
