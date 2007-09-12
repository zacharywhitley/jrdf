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
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.util.ClosableIterator;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;

public abstract class AbstractGlobalizedGraphUnitTest extends TestCase {
    private static final int NUMBER_OF_MOLECULES = 10;
    private static final String BASE_URL = "http://example.org/";
    private static final String URL1 = BASE_URL + "1";
    private static final String URL2 = BASE_URL + "2";
    private static final String LITERAL1 = "xyz";
    private static final String LITERAL2 = "abc";
    private GlobalizedGraph globalizedGraph;
    private TripleFactory tripleFactory;
    private TripleComparator comparator;

    public abstract GlobalizedGraph getGlobalizedGraph();

    public abstract TripleFactory getTripleFactory();

    public abstract TripleComparator getTripleComparator();

    @Override
    public void setUp() {
        globalizedGraph = getGlobalizedGraph();
        tripleFactory = getTripleFactory();
        comparator = getTripleComparator();
    }

    // TODO Add test here to see if adding the molecule with the same head triple but different context appends the new
    // tail triples.
    public void testAdd() throws Exception {
        Molecule molecule = createMolecule(createHeadTriple());

        assertTrue(globalizedGraph.isEmpty());
        long expected = 0;
        long value = globalizedGraph.getNumberOfMolecules();
        assertEquals(expected, value);

        globalizedGraph.add(molecule);

        assertFalse(globalizedGraph.isEmpty());
        expected = 1;
        value = globalizedGraph.getNumberOfMolecules();
        assertEquals(expected, value);
    }

    public void testRemove() throws Exception {
        Molecule molecule = createMolecule(createHeadTriple());

        assertTrue(globalizedGraph.isEmpty());
        long expected = 0;
        long value = globalizedGraph.getNumberOfMolecules();
        assertEquals(expected, value);

        globalizedGraph.add(molecule);

        assertFalse(globalizedGraph.isEmpty());
        expected = 1;
        value = globalizedGraph.getNumberOfMolecules();
        assertEquals(expected, value);

        //test removal
        globalizedGraph.remove(molecule);
        assertTrue(globalizedGraph.isEmpty());
    }

    public void testNumberOfMolecules() throws Exception {
        List<Triple> headTriples = getHeadTriples();
        addMolecules(headTriples);
        assertEquals(NUMBER_OF_MOLECULES, globalizedGraph.getNumberOfMolecules());
    }

    public void testContains() throws Exception {
        // Goes through all 8 possibilities and checks contains.
        for (int i = 0 ; i < 8; i++) {
            // 4 falses then 4 trues.
            boolean findAnySubject = (i & 4) != 0;
            // 2 falses then 2 trues.
            boolean findAnyPredicate = (i & 2) != 0;
            // true then false
            boolean findAnyObject = (i & 1) != 0;
            checkContains(findAnySubject, findAnyPredicate, findAnyObject);
        }
    }

    private void checkContains(boolean findAnySubject, boolean findAnyPredicate, boolean findAnyObject)
        throws GraphElementFactoryException {
        List<Triple> headTriples = getHeadTriples();
        for (int i = 0; i < NUMBER_OF_MOLECULES; i++) {
            Triple headTriple = headTriples.get(i);
            SubjectNode subject = findAnySubject ? ANY_SUBJECT_NODE : headTriple.getSubject();
            PredicateNode predicate = findAnyPredicate ? ANY_PREDICATE_NODE : headTriple.getPredicate();
            ObjectNode object = findAnyObject ? ANY_OBJECT_NODE : headTriple.getObject();
            addMolecules(headTriples);
            assertTrue(globalizedGraph.contains(subject, predicate, object));
        }
    }

    public void testFindAny() throws Exception {
        List<Triple> headTriples = getHeadTriples();
        addTriples(headTriples);
        ClosableIterator<Molecule> closableIterator = globalizedGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE,
            ANY_OBJECT_NODE);
        assertTrue(closableIterator.hasNext());
    }

    protected List<Triple> getHeadTriples() throws GraphElementFactoryException {
        List<Triple> headTriples = new ArrayList<Triple>();
        for (int i = 0; i < NUMBER_OF_MOLECULES; i++) {
            headTriples.add(createHeadTriple());
        }
        return headTriples;
    }

    private Triple createHeadTriple() throws GraphElementFactoryException {
        double random = Math.random();
        return tripleFactory.createTriple(URI.create(URL1 + random), URI.create(URL2), LITERAL1);
    }

    private void addMolecules(List<Triple> headTriples) throws GraphElementFactoryException {
        for (int i = 0; i < NUMBER_OF_MOLECULES; i++) {
            Molecule m = createMolecule(headTriples.get(i));
            globalizedGraph.add(m);
        }
    }

    private void addTriples(List<Triple> triples) {
        for (Triple triple : triples) {
            Molecule m = new MoleculeImpl(comparator);
            m.add(triple);
            globalizedGraph.add(m);
        }
    }

    private Molecule createMolecule(Triple headTriple) throws GraphElementFactoryException {
        Molecule m = new MoleculeImpl(comparator);
        URIReference uriReference = (URIReference) headTriple.getSubject();
        Triple triple2 = tripleFactory.createTriple(uriReference.getURI(), URI.create(URL2), LITERAL2);
        m.add(headTriple);
        m.add(triple2);
        return m;
    }
}
