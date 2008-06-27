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

package org.jrdf.graph.global.index.longindex.bdb;

import junit.framework.TestCase;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.index.longindex.MoleculeIndex;
import org.jrdf.map.BdbMapFactory;
import org.jrdf.map.MapFactory;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.ClosableMap;
import org.jrdf.util.ClosableMapImpl;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class MoleculeIndexBdbUnitTest extends TestCase {
    private static final TempDirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final BdbEnvironmentHandler BDB_HANDLER = new BdbEnvironmentHandlerImpl(HANDLER);
    private static final MapFactory FACTORY = new BdbMapFactory(BDB_HANDLER, "molMaps");
    private MoleculeIndex<Long> index;

    public void setUp() throws Exception {
        super.setUp();
        index = new MoleculeIndexBdb(FACTORY);
    }

    public void tearDown() {
        index.clear();
        index.close();
    }

    public void testBasicOps() throws GraphException {
        assertFalse("No keys", index.contains(0L));
        index.add(0L, 1L, 2L, 3L);
        assertTrue("0L exists", index.contains(0L));
        assertEquals("size = 1", 1, index.getSize());
        index.add(0L, 1L, 2L, 3L);
        assertEquals("size = 1", 1, index.getSize());
        index.remove(0L, 0L, 0L, 0L);
        assertEquals("size = 1", 1, index.getSize());
        assertTrue("Contains 0L", index.contains(0L));
        index.remove(0L, 1L, 2L, 3L);
        assertFalse("Not contains 0L", index.contains(0L));
        assertEquals("size = 0", 0, index.getSize());
        assertFalse("Not contains 0L", index.removeSubIndex(0L));
    }

    public void testComplexOps() throws GraphException {
        index.add(0L, 1L, 2L, 3L);
        index.add(0L, 1L, 2L, 4L);
        index.add(0L, 1L, 2L, 5L);
        index.add(1L, 4L, 5L, 6L);
        assertEquals("size  = 4", 4, index.getSize());
        ClosableMap<Long, ClosableMap<Long, Set<Long>>> map = index.getSubIndex(0L);
        assertEquals("subindex = 1", 1, map.size());
        ClosableMap<Long, ClosableMap<Long, Set<Long>>> goodMap =
                new ClosableMapImpl<Long, ClosableMap<Long, Set<Long>>>();
        ClosableMap<Long, Set<Long>> subMap = new ClosableMapImpl<Long, Set<Long>>();
        final HashSet<Long> set = new HashSet<Long>();
        set.add(3L);
        set.add(4L);
        set.add(5L);
        subMap.put(2L, set);
        goodMap.put(1L, subMap);
        assertEquals("Same map", goodMap, map);
    }

    public void testIterator() throws GraphException {
        index.add(0L, 1L, 2L, 3L);
        index.add(0L, 1L, 2L, 4L);
        index.add(0L, 1L, 2L, 5L);
        index.add(1L, 4L, 5L, 6L);
        ClosableIterator<Long[]> iterator = index.iterator();
        assertTrue("Has element", iterator.hasNext());
        Set<Long[]> set = addLongsToSet(new Long[]{0L, 1L, 2L, 3L}, new Long[]{0L, 1L, 2L, 4L},
                new Long[]{0L, 1L, 2L, 5L}, new Long[]{1L, 4L, 5L, 6L});
        int length = 0;
        while (iterator.hasNext()) {
            final Long[] longs = iterator.next();
            length++;
            /*System.err.print(longs[0] + " " + longs[1] + " " + longs[2] + " " + longs[3] +
                    System.getProperty("line.separator"));*/
            assertTrue("Contains longs", setContainsLongArray(set, longs));
        }
        iterator.close();
        assertEquals("Same length", set.size(), length);
    }

    private Set<Long[]> addLongsToSet(Long[]... longs) {
        Set<Long[]> set = new HashSet<Long[]>();
        for (Long[] array : longs) {
            set.add(array);
        }
        return set;
    }

    private boolean setContainsLongArray(Set<Long[]> set, Long[] longs) {
        boolean found;
        for (Long[] item : set) {
            found = (item[0] == longs[0] && item[1] == longs[1] &&
                    item[2] == longs[2] && item[3] == longs[3]);
            if (found) {
                return true;
            }
        }
        return false;
    }
}
