/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.molecule;

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactoryImpl;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.mem.BlankNodeComparator;
import org.jrdf.graph.mem.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.mem.LocalizedNodeComparator;
import org.jrdf.graph.mem.LocalizedNodeComparatorImpl;
import org.jrdf.graph.mem.NodeComparatorImpl;
import org.jrdf.graph.mem.TripleComparatorImpl;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: imrank
 * Date: 7/09/2007
 * Time: 11:31:47
 * To change this template use File | Settings | File Templates.
 */
public class GlobalizedGraphUnitTest extends TestCase {
    JRDFFactory factory = SortedMemoryJRDFFactoryImpl.getFactory();

    LocalizedNodeComparator localizedNodeComparator = new LocalizedNodeComparatorImpl();
    BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localizedNodeComparator);
    NodeComparator nodeComparator = new NodeComparatorImpl(new NodeTypeComparatorImpl(), blankNodeComparator);
    TripleComparator comparator = new TripleComparatorImpl(nodeComparator);

    protected MoleculeIndex moleculeIndex;
    private final Graph graph = factory.getNewGraph();
    protected final TripleFactory tripleFactory = graph.getTripleFactory();


    private MoleculeIndex spoIndex;
    private MoleculeIndex ospIndex;
    private MoleculeIndex posIndex;
    private MoleculeIteratorFactoryImpl iteratorFactory;
    private MoleculeIndex[] indexes;
    private GlobalizedGraphImpl globalizedGraph;

    private final String BASE_URL = "http://example.org/";
    private final String URL1 = BASE_URL + "1";
    private final String URL2 = BASE_URL + "2";
    private final String LITERAL1 = "xyz";
    private final String LITERAL2 = "abc";


    public void setUp() {
        spoIndex = new MoleculeIndexMem(new HashMap<Node, Map<Node, Map<Node, Set<Triple>>>>());
        posIndex = new MoleculeIndexMem(new HashMap<Node, Map<Node, Map<Node, Set<Triple>>>>());
        ospIndex = new MoleculeIndexMem(new HashMap<Node, Map<Node, Map<Node, Set<Triple>>>>());

        indexes = new MoleculeIndex[] {spoIndex, posIndex,  ospIndex};
        iteratorFactory = new MoleculeIteratorFactoryImpl();
        globalizedGraph = new GlobalizedGraphImpl(indexes, iteratorFactory);
    }

    public void testAdd() throws Exception {
        Molecule molecule = getMolecule();

        long expected = 0;
        long value = globalizedGraph.numberOfMolecules();
        assertEquals(expected, value);

        globalizedGraph.add(molecule);

        expected = 1;
        value = globalizedGraph.numberOfMolecules();
        assertEquals(expected, value);
    }

    private Molecule getMolecule() throws GraphElementFactoryException {
        Molecule m = new MoleculeImpl(comparator);

        //create some triples
        double random = Math.random();
        Triple triple = tripleFactory.createTriple(URI.create(URL1 + random), URI.create(URL2), LITERAL1);
        random = Math.random();
        Triple triple2 = tripleFactory.createTriple(URI.create(URL1 + random), URI.create(URL2), LITERAL2);
        m.add(triple);
        m.add(triple2);
        return m;
    }

}
