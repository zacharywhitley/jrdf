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

package org.jrdf.graph.global.index;

import junit.framework.TestCase;
import org.jrdf.graph.Node;
import org.jrdf.graph.Triple;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.local.mem.BlankNodeComparator;
import org.jrdf.graph.local.mem.BlankNodeComparatorImpl;
import org.jrdf.graph.local.mem.NodeComparatorImpl;
import org.jrdf.graph.local.mem.TripleComparatorImpl;
import org.jrdf.graph.global.BlankNodeImpl;
import org.jrdf.graph.global.LiteralImpl;
import org.jrdf.graph.global.TripleImpl;
import org.jrdf.graph.global.URIReferenceImpl;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeFactory;
import org.jrdf.graph.global.molecule.MoleculeFactoryImpl;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.net.URI;

public abstract class AbstractMoleculeIndexUnitTest extends TestCase {
    private final BlankNodeComparator blankNodeComparator = new BlankNodeComparatorImpl();
    private final NodeComparator nodeComparator = new NodeComparatorImpl(new NodeTypeComparatorImpl(), blankNodeComparator);
    private final TripleComparator tripleComparator = new TripleComparatorImpl(nodeComparator);
    private final String BASE_URL = "http://example.org/";
    private final String URL1 = BASE_URL + "1";
    private final String URL2 = BASE_URL + "2";
    private final String LITERAL1 = "xyz";
    private final String LITERAL2 = "abc";
    protected MoleculeFactory moleculeFactory = new MoleculeFactoryImpl(tripleComparator);
    protected MoleculeIndex moleculeIndex;

    protected abstract MoleculeIndex getIndex();

    protected abstract Node[] getNodes(Triple triple);

    public void setUp() throws Exception {
        moleculeIndex = getIndex();
    }



    public void testAdd() throws Exception {
        addMolecule();
    }

    public void testGetTripleSize() throws Exception {
        addMolecule();

        long numTriples = moleculeIndex.getNumberOfTriples();
        int expected = 2;
        assertEquals(expected, numTriples);
    }

    public void testGetMoleculeSize() throws Exception {
        addMolecule();

        long numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        int expected = 1;
        assertEquals(expected, numberOfMolecules);
    }

    public void testClean() throws Exception {
        addMolecule();
        long numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        int expected = 1;
        assertEquals(expected, numberOfMolecules);

        moleculeIndex.clear();

        numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        expected = 0;
        assertEquals(expected, numberOfMolecules);
    }

    public void testRemove() throws Exception {
        Molecule m1 = addMolecule();
        addMolecule();

        long numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        int expected = 2;
        assertEquals(expected, numberOfMolecules);

        //remove the molecule
        Triple headTriple = m1.getHeadTriple();
        Node[] nodes = getNodes(headTriple);
        moleculeIndex.remove(nodes[0], nodes[1], nodes[2]);

        //check remaining size
        numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        expected = 1;
        assertEquals(expected, numberOfMolecules);

        long numberOfTriples = moleculeIndex.getNumberOfTriples();
        expected = 2;
        assertEquals(expected, numberOfTriples);

        //TODO check remaing molecule is m2
    }

    /**
     * This tests the removal where one of the molecules
     * has no tail triples.
     *
     * @throws Exception
     */
    public void testRemoveMoleculeWithNoTail() throws Exception {
        Molecule m1 = addMolecule();
        addMoleculeWithNoTail();

        long numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        int expected = 2;
        assertEquals(expected, numberOfMolecules);

        //remove the molecule
        Triple headTriple = m1.getHeadTriple();
        Node[] nodes = getNodes(headTriple);
        moleculeIndex.remove(nodes[0], nodes[1], nodes[2]);

        //check remaining size
        numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        expected = 1;
        assertEquals(expected, numberOfMolecules);

        long numberOfTriples = moleculeIndex.getNumberOfTriples();
        expected = 1;
        assertEquals(expected, numberOfTriples);

        //TODO check remaing molecule is m2
    }

    public void testGetMolecule() throws Exception {
        Molecule m1 = addMolecule();
        addMolecule();

        long numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        int expected = 2;
        assertEquals(expected, numberOfMolecules);

        //remove the molecule
        Triple headTriple = m1.getHeadTriple();
        Molecule molecule = moleculeIndex.getMolecule(headTriple);
        assertNotNull(molecule);
        assertEquals(molecule, m1);

        //check remaining size
        numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        expected = 2;
        assertEquals(expected, numberOfMolecules);

        long numberOfTriples = moleculeIndex.getNumberOfTriples();
        expected = 4;
        assertEquals(expected, numberOfTriples);

        //TODO check remaing molecule is m2
    }


    private Molecule addMolecule() throws Exception {
        double random = Math.random();
        BlankNodeImpl blankNode = new BlankNodeImpl();
        Triple triple = new TripleImpl(new URIReferenceImpl(URI.create(URL1 + random)), new URIReferenceImpl(URI.create(URL2)),
            blankNode);
        random = Math.random();
        Triple triple2 = new TripleImpl(blankNode, new URIReferenceImpl(URI.create(URL2)), new LiteralImpl(LITERAL2));
        Molecule m = moleculeFactory.createMolecule(triple);
        m.add(triple2);
        Triple headTriple = m.getHeadTriple();
        Node[] nodes = getNodes(headTriple);
        moleculeIndex.add(nodes[0], nodes[1], nodes[2], m);
        return m;
    }

    private Molecule addMoleculeWithNoTail() throws Exception {
        double random = Math.random();
        Triple triple = new TripleImpl(new URIReferenceImpl(URI.create(URL1 + random)), new URIReferenceImpl(URI.create(URL2)), new LiteralImpl(LITERAL1));
        Node[] nodes = getNodes(triple);
        Molecule m = moleculeFactory.createMolecule(triple);
        moleculeIndex.add(nodes[0], nodes[1], nodes[2], m);
        Triple headTriple = m.getHeadTriple();
        nodes = getNodes(headTriple);
        moleculeIndex.add(nodes[0], nodes[1], nodes[2], m);
        return m;
    }
}