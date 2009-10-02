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

package org.jrdf.graph.global;

import org.jrdf.graph.AbstractBlankNode;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.global.index.ReadableIndex;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.mem.MoleculeImpl;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.util.ClosableIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version :$
 */
public class MoleculeGraphHandlerImpl implements MoleculeGraphHandler {
    private NodePool nodePool;
    private ReadableIndex<Long> readableIndex;
    private MoleculeComparator moleculeComparator;

    public MoleculeGraphHandlerImpl(final NodePool newNodePool, final ReadableIndex<Long> newReadableIndex,
        final MoleculeComparator newMoleculeComparator) {
        this.nodePool = newNodePool;
        this.readableIndex = newReadableIndex;
        this.moleculeComparator = newMoleculeComparator;
    }

    public Molecule createMolecule(final Long pid, final Long mid) throws GraphException {
        return reconstructMolecule(null, pid, mid);
    }

    // TODO if a sub molecule is to be returned, the isTopLevel field isn't correctly set when creating the molecule.
    // TODO Maybe get the top-level molecule and return the subMolecule?
    private Molecule reconstructMolecule(Molecule parentMolecule, Long pid, Long mid) throws GraphException {
        Triple[] roots = iteratorAsTriples(readableIndex.findTriplesForMid(pid, mid));
        Map<BlankNode, Triple> rootTripleMap = getBNodeToRootMap(parentMolecule);
        Molecule molecule = new MoleculeImpl(moleculeComparator, roots);
        molecule = createSubMolecules(mid, molecule);
        if (null == parentMolecule) {
            return molecule;
        } else {
            Triple linkingTriple = findLinkingTriple(parentMolecule, roots, rootTripleMap, molecule);
            parentMolecule.add(linkingTriple, molecule);
            return parentMolecule;
        }
    }

    private Triple[] iteratorAsTriples(ClosableIterator<Long[]> iterator) {
        try {
            List<Triple> triples = new ArrayList<Triple>();
            while (iterator.hasNext()) {
                Long[] longs = iterator.next();
                Triple triple = new TripleImpl((SubjectNode) nodePool.getNodeById(longs[0]),
                    (PredicateNode) nodePool.getNodeById(longs[1]), (ObjectNode) nodePool.getNodeById(longs[2]));
                triples.add(triple);
            }
            Triple[] tripleArray = new Triple[triples.size()];
            return triples.toArray(tripleArray);
        } finally {
            iterator.close();
        }
    }

    private Map<BlankNode, Triple> getBNodeToRootMap(Molecule molecule) {
        if (null == molecule) {
            return null;
        }
        final Iterator<Triple> triples = molecule.getRootTriples();
        Map<BlankNode, Triple> rootTripleMap = new HashMap<BlankNode, Triple>();
        while (triples.hasNext()) {
            Triple triple = triples.next();
            ObjectNode obj = triple.getObject();
            if (obj instanceof AbstractBlankNode) {
                rootTripleMap.put((BlankNode) obj, triple);
            }
        }
        return rootTripleMap;
    }

    private Molecule createSubMolecules(Long mid, Molecule molecule) throws GraphException {
        ClosableIterator<Long> childIDs = readableIndex.findChildIds(mid);
        while (childIDs.hasNext()) {
            Long childID = childIDs.next();
            molecule = reconstructMolecule(molecule, mid, childID);
        }
        childIDs.close();
        return molecule;
    }

    // TODO YF a more robust version of findLinkingTriple is needed.
    private Triple findLinkingTriple(Molecule parentMolecule, Triple[] roots, Map<BlankNode, Triple> rootTripleMap,
                                     Molecule molecule) throws GraphException {
        for (Triple triple : roots) {
            SubjectNode sub = triple.getSubject();
            if (AbstractBlankNode.isBlankNode(sub) && mapContainsBNode(rootTripleMap, sub)) {
                return rootTripleMap.get(sub);
            }

        }
        throw new GraphException("Cannot find the linking triple for parent : " + parentMolecule +
                "\nand submolecule" + molecule);
    }

    private boolean mapContainsBNode(Map<BlankNode, Triple> nodeTripleMap, SubjectNode sub) {
        return nodeTripleMap != null && nodeTripleMap.containsKey(sub);
    }
}
