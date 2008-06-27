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
import org.jrdf.graph.global.index.longindex.MoleculeStructureIndex;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.ClosableIteratorImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MoleculeStructureIndexMem implements MoleculeStructureIndex<Long> {
    private Map<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>> index;

    public MoleculeStructureIndexMem(Map<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>> newIndex) {
        index = newIndex;
    }

    public MoleculeStructureIndexMem() {
        index = new HashMap<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>>();
    }

    public void add(Long... quin) throws GraphException {
        Map<Long, Map<Long, Map<Long, Set<Long>>>> mids = index.get(quin[0]);
        if (null == mids) {
            mids = new HashMap<Long, Map<Long, Map<Long, Set<Long>>>>();
            index.put(quin[0], mids);
        }
        Map<Long, Map<Long, Set<Long>>> subjectIndex = mids.get(quin[1]);
        if (null == subjectIndex) {
            subjectIndex = new HashMap<Long, Map<Long, Set<Long>>>();
            mids.put(quin[1], subjectIndex);
        }
        Map<Long, Set<Long>> predicateIndex = subjectIndex.get(quin[2]);
        if (null == predicateIndex) {
            predicateIndex = new HashMap<Long, Set<Long>>();
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

    public ClosableIterator<Map.Entry<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>>> iterator() {
        return new ClosableIteratorImpl<Map.Entry<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>>>(
            index.entrySet().iterator());
    }

    public void remove(Long... quin) throws GraphException {
        Map<Long, Map<Long, Map<Long, Set<Long>>>> mids = index.get(quin[0]);
        if (null == mids) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        Map<Long, Map<Long, Set<Long>>> subjectIndex = mids.get(quin[1]);
        if (null == subjectIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        Map<Long, Set<Long>> predicateIndex = subjectIndex.get(quin[2]);
        if (null == predicateIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
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

    public boolean keyExists(Long node) {
        return index.containsKey(node);
    }

    public Map<Long, Map<Long, Map<Long, Set<Long>>>> getSubIndex(Long first) {
        return index.get(first);
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
        for (Map<Long, Map<Long, Map<Long, Set<Long>>>> subIndex : index.values()) {
            for (Map<Long, Map<Long, Set<Long>>> subSubIndex : subIndex.values()) {
                for (Map<Long, Set<Long>> subSubSubIndex : subSubIndex.values()) {
                    for (Set<Long> subSubSubSubIndex : subSubSubIndex.values()) {
                        size += subSubSubSubIndex.size();
                    }
                }
            }
        }
        return size;
    }
}
