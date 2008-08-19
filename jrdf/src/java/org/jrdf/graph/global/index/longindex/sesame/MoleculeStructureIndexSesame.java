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

package org.jrdf.graph.global.index.longindex.sesame;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.index.longindex.MoleculeStructureIndex;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.btree.BTree;
import static org.jrdf.util.btree.ByteHandler.toBytes;
import org.jrdf.util.btree.EntryIteratorFixedLengthLongArray;
import org.jrdf.util.btree.EntryIteratorFourFixedOneArray;
import org.jrdf.util.btree.EntryIteratorOneFixedOneArray;
import org.jrdf.util.btree.EntryIteratorThreeFixedOneArray;
import org.jrdf.util.btree.EntryIteratorTwoFixedOneArray;
import org.jrdf.util.btree.RecordIteratorHelper;

import java.io.IOException;

public final class MoleculeStructureIndexSesame implements MoleculeStructureIndex<Long> {
    private static final int QUIN = 5;
    private BTree btree;

    MoleculeStructureIndexSesame(BTree newBtree) {
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
        try {
            boolean changed = RecordIteratorHelper.remove(btree, node);
            if (!changed) {
                throw new GraphException("Unable to remove nonexistent statement");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        try {
            btree.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ClosableIterator<Long[]> iterator() {
        return new EntryIteratorFixedLengthLongArray(QUIN, btree);
    }

    public ClosableIterator<Long[]> getSubIndex(Long first) {
        return new EntryIteratorFixedLengthLongArray(QUIN, btree, first);
    }

    public ClosableIterator<Long[]> getSubSubIndex(Long first, Long second) {
        return new EntryIteratorFixedLengthLongArray(QUIN, btree, first, second);
    }

    public ClosableIterator<Long[]> getFourthIndex(Long first, Long second, Long third) {
        return new EntryIteratorFixedLengthLongArray(QUIN, btree, first, second, third);
    }

    public ClosableIterator<Long> getFifthIndex(Long first, Long second, Long third, Long fourth) {
        return new EntryIteratorFourFixedOneArray(first, second, third, fourth, btree);
    }

    public ClosableIterator<Long> getFourthIndexOnly(Long first, Long second, Long third) {
        return new EntryIteratorThreeFixedOneArray(first, second, third, btree);
    }

    public ClosableIterator<Long> getFourthForTwoValues(Long first, Long second) {
        return new EntryIteratorTwoFixedOneArray(first, second, btree);
    }

    public ClosableIterator<Long> getFourthForOneValue(Long first) {
        return new EntryIteratorOneFixedOneArray(first, btree);
    }

    public ClosableIterator<Long> getAllFourthIndex() {
        return new EntryIteratorThreeFixedOneArray(0L, 0L, 0L, btree);
    }

    public boolean contains(Long first) {
        try {
            return RecordIteratorHelper.contains(btree, first, QUIN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsPIDMID(Long pid, Long mid) {
        try {
            return RecordIteratorHelper.contains(btree, pid, mid, QUIN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean removeSubIndex(Long first) {
        try {
            return RecordIteratorHelper.removeSubIndex(btree, first);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getSize() {
        try {
            return RecordIteratorHelper.getSize(btree);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sync() {
        try {
            btree.sync();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            btree.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}