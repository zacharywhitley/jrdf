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

import org.jrdf.util.ClosableIterator;
import org.jrdf.util.ClosableIteratorImpl;
import static org.jrdf.util.btree.RecordIteratorHelper.getIterator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class EntryIteratorOneFixedOneArray implements ClosableIterator<Long> {
    private static final int QUIN = 5;
    private RecordIterator iterator;
    private byte[] currentValues;
    private Set<Long> set;
    private ClosableIterator<Long> longIterator;

    public EntryIteratorOneFixedOneArray(Long newFirst, BTree newBTree) {
        this.iterator = getIterator(newBTree, newFirst, 0L, 0L, 0L, 0L);
        try {
            this.currentValues = iterator.next();
            this.set = new HashSet<Long>();
            while (currentValues != null) {
                Long[] longs = ByteHandler.fromBytes(currentValues, QUIN);
                set.add(longs[QUIN - 2]);
                currentValues = iterator.next();
            }
            longIterator = new ClosableIteratorImpl<Long>(set.iterator());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean close() {
        try {
            set.clear();
            iterator.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean hasNext() {
        return longIterator.hasNext();
    }

    public Long next() {
        return longIterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove collection values - read only");
    }
}
