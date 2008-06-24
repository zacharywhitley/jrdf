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

package org.jrdf.graph.global.index.longindex.bdb;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.index.longindex.MoleculeIndex;
import org.jrdf.graph.global.index.longindex.mem.FlatteningClosableIterator;
import org.jrdf.map.MapFactory;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.ClosableIteratorImpl;
import org.jrdf.util.ClosableMap;
import org.jrdf.util.ClosableMapImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class MoleculeIndexBdb implements MoleculeIndex<Long>, Serializable {
    private static final long serialVersionUID = 3900194360118641252L;

    protected Map<Long, ArrayList<Long[]>> index;

    private MoleculeIndexBdb() {
    }

    public MoleculeIndexBdb(MapFactory newCreator) {
        index = newCreator.createMap(Long.class, ArrayList.class);
    }

    public void add(Long... quad) throws GraphException {
        ArrayList<Long[]> subIndex = index.get(quad[0]);
        if (subIndex == null) {
            subIndex = new ArrayList<Long[]>();
        }
        boolean found = false;
        for (Long[] grp : subIndex) {
            if (grp[0].equals(quad[1]) && grp[1].equals(quad[2]) && grp[2].equals(quad[3])) {
                found = true;
                break;
            }
        }
        if (!found) {
            Long[] group = {quad[1], quad[2], quad[3]};
            subIndex.add(group);
            index.put(quad[0], subIndex);
        }
    }

    public void remove(Long... quad) throws GraphException {
        ArrayList<Long[]> subIndx = index.get(quad[0]);
        if (null == subIndx) {
            throw new GraphException("Unable to remove nonexistent molecule");
        }
        Long[] groupToRemove = null;
        for (Long[] group : subIndx) {
            if (group[0].equals(quad[1]) && group[1].equals(quad[2]) && group[2].equals(quad[3])) {
                groupToRemove = group;
                break;
            }
        }
        removeTriple(subIndx, groupToRemove, quad[0]);
    }

    private void removeTriple(ArrayList<Long[]> subIndex, Long[] groupToRemove, Long first) {
        subIndex.remove(groupToRemove);
        index.remove(first);
        if (!subIndex.isEmpty()) {
            index.put(first, subIndex);
        }
    }

    public void clear() {
        index.clear();
    }

    public boolean contains(Long first) {
        return index.containsKey(first);
    }

    public ClosableIterator<Long[]> iterator() {
        //return new FlatteningEntrySetClosableIterator<Long[]>(index.entrySet());
        Map<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> map =
                new HashMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>();
        for (Long key : index.keySet()) {
            map.put(key, getSubIndex(key));
        }
        ClosableIteratorImpl<Map.Entry<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>> iterator =
            new ClosableIteratorImpl<Map.Entry<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>>(
                map.entrySet().iterator());
        return new FlatteningClosableIterator(iterator);
    }

    public boolean removeSubIndex(Long first) {
        boolean containsKey = index.containsKey(first);
        if (containsKey) {
            index.remove(first);
        }
        return containsKey;
    }

    public long getSize() {
        int size = 0;
        // go over the index map
        for (ArrayList<Long[]> list : index.values()) {
            // go over the sub indexes
            size += list.size();
        }
        return size;
    }

    public ClosableMap<Long, ClosableMap<Long, Set<Long>>> getSubIndex(Long first) {
        if (index.containsKey(first)) {
            ClosableMap<Long, ClosableMap<Long, Set<Long>>> resultMap =
                    new ClosableMapImpl<Long, ClosableMap<Long, Set<Long>>>();
            // a triple in Long array
            final ArrayList<Long[]> list = index.get(first);
            for (Long[] triple : list) {
                ClosableMap<Long, Set<Long>> poMap;
                if (resultMap.containsKey(triple[0])) {
                    poMap = resultMap.remove(triple[0]);
                } else {
                    poMap = new ClosableMapImpl<Long, Set<Long>>();
                }
                poMap.put(triple[1], createSubSubIndex(triple, poMap));
                resultMap.put(triple[0], poMap);
            }
            return resultMap;
        } else {
            return null;
        }
    }

    private Set<Long> createSubSubIndex(Long[] triple, ClosableMap<Long, Set<Long>> poMap) {
        Set<Long> objects;
        if (poMap.containsKey(triple[1])) {
            objects = poMap.remove(triple[1]);
        } else {
            objects = new HashSet<Long>();
        }
        objects.add(triple[2]);
        return objects;
    }

    public void close() {
    }

    public boolean keyExists(Long node) {
        return index.containsKey(node);
    }
}
