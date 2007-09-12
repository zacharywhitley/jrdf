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

package org.jrdf.graph.global.index;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.Triple;
import org.jrdf.graph.global.molecule.Molecule;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractMoleculeIndexMem implements MoleculeIndex, Serializable {
    private static final long serialVersionUID = 850704300941647768L;
    private Map<Node, Map<Node, Map<Node, Molecule>>> index;

    protected AbstractMoleculeIndexMem() {
        this.index = new HashMap<Node, Map<Node, Map<Node, Molecule>>>();
    }

    protected AbstractMoleculeIndexMem(Map<Node, Map<Node, Map<Node, Molecule>>> newIndex) {
        this.index = newIndex;
    }

    public void add(Node first, Node second, Node third, Molecule molecule) {
        addInternalNodes(first, second, third, molecule);
        Iterator<Triple> iterator = molecule.tailTriples();
        while (iterator.hasNext()) {
            Triple triple = iterator.next();
            Node[] nodes = getNodes(triple);
            addInternalNodes(nodes[0], nodes[1], nodes[2], molecule);
        }
    }

    protected abstract Node[] getNodes(Triple triple);

    private void addInternalNodes(Node first, Node second, Node third, Molecule molecule) {
        Map<Node, Map<Node, Molecule>> subIndex = index.get(first);

        //check subindex exists
        if (null == subIndex) {
            subIndex = new HashMap<Node, Map<Node, Molecule>>();
            index.put(first, subIndex);
        }

        //find the final group
        Map<Node, Molecule> group = subIndex.get(second);
        if (null == group) {
            group = new HashMap<Node, Molecule>();
            subIndex.put(second, group);
        }
        group.put(third, molecule);
    }

    public void remove(Node first, Node second, Node third) throws GraphException {
        Molecule molecule = removeInternal(first, second, third);
        Iterator<Triple> iterator = molecule.tailTriples();
        while (iterator.hasNext()) {
            Triple triple = iterator.next();
            Node[] nodes = getNodes(triple);
            removeInternal(nodes[0], nodes[1], nodes[2]);
        }
    }

    private Molecule removeInternal(Node first, Node second, Node third) throws GraphException {
        Map<Node, Map<Node, Molecule>> subIndex = index.get(first);
        if (null == subIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }

        Map<Node, Molecule> group = subIndex.get(second);
        if (null == group) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        Molecule molecule = group.get(third);

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

    public long getNumberOfTriples() {
        long size = 0;
        Collection<Map<Node, Map<Node, Molecule>>> spoMap = index.values();
        for (Map<Node, Map<Node, Molecule>> poMap : spoMap) {
            for (Map<Node, Molecule> oMap : poMap.values()) {
                size += oMap.size();
            }
        }
        return size;
    }

    public long getNumberOfMolecules() {
        long size = 0;
        for (Node node0 : index.keySet()) {
            Map<Node, Map<Node, Molecule>> node2ToNode3Map = index.get(node0);
            for (Node node1 : node2ToNode3Map.keySet()) {
                Map<Node, Molecule> node3ToMolecule = node2ToNode3Map.get(node1);
                for (Node node2 : node3ToMolecule.keySet()) {
                    Molecule molecule = node3ToMolecule.get(node2);
                    Node[] headNodes = getNodes(molecule.getHeadTriple());
                    if (headNodes[0].equals(node0) && headNodes[1].equals(node1) && headNodes[2].equals(node2)) {
                        size++;
                    }
                }
            }
        }
        return size;
    }

    public Map<Node, Map<Node, Molecule>> getSubIndex(Node first) {
        return index.get(first);
    }

    public boolean removeSubIndex(Node first) {
        index.remove(first);
        return index.containsKey(first);
    }

    public String toString() {
        return index.toString();
    }
}
