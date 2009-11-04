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

package org.jrdf.graph.global.index.longindex.mem;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.index.longindex.MoleculeStructureIndex;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.ClosableIteratorImpl;
import org.jrdf.util.ClosableMap;
import org.jrdf.util.ClosableMapImpl;
import org.jrdf.util.EmptyClosableIterator;
import org.jrdf.util.FlatteningFiveLongClosableIterator;
import org.jrdf.util.FlatteningFourLongClosableIterator;
import org.jrdf.util.FlatteningThreeLongClosableIterator;
import org.jrdf.util.FlatteningTwoLongClosableIterator;
import org.jrdf.util.LongArrayEmptyClosableIterator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MoleculeStructureIndexMem implements MoleculeStructureIndex<Long> {
    private ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>> index;

    public MoleculeStructureIndexMem(ClosableMap<Long, ClosableMap<Long, ClosableMap<Long,
            ClosableMap<Long, Set<Long>>>>> newIndex) {
        index = newIndex;
    }

    public MoleculeStructureIndexMem() {
        index = new ClosableMapImpl<Long, ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>>();
    }

    public void add(Long... quin) throws GraphException {
        ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> mids = index.get(quin[0]);
        if (null == mids) {
            mids = new ClosableMapImpl<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>();
            index.put(quin[0], mids);
        }
        ClosableMap<Long, ClosableMap<Long, Set<Long>>> subjectIndex = mids.get(quin[1]);
        if (null == subjectIndex) {
            subjectIndex = new ClosableMapImpl<Long, ClosableMap<Long, Set<Long>>>();
            mids.put(quin[1], subjectIndex);
        }
        ClosableMap<Long, Set<Long>> predicateIndex = subjectIndex.get(quin[2]);
        if (null == predicateIndex) {
            predicateIndex = new ClosableMapImpl<Long, Set<Long>>();
            subjectIndex.put(quin[2], predicateIndex);
        }
        Set<Long> objectIndex = predicateIndex.get(quin[3]);
        if (null == objectIndex) {
            objectIndex = new HashSet<Long>();
            predicateIndex.put(quin[3], objectIndex);
        }
        objectIndex.add(quin[4]);
    }

    public boolean contains(Long node) {
        return index.containsKey(node);
    }

    public ClosableIterator<Long[]> iterator() {
        ClosableIterator<Map.Entry<Long, ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>>> iterator =
            new ClosableIteratorImpl<Map.Entry<Long, ClosableMap<Long, ClosableMap<Long, ClosableMap<Long,
                Set<Long>>>>>>(index.entrySet().iterator());
        return new FlatteningFiveLongClosableIterator(iterator);
    }

    // TODO Cover the other graph exceptions in tests - currently only the first one is test driven.
    // Search for Unable to remove nonexistent statement.
    public void remove(Long... quin) throws GraphException {
        ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> mids = index.get(quin[0]);
        if (null == mids) {
            throw new GraphException("Failed to remove nonexistent triple");
        }
        ClosableMap<Long, ClosableMap<Long, Set<Long>>> subjectIndex = mids.get(quin[1]);
        if (null == subjectIndex) {
            throw new GraphException("Failed to remove nonexistent triple");
        }
        Map<Long, Set<Long>> predicateIndex = subjectIndex.get(quin[2]);
        if (null == predicateIndex) {
            throw new GraphException("Failed to remove nonexistent triple");
        }
        Set<Long> objectIndex = predicateIndex.get(quin[3]);
        if (null == objectIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        if (!objectIndex.remove(quin[4])) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        if (objectIndex.isEmpty()) {
            predicateIndex.remove(quin[3]);
            if (predicateIndex.isEmpty()) {
                subjectIndex.remove(quin[2]);
                if (subjectIndex.isEmpty()) {
                    mids.remove(quin[1]);
                    if (mids.isEmpty()) {
                        index.remove(quin[0]);
                    }
                }
            }
        }
    }

    public boolean containsPIDMID(Long pid, Long mid) {
        final ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> map = index.get(pid);
        if (map != null) {
            return map.containsKey(mid);
        }
        return false;
    }

    public ClosableIterator<Long[]> getSubIndex(Long first) {
        ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> map = index.get(first);
        if (map == null) {
            return new LongArrayEmptyClosableIterator();
        } else {
            Iterator<Map.Entry<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>> entryIterator =
                    map.entrySet().iterator();
            return new FlatteningFourLongClosableIterator(new ClosableIteratorImpl<Map.Entry<Long,
                    ClosableMap<Long, ClosableMap<Long, Set<Long>>>>>(entryIterator));
        }
    }

    public ClosableIterator<Long[]> getSubSubIndex(Long first, Long second) {
        ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> subIndex = index.get(first);
        if (subIndex != null) {
            ClosableMap<Long, ClosableMap<Long, Set<Long>>> map = subIndex.get(second);
            if (map != null) {
                Iterator<Map.Entry<Long, ClosableMap<Long, Set<Long>>>> entryIterator = map.entrySet().iterator();
                return new FlatteningThreeLongClosableIterator(
                        new ClosableIteratorImpl<Map.Entry<Long, ClosableMap<Long, Set<Long>>>>(entryIterator));
            }
        }
        return new LongArrayEmptyClosableIterator();
    }

    public ClosableIterator<Long[]> getFourthIndex(Long first, Long second, Long third) {
        ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> subIndex = index.get(first);
        if (subIndex != null) {
            ClosableMap<Long, ClosableMap<Long, Set<Long>>> subSubIndex = subIndex.get(second);
            if (subSubIndex != null) {
                final ClosableMap<Long, Set<Long>> map = subSubIndex.get(third);
                if (map != null) {
                    return new FlatteningTwoLongClosableIterator(map);
                }
            }
        }
        return new LongArrayEmptyClosableIterator();
    }

    public ClosableIterator<Long> getFifthIndex(Long first, Long second, Long third, Long fourth) {
        ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> firstIndex = index.get(first);
        if (firstIndex != null) {
            ClosableMap<Long, ClosableMap<Long, Set<Long>>> secondIndex = firstIndex.get(second);
            if (secondIndex != null) {
                ClosableMap<Long, Set<Long>> thirdIndex = secondIndex.get(third);
                if (thirdIndex != null) {
                    Set<Long> fourthIndex = thirdIndex.get(fourth);
                    return new ClosableIteratorImpl<Long>(fourthIndex.iterator());
                }
            }
        }
        return new EmptyClosableIterator<Long>();
    }

    public ClosableIterator<Long> getFourthIndexOnly(Long first, Long second, Long third) {
        ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> firstIndex = index.get(first);
        if (firstIndex != null) {
            ClosableMap<Long, ClosableMap<Long, Set<Long>>> secondIndex = firstIndex.get(second);
            if (secondIndex != null) {
                ClosableMap<Long, Set<Long>> thirdIndex = secondIndex.get(third);
                if (thirdIndex != null) {
                    return new ClosableIteratorImpl<Long>(thirdIndex.keySet().iterator());
                }
            }
        }
        return new EmptyClosableIterator<Long>();
    }

    public ClosableIterator<Long> getAllFourthIndex() {
        Set<Long> longs = new HashSet<Long>();
        final Collection<ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>> collection =
                index.values();
        for (ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> map : collection) {
            final Collection<ClosableMap<Long, ClosableMap<Long, Set<Long>>>> subCollection = map.values();
            for (ClosableMap<Long, ClosableMap<Long, Set<Long>>> subMap : subCollection) {
                final Collection<ClosableMap<Long, Set<Long>>> subSubCollection = subMap.values();
                for (ClosableMap<Long, Set<Long>> subSubMap : subSubCollection) {
                    longs.addAll(subSubMap.keySet());
                }
            }
        }
        return new ClosableIteratorImpl<Long>(longs.iterator());
    }

    public ClosableIterator<Long> getFourthForTwoValues(Long first, Long second) {
        final ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> firstMap = index.get(first);
        Set<Long> longs = new HashSet<Long>();
        if (firstMap != null) {
            final ClosableMap<Long, ClosableMap<Long, Set<Long>>> secondMap = firstMap.get(second);
            if (secondMap != null) {
                final Collection<ClosableMap<Long, Set<Long>>> closableMaps = secondMap.values();
                return new ClosableIteratorImpl<Long>(squash(closableMaps, longs).iterator());
            }
        }
        return new EmptyClosableIterator<Long>();
    }

    public ClosableIterator<Long> getFourthForOneValue(Long first) {
        final ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> subIndex = index.get(first);
        Set<Long> longs = new HashSet<Long>();
        if (subIndex != null) {
            final Collection<ClosableMap<Long, ClosableMap<Long, Set<Long>>>> collection = subIndex.values();
            return new ClosableIteratorImpl<Long>(moreSquash(collection, longs).iterator());
        }
        return new EmptyClosableIterator<Long>();
    }

    private Set<Long> moreSquash(Collection<ClosableMap<Long, ClosableMap<Long, Set<Long>>>> collection,
                                 Set<Long> longs) {
        final Iterator<ClosableMap<Long, ClosableMap<Long, Set<Long>>>> iterator = collection.iterator();
        while (iterator.hasNext()) {
            final ClosableMap<Long, ClosableMap<Long, Set<Long>>> map = iterator.next();
            longs = squash(map.values(), longs);
        }
        return longs;
    }

    private Set<Long> squash(Collection<ClosableMap<Long, Set<Long>>> collection, Set<Long> longs) {
        final Iterator<ClosableMap<Long, Set<Long>>> iterator = collection.iterator();
        while (iterator.hasNext()) {
            final ClosableMap<Long, Set<Long>> map = iterator.next();
            longs.addAll(map.keySet());
        }
        return longs;
    }

    public boolean removeSubIndex(Long first) {
        final boolean containsKey = index.containsKey(first);
        index.remove(first);
        return containsKey;
    }

    public void clear() {
        index.clear();
    }

    public void close() {
    }

    public long getSize() {
        long size = 0;
        for (ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> subIndex : index.values()) {
            for (ClosableMap<Long, ClosableMap<Long, Set<Long>>> subSubIndex : subIndex.values()) {
                for (ClosableMap<Long, Set<Long>> subSubSubIndex : subSubIndex.values()) {
                    for (Set<Long> subSubSubSubIndex : subSubSubIndex.values()) {
                        size += subSubSubSubIndex.size();
                    }
                }
            }
        }
        return size;
    }
}
