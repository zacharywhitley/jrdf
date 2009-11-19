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

package org.jrdf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.graph.AbstractGraphIntegrationTest;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.MoleculeFactory;
import org.jrdf.graph.global.molecule.mem.MoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.local.TripleComparatorFactoryImpl;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

public class PersistentGlobalJRDFGraphIntegrationTest extends AbstractGraphIntegrationTest {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final TripleComparator TRIPLE_COMPARATOR = new TripleComparatorFactoryImpl().newComparator();
    private static final MoleculeComparator MOLECULE_COMPARATOR =
            new MoleculeHeadTripleComparatorImpl(TRIPLE_COMPARATOR);
    private static final MoleculeFactory MOLECULE_FACTORY = new MoleculeFactoryImpl(MOLECULE_COMPARATOR);
    private static int graphNumber = 1;
    private PersistentGlobalJRDFFactory factory;

    @Before
    public void setUp() throws Exception {
        HANDLER.makeDir();
        factory = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        //graph.close();
        factory.close();
        HANDLER.removeDir();
    }

    public Graph newGraph() throws Exception {
        MoleculeGraph moleculeGraph = factory.getNewGraph("temp" + graphNumber++);
        moleculeGraph.clear();
        return moleculeGraph;
    }

    @Test
    public void testMoleculeSize() throws Exception {
        MoleculeGraph moleculeGraph = factory.getNewGraph("foo");
        moleculeGraph.clear();
        GraphElementFactory graphElementFactory = moleculeGraph.getElementFactory();
        TripleFactory tripleFactory = moleculeGraph.getTripleFactory();
        URIReference ref1 = graphElementFactory.createURIReference(URI.create("urn:foo"));
        URIReference ref2 = graphElementFactory.createURIReference(URI.create("urn:bar"));
        URIReference ref3 = graphElementFactory.createURIReference(URI.create("urn:baz"));
        BlankNode bnode1 = graphElementFactory.createBlankNode();
        addMolecule(tripleFactory.createTriple(bnode1, ref1, ref1), 1L);
        addMolecule(tripleFactory.createTriple(bnode1, ref2, ref2), 2L);
        addMolecule(tripleFactory.createTriple(bnode1, ref3, ref3), 3L);
        factory = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
        moleculeGraph = factory.getExistingGraph("foo");
        assertThat(moleculeGraph.getNumberOfMolecules(), is(3L));
    }

    private void addMolecule(Triple triple, long expectedMoleculeNumber) throws GraphException {
        MoleculeGraph moleculeGraph = factory.getExistingGraph("foo");
        assertThat(moleculeGraph.getNumberOfMolecules(), is(expectedMoleculeNumber - 1));
        moleculeGraph.add(MOLECULE_FACTORY.createMolecule(triple));
        assertThat(moleculeGraph.getNumberOfMolecules(), is(expectedMoleculeNumber));
        moleculeGraph.close();
        factory.close();
    }
}