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

package org.jrdf.graph.local.index.longindex.db4o;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.map.MapFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

// TODO How is this Serializable?
public final class LongIndexDb4o implements LongIndex, Serializable {
    private static final long serialVersionUID = 6044200669651883129L;
    private MapFactory creator;
    private Map<Long, LinkedList<Long[]>> index;

    private LongIndexDb4o() {
    }

    public LongIndexDb4o(MapFactory newCreator) {
        this.creator = newCreator;
        this.index = creator.createMap(Long.class, LinkedList.class);
    }

    public void add(Long... triple) {
        // find the sub index
        LinkedList<Long[]> subIndex = index.get(triple[0]);
        // check that the subindex exists
        if (null == subIndex) {
            // no, so create it
            subIndex = new LinkedList<Long[]>();
        }
        boolean found = false;
        for (Long[] grp : subIndex) {
            if (grp[0].equals(triple[1]) && grp[1].equals(triple[2])) {
                found = true;
            }
        }
        if (!found) {
            Long[] group = {triple[1], triple[2]};
            subIndex.add(group);
            index.put(triple[0], subIndex);
        }
    }

    public void remove(Long... node) throws GraphException {
        // find the sub index
        LinkedList<Long[]> subIndex = index.get(node[0]);
        // check that the subindex exists
        if (null == subIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        // find the group
        Long[] groupToRemove = null;
        for (Long[] group : subIndex) {
            if (node[1].equals(group[0]) && node[2].equals(group[1])) {
                groupToRemove = group;
                break;
            }
        }
        removeTriple(subIndex, groupToRemove, node[0]);
    }

    private void removeTriple(LinkedList<Long[]> subIndex, Long[] groupToRemove, Long first) {
        subIndex.remove(groupToRemove);
        index.remove(first);
        if (!subIndex.isEmpty()) {
            index.put(first, subIndex);
        }
    }

    public void clear() {
        index.clear();
    }

    public void close() {
        creator.close();
    }

    public Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator() {
        Map<Long, Map<Long, Set<Long>>> map = new HashMap<Long, Map<Long, Set<Long>>>();
        Set<Long> set = index.keySet();
        for (Long indx : set) {
            Map<Long, Set<Long>> subIndex = getSubIndex(indx);
            map.put(indx, subIndex);
        }
        return map.entrySet().iterator();
    }

    public Map<Long, Set<Long>> getSubIndex(Long first) {
        if (index.containsKey(first)) {
            Map<Long, Set<Long>> resultMap = new HashMap<Long, Set<Long>>();
            for (Long[] elements : index.get(first)) {
                Set<Long> longs;
                if (resultMap.containsKey(elements[0])) {
                    longs = resultMap.remove(elements[0]);
                } else {
                    longs = new HashSet<Long>();
                }
                longs.add(elements[1]);
                resultMap.put(elements[0], longs);
            }
            return resultMap;
        } else {
            return null;
        }
    }

    public boolean contains(Long first) {
        return index.containsKey(first);
    }

    public boolean removeSubIndex(Long first) {
        index.remove(first);
        return index.containsKey(first);
    }

    public long getSize() {
        int size = 0;
        // go over the index map
        Set<Map.Entry<Long, LinkedList<Long[]>>> entries = index.entrySet();
        for (Map.Entry<Long, LinkedList<Long[]>> entry : entries) {
            LinkedList<Long[]> list = entry.getValue();
            size += list.size();
        }
        return size;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Long longIndexId : index.keySet()) {
            builder.append("Index: " + longIndexId + "\n");
            LinkedList<Long[]> list = index.get(longIndexId);
            for (Long[] values : list) {
                builder.append("\tValues:" + Arrays.asList(values) + "\n");
            }
        }
        return builder.toString();
    }
}
