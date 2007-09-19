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

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.util.ClosableIterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MoleculeImpl implements Molecule {
    // TODO This should probably be a set of molecules not triples.  For blank nodes that link to another set of blank nodes
    // ie. _1 a b, _1 c _2, _2 a b, _2 c d, _2 e _3, _3 f g
    private SortedSet<Triple> triples;
    private Map<PredicateNode, SubjectNode> predicateSubjectMap = new HashMap<PredicateNode, SubjectNode>();
    private Map<PredicateNode, ObjectNode> predicateObjectMap = new HashMap<PredicateNode, ObjectNode>();
    private static final int THIRTY_ONE = 31;


    public MoleculeImpl(TripleComparator comparator) {
        this.triples = new TreeSet<Triple>(comparator);
    }

    public Triple getHeadTriple() {
        return triples.last();
    }

    public void remove(Triple triple) {
        predicateSubjectMap.remove(triple.getPredicate());
        predicateObjectMap.remove(triple.getPredicate());
        triples.remove(triple);
    }

    public void clear() {
        predicateSubjectMap.clear();
        predicateObjectMap.clear();
        triples.clear();
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
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

    public ClosableIterator<Triple> find(Triple triple) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    public boolean contains(Triple triple) {
        return contains(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    public Iterator<Triple> iterator() {
        return triples.iterator();
    }

    public Iterator<Triple> tailTriples() {
        return getTailTriples().iterator();
    }

    public LinkedHashSet<Triple> getTailTriples() {
        LinkedHashSet<Triple> set = new LinkedHashSet<Triple>();
        Iterator<Triple> iterator = triples.iterator();
        while (iterator.hasNext()) {
            Triple triple = iterator.next();
            if (iterator.hasNext()) {
                set.add(triple);
            }
        }
        return set;
    }

    public void add(Triple triple) {
        predicateSubjectMap.put(triple.getPredicate(), triple.getSubject());
        predicateObjectMap.put(triple.getPredicate(), triple.getObject());
        triples.add(triple);
    }

    public void add(Set<Triple> triples) {
        for (Triple triple : triples) {
            add(triple);
        }
    }

    public int size() {
        return triples.size();
    }

    public String toString() {
        final StringBuilder res = new StringBuilder();
        final Iterator<Triple> allTriples = iterator();
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
        return BlankNode.class.isAssignableFrom(node.getClass());
    }

    public boolean equals(Object obj) {

        // Check equal by reference
        if (this == obj) {
            return true;
        }

        // Check for null and ensure exactly the same class - not subclass.
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Cast and check for equality by value. (same class)
        try {
            Molecule tmpMolecule = (Molecule) obj;
            return tmpMolecule.getHeadTriple().equals(this.getHeadTriple());
        } catch (ClassCastException cce) {
            return false;
        }
    }


    public int hashCode() {
        int result;
        result = (triples != null ? triples.hashCode() : 0);
        result = THIRTY_ONE * result + (predicateSubjectMap != null ? predicateSubjectMap.hashCode() : 0);
        result = THIRTY_ONE * result + (predicateObjectMap != null ? predicateObjectMap.hashCode() : 0);
        return result;
    }
}
