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

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.util.ClosableIterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: imrank
 * Date: 3/08/2007
 * Time: 15:03:52
 * To change this template use File | Settings | File Templates.
 */
public class MoleculeImpl implements Molecule {
    //private static final JRDFFactory JRDF_FACTORY = SortedMemoryJRDFFactoryImpl.getFactory();
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

    public ClosableIterator<Triple> find(SubjectNode subjNode, PredicateNode predNode, ObjectNode objNode)
        throws GraphException {
        return graph.find(subjNode, predNode, objNode);
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
        this.triples.addAll(triples);
    }

    public boolean contains(Triple triple) {
        return triples.contains(triple);
    }

    public int size() {
        return triples.size();
    }

    public String toString() {
        String res = "";
        try {
            Iterator<Triple> allTriples = getTripleIterator();
            res += "{\n";
            while (allTriples.hasNext()) {
                Triple t = allTriples.next();
                res += t.toString() + '\n';
            }
            res += "}";
        } catch (Exception e) {
            e.printStackTrace();
            res = "EMPTY";
        }
        return res;
    }


}
