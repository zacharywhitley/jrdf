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

package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.local.index.longindex.LongIndex;
import static org.jrdf.graph.local.index.longindex.sesame.ByteHandler.fromBytes;
import static org.jrdf.graph.local.index.longindex.sesame.ByteHandler.toBytes;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class LongIndexSesame implements LongIndex {
    private static final int TRIPLES = 3;
    private TripleBTree btree;

    public LongIndexSesame(TripleBTree newBtree) {
        this.btree = newBtree;
    }

    public void add(Long... node) throws GraphException {
        try {
            btree.insert(toBytes(node));
        } catch (IOException e) {
            throw new GraphException(e);
        }
    }

    public void remove(Long... node) throws GraphException {
        BTreeIterator bTreeIterator = btree.getIterator(node);
        if (getNextBytes(bTreeIterator) == null) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        removeBytes(toBytes(node));
    }

    public void clear() {
        try {
            btree.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator() {
        return new EntryIterator(btree.iterateAll());
    }

    public Map<Long, Set<Long>> getSubIndex(Long first) {
        Map<Long, Set<Long>> resultMap = new HashMap<Long, Set<Long>>();
        BTreeIterator bTreeIterator = btree.getIterator(first, 0L, 0L);
        byte[] bytes = getNextBytes(bTreeIterator);
        while (bytes != null) {
            Long[] longs = fromBytes(bytes, TRIPLES);
            Set<Long> longSet = getLongSet(longs, resultMap);
            longSet.add(longs[2]);
            resultMap.put(longs[1], longSet);
            bytes = getNextBytes(bTreeIterator);
        }
        return resultMap.isEmpty() ? null : resultMap;
    }

    public boolean contains(Long first) {
        byte[] bytes = getNextBytes(btree.getIterator(first, 0L, 0L));
        return bytes != null;
    }

    public boolean removeSubIndex(Long first) {
        BTreeIterator bTreeIterator = btree.getIterator(first, 0L, 0L);
        byte[] bytes = getNextBytes(bTreeIterator);
        boolean changed = bytes != null;
        while (bytes != null) {
            removeBytes(bytes);
            bytes = getNextBytes(bTreeIterator);
        }
        return changed;
    }

    public long getSize() {
        long counter = 0;
        BTreeIterator bTreeIterator = btree.iterateAll();
        while (getNextBytes(bTreeIterator) != null) {
            counter++;
        }
        return counter;
    }

    public void close() {
        try {
            btree.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<Long> getLongSet(Long[] longs, Map<Long, Set<Long>> resultMap) {
        Set<Long> longSet;
        if (resultMap.containsKey(longs[1])) {
            longSet = resultMap.get(longs[1]);
        } else {
            longSet = new HashSet<Long>();
        }
        return longSet;
    }

    private void removeBytes(byte[] bytes) {
        try {
            btree.remove(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getNextBytes(BTreeIterator bTreeIterator) {
        try {
            return bTreeIterator.next();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
