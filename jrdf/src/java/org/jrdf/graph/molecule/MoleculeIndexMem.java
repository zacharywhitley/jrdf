/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.molecule;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MoleculeIndexMem implements MoleculeIndex, Serializable {
    private static final long serialVersionUID = 7410378771227279041L;
    private TripleComparator tripleComparator;
    private Map<Node, Map<Node, Map<Node, SortedSet<Triple>>>> index;

    private MoleculeIndexMem() {
    }

    public MoleculeIndexMem(TripleComparator newTripleComparator) {
        this.index = new HashMap<Node, Map<Node, Map<Node, SortedSet<Triple>>>>();
        this.tripleComparator = newTripleComparator;
    }

    public MoleculeIndexMem(Map<Node, Map<Node, Map<Node, SortedSet<Triple>>>> newIndex,
        TripleComparator newTripleComparator) {
        this.index = newIndex;
        this.tripleComparator = newTripleComparator;
    }

    public void add(Node[] nodes, SortedSet<Triple> tail) {
        add(nodes[0], nodes[1], nodes[2], tail);
    }

    public void add(Node first, Node second, Node third, SortedSet<Triple> tail) {
        Map<Node, Map<Node, SortedSet<Triple>>> subIndex = index.get(first);

        //check subindex exists
        if (null == subIndex) {
            subIndex = new HashMap<Node, Map<Node, SortedSet<Triple>>>();
            index.put(first, subIndex);
        }

        //find the final group
        Map<Node, SortedSet<Triple>> group = subIndex.get(second);
        if (null == group) {
            group = new HashMap<Node, SortedSet<Triple>>();
            subIndex.put(second, group);
        }

        Set<Triple> tailGroup = group.get(third);
        if (null == tailGroup) {
            addTailTriples(tail);
        }
        group.put(third, tail);
    }

    public void remove(Node first, Node second, Node third) throws GraphException {
        Map<Node, Map<Node, SortedSet<Triple>>> subIndex = index.get(first);
        if (null == subIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }

        Map<Node, SortedSet<Triple>> group = subIndex.get(second);
        if (null == group) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        Set<Triple> tailGroup = group.get(third);

        //remove the main index value
        if (group.remove(third) == null) {
            throw new GraphException("Unable to remove nonexistent statement");
        }

        if (group.isEmpty()) {
            subIndex.remove(second);
            if (subIndex.isEmpty()) {
                index.remove(first);
            }
        }
        removeTailTriples(tailGroup);
    }

    private void removeTailTriples(Set<Triple> tailGroup) throws GraphException {
        //remove all other tail triples
        for (Triple triple : tailGroup) {
            remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
        }
    }

    public void remove(Node[] nodes) throws GraphException {
        remove(nodes[0], nodes[1], nodes[2]);
    }

    public void clear() {
        index.clear();
    }

    public boolean contains(Node node) {
        return index.containsKey(node);
    }

    public long numberOfTriples() {
        long size = 0;
        Collection<Map<Node, Map<Node, SortedSet<Triple>>>> spoMap = index.values();
        for (Map<Node, Map<Node, SortedSet<Triple>>> poMap : spoMap) {
            for (Map<Node, SortedSet<Triple>> oMap : poMap.values()) {
                size += oMap.size();
            }
        }
        return size;
    }

    public long numberOfMolecules() {
        long tailTriples = numberOfTailTriples();
        long triples = numberOfTriples();
        return triples - tailTriples;
    }

    public Map<Node, Map<Node, SortedSet<Triple>>> getSubIndex(Node first) {
        return index.get(first);
    }

    public boolean removeSubIndex(Node first) {
        index.remove(first);
        return index.containsKey(first);
    }

    private long numberOfTailTriples() {
        long size = 0;

        Collection<Map<Node, Map<Node, SortedSet<Triple>>>> spoMap = index.values();
        for (Map<Node, Map<Node, SortedSet<Triple>>> poMap : spoMap) {
            for (Map<Node, SortedSet<Triple>> oMap : poMap.values()) {
                Collection<SortedSet<Triple>> sets = oMap.values();
                for (Set<Triple> triples : sets) {
                    if (triples != null) {
                        size += triples.size();
                    }
                }
            }
        }
        return size;
    }

    private Set<Triple> addTailTriples(SortedSet<Triple> tailGroup) {
        Set<Triple> res = new TreeSet<Triple>(tripleComparator);
        if (tailGroup != null) {
            for (Triple triple : tailGroup) {
                SortedSet<Triple> newEmptySet = new TreeSet<Triple>(tripleComparator);
                add(triple.getSubject(), triple.getPredicate(), triple.getObject(), newEmptySet);
                res.add(triple);
            }
        }
        return res;
    }
}
