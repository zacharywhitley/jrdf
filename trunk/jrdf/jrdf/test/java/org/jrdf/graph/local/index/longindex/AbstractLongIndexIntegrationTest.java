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

package org.jrdf.graph.local.index.longindex;

import junit.framework.TestCase;
import org.jrdf.graph.GraphException;
import org.jrdf.map.MapFactory;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.*;
import static org.jrdf.util.test.SetUtil.*;

import java.util.Set;

public abstract class AbstractLongIndexIntegrationTest extends TestCase {
    protected LongIndex longIndex;
    protected MapFactory factory;

    public void testAddAndContains() throws Exception {
        longIndex.add(1L, 2L, 3L);
        longIndex.add(1L, 2L, 4L);
        longIndex.add(2L, 2L, 4L);
        longIndex.add(2L, 2L, 5L);
        assertTrue(longIndex.contains(2L));
    }

    public void testAddAndSubIndex() throws Exception {
        longIndex.add(1L, 2L, 3L);
        longIndex.add(1L, 2L, 4L);
        longIndex.add(2L, 2L, 4L);
        longIndex.add(2L, 2L, 5L);
        checkSubIndexResult(1L, 2L, asSet(3L, 4L));
        checkSubIndexResult(2L, 2L, asSet(4L, 5L));
    }

    public void testAddAndRemoveSubIndex() throws Exception {
        longIndex.add(1L, 2L, 3L);
        longIndex.add(1L, 2L, 4L);
        longIndex.add(2L, 2L, 4L);
        longIndex.add(2L, 2L, 5L);
        boolean changed = longIndex.removeSubIndex(1L);
        assertTrue(changed);
        assertEquals(2, longIndex.getSize());
    }

    public void testIterator() throws Exception {
        longIndex.add(1L, 2L, 3L);
        longIndex.add(1L, 2L, 4L);
        longIndex.add(2L, 2L, 4L);
        longIndex.add(2L, 2L, 5L);
        ClosableIterator<Long[]> entryIterator = longIndex.iterator();
    }

    public void testAddition() throws Exception {
        longIndex.add(1L, 2L, 3L);
        checkNumberOfTriples(1, longIndex.getSize());
        longIndex.add(1L, 2L, 3L);
        checkNumberOfTriples(1, longIndex.getSize());
        longIndex.add(4L, 5L, 6L);
        checkNumberOfTriples(2, longIndex.getSize());
    }

    // TODO Missing one removing a non-existent triple.
    public void testRemove() throws Exception {
        longIndex.add(1L, 2L, 3L);
        longIndex.add(3L, 4L, 3L);
        assertThrows(GraphException.class, new AssertThrows.Block(){
            public void execute() throws Throwable {
                longIndex.remove(1L, 1L, 1L);
            }
        });
        checkNumberOfTriples(2, longIndex.getSize());
        longIndex.remove(1L, 2L, 3L);
        checkNumberOfTriples(1, longIndex.getSize());
        longIndex.remove(3L, 4L, 3L);
        checkNumberOfTriples(0, longIndex.getSize());
    }

    public void testClear() throws Exception {
        longIndex.add(1L, 2L, 3L);
        longIndex.add(3L, 4L, 3L);
        longIndex.clear();
        checkNumberOfTriples(0, longIndex.getSize());
    }

    private void checkSubIndexResult(long subject, long predicate, Set<Long> objects) {
        final ClosableIterator<Long[]> index = longIndex.getSubIndex(subject);
        while (index.hasNext()) {
            Long[] result = index.next();
            assertEquals(predicate, result[0].longValue());
            assertTrue(objects.contains(result[1]));
        }
    }

    private void checkNumberOfTriples(final int expectedNumber, final long actualSize) {
        assertEquals("Incorrect numober of triples: ", expectedNumber, actualSize);
    }
}
