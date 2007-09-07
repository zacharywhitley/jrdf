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

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Triple;

/**
 * Created by IntelliJ IDEA.
 * User: imrank
 * Date: 7/09/2007
 * Time: 11:31:47
 * To change this template use File | Settings | File Templates.
 */
public class GlobalizedGraphUnitTest extends AbstractGlobalizedGraphUnitTest {
    private final int NUMBER_OF_MOLECULES = 10;


    public void testAdd() throws Exception {
        Molecule molecule = getMolecule();

        assertTrue(globalizedGraph.isEmpty());
        long expected = 0;
        long value = globalizedGraph.numberOfMolecules();
        assertEquals(expected, value);

        globalizedGraph.add(molecule);

        assertFalse(globalizedGraph.isEmpty());
        expected = 1;
        value = globalizedGraph.numberOfMolecules();
        assertEquals(expected, value);
    }


    public void testRemove() throws Exception {
        Molecule molecule = getMolecule();

        assertTrue(globalizedGraph.isEmpty());
        long expected = 0;
        long value = globalizedGraph.numberOfMolecules();
        assertEquals(expected, value);

        globalizedGraph.add(molecule);

        assertFalse(globalizedGraph.isEmpty());
        expected = 1;
        value = globalizedGraph.numberOfMolecules();
        assertEquals(expected, value);

        //test removal
        globalizedGraph.remove(molecule);
        assertTrue(globalizedGraph.isEmpty());
    }

    public void testNumberOfMolecules() throws Exception {
        addMolecules();

        long value = globalizedGraph.numberOfMolecules();
        assertEquals(NUMBER_OF_MOLECULES, value);
    }

    public void testContains() throws Exception {
        Molecule molecule = addMolecules();

        globalizedGraph.add(molecule);

        boolean value = globalizedGraph.contains(molecule);
        assertTrue(value);
    }

    public void testContainsAnySubject() throws Exception {
        Molecule molecule = addMolecules();

        globalizedGraph.add(molecule);

        Triple headTriple = molecule.getHeadTriple();
        tripleFactory.createTriple(ANY_SUBJECT_NODE, headTriple.getPredicate(), headTriple.getObject());
        boolean value = globalizedGraph.contains(molecule);
        assertTrue(value);
    }


    public void testContainsAnyPredicate() throws Exception {
        Molecule molecule = addMolecules();

        globalizedGraph.add(molecule);

        Triple headTriple = molecule.getHeadTriple();
        tripleFactory.createTriple(headTriple.getSubject(), ANY_PREDICATE_NODE, headTriple.getObject());
        boolean value = globalizedGraph.contains(molecule);
        assertTrue(value);
    }

    public void testContainsAnyObject() throws Exception {
        Molecule molecule = addMolecules();

        globalizedGraph.add(molecule);

        Triple headTriple = molecule.getHeadTriple();
        tripleFactory.createTriple(headTriple.getSubject(), headTriple.getPredicate(), ANY_OBJECT_NODE);
        boolean value = globalizedGraph.contains(molecule);
        assertTrue(value);
    }

    public void testContainsAny() throws Exception {
        Molecule molecule = addMolecules();
        boolean value;

        globalizedGraph.add(molecule);

        tripleFactory.createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        value = globalizedGraph.contains(molecule);
        assertTrue(value);
    }

    private Molecule addMolecules() throws GraphElementFactoryException {
        Molecule molecule = null;

        for (int i = 0; i < NUMBER_OF_MOLECULES; i++) {
            molecule = getMolecule();
            globalizedGraph.add(molecule);
        }
        return molecule;
    }

}
