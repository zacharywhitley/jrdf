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

package org.jrdf.util.btree;

import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;

public class IteratorBTreeSet implements Set<Map.Entry<Long, Set<Long>>> {
    private final long first;
    private final TripleBTree bTree;

    public IteratorBTreeSet(long first, TripleBTree bTree) {
        this.first = first;
        this.bTree = bTree;
    }

    public int size() {
        int size = 0;
        IteratorBTree tree = new IteratorBTree(first, bTree);
        try {
            while (tree.hasNext()) {
                Map.Entry<Long, Set<Long>> setEntry = tree.next();
                size += setEntry.getValue().size();
            }
            return size;
        } finally {
            tree.close();
        }
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Cannot call this method");
    }

    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Cannot call this method");
    }

    public Iterator<Map.Entry<Long, Set<Long>>> iterator() {
        return new IteratorBTree(first, bTree);
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException("Cannot call this method");
    }

    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Cannot call this method");
    }

    public boolean add(Map.Entry<Long, Set<Long>> o) {
        throw new UnsupportedOperationException("Cannot call this method");
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Cannot call this method");
    }

    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Cannot call this method");
    }

    public boolean addAll(Collection<? extends Map.Entry<Long, Set<Long>>> c) {
        throw new UnsupportedOperationException("Cannot call this method");
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Cannot call this method");
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Cannot call this method");
    }

    public void clear() {
        throw new UnsupportedOperationException("Cannot call this method");
    }
}
