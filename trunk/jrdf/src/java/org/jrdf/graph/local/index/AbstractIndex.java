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

package org.jrdf.graph.local.index;

import org.jrdf.graph.GraphException;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.ClosableMap;
import org.jrdf.util.ClosableMapImpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractIndex<T> implements Index<T>, Serializable {
    private static final long serialVersionUID = 6761527324041518032L;
    protected Map<T, ClosableMap<T, Set<T>>> index;

    protected AbstractIndex(Map<T, ClosableMap<T, Set<T>>> newIndex) {
        index = newIndex;
    }

    protected AbstractIndex() {
        index = new HashMap<T, ClosableMap<T, Set<T>>>();
    }

    public void add(T... triple) {
        // find the sub index
        ClosableMap<T, Set<T>> subIndex = index.get(triple[0]);
        // check that the subindex exists
        if (null == subIndex) {
            // no, so create it and add it to the index
            subIndex = new ClosableMapImpl<T, Set<T>>();
            index.put(triple[0], subIndex);
        }

        // find the final group
        Set<T> group = subIndex.get(triple[1]);
        // check that the group exists
        if (null == group) {
            // no, so create it and add it to the subindex
            group = new HashSet<T>();
            subIndex.put(triple[1], group);
        }

        // Add the final node to the group
        group.add(triple[2]);
    }

    public boolean contains(T node) {
        return index.containsKey(node);
    }

    public abstract ClosableIterator<T[]> iterator();

    public abstract ClosableIterator<T[]> getSubIndex(T first);

    // TODO AN Make it clean like LongIndexBdb
    // TODO Test drive other graph exceptions - trying to remove no-existent statements.
    // Search for Unable to remove nonexistent statement
    public void remove(T... node) throws GraphException {
        // find the sub index
        Map<T, Set<T>> subIndex = index.get(node[0]);
        // check that the subindex exists
        if (null == subIndex) {
            throw new GraphException("Failed to remove nonexistent triple");
        }
        // find the final group
        Set<T> group = subIndex.get(node[1]);
        // check that the group exists
        if (null == group) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        // remove from the group, report error if it didn't exist
        if (!group.remove(node[2])) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        // clean up the graph
        if (group.isEmpty()) {
            subIndex.remove(node[1]);
            if (subIndex.isEmpty()) {
                index.remove(node[0]);
            }
        }
    }

    public boolean keyExists(T node) {
        return index.containsKey(node);
    }

    public boolean removeSubIndex(T first) {
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
        // go over the index map
        for (Map<T, Set<T>> map : index.values()) {
            // go over the sub indexes
            for (Set<T> s : map.values()) {
                // accumulate the sizes of the groups
                size += s.size();
            }
        }
        return size;
    }
}
