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

package org.jrdf.graph.global.molecule;

import static org.jrdf.graph.AbstractBlankNode.isBlankNode;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.molecule.mem.NewMolecule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BlankNodeMapperImpl implements BlankNodeMapper {
    private TripleComparator tripleComparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private Map<BlankNode, BlankNode> map;

    /**
     * TODO: Seems blank nodes of top-level root triples are not added to the map.
     * @param m1
     * @param m2
     * @return
     */
    public Map<BlankNode, BlankNode> createMap(NewMolecule m1, NewMolecule m2) {
        map = new HashMap<BlankNode, BlankNode>();
        Iterator<Triple> m1Triples = m1.getRootTriples();
        while (m1Triples.hasNext()) {
            Triple m1RootTriple = m1Triples.next();
            if (!m2.contains(m1RootTriple)) {
                return map;
            } else {
                Set<NewMolecule> m1NewMolecules = m1.getSubMolecules(m1RootTriple);
                Set<NewMolecule> m2NewMolecules = m2.getSubMolecules(m1RootTriple);
                if (m1NewMolecules.isEmpty() && m2NewMolecules.isEmpty()) {
                    addTerminatingBlankNodes(m2, m1RootTriple);
                } else if (m2NewMolecules.size() == 1 && m1NewMolecules.size() == 1) {
                    if (addNestedBlankNodes(m1NewMolecules, m2NewMolecules)) {
                        return new HashMap<BlankNode, BlankNode>();
                    }
                } else {
                    throw new UnsupportedOperationException("Cannot handle more than one level of submolecules at " +
                        "this time");
                }
            }
        }
        return map;
    }

    private boolean addNestedBlankNodes(Set<NewMolecule> m1NewMolecules, Set<NewMolecule> m2NewMolecules) {
        NewMolecule sm1 = m1NewMolecules.iterator().next();
        NewMolecule sm2 = m2NewMolecules.iterator().next();
        // possible remove this comparison - doesn't break any tests.
        if (sm2.size() >= sm1.size()) {
            Map<BlankNode, BlankNode> newMap = createMap(sm1, sm2);
            if (!newMap.isEmpty()) {
                for (Map.Entry<BlankNode, BlankNode> entry : newMap.entrySet()) {
                    if (map.keySet().contains(entry.getKey()) &&
                        !map.get(entry.getKey()).equals(entry.getValue())) {
                        return true;
                    }
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return false;
    }

    private void addTerminatingBlankNodes(NewMolecule m2, Triple m1RootTriple) {
        Iterator<Triple> m2Triples = m2.getRootTriples();
        while (m2Triples.hasNext()) {
            Triple m2RootTriple = m2Triples.next();
            if (tripleComparator.compare(m1RootTriple, m2RootTriple) == 0) {
                if (isBlankNode(m1RootTriple.getSubject())) {
                    map.put((BlankNode) m2RootTriple.getSubject(), (BlankNode) m1RootTriple.getSubject());
                }
                if (isBlankNode(m2RootTriple.getObject())) {
                    map.put((BlankNode) m2RootTriple.getObject(), (BlankNode) m1RootTriple.getObject());
                }
            }
        }
    }
}
