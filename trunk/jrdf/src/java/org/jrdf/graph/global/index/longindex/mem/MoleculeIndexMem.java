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

package org.jrdf.graph.global.index.longindex.mem;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.index.longindex.MoleculeIndex;
import org.jrdf.graph.local.iterator.LongArrayEmptyClosableIterator;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.ClosableIteratorImpl;
import org.jrdf.util.ClosableMap;
import org.jrdf.util.ClosableMapImpl;
import org.jrdf.util.FlatteningFourLongClosableIterator;
import org.jrdf.util.FlatteningThreeLongClosableIterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class MoleculeIndexMem implements MoleculeIndex<Long> {
    protected Map<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> index;

    public MoleculeIndexMem(ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> newIndex) {
        index = newIndex;
    }

    public MoleculeIndexMem() {
        index = new HashMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>();
    }

    public void add(Long... quad) {
        ClosableMap<Long, ClosableMap<Long, Set<Long>>> subIndex = index.get(quad[0]);
        if (null == subIndex) {
            subIndex = new ClosableMapImpl<Long, ClosableMap<Long, Set<Long>>>();
            index.put(quad[0], subIndex);
        }
        ClosableMap<Long, Set<Long>> subSubIndex = subIndex.get(quad[1]);
        if (null == subSubIndex) {
            subSubIndex = new ClosableMapImpl<Long, Set<Long>>();
            subIndex.put(quad[1], subSubIndex);
        }
        Set<Long> subSubSubIndex = subSubIndex.get(quad[2]);
        if (null == subSubSubIndex) {
            subSubSubIndex = new HashSet<Long>();
            subSubIndex.put(quad[2], subSubSubIndex);
        }
        subSubSubIndex.add(quad[3]);
    }

    public boolean contains(Long node) {
        return index.containsKey(node);
    }

    public ClosableIterator<Long[]> iterator() {
        ClosableIteratorImpl<Map.Entry<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>> iterator =
            new ClosableIteratorImpl<Map.Entry<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>>(
                index.entrySet().iterator());
        return new FlatteningFourLongClosableIterator(iterator);
    }

    public void remove(Long... node) throws GraphException {
        ClosableMap<Long, ClosableMap<Long, Set<Long>>> subIndex = index.get(node[0]);
        if (null == subIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        ClosableMap<Long, Set<Long>> subSubIndex = subIndex.get(node[1]);
        if (null == subSubIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        Set<Long> subSubSubIndex = subSubIndex.get(node[2]);
        if (null == subSubSubIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        if (!subSubSubIndex.remove(node[3])) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        if (subSubSubIndex.isEmpty()) {
            subSubIndex.remove(node[2]);
            if (subSubIndex.isEmpty()) {
                subIndex.remove(node[1]);
                if (subIndex.isEmpty()) {
                    index.remove(node[0]);
                }
            }
        }
    }

    public boolean keyExists(Long node) {
        return index.containsKey(node);
    }

    public ClosableIterator<Long[]> getSubIndex(Long first) {
        final ClosableMap<Long, ClosableMap<Long, Set<Long>>> map = index.get(first);
        if (map == null) {
            return new LongArrayEmptyClosableIterator();
        } else {
            final Iterator<Map.Entry<Long, ClosableMap<Long, Set<Long>>>> entryIterator = map.entrySet().iterator();
            return new FlatteningThreeLongClosableIterator(
                    new ClosableIteratorImpl<Map.Entry<Long, ClosableMap<Long, Set<Long>>>>(entryIterator));
        }
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
        for (ClosableMap<Long, ClosableMap<Long, Set<Long>>> subIndex : index.values()) {
            for (Map<Long, Set<Long>> subSubIndex : subIndex.values()) {
                for (Set<Long> subSubSubIndex : subSubIndex.values()) {
                    size += subSubSubIndex.size();
                }
            }
        }
        return size;
    }
}
