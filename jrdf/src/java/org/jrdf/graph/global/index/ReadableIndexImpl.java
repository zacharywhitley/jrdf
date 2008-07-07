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

package org.jrdf.graph.global.index;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.index.longindex.MoleculeIndex;
import org.jrdf.graph.global.index.longindex.MoleculeStructureIndex;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.EntryIteratorOneFixedFourArray;
import org.jrdf.util.EntryIteratorOneFixedOneArray;
import org.jrdf.util.EntryIteratorTwoFixedFourArray;

import static java.util.Arrays.asList;

public class ReadableIndexImpl implements ReadableIndex<Long> {
    private final MoleculeIndex<Long>[] indexes;
    private final MoleculeStructureIndex<Long> structureIndex;

    public ReadableIndexImpl(MoleculeIndex<Long>[] newIndexes, MoleculeStructureIndex<Long> newStructureIndex) {
        this.indexes = newIndexes;
        this.structureIndex = newStructureIndex;
    }

    public Long findHeadTripleMid(Long PID, Long... triple) throws GraphException {
        final ClosableIterator<Long[]> index = structureIndex.getSubIndex(PID);
        while (index.hasNext()) {
            Long[] midSPO = index.next();
            // Make sure object equals required value and mid is not 1L (not in a molecule).
            if (midSPO[1].equals(triple[0]) && midSPO[2].equals(triple[1]) && midSPO[3].equals(triple[2])) {
                return midSPO[0];
            }
        }
        throw new GraphException("Cannot find triple:  " + asList(triple));
    }

    public Long findMid(Long... triple) throws GraphException {
        final ClosableIterator<Long[]> index = indexes[0].getSubSubIndex(triple[0], triple[1]);
        while (index.hasNext()) {
            Long[] oAndMid = index.next();
            // Make sure object equals required value and mid is not 1L (not in a molecule).
            if (oAndMid[0].equals(triple[2]) && !oAndMid[1].equals(1L)) {
                return oAndMid[1];
            }
        }
        throw new GraphException("Cannot find triple:  " + asList(triple));
    }

    public ClosableIterator<Long[]> findTriplesForMid(Long pid, Long mid) {
        return new EntryIteratorTwoFixedFourArray(structureIndex.getSubSubIndex(pid, mid), mid);
    }

    /**
     * spo & mid.
     * @param pid
     * @return
     */
    public ClosableIterator<Long[]> findTriplesForPid(Long pid) {
        return new EntryIteratorOneFixedFourArray(structureIndex.getSubIndex(pid));
    }

    public Long findEnclosingMoleculeId(Long mid) throws GraphException {
        ClosableIterator<Long[]> subIndex = structureIndex.getSubIndex(1L);
        Long pid;
        while (subIndex.hasNext()) {
            Long[] quad = subIndex.next();
            if (quad[0] == mid) {
                return 1L;
            }
        }
        subIndex = structureIndex.getSubIndex(1L);
        while (subIndex.hasNext()) {
            Long[] quad = subIndex.next();
            pid = quad[0];
            if (findParentMoleculeID(pid, mid) != 0L) {
                return pid;
            }
        }
        throw new GraphException("Cannot find parent molecule id for: " + mid);
    }

    /**
     * Search for the parent ID in a width-first search.
     * @param parentID
     * @param mid
     * @return
     */
    private Long findParentMoleculeID(Long parentID, Long mid) {
        final ClosableIterator<Long[]> subIndex = structureIndex.getSubIndex(parentID);
        while (subIndex.hasNext()) {
            Long[] quad = subIndex.next();
            if (quad[0] == mid) {
                return parentID;
            }
            return findParentMoleculeID(quad[0], mid);
        }
        return 0L;
    }

    public ClosableIterator<Long> findChildIDs(Long mid) {
        return new EntryIteratorOneFixedOneArray(structureIndex.getSubIndex(mid));
    }
}
