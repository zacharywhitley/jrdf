/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.graph.global.index.longindex;

import junit.framework.TestCase;
import org.jrdf.graph.GraphException;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.SetUtil.asSet;

import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractMoleculeStructureIndexIntegrationTest extends TestCase {
    protected MoleculeStructureIndex<Long> index;

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() {
        index.clear();
        index.close();
    }

    // TODO Test Unable to remove nonexistent statement remove.
    public void testBasicOps() throws GraphException {
        assertFalse("No keys", index.contains(1L));
        index.add(1L, 2L, 3L, 4L, 5L);
        assertTrue("1L exists", index.contains(1L));
        assertEquals("size = 1", 1, index.getSize());
        index.add(1L, 2L, 3L, 4L, 5L);
        assertEquals("size = 1", 1, index.getSize());
        AssertThrows.assertThrows(GraphException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                index.remove(1L, 1L, 1L, 1L, 1L);
            }
        });
        assertEquals("size = 1", 1, index.getSize());
        assertTrue("Contains 1L", index.contains(1L));
        index.remove(1L, 2L, 3L, 4L, 5L);
        assertFalse("Not contains 1L", index.contains(1L));
        assertEquals("size = 0", 0, index.getSize());
        assertFalse("Not contains 1L", index.removeSubIndex(1L));
    }

    public void testComplexOps() throws GraphException {
        assertEquals(0, index.getSize());
        index.add(1L, 1L, 2L, 3L, 4L);
        index.add(1L, 1L, 2L, 4L, 5L);
        index.add(1L, 1L, 2L, 5L, 6L);
        index.add(2L, 4L, 5L, 6L, 7L);
        assertEquals("size  = 4", 4, index.getSize());
        final ClosableIterator<Long[]> iterator = index.getSubIndex(1L);
        Set<Long[]> results = asSet(new Long[]{1L, 2L, 3L, 4L}, new Long[]{1L, 2L, 4L, 5L}, new Long[]{1L, 2L, 5L, 6L});
        checkResults(iterator, results);
    }

    private void checkResults(ClosableIterator<Long[]> iterator, Set<Long[]> results) {
        int length = 0;
        while (iterator.hasNext()) {
            final Long[] longs = iterator.next();
            length++;
            assertTrue("Does not contain expected results: " + asList(longs), setContainsLongArray(results, longs));
        }
        iterator.close();
        assertEquals("Same length", results.size(), length);
    }

    private boolean setContainsLongArray(Set<Long[]> set, Long[] longs) {
        for (Long[] item : set) {
            boolean found = true;
            for (int i = 0; i < item.length; i++) {
                found &= item[i].equals(longs[i]);
            }
            if (found) {
                return true;
            }
        }
        return false;
    }

    public void testIterator() throws GraphException {
        assertEquals(0, index.getSize());
        index.add(1L, 1L, 2L, 3L, 4L);
        index.add(1L, 1L, 2L, 4L, 5L);
        index.add(1L, 1L, 2L, 5L, 6L);
        index.add(2L, 4L, 5L, 6L, 7L);
        assertEquals(4, index.getSize());
        ClosableIterator<Long[]> iterator = index.iterator();
        assertTrue("Has element", iterator.hasNext());
        Set<Long[]> results = asSet(new Long[]{1L, 1L, 2L, 3L, 4L}, new Long[]{1L, 1L, 2L, 4L, 5L},
                new Long[]{1L, 1L, 2L, 5L, 6L}, new Long[]{2L, 4L, 5L, 6L, 7L});
        checkResults(iterator, results);
    }

    public void testGetSubSubIndex() throws GraphException {
        index.add(1L, 1L, 2L, 3L, 4L);
        index.add(1L, 1L, 2L, 4L, 5L);
        index.add(1L, 1L, 2L, 5L, 6L);
        index.add(1L, 1L, 3L, 3L, 4L);
        index.add(2L, 2L, 2L, 5L, 6L);
        index.add(2L, 4L, 5L, 6L, 7L);
        ClosableIterator<Long[]> subIndex = index.getSubSubIndex(1L, 1L);
        Set<Long[]> results = asSet(new Long[]{2L, 3L, 4L}, new Long[]{2L, 4L, 5L}, new Long[]{2L, 5L, 6L},
            new Long[]{3L, 3L, 4L});
        checkResults(subIndex, results);
    }

    public void testGetFourthIndex() throws GraphException {
        index.add(1L, 1L, 2L, 3L, 4L);
        index.add(1L, 1L, 2L, 4L, 5L);
        index.add(1L, 1L, 2L, 5L, 6L);
        index.add(1L, 1L, 3L, 3L, 4L);
        index.add(2L, 2L, 2L, 5L, 6L);
        index.add(2L, 4L, 5L, 6L, 7L);
        final ClosableIterator<Long[]> iterator = index.getFourthIndex(2L, 4L, 5L);
        Set<Long[]> results = new HashSet<Long[]>();
        results.add(new Long[]{6L, 7L});
        checkResults(iterator, results);
    }

    public void testgetFifthIndex() throws GraphException {
        index.add(1L, 1L, 2L, 3L, 4L);
        index.add(1L, 1L, 2L, 4L, 5L);
        index.add(1L, 1L, 2L, 5L, 6L);
        index.add(1L, 1L, 3L, 3L, 4L);
        index.add(2L, 2L, 2L, 5L, 6L);
        index.add(2L, 4L, 5L, 6L, 7L);
        index.add(1L, 1L, 2L, 3L, 5L);
        ClosableIterator<Long> iterator = index.getFifthIndex(1L, 1L, 2L, 3L);
        Set<Long> set = asSet(new Long[]{4L, 5L});
        checkLongIterator(set, iterator);
    }

    public void testGetFourthIndexOnly() throws GraphException {
        index.add(1L, 1L, 2L, 3L, 4L);
        index.add(1L, 1L, 2L, 4L, 5L);
        index.add(1L, 1L, 2L, 5L, 6L);
        index.add(1L, 1L, 3L, 3L, 4L);
        index.add(2L, 2L, 2L, 5L, 6L);
        index.add(2L, 4L, 5L, 6L, 7L);
        index.add(1L, 1L, 2L, 3L, 5L);
        index.add(1L, 1L, 2L, 2L, 7L);
        final ClosableIterator<Long> iterator = index.getFourthIndexOnly(1L, 1L, 2L);
        Set<Long> set = asSet(new Long[]{2L, 3L, 4L, 5L});
        checkLongIterator(set, iterator);
    }

    public void testGetAllFourthIndex() throws GraphException {
        index.add(1L, 1L, 2L, 3L, 4L);
        index.add(1L, 1L, 2L, 4L, 5L);
        index.add(1L, 1L, 2L, 5L, 6L);
        index.add(1L, 1L, 3L, 3L, 4L);
        index.add(2L, 2L, 2L, 5L, 6L);
        index.add(2L, 4L, 5L, 6L, 7L);
        index.add(1L, 1L, 2L, 3L, 5L);
        index.add(1L, 1L, 2L, 2L, 7L);
        final ClosableIterator<Long> iterator = index.getAllFourthIndex();
        Set<Long> set = asSet(new Long[]{2L, 3L, 4L, 5L, 6L});
        checkLongIterator(set, iterator);
    }

    private void checkLongIterator(Set<Long> set, ClosableIterator<Long> iterator) {
        int length = 0;
        while (iterator.hasNext()) {
            length++;
            Long item = iterator.next();
            assertTrue("Contains long", setContainsLong(set, item));
        }
        iterator.close();
        assertEquals("Same length", set.size(), length);
    }

    private boolean setContainsLong(Set<Long> set, Long item) {
        for (Long entry : set) {
            if (entry.equals(item)) {
                return true;
            }
        }
        return false;
    }
}
