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

package org.jrdf.util;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class FlatteningThreeLongClosableIterator implements ClosableIterator<Long[]> {
    protected ClosableIterator<Map.Entry<Long, ClosableMap<Long, Set<Long>>>> iterator;
    protected Iterator<Map.Entry<Long, Set<Long>>> subIterator;
    protected Iterator<Long> itemIterator;
    protected Map.Entry<Long, ClosableMap<Long, Set<Long>>> firstEntry;
    protected Map.Entry<Long, Set<Long>> secondEntry;

    public FlatteningThreeLongClosableIterator(ClosableIterator<Map.Entry<Long,
            ClosableMap<Long, Set<Long>>>> entryIterator) {
        this.iterator = entryIterator;
    }

    public boolean close() {
        return iterator.close();
    }

    public boolean hasNext() {
        return (itemIteratorHasNext() || (subIteratorHasNext()) || (iteratorHasNext()));
    }

    private boolean itemIteratorHasNext() {
        return (null != itemIterator && itemIterator.hasNext());
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
        final Long third = itemIterator.next();
        final Long second = secondEntry.getKey();
        final Long first = firstEntry.getKey();
        return new Long[]{first, second, third};
    }

    /**
     * Helper method to move the iterators on to the next position.
     * If there is no next position then {@link #itemIterator itemIterator}
     * will be set to null, telling {@link #hasNext() hasNext} to return
     * <code>false</code>.
     */
    protected void updatePosition() {
        // progress to the next item if needed
        if (null == itemIterator || !itemIterator.hasNext()) {
            // the current iterator been exhausted
            if (null == subIterator || !subIterator.hasNext()) {
                // the subiterator has been exhausted
                if (!iterator.hasNext()) {
                    // the main iterator has been exhausted
                    // tell the iterator to finish
                    iterator = null;
                    return;
                }
                // move on the main iterator
                firstEntry = iterator.next();

                // now get an iterator to the sub index map
                subIterator = firstEntry.getValue().entrySet().iterator();
            }
            // get the next entry of the sub index
            secondEntry = subIterator.next();
            // get an interator to the next collection from the sub index
            itemIterator = secondEntry.getValue().iterator();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}