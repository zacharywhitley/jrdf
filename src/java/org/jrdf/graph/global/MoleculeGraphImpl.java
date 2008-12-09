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
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.global.index.AddMoleculeToIndex;
import org.jrdf.graph.global.index.ReadableIndex;
import org.jrdf.graph.global.index.WritableIndex;
import org.jrdf.graph.global.molecule.ClosableMoleculeIterator;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.MoleculeHandler;
import org.jrdf.graph.global.molecule.MoleculeToText;
import org.jrdf.graph.global.molecule.MoleculeTraverser;
import org.jrdf.graph.global.molecule.mem.MoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeTraverserImpl;
import org.jrdf.graph.local.TripleComparatorFactoryImpl;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.ValueNodeType;
import org.jrdf.util.ClosableIterable;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.LongIndexToMoleculeIterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MoleculeGraphImpl implements MoleculeGraph {
    private static final int QUIN_SIZE = 5;
    private static final int QUAD_SIZE = 4;
    private final WritableIndex<Long> writableIndex;
    private final ReadableIndex<Long> readableIndex;
    private final MoleculeLocalizer localizer;
    private final Graph graph;
    private TripleComparator comparator;
    private MoleculeComparator moleculeComparator;
    private MoleculeGraphHandler handler;

    public MoleculeGraphImpl(WritableIndex<Long> newWriteIndex, ReadableIndex<Long> newReadIndex,
        MoleculeLocalizer newLocalizer, Graph newGraph, NodePool nodePool) {
        this.writableIndex = newWriteIndex;
        this.readableIndex = newReadIndex;
        this.localizer = newLocalizer;
        this.graph = newGraph;
        this.comparator = new TripleComparatorFactoryImpl().newComparator();
        this.moleculeComparator = new MoleculeHeadTripleComparatorImpl(comparator);
        this.handler = new MoleculeGraphHandlerImpl(nodePool, readableIndex, moleculeComparator);
    }

    public void add(Molecule molecule) {
        MoleculeTraverser traverser = new MoleculeTraverserImpl();
        traverser.traverse(molecule, new AddMoleculeToIndex(writableIndex, localizer));
    }

    public void delete(Molecule molecule) throws GraphException {
        final Triple headTriple = molecule.getHeadTriple();
        final Long headTripleMid = findMoleculeID(headTriple);
        Set<Long> mids = new HashSet<Long>();
        mids.add(headTripleMid);
        Set<Long[]> indicesToRemove = new HashSet<Long[]>();
        indicesToRemove = deleteChildMolecules(mids, indicesToRemove);
        getIndicesToRemove(headTripleMid, indicesToRemove);
        //indicesToRemove = getIndicesToRemove(headTripleMid, indicesToRemove);
        for (Long[] index : indicesToRemove) {
            writableIndex.remove(index);
        }
        indicesToRemove.clear();
    }

    private Set<Long[]> getIndicesToRemove(Long headTripleMid, Set<Long[]> indicesToRemove) {
        ClosableIterator<Long[]> iterator = readableIndex.findTriplesForMid(1L, headTripleMid);
        while (iterator.hasNext()) {
            Long[] spo = iterator.next();
            Long[] quin = new Long[QUIN_SIZE];
            System.arraycopy(spo, 0, quin, 0, 3);
            quin[3] = headTripleMid;
            quin[4] = 1L;
            indicesToRemove.add(quin);
        }
        iterator.close();
        return indicesToRemove;
    }

    private Long findMoleculeID(Triple headTriple) throws GraphException {
        final Long[] longsForTriple = localizer.localizeTriple(headTriple);
        return readableIndex.findHeadTripleMid(1L, longsForTriple);
    }

    private Set<Long[]> deleteChildMolecules(Set<Long> mids, Set<Long[]> toRemove) throws GraphException {
        Set<Long> newMids = new HashSet<Long>();
        for (Long pid : mids) {
            final ClosableIterator<Long[]> mspos = readableIndex.findTriplesForPid(pid);
            while (mspos.hasNext()) {
                Long[] mspo = mspos.next();
                newMids.add(mspo[0]);
                Long[] quin = new Long[QUIN_SIZE];
                System.arraycopy(mspo, 0, quin, 0, QUAD_SIZE);
                quin[QUAD_SIZE] = pid;
                //writableIndex.remove(quin);
                toRemove.add(quin);
            }
            mspos.close();
        }
        if (!newMids.isEmpty()) {
            deleteChildMolecules(newMids, toRemove);
            newMids.clear();
        }
        return toRemove;
    }

    public Molecule findTopLevelMolecule(Triple triple) throws GraphException {
        final Long[] localizedTriple = localizer.localizeTriple(triple);
        final ClosableIterator<Long[]> iterator = readableIndex.getMidPidPairs(localizedTriple);
        Long mid = 1L;
        if (iterator.hasNext()) {
            final Long[] pair = iterator.next();
            mid = pair[0];
        }
        iterator.close();
        mid = readableIndex.findTopMoleculeID(mid);
        return handler.createMolecule(1L, mid);
    }

    public Molecule findEnclosingMolecule(Triple triple) throws GraphException {
        Long[] localizedTriple = localizer.localizeTriple(triple);
        Long mid = readableIndex.findMid(localizedTriple);
        Long pid = readableIndex.findEnclosingMoleculeId(mid);
        return handler.createMolecule(pid, mid);
    }

    public ClosableIterator<Molecule> findMolecules(SubjectNode subject, PredicateNode predicate, ObjectNode object)
        throws GraphException {
        Triple triple = new TripleImpl(subject, predicate, object);
        return findMolecules(triple);
    }

    public ClosableIterator<Molecule> findMolecules(Triple rootTriple) throws GraphException {
        Long[] localizedTriple = localizer.localizeTriple(rootTriple);
        ClosableIterator<Long> midIterator = readableIndex.findMoleculeIDs(localizedTriple);
        return new ClosableMoleculeIterator(midIterator, handler);
    }

    // TODO Add more stuff here to handle molecule indexes
    public boolean contains(Triple triple) throws GraphException {
        return graph.contains(triple);
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
        return graph.contains(subject, predicate, object);
    }

    public ClosableIterable<Triple> find(Triple triple) throws GraphException {
        return graph.find(triple);
    }

    public ClosableIterable<Triple> find(SubjectNode subject, PredicateNode predicate, ObjectNode object)
        throws GraphException {
        return graph.find(subject, predicate, object);
    }

    public ClosableIterator<Triple> findUnsorted(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        return graph.findUnsorted(subject, predicate, object);
    }

    public ClosableIterable<? extends Node> findNodes(NodeType type) {
        return graph.findNodes(type);
    }

    public ClosableIterable<PredicateNode> findPredicates(Resource resource) throws GraphException {
        return graph.findPredicates(resource);
    }

    public ClosableIterable<Resource> findResources(ValueNodeType type) {
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
        writableIndex.clear();
        graph.clear();
    }

    public void close() {
        writableIndex.close();
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

    public ClosableIterator<Molecule> iterator() throws GraphException {
        return new LongIndexToMoleculeIterator(readableIndex.findChildIds(1L), handler);
    }

    public long getNumberOfMolecules() {
        final ClosableIterator<Long> iterator = readableIndex.findChildIds(1L);
        try {
            long size = 0;
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
            return size;
        } finally {
            iterator.close();
        }
    }

    public Molecule addRootTriple(Molecule molecule, Triple rootTriple) throws GraphException {
        Triple headTriple = molecule.getHeadTriple();
        Long[] tripleAsLongs = localizer.localizeTriple(headTriple);
        ClosableIterator<Long> iterator = readableIndex.findMoleculeIDs(tripleAsLongs);
        try {
            while (iterator.hasNext()) {
                final Long mid = iterator.next();
                if (readableIndex.isSubmoleculeOfParentID(1L, mid)) {
                    Molecule m = handler.createMolecule(1L, mid);
                    if (m != null && moleculeComparator.compare(molecule, m) == 0) {
                        AddMoleculeToIndex amti = new AddMoleculeToIndex(writableIndex, localizer);
                        amti.handleTriple(rootTriple);
                        return molecule.add(rootTriple);
                    }
                }
            }
        } finally {
            iterator.close();
        }
        throw new GraphException("Cannot add new triple to molecule");
    }

    // TODO recursively remove submolecules
    public Molecule removeRootTriple(Molecule molecule, Triple rootTriple) throws GraphException {
        Long[] tripleAsLongs = localizer.localizeTriple(rootTriple);
        ClosableIterator<Long> iterator = readableIndex.findMoleculeIDs(tripleAsLongs);
        try {
            while (iterator.hasNext()) {
                Long aLong = iterator.next();
                if (readableIndex.isSubmoleculeOfParentID(1L, aLong)) {
                    Molecule m = handler.createMolecule(1L, aLong);
                    if (m != null && moleculeComparator.compare(molecule, m) == 0) {
                        return removeSubMolecules(molecule, rootTriple, tripleAsLongs, aLong);
                    }
                }
            }
        } finally {
            iterator.close();
        }
        throw new GraphException("Cannot remove triple from molecule");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        MoleculeTraverser traverser = new MoleculeTraverserImpl();
        try {
            ClosableIterator<Molecule> iterator = iterator();
            try {
                MoleculeHandler handler = new MoleculeToText(builder, localizer.getLocalizer());
                while (iterator.hasNext()) {
                    Molecule molecule = iterator.next();
                    traverser.traverse(molecule, handler);
                }
                return builder.toString();
            } finally {
                iterator.close();
            }
        } catch (GraphException e) {
            throw new RuntimeException("Cannot return string representation", e);
        }
    }

    // TODO finish implementation
    private Molecule removeSubMolecules(Molecule molecule, Triple rootTriple,
        Long[] tripleAsLongs, Long aLong) throws GraphException {
        removeFromIndex(tripleAsLongs, aLong);
        molecule.remove(rootTriple);
        return molecule;
    }

    private void removeFromIndex(Long[] tripleAsLongs, Long mid) throws GraphException {
        ClosableIterator<Long> iterator = readableIndex.findMoleculeIDs(tripleAsLongs);
        while (iterator.hasNext()) {
            Long subMID = iterator.next();
            if (readableIndex.isSubmoleculeOfParentID(mid, subMID)) {
                Long[] quin = new Long[QUIN_SIZE];
                System.arraycopy(tripleAsLongs, 0, quin, 0, QUIN_SIZE - 2);
                writableIndex.remove(quin);
                ClosableIterator<Long[]> triples = readableIndex.findTriplesForMid(mid, subMID);
                while (triples.hasNext()) {
                    Long[] triple = triples.next();
                    removeFromIndex(triple, subMID);
                }
                triples.close();
            }
        }
        iterator.close();
    }
}