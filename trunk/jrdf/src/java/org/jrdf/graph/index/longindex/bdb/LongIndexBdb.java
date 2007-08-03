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

package org.jrdf.graph.index.longindex.bdb;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.map.MapFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

// TODO Abdul How is this Serializable?
public final class LongIndexBdb  implements LongIndex, Serializable {
    private static final long serialVersionUID = 6044200669651883129L;
    private Map<Long, LinkedList> index;

    private LongIndexBdb() {
    }

    public LongIndexBdb(MapFactory newCreator) {
        index = newCreator.createMap(Long.class, LinkedList.class);
    }

    public LongIndexBdb(Map<Long, LinkedList> newIndex) {
        index = newIndex;
    }

    public void add(Long[] triple) throws GraphException {
        add(triple[0], triple[1], triple[2]);
    }

    public void add(Long first, Long second, Long third) throws GraphException {
        // find the sub index
        LinkedList subIndex = index.get(first);
        // check that the subindex exists
        if (null == subIndex) {
            // no, so create it
            subIndex = new LinkedList();
        }
        boolean found = false;
        for (Iterator itr = subIndex.iterator(); itr.hasNext();) {
            Long[] grp = (Long[]) itr.next();
            if (grp[0].equals(second) && grp[1].equals(third)) {
                found = true;
            }
        }
        if (!found) {
            Long[] group = {second, third};
            subIndex.add(group);
            index.put(first, subIndex);
        }
    }

    public void remove(Long[] triple) throws GraphException {
        remove(triple[0], triple[1], triple[2]);
    }

    public void remove(Long first, Long second, Long third) throws GraphException {
        // find the sub index
        LinkedList subIndex = index.get(first);
        // check that the subindex exists
        if (null == subIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        // find the group
        for (Iterator itr = subIndex.iterator(); itr.hasNext();) {
            Long[] group = (Long[]) itr.next();
            if (second.equals(group[0]) && third.equals(group[1])) {
                subIndex.remove(group);
                if (subIndex.isEmpty()) {
                    index.remove(first);
                }
            }
        }
    }

    public void clear() {
        index.clear();
    }

    public  Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator() {
        Map<Long, Map<Long, Set<Long>>> map = new HashMap<Long, Map<Long, Set<Long>>>();
        Set<Long> set = index.keySet();
        for (Iterator itr = set.iterator(); itr.hasNext();) {
            Long indx = (Long) itr.next();
            Map<Long, Set<Long>> subIndex = getSubIndex(indx);
            map.put(indx, subIndex);
        }
        return map.entrySet().iterator();
    }

    public Map<Long, Set<Long>> getSubIndex(Long first) {
        Set<Long> set = new HashSet<Long>();
        Map<Long, Set<Long>> map = new HashMap<Long, Set<Long>>();
        LinkedList subIndex = index.get(first);
//        Long oldPred = null;
        for (Iterator itr = subIndex.iterator(); itr.hasNext();) {
            Long[] group = (Long[]) itr.next();
            set.add(group[1]);
            map.put(group[0], set);
        }
        return map;
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
        for (Iterator it = index.values().iterator(); it.hasNext();) {
            LinkedList list = (LinkedList) it.next();
            // go over the sub indexes
            size += list.size();
        }
        return size;
    }
}
