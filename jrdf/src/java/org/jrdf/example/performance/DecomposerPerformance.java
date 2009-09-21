/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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

package org.jrdf.example.performance;

import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.molecule.BlankNodeMapper;
import org.jrdf.graph.global.molecule.GraphDecomposer;
import org.jrdf.graph.global.molecule.MergeSubmolecules;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.MoleculeFactory;
import org.jrdf.graph.global.molecule.mem.BlankNodeMapperImpl;
import org.jrdf.graph.global.molecule.mem.LocalMergeSubmolecules;
import org.jrdf.graph.global.molecule.mem.LocalMergeSubmoleculesImpl;
import org.jrdf.graph.global.molecule.mem.MergeSubmoleculesImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeSubsumptionImpl;
import org.jrdf.graph.global.molecule.mem.NaiveGraphDecomposerImpl;
import org.jrdf.graph.local.TripleComparatorFactoryImpl;
import org.jrdf.collection.MemCollectionFactory;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class DecomposerPerformance {
    private static final int CHAIN_SIZE = 10;
    private static final int LOOP_SIZE = 9;
    private static final int NUMBER_OF_MOLECULES = 1000;
    private final JRDFFactory factory = SortedMemoryJRDFFactory.getFactory();
    private final Graph graph = factory.getNewGraph();
    private final GraphElementFactory elementFactory = graph.getElementFactory();
    private final TripleComparator comparator = new TripleComparatorFactoryImpl().newComparator();
    private final MoleculeComparator moleculeComparator = new MoleculeHeadTripleComparatorImpl(comparator);
    private final MemCollectionFactory setFactory = new MemCollectionFactory();
    private final MoleculeFactory moleculeFactory = new MoleculeFactoryImpl(moleculeComparator);
    private final GraphDecomposer decomposer = new NaiveGraphDecomposerImpl(setFactory, moleculeFactory,
        moleculeComparator, comparator);
    private final BlankNodeMapper mapper = new BlankNodeMapperImpl();
    private final MergeSubmolecules globalMerger = new MergeSubmoleculesImpl(comparator, moleculeComparator,
        moleculeFactory, new MoleculeSubsumptionImpl());
    private final LocalMergeSubmolecules localMerger = new LocalMergeSubmoleculesImpl(globalMerger, moleculeFactory);

    private void testPerformance() throws Exception {
        for (int i = 0; i < NUMBER_OF_MOLECULES; i++) {
            addChain("urn:foo");
        }
        System.out.println("Graph size = " + graph.getNumberOfTriples());
        MoleculeHeadTripleComparatorImpl tripleComparator = new MoleculeHeadTripleComparatorImpl(
            new GroundedTripleComparatorFactoryImpl().newComparator());
        Set<Molecule> results = new TreeSet<Molecule>(tripleComparator);
        long startTime = System.currentTimeMillis();
        Set<Molecule> moleculeSet = decomposer.decompose(graph);
        Molecule[] molecules = moleculeSet.toArray(new Molecule[]{});
        results.add(molecules[0]);
        int count = mergeMolecules(results, moleculeSet);
        System.out.println("Time taken " + (System.currentTimeMillis() - startTime) + ", comparisons: " + count +
            ", no: of triples = " + results.iterator().next().size());
    }

    private int mergeMolecules(Set<Molecule> results, Set<Molecule> molecules) {
        int count = 0;
        int length = molecules.size();
        Vector<Molecule> moleculeArray = new Vector<Molecule>(molecules);
        System.out.println("vec size = " + length + "\n");
        boolean skip = false;
        int i1 = 0, i2;
        while (i1 < length && length > 1) {
            i2 = i1 + 1;
            while (i2 < length) {
                Molecule m1 = moleculeArray.get(i1);
                Molecule m2 = moleculeArray.get(i2);
                Map<BlankNode, BlankNode> map = mapper.createMap(m1, m2);
                Molecule molecule = localMerger.merge(m1, m2, map);
                //map.clear();
                if (molecule != null) {
                    moleculeArray.remove(m1);
                    moleculeArray.remove(m2);
                    moleculeArray.add(molecule);
                    addResult(results, m1);
                    i1 = 0;
                    count++;
                    length = moleculeArray.size();
                    skip = true;
                    break;
                } else {
                    i2++;
                    skip = false;
                }
            }
            if (skip) {
                continue;
            } else {
                i1++;
            }
        }
        return count;
    }

    private void addResult(Set<Molecule> results, Molecule molecule) {
        results.add(molecule);
    }

    private void addGrounded(String predicate) throws Exception {
        URIReference p = elementFactory.createURIReference(URI.create(predicate));
        graph.add(p, p, p);
    }

    private void addTriple(String predicate) throws Exception {
        BlankNode s = elementFactory.createBlankNode();
        URIReference p = elementFactory.createURIReference(URI.create(predicate));
        BlankNode o = elementFactory.createBlankNode();
        graph.add(s, p, o);
    }

    private void addChain(String predicate) throws Exception {
        URIReference p1 = elementFactory.createURIReference(URI.create(predicate));
        BlankNode thisNode = elementFactory.createBlankNode();
        for (int i = 0; i < CHAIN_SIZE; i++) {
            BlankNode nextNode = elementFactory.createBlankNode();
            graph.add(thisNode, p1, nextNode);
            thisNode = nextNode;
        }
    }

    private void addLoop(String predicate) throws Exception {
        URIReference p1 = elementFactory.createURIReference(URI.create(predicate));
        BlankNode firstNode = elementFactory.createBlankNode();
        BlankNode thisNode = firstNode;
        for (int i = 0; i < LOOP_SIZE; i++) {
            BlankNode nextNode = elementFactory.createBlankNode();
            graph.add(thisNode, p1, nextNode);
            thisNode = nextNode;
        }
        graph.add(thisNode, p1, firstNode);
    }

    public static void main(String[] args) throws Exception {
        DecomposerPerformance performance = new DecomposerPerformance();
        performance.testPerformance();
    }
}
