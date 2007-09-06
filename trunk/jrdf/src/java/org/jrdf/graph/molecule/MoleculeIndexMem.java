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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: imrank
 * Date: 6/09/2007
 * Time: 13:51:31
 * To change this template use File | Settings | File Templates.
 */
public class MoleculeIndexMem implements MoleculeIndex, Serializable {
    private static final long serialVersionUID = 7410378771227279041L;

    private Map<Node, Map<Node, Map<Node, Set<Triple>>>> index;

    public MoleculeIndexMem() {
        index = new HashMap<Node, Map<Node, Map<Node, Set<Triple>>>>();
    }

    public MoleculeIndexMem(Map<Node, Map<Node, Map<Node, Set<Triple>>>> newIndex) {
        index = newIndex;
    }

    public void add(Node[] nodes, Set<Triple> tail) {
        add(nodes[0], nodes[1], nodes[2], tail);
    }

    public void add(Node first, Node second, Node third, Set<Triple> tail) {
        Map<Node, Map<Node, Set<Triple>>> subIndex = index.get(first);

        //check subindex exists
        if (null == subIndex) {
            subIndex = new HashMap<Node, Map<Node, Set<Triple>>>();
            index.put(first, subIndex);
        }

        //find the final group
        Map<Node, Set<Triple>> group = subIndex.get(second);

        if (null == group) {
            group = new HashMap<Node, Set<Triple>>();
            subIndex.put(second, group);
        }

        Set<Triple> tailGroup = group.get(third);
        if (null == tailGroup) {
            addTailTriples(tail);
        }
        group.put(third, tail);
    }

    public void remove(Node first, Node second, Node third) throws GraphException {
        Map<Node, Map<Node, Set<Triple>>> subIndex = index.get(first);
        if (null == subIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }

        Map<Node, Set<Triple>> group = subIndex.get(second);
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
        Iterator<Triple> tailGroupIterator = tailGroup.iterator();
        while (tailGroupIterator.hasNext()) {
            Triple triple = tailGroupIterator.next();
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

        Collection<Map<Node, Map<Node, Set<Triple>>>> spoMap = index.values();
        for (Iterator<Map<Node, Map<Node, Set<Triple>>>> spoIterator = spoMap.iterator(); spoIterator.hasNext();) {
            Map<Node, Map<Node, Set<Triple>>> poMap = spoIterator.next();

            Iterator<Map<Node, Set<Triple>>> poIterator = poMap.values().iterator();
            while (poIterator.hasNext()) {
                Map<Node, Set<Triple>> oMap = poIterator.next();
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

    private long numberOfTailTriples() {
        long size = 0;

        Collection<Map<Node, Map<Node, Set<Triple>>>> spoMap = index.values();
        for (Iterator<Map<Node, Map<Node, Set<Triple>>>> spoIterator = spoMap.iterator(); spoIterator.hasNext();) {
            Map<Node, Map<Node, Set<Triple>>> poMap = spoIterator.next();

            Iterator<Map<Node, Set<Triple>>> poIterator = poMap.values().iterator();
            while (poIterator.hasNext()) {
                Map<Node, Set<Triple>> oMap = poIterator.next();
                Collection<Set<Triple>> sets = oMap.values();
                Iterator<Set<Triple>> tailSetIterator = sets.iterator();
                while (tailSetIterator.hasNext()) {
                    Set<Triple> triples = tailSetIterator.next();
                    if (triples != null) {
                        size += triples.size();
                    }
                }
            }
        }
        return size;
    }

    private Set<Triple> addTailTriples(Set<Triple> tailGroup) {
        Set<Triple> res = new HashSet<Triple>();

        if (tailGroup != null) {
            Iterator<Triple> iterator = tailGroup.iterator();
            while (iterator.hasNext()) {
                Triple triple = iterator.next();
                add(triple.getSubject(), triple.getPredicate(), triple.getObject(), Collections.EMPTY_SET);
                res.add(triple);
            }
        }

        return res;
    }
}
