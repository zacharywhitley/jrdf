/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.local.index.operation.mem;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.util.ClosableIterator;

import java.util.Set;


/**
 * Just a spike.  Please test drive.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class BasicOperations {

    // TODO AN Finish making things small and common - then test drive.
    public static void copyEntriesToIndex(LongIndex existingIndex, LongIndex newIndex) throws GraphException {
        DoTripleStuff doCopyTripleStuff = new DoCopyTripleStuff(newIndex);
        goOverIndex(existingIndex, doCopyTripleStuff);
    }

    public static void reconstruct(LongIndex existingIndex, LongIndex firstNewIndex, LongIndex secondNewIndex) throws
        GraphException {
        DoTripleStuff doReconstructTripleStuff = new DoReconstructTripleStuff(firstNewIndex, secondNewIndex);
        goOverIndex(existingIndex, doReconstructTripleStuff);
    }

    private static void goOverIndex(LongIndex existingIndex, DoTripleStuff doTripleStuff) throws GraphException {
        ClosableIterator<Long[]> tripleIterator = existingIndex.iterator();
        try {
            while (tripleIterator.hasNext()) {
                Long[] triple = tripleIterator.next();
                doTripleStuff.doStuff(triple[0], triple[1], triple[2]);
            }
        } finally {
            tripleIterator.close();
        }
    }

    public static void removeEntriesFromIndex(LongIndex existingIndex, LongIndex newIndex) throws GraphException {
        ClosableIterator<Long[]> firstEntries = existingIndex.iterator();
        try {
            while (firstEntries.hasNext()) {
                Long[] values = firstEntries.next();
                newIndex.remove(values);
            }
        } finally {
            firstEntries.close();
        }
    }

    public static void performIntersection(LongIndex firstExistingIndex, LongIndex secondExistingIndex,
        LongIndex newIndex) throws GraphException {
        ClosableIterator<Long[]> firstEntries = firstExistingIndex.iterator();
        ClosableIterator<Long[]> secondEntries = secondExistingIndex.iterator();
        try {
            while (firstEntries.hasNext()) {
                newIndex.add(firstEntries.next());
            }
            while (secondEntries.hasNext()) {
                newIndex.add(secondEntries.next());
            }
        } finally {
            firstEntries.close();
            secondEntries.close();
        }
    }

    interface DoTripleStuff {
        void doStuff(Long first, Long second, Long third) throws GraphException;
    }

    public static class DoCopyTripleStuff implements DoTripleStuff {
        private LongIndex index;

        public DoCopyTripleStuff(LongIndex newIndex) {
            this.index = newIndex;
        }

        public void doStuff(Long first, Long second, Long third) throws GraphException {
            index.add(first, second, third);
        }
    }

    public static class DoReconstructTripleStuff implements DoTripleStuff {
        private LongIndex index1;
        private LongIndex index2;

        public DoReconstructTripleStuff(LongIndex newIndex1, LongIndex newIndex2) {
            this.index1 = newIndex1;
            this.index2 = newIndex2;
        }

        public void doStuff(Long first, Long second, Long third) throws GraphException {
            index1.add(second, third, first);
            index2.add(third, first, second);
        }
    }

    public static class DoRemoveTripleStuff implements DoTripleStuff {
        private Set<Long> longSet;

        public DoRemoveTripleStuff(Set<Long> newLongSet) {
            this.longSet = newLongSet;
        }

        public void doStuff(Long first, Long second, Long third) throws GraphException {
            longSet.remove(third);
        }
    }

    public static class DoIntersectionTripleStuff implements DoTripleStuff {
        private LongIndex index;
        private Set<Long> longSet;

        public DoIntersectionTripleStuff(LongIndex newIndex, Set<Long> newLongSet) {
            this.index = newIndex;
            this.longSet = newLongSet;
        }

        public void doStuff(Long first, Long second, Long third) throws GraphException {
            if (longSet.contains(third)) {
                index.add(first, second, third);
            }
        }
    }
}
