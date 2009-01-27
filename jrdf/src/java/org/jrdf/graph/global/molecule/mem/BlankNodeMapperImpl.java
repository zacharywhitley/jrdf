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

package org.jrdf.graph.global.molecule.mem;

import org.jrdf.graph.AbstractBlankNode;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.molecule.BlankNodeMapper;
import org.jrdf.graph.global.molecule.Molecule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Finds the mapping of blank nodes between two molecules.
 *
 * @version $Revision: 1226 $
 */
public class BlankNodeMapperImpl implements BlankNodeMapper {
    private TripleComparator tripleComparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private Map<BlankNode, BlankNode> map;

    public Map<BlankNode, BlankNode> getMap() {
        return map;
    }

    public Map<BlankNode, BlankNode> createMap(Molecule m1, Molecule m2) {
        map = new HashMap<BlankNode, BlankNode>();
        map = populateMap(m1, m2, map);
        return map;
    }

    private Map<BlankNode, BlankNode> populateMap(Molecule m1, Molecule m2, Map<BlankNode, BlankNode> map) {
        Set<Triple> m1RootTriples = m1.getRootTriplesAsSet();
        Set<Triple> m2RootTriples = m2.getRootTriplesAsSet();
        if (m1RootTriples.size() < m2RootTriples.size()) {
            return new HashMap<BlankNode, BlankNode>();
        }
        Iterator<Triple> m1Roots = m1RootTriples.iterator();
        Iterator<Triple> m2Roots = m2RootTriples.iterator();
        while (m2Roots.hasNext()) {
            Triple m2RootTriple = m2Roots.next();
            if (!m1.contains(m2RootTriple)) {
                return new HashMap<BlankNode, BlankNode>();
            }
            findCorrespondingTriple(m1Roots, m2RootTriple, map);
            Set<Molecule> sm1s = m1.getSubMolecules(m2RootTriple);
            Set<Molecule> sm2s = m2.getSubMolecules(m2RootTriple);
            if (!sm1s.isEmpty() && !sm2s.isEmpty()) {
                if (sm1s.size() == 1 && sm2s.size() == 1) {
                    Molecule sm1 = sm1s.iterator().next();
                    Molecule sm2 = sm2s.iterator().next();
                    Map<BlankNode, BlankNode> curMap = populateMap(sm1, sm2, new HashMap<BlankNode, BlankNode>());
                    if (curMap.size() == 0) {
                        return curMap;
                    } else {
                        for (Map.Entry<BlankNode, BlankNode> entry : curMap.entrySet()) {
                            map.put(entry.getKey(), entry.getValue());
                        }
                    }
                } else {
                    throw new UnsupportedOperationException("Cannot handle more than one level of " +
                            "submolecules at this time");
                }
            } else if (sm1s.size() < sm2s.size()) {
                return new HashMap<BlankNode, BlankNode>();
            }
        }
        return map;
    }

    /**
     * In the calling method, it has already guaranteed that m2RootTriple is contained in m1,
     * hence no checking is necessary here.
     * @param m1Roots
     * @param m2RootTriple
     * @param map
     */
    private void findCorrespondingTriple(Iterator<Triple> m1Roots, Triple m2RootTriple, Map<BlankNode, BlankNode> map) {
        while (m1Roots.hasNext()) {
            Triple t1 = m1Roots.next();
            if (tripleComparator.compare(t1, m2RootTriple) == 0) {
                addBlankNodesToMapForTriples(m2RootTriple, t1, map);
                break;
            }
        }
    }

    private void addBlankNodesToMapForTriples(Triple m2RootTriple, Triple t1, Map<BlankNode, BlankNode> map) {
        if (AbstractBlankNode.isBlankNode(t1.getSubject())) {
            map.put((BlankNode) m2RootTriple.getSubject(), (BlankNode) t1.getSubject());
        }
        if (AbstractBlankNode.isBlankNode(t1.getObject())) {
            map.put((BlankNode) m2RootTriple.getObject(), (BlankNode) t1.getObject());
        }
    }
}
