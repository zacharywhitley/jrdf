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

import org.jrdf.collection.MapFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.index.longindex.MoleculeIndex;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.ClosableIteratorImpl;
import org.jrdf.util.FlatteningEntrySetClosableIterator;
import org.jrdf.util.ListToOneClosableIterator;
import org.jrdf.util.ListToOneValueClosableIterator;
import org.jrdf.util.ListToTwoValuesClosableIterator;
import org.jrdf.util.LongArrayEmptyClosableIterator;
import org.jrdf.util.LongEmptyClosableIterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class  MoleculeIndexBdb implements MoleculeIndex<Long> {
    protected Map<Long, LinkedList<Long[]>> index;

    public MoleculeIndexBdb(MapFactory newCreator) {
        index = newCreator.createMap(Long.class, LinkedList.class);
    }

    public void add(Long... quad) throws GraphException {
        LinkedList<Long[]> subIndex = index.get(quad[0]);
        if (subIndex == null) {
            subIndex = new LinkedList<Long[]>();
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
        LinkedList<Long[]> subIndex = index.get(quad[0]);
        if (subIndex != null) {
            for (Long[] group : subIndex) {
                if (group[0].equals(quad[1]) && group[1].equals(quad[2]) && group[2].equals(quad[3])) {
                    removeTriple(subIndex, group, quad[0]);
                    return;
                }
            }
        }
        throw new GraphException("Unable to remove nonexistent molecule");
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

    public boolean contains(Long first) {
        return index.containsKey(first);
    }

    public ClosableIterator<Long[]> iterator() {
        return new FlatteningEntrySetClosableIterator(index.entrySet());
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
        for (LinkedList<Long[]> list : index.values()) {
            // go over the sub indexes
            size += list.size();
        }
        return size;
    }

    public ClosableIterator<Long[]> getSubIndex(Long first) {
        final List<Long[]> list = index.get(first);
        if (list == null) {
            return new LongArrayEmptyClosableIterator();
        } else {
            return new ClosableIteratorImpl<Long[]>(list.iterator());
        }
    }

    public ClosableIterator<Long[]> getSubSubIndex(Long first, Long second) {
        final List<Long[]> list = index.get(first);
        if (list == null) {
            return new LongArrayEmptyClosableIterator();
        } else {
            return new ListToTwoValuesClosableIterator(second, list.iterator());
        }
    }

    public ClosableIterator<Long> getMidForTwoValues(Long first, Long second) {
        final List<Long[]> list = index.get(first);
        Set<Long> set = new HashSet<Long>();
        if (list != null) {
            final ClosableIterator<Long> objectIDs = new ListToOneValueClosableIterator(second, list.iterator());
            while (objectIDs.hasNext()) {
                final Long oid = objectIDs.next();
                set = addAllFromIterator(first, second, set, oid);
            }
            objectIDs.close();
            return new ClosableIteratorImpl<Long>(set.iterator());
        }
        return new LongEmptyClosableIterator();
    }

    private Set<Long> addAllFromIterator(Long first, Long second, Set<Long> set, Long oid) {
        final ClosableIterator<Long> subSubSubIndex = getSubSubSubIndex(first, second, oid);
        while (subSubSubIndex.hasNext()) {
            set.add(subSubSubIndex.next());
        }
        subSubSubIndex.close();
        return set;
    }

    public ClosableIterator<Long> getAllMIDs() {
        final ClosableIterator<Long[]> iterator = this.iterator();
        Set<Long> set = new HashSet<Long>();
        while (iterator.hasNext()) {
            final Long[] longs = iterator.next();
            set.add(longs[3]);
        }
        iterator.close();
        return new ClosableIteratorImpl<Long>(set.iterator());
    }

    public ClosableIterator<Long> getMidForOneValue(Long first) {
        final List<Long[]> list = index.get(first);
        Set<Long> set = new HashSet<Long>();
        if (list != null) {
            for (Long[] entry : list) {
                set.add(entry[2]);
            }
            return new ClosableIteratorImpl<Long>(set.iterator());
        }
        return new LongEmptyClosableIterator();
    }

    public void close() {
    }

    public ClosableIterator<Long> getSubSubSubIndex(Long first, Long second, Long third) {
        LinkedList<Long[]> list = index.get(first);
        if (list != null) {
            Iterator<Long[]> iterator = list.iterator();
            return new ListToOneClosableIterator(second, third, iterator);
        }
        return new LongEmptyClosableIterator();
    }
}
