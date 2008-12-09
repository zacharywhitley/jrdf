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
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import org.jrdf.util.bdb.ByteTripleComparatorFactoryImpl;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.Graph;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.ReverseGroundedTripleComparatorFactoryImpl;
import org.jrdf.SpringJRDFFactory;
import org.jrdf.JRDFFactory;

import java.util.SortedSet;
import java.util.Iterator;
import java.util.Comparator;
import java.net.URI;

public class BdbSortedSetFactoryIntegrationTest extends TestCase {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final JRDFFactory FACTORY = SpringJRDFFactory.getFactory();
    private Comparator<byte[]> tc1;
    private Comparator<byte[]> tc2;
    private TripleImpl triple1;
    private TripleImpl triple2;
    private SortedSet<Triple> triples1;
    private SortedSet<Triple> triples2;
    private BdbCollectionFactory factory;

    public void setUp() throws Exception {
        HANDLER.removeDir();
        HANDLER.makeDir();
        BdbEnvironmentHandler handler = new BdbEnvironmentHandlerImpl(HANDLER);
        factory = new BdbCollectionFactory(handler, "bdbsortedset");
        tc1 = new ByteTripleComparatorFactoryImpl(new GroundedTripleComparatorFactoryImpl()).newComparator();
        tc2 = new ByteTripleComparatorFactoryImpl(new ReverseGroundedTripleComparatorFactoryImpl()).newComparator();
        triples1 = factory.createSet(Triple.class, tc1);
        triples2 = factory.createSet(Triple.class, tc2);
        createTriples();
    }

    public void tearDown() {
        factory.close();
    }

    public void testBasicConstruction() throws Exception {
        triples1.add(triple1);
        triples1.add(triple2);
        triples2.add(triple1);
        triples2.add(triple2);
        Iterator<Triple> iterator1 = triples1.iterator();
        Iterator<Triple> iterator2 = triples2.iterator();
        assertEquals(triple1, iterator2.next());
        assertEquals(triple2, iterator2.next());
        assertEquals(triple2, iterator1.next());
        assertEquals(triple1, iterator1.next());
    }

    private void createTriples() throws Exception {
        Graph newGraph = FACTORY.getNewGraph();
        GraphElementFactory gef = newGraph.getElementFactory();
        BlankNode bn1 = gef.createBlankNode();
        URIReference uri1 = gef.createURIReference(URI.create("urn:foo"));
        triple1 = new TripleImpl(bn1, uri1, uri1);
        triple2 = new TripleImpl(bn1, uri1, bn1);
    }
}
