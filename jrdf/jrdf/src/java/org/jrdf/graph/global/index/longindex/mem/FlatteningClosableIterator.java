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

import org.jrdf.util.ClosableIterator;
import org.jrdf.util.ClosableMap;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class FlatteningClosableIterator implements ClosableIterator<Long[]> {
    private ClosableIterator<Map.Entry<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>> iterator;
    private Iterator<Map.Entry<Long, ClosableMap<Long, Set<Long>>>> subIterator;
    private Iterator<Map.Entry<Long, Set<Long>>> subSubIterator;
    private Iterator<Long> itemIterator;
    private Map.Entry<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> firstEntry;
    private Map.Entry<Long, ClosableMap<Long, Set<Long>>> secondEntry;
    private Map.Entry<Long, Set<Long>> thirdEntry;

    public FlatteningClosableIterator(ClosableIterator<Map.Entry<Long, ClosableMap<Long,
        ClosableMap<Long, Set<Long>>>>> newIterator) {
        this.iterator = newIterator;
    }

    public boolean close() {
        return iterator.close();
    }

    public boolean hasNext() {
        return (itemIteratorHasNext() || (subSubIteratorHasNext()) || (subIteratorHasNext()) || (iteratorHasNext()));
    }

    private boolean itemIteratorHasNext() {
        return (null != itemIterator && itemIterator.hasNext());
    }

    private boolean subSubIteratorHasNext() {
        return null != subSubIterator && subSubIterator.hasNext();
    }

    private boolean subIteratorHasNext() {
        return null != subIterator && subIterator.hasNext();
    }

    private boolean iteratorHasNext() {
        return null != iterator && iterator.hasNext();
    }

    public Long[] next() {
        if (null == iterator) {
            throw new NoSuchElementException();
        }

        // move to the next position
        updatePosition();

        if (null == iterator) {
            throw new NoSuchElementException();
        }
        Long fourth = itemIterator.next();
        Long third = thirdEntry.getKey();
        Long second = secondEntry.getKey();
        Long first = firstEntry.getKey();
        return new Long[]{first, second, third, fourth};
    }

    /**
     * Helper method to move the iterators on to the next position.
     * If there is no next position then {@link #itemIterator itemIterator}
     * will be set to null, telling {@link #hasNext() hasNext} to return
     * <code>false</code>.
     */
    private void updatePosition() {
        // progress to the next item if needed
        if (null == itemIterator || !itemIterator.hasNext()) {
            if (hasMoreEntries()) {
                thirdEntry = subSubIterator.next();
                itemIterator = thirdEntry.getValue().iterator();
            }
        }
    }

    private boolean hasMoreEntries() {
        if (null == subSubIterator || !subSubIterator.hasNext()) {
            if (null == subIterator || !subIterator.hasNext()) {
                if (!iterator.hasNext()) {
                    iterator = null;
                    return false;
                }
                // move on the main iterator
                firstEntry = iterator.next();
                subIterator = firstEntry.getValue().entrySet().iterator();
            }
            secondEntry = subIterator.next();
            subSubIterator = secondEntry.getValue().entrySet().iterator();
        }
        return true;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}