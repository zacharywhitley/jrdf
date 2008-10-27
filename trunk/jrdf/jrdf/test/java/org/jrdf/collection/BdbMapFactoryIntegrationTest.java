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

package org.jrdf.collection;

import junit.framework.TestCase;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class BdbMapFactoryIntegrationTest extends TestCase {
    private final DirectoryHandler directoryHandler = new TempDirectoryHandler();
    private final BdbEnvironmentHandler mapHandler = new BdbEnvironmentHandlerImpl(directoryHandler);
    private MapFactory factory;

    public void setUp() throws Exception {
        super.setUp();
        directoryHandler.removeDir();
    }

    public void testCreateStringLong() {
        factory = new BdbMapFactory(mapHandler, "testDb2");
        Map<String, Long> stringPool = factory.createMap(String.class, Long.class);
        for (int i = 0; i < 100; i++) {
            stringPool.put("Foo" + i, new Long(i));
            Long aLong = stringPool.get("Foo" + i);
            assertEquals(new Long(i).longValue(), aLong.longValue());
        }
    }

    public void testCreateLongString() {
        factory = new BdbMapFactory(mapHandler, "testDb3");
        Map<Long, String> stringPool = factory.createMap(Long.class, String.class);
        for (int i = 0; i < 100; i++) {
            stringPool.put(new Long(i), "Foo" + i);
            String value = stringPool.get(new Long(i));
            assertEquals("Foo" + i, value);
        }
    }

    public void testCreateLongArrayList() {
        factory = new BdbMapFactory(mapHandler, "testDb4");
        Map<Long, LinkedList<Long[]>> tripleStore = factory.createMap(Long.class, LinkedList.class);
        for (int i = 0; i < 100; i++) {
            LinkedList<Long[]> list = new LinkedList<Long[]>();
            list.add(new Long[]{new Long(i + 1), new Long(i + 2)});
            tripleStore.put(new Long(i), list);
            LinkedList<Long[]> longs = tripleStore.get(new Long(i));
            for (int index = 0; index < list.size() - 1; index++) {
                Long[] listValues = list.get(index);
                assertEquals(Arrays.asList(longs.get(i)), Arrays.asList(listValues));
            }
        }
    }
}
