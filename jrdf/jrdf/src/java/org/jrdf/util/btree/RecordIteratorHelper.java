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

import java.io.IOException;

public final class RecordIteratorHelper {
    private static final int OFFSET = 8;
    private static final long ON_MASK = 0xffffffffffffffffL;

    private RecordIteratorHelper() {
    }

    public static RecordIterator getIterator(BTree btree, Long... node) {
        int indexSize = node.length;
        int valueSize = indexSize * OFFSET;
        byte[] key = ByteHandler.toBytes(node);
        byte[] filter = new byte[valueSize];
        byte[] minValue = new byte[valueSize];
        byte[] maxValue = new byte[valueSize];
        for (int i = 0; i < indexSize; i++) {
            addToFilter(filter, i, node);
            addToMinValue(minValue, i, node);
            addToMaxValue(maxValue, i, node);
        }
        return btree.iterateRangedValues(key, filter, minValue, maxValue);
    }

    private static void addToFilter(byte[] filter, int index, Long... node) {
        if (node[index] != 0) {
            ByteArrayUtil.putLong(ON_MASK, filter, index * OFFSET);
        }
    }

    private static void addToMinValue(byte[] minValue, int index, Long... node) {
        ByteArrayUtil.putLong(node[index], minValue, index * OFFSET);
    }

    private static void addToMaxValue(byte[] maxValue, int index, Long... node) {
        if (node[index] == 0) {
            ByteArrayUtil.putLong(ON_MASK, maxValue, index * OFFSET);
        } else {
            ByteArrayUtil.putLong(node[index], maxValue, index * OFFSET);
        }
    }

    public static boolean contains(BTree btree, Long first, int length) throws IOException {
        Long[] longs = new Long[length];
        longs[0] = first;
        for (int i = 1; i < length; i++) {
            longs[i] = 0L;
        }
        RecordIterator iterator = getIterator(btree, longs);
        try {
            byte[] bytes = iterator.next();
            return bytes != null;
        } finally {
            iterator.close();
        }
    }

    public static boolean contains(BTree btree, Long first, Long second, int length) throws IOException {
        Long[] longs = new Long[length];
        longs[0] = first;
        longs[1] = second;
        for (int i = 2; i < length; i++) {
            longs[i] = 0L;
        }
        RecordIterator iterator = getIterator(btree, longs);
        try {
            byte[] bytes = iterator.next();
            return bytes != null;
        } finally {
            iterator.close();
        }
    }

    public static boolean remove(BTree btree, Long... node) throws IOException {
        RecordIterator iterator = getIterator(btree, node);
        try {
            if (iterator.next() == null) {
                return false;
            }
            btree.remove(ByteHandler.toBytes(node));
            return true;
        } finally {
            iterator.close();
        }
    }

    public static boolean removeSubIndex(BTree btree, Long first) throws IOException {
        RecordIterator iterator = getIterator(btree, first, 0L, 0L, 0L);
        try {
            byte[] bytes = iterator.next();
            boolean changed = bytes != null;
            while (bytes != null) {
                btree.remove(bytes);
                bytes = iterator.next();
            }
            return changed;
        } finally {
            iterator.close();
        }

    }

    public static long getSize(BTree btree) throws IOException {
        RecordIterator recordIterator = btree.iterateAll();
        try {
            long counter = 0;
            while (recordIterator.next() != null) {
                counter++;
            }
            return counter;
        } finally {
            recordIterator.close();
        }
    }
}
