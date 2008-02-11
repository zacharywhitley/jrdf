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

package org.jrdf.graph.global.index;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.Triple;
import org.jrdf.graph.global.molecule.mem.NewMolecule;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractNewMoleculeIndexMem implements NewMoleculeIndex, Serializable {
    private static final long serialVersionUID = -8587157481997476484L;
    private Map<Node, Map<Node, Map<Node, NewMolecule>>> index;

    protected AbstractNewMoleculeIndexMem() {
        this.index = new HashMap<Node, Map<Node, Map<Node, NewMolecule>>>();
    }

    protected AbstractNewMoleculeIndexMem(Map<Node, Map<Node, Map<Node, NewMolecule>>> newIndex) {
        this.index = newIndex;
    }

    public void add(Node first, Node second, Node third, NewMolecule molecule) {
        addInternalNodes(first, second, third, molecule);
//        Iterator<Triple> iterator = molecule.tailTriples();
//        while (iterator.hasNext()) {
//            Triple triple = iterator.next();
//            Node[] nodes = getNodes(triple);
//            addInternalNodes(nodes[0], nodes[1], nodes[2], molecule);
//        }
    }

    protected abstract Node[] getNodes(Triple triple);

    private void addInternalNodes(Node first, Node second, Node third, NewMolecule molecule) {
        Map<Node, Map<Node, NewMolecule>> subIndex = index.get(first);

        //check subindex exists
        if (null == subIndex) {
            subIndex = new HashMap<Node, Map<Node, NewMolecule>>();
            index.put(first, subIndex);
        }

        //find the final group
        Map<Node, NewMolecule> group = subIndex.get(second);
        if (null == group) {
            group = new HashMap<Node, NewMolecule>();
            subIndex.put(second, group);
        }
        if (group.containsKey(third)) {
            NewMolecule tmpMolecule = group.remove(third);
            group.put(third, tmpMolecule.add(molecule));
        } else {
            group.put(third, molecule);
        }
    }

    public void remove(Node first, Node second, Node third) throws GraphException {
        NewMolecule molecule = removeInternal(first, second, third);
        Iterator<Triple> iterator = molecule.getRootTriples();
        while (iterator.hasNext()) {
            Triple triple = iterator.next();
            Node[] nodes = getNodes(triple);
            removeInternal(nodes[0], nodes[1], nodes[2]);
        }
    }

    public void remove(NewMolecule molecule) throws GraphException {
        Node[] nodes = getNodes(molecule.getHeadTriple());
        remove(nodes[0], nodes[1], nodes[2]);
    }

    private NewMolecule removeInternal(Node first, Node second, Node third) throws GraphException {
        Map<Node, Map<Node, NewMolecule>> subIndex = index.get(first);
        if (null == subIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }

        Map<Node, NewMolecule> group = subIndex.get(second);
        if (null == group) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        NewMolecule molecule = group.get(third);

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
        return molecule;
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

    public NewMolecule getMolecule(Triple headTriple) {
        Node[] nodes = getNodes(headTriple);
        NewMolecule molecule = null;
        Map<Node, Map<Node, NewMolecule>> subIndex = getSubIndex(nodes[0]);
        if (subIndex != null) {
            Map<Node, NewMolecule> subSubIndex = subIndex.get(nodes[1]);
            if (subSubIndex != null && subSubIndex.containsKey(nodes[2])) {
                molecule = subSubIndex.get(nodes[2]);
            }
        }
        return molecule;
    }

    public long getNumberOfTriples() {
        long size = 0;
        Collection<Map<Node, Map<Node, NewMolecule>>> spoMap = index.values();
        for (Map<Node, Map<Node, NewMolecule>> poMap : spoMap) {
            for (Map<Node, NewMolecule> oMap : poMap.values()) {
                size += oMap.size();
            }
        }
        return size;
    }

    public long getNumberOfMolecules() {
        long size = 0;
        //spo-m
        for (Node node : index.keySet()) {
            Map<Node, Map<Node, NewMolecule>> firstLevelIndex = index.get(node);
            // po-m
            for (Map.Entry<Node, Map<Node, NewMolecule>> mapEntries : firstLevelIndex.entrySet()) {
                //o-m
                for (Map.Entry<Node, NewMolecule> secondLevelIndex : mapEntries.getValue().entrySet()) {
                    //m
                    NewMolecule molecule = secondLevelIndex.getValue();
                    Node[] headNodes = getNodes(molecule.getHeadTriple());
                    if (headNodes[0].equals(node) && headNodes[1].equals(mapEntries.getKey()) &&
                        headNodes[2].equals(secondLevelIndex.getKey())) {
                        size++;
                    }
                }
            }
        }
        return size;
    }

    public Map<Node, Map<Node, NewMolecule>> getSubIndex(Node first) {
        return index.get(first);
    }

    public boolean removeSubIndex(Node first) {
        index.remove(first);
        return index.containsKey(first);
    }


    public Iterator<Map.Entry<Node, Map<Node, Map<Node, NewMolecule>>>> keySetIterator() {
        return index.entrySet().iterator();
    }

    public String toString() {
        return index.toString();
    }
}