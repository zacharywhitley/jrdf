/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */

package org.jrdf.graph.global;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Resource;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.global.index.AddNewMoleculeToIndex;
import org.jrdf.graph.global.index.ReadableIndex;
import org.jrdf.graph.global.index.WritableIndex;
import org.jrdf.graph.global.molecule.MoleculeTraverser;
import org.jrdf.graph.global.molecule.mem.MoleculeTraverserImpl;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.ValueNodeType;
import org.jrdf.util.ClosableIterator;

import java.util.Iterator;
import java.util.Set;

public class MoleculeGraphImpl implements MoleculeGraph {
    private final WritableIndex<Long> writableIndex;
    private final ReadableIndex<Long> readableIndex;
    private final MoleculeLocalizer localizer;
    private final Graph graph;

    public MoleculeGraphImpl(WritableIndex<Long> newWriteIndex, ReadableIndex<Long> newReadIndex,
        MoleculeLocalizer newLocalizer, Graph newGraph) {
        this.writableIndex = newWriteIndex;
        this.readableIndex = newReadIndex;
        this.localizer = newLocalizer;
        this.graph = newGraph;
    }

    public void add(Molecule molecule) {
        MoleculeTraverser traverser = new MoleculeTraverserImpl();
        traverser.traverse(molecule, new AddNewMoleculeToIndex(writableIndex, localizer));
    }

    public void delete(Molecule molecule) {
        try {
            Long[] longs = localizer.localizeTriple(molecule.getHeadTriple());
            // find mid based on molecule head triple and subsequent triples.
            Long mid = readableIndex.findMid(longs);
            // Find the triple that matches in the structureIndex where it is 1, mid, *, *, *
            Set<Long[]> triplesForMid = readableIndex.findTriplesForMid(mid);
            // TODO Recursively reconstruct molecule.
            // Delete all triples in the molecule.
            for (Long[] triple : triplesForMid) {
                writableIndex.remove(triple[0], triple[1], triple[2], mid, 0L);
            }
        } catch (GraphException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO Add more stuff here to handle molecule indexes
    public boolean contains(Triple triple) throws GraphException {
        return graph.contains(triple);
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
        return graph.contains(subject, predicate, object);
    }

    public ClosableIterator<Triple> find(Triple triple) throws GraphException {
        return graph.find(triple);
    }

    public ClosableIterator<Triple> find(SubjectNode subject, PredicateNode predicate, ObjectNode object)
        throws GraphException {
        return graph.find(subject, predicate, object);
    }

    public ClosableIterator<? extends Node> findNodes(NodeType type) {
        return graph.findNodes(type);
    }

    public ClosableIterator<PredicateNode> findPredicates(Resource resource) throws GraphException {
        return graph.findPredicates(resource);
    }

    public ClosableIterator<? super Resource> findResources(ValueNodeType type) {
        return graph.findResources(type);
    }

    public void add(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
        graph.add(subject, predicate, object);
    }

    public void add(Triple triple) throws GraphException {
        graph.add(triple);
    }

    public void add(Iterator<Triple> triples) throws GraphException {
        graph.add(triples);
    }

    public void add(Triple... triples) throws GraphException {
        graph.add(triples);
    }

    public void clear() {
        graph.clear();
    }

    public void close() {
        graph.close();
    }

    public void remove(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
        graph.remove(subject, predicate, object);
    }

    public void remove(Triple triple) throws GraphException {
        graph.remove(triple);
    }

    public void remove(Iterator<Triple> triples) throws GraphException {
        graph.remove(triples);
    }

    public void remove(Triple... triples) throws GraphException {
        graph.remove(triples);
    }

    public GraphElementFactory getElementFactory() {
        return graph.getElementFactory();
    }

    public TripleFactory getTripleFactory() {
        return graph.getTripleFactory();
    }

    public long getNumberOfTriples() throws GraphException {
        return graph.getNumberOfTriples();
    }

    public boolean isEmpty() throws GraphException {
        return graph.isEmpty();
    }
}
