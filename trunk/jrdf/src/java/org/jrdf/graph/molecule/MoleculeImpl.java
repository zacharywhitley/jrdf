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

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MoleculeImpl implements Molecule {
    private Set<Triple> triples;
    private Graph graph;

    public MoleculeImpl(Graph graph) {
        triples = new HashSet<Triple>();
        this.graph = graph;
    }

    public MoleculeImpl(Graph graph, Triple triple) {
        this(graph);
        addTriple(triple);
    }

    public boolean contains(SubjectNode subjNode, PredicateNode predNode, ObjectNode objNode)
        throws GraphException {
        return graph.contains(subjNode, predNode, objNode);
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
}
