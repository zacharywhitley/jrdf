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

package org.jrdf.graph.local.index.longindex.bdb;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.util.LongArrayEmptyClosableIterator;
import org.jrdf.util.LongEmptyClosableIterator;
import org.jrdf.collection.MapFactory;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.ClosableIteratorImpl;
import org.jrdf.util.FlatteningEntrySetClosableIterator;
import org.jrdf.util.ListToOneValueClosableIterator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class LongIndexBdb implements LongIndex {
    private Map<Long, LinkedList<Long[]>> index;

    private LongIndexBdb() {
    }

    public LongIndexBdb(MapFactory newCreator) {
        index = newCreator.createMap(Long.class, LinkedList.class);
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
                break;
            }
        }
        if (!found) {
            Long[] group = {triple[1], triple[2]};
            subIndex.add(group);
            index.put(triple[0], subIndex);
        }
    }

    public void remove(Long... node) throws GraphException {
        LinkedList<Long[]> subIndex = index.get(node[0]);
        if (subIndex != null) {
            for (Long[] group : subIndex) {
                if (node[1].equals(group[0]) && node[2].equals(group[1])) {
                    removeTriple(subIndex, group, node[0]);
                    return;
                }
            }
        }
        throw new GraphException("Failed to remove nonexistent triple");
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

    public ClosableIterator<Long[]> iterator() {
        return new FlatteningEntrySetClosableIterator(index.entrySet());
    }

    public ClosableIterator<Long[]> getSubIndex(Long first) {
        final List<Long[]> list = index.get(first);
        if (list == null) {
            return new LongArrayEmptyClosableIterator();
        } else {
            return new ClosableIteratorImpl<Long[]>(list.iterator());
        }
    }

    public ClosableIterator<Long> getSubSubIndex(Long first, Long second) {
        final List<Long[]> list = index.get(first);
        if (list == null) {
            return new LongEmptyClosableIterator();
        } else {
            return new ListToOneValueClosableIterator(second, list.iterator());
        }
    }

    public boolean contains(Long first) {
        return index.containsKey(first);
    }

    public boolean removeSubIndex(Long first) {
        final boolean changed = index.containsKey(first);
        index.remove(first);
        return changed;
    }

    public long getSize() {
        int size = 0;
        // go over the index map
        for (LinkedList<Long[]> list : index.values()) {
            // go over the sub indexes
            size += list.size();
        }
        return size;
    }

    public void close() {
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
