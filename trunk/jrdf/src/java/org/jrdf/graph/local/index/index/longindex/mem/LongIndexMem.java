/*
 * $Header$
 * $Revision: 1247 $
 * $Date: 2007-07-23 09:35:40 +1000 (Mon, 23 Jul 2007) $
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

package org.jrdf.graph.local.index.index.longindex.mem;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.local.index.index.longindex.LongIndex;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An in memory version of ${@link org.jrdf.graph.local.index.index.longindex.LongIndex}.
 *
 * @author Andrew Newman
 * @version $Revision: 1247 $
 */
public final class LongIndexMem implements LongIndex, Serializable {
    private static final long serialVersionUID = -8212815756891200898L;
    private Map<Long, Map<Long, Set<Long>>> index;

    public LongIndexMem() {
        index = new HashMap<Long, Map<Long, Set<Long>>>();
    }

    public LongIndexMem(Map<Long, Map<Long, Set<Long>>> newIndex) {
        index = newIndex;
    }

    public void add(Long[] triple) {
        add(triple[0], triple[1], triple[2]);
    }

    public void add(Long first, Long second, Long third) {
        // find the sub index
        Map<Long, Set<Long>> subIndex = index.get(first);
        // check that the subindex exists
        if (null == subIndex) {
            // no, so create it and add it to the index
            subIndex = new HashMap<Long, Set<Long>>();
            index.put(first, subIndex);
        }

        // find the final group
        Set<Long> group = subIndex.get(second);
        // check that the group exists
        if (null == group) {
            // no, so create it and add it to the subindex
            group = new HashSet<Long>();
            subIndex.put(second, group);
        }

        // Add the final node to the group
        group.add(third);
    }

    public void remove(Long[] triple) throws GraphException {
        remove(triple[0], triple[1], triple[2]);
    }

    public void remove(Long first, Long second, Long third) throws GraphException {

        // find the sub index
        Map<Long, Set<Long>> subIndex = index.get(first);
        // check that the subindex exists
        if (null == subIndex) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        // find the final group
        Set<Long> group = subIndex.get(second);
        // check that the group exists
        if (null == group) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        // remove from the group, report error if it didn't exist
        if (!group.remove(third)) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        // clean up the graph
        if (group.isEmpty()) {
            subIndex.remove(second);
            if (subIndex.isEmpty()) {
                index.remove(first);
            }
        }
    }

    public void clear() {
        index.clear();
    }

    public Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator() {
        return index.entrySet().iterator();
    }

    public Map<Long, Set<Long>> getSubIndex(Long first) {
        return index.get(first);
    }

    public boolean contains(Long first) {
        return index.containsKey(first);
    }

    public boolean removeSubIndex(Long first) {
        index.remove(first);
        return index.containsKey(first);
    }

    public long getSize() {
        long size = 0;
        // go over the index map
        for (Map<Long, Set<Long>> map : index.values()) {
            // go over the sub indexes
            for (Set<Long> s : map.values()) {
                // accumulate the sizes of the groups
                size += s.size();
            }
        }
        return size;
    }
}