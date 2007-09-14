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

package org.jrdf.graph.global.molecule;

import junit.framework.TestCase;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.AnyPredicateNode;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GlobalizedGraph;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.factory.GlobalizedGraphMemFactoryImpl;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.test.GlobalizedGraphTestUtil;

import java.util.List;

/**
 * User: imrank
 * Date: 13/09/2007
 * Time: 11:18:05
 */
public class MoleculeIteratorFactoryImplUnitTest extends TestCase {
    private TripleComparator comparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private GlobalizedGraph globalizedGraph;

    public void setUp() throws Exception {
        globalizedGraph = new GlobalizedGraphMemFactoryImpl(comparator).getNewGlobalizedGraph();
    }

//    public void testFind() throws Exception {
//        // Goes through all 8 possibilities and checks contains.
//        for (int i = 0 ; i < 8; i++) {
//            // 4 falses then 4 trues.
//            boolean findAnySubject = (i & 4) != 0;
//            // 2 falses then 2 trues.
//            boolean findAnyPredicate = (i & 2) != 0;
//            // true then false
//            boolean findAnyObject = (i & 1) != 0;
//            checkFind(findAnySubject, findAnyPredicate, findAnyObject);
//        }
//    }
//
//    private void checkFind(boolean findAnySubject, boolean findAnyPredicate, boolean findAnyObject) {
//        List<Triple> headTriples = GlobalizedGraphTestUtil.getHeadTriples();
//        for (int i = 0; i < GlobalizedGraphTestUtil.NUMBER_OF_MOLECULES; i++) {
//            Triple headTriple = headTriples.get(i);
//            SubjectNode subject = findAnySubject ? ANY_SUBJECT_NODE : headTriple.getSubject();
//            PredicateNode predicate = findAnyPredicate ? ANY_PREDICATE_NODE : headTriple.getPredicate();
//            ObjectNode object = findAnyObject ? ANY_OBJECT_NODE : headTriple.getObject();
//            GlobalizedGraphTestUtil.addMolecules(headTriples, globalizedGraph, comparator);
//            assertTrue(globalizedGraph.find(subject, predicate, object).hasNext());
//        }
//    }

    public void testThreeFixed() throws Exception {
        List<Triple> headTriples = GlobalizedGraphTestUtil.getHeadTriples();
        Triple firstHeadTriple = headTriples.get(0);
        SubjectNode subject = firstHeadTriple.getSubject();
        PredicateNode predicate = firstHeadTriple.getPredicate();
        ObjectNode object = firstHeadTriple.getObject();

        GlobalizedGraphTestUtil.addMolecules(headTriples, globalizedGraph, comparator);
        ClosableIterator<Molecule> closableIterator = globalizedGraph.find(subject, predicate, object);
        assertTrue(closableIterator.hasNext());
        assertNotNull(closableIterator.next());
        assertFalse(closableIterator.hasNext());
    }

    public void testTwoFixed() throws Exception {
        List<Triple> headTriples = GlobalizedGraphTestUtil.getHeadTriples();
        Triple firstHeadTriple = headTriples.get(0);
        SubjectNode subject = firstHeadTriple.getSubject();
        PredicateNode predicate = firstHeadTriple.getPredicate();
        ObjectNode object = firstHeadTriple.getObject();

        GlobalizedGraphTestUtil.addMolecules(headTriples, globalizedGraph, comparator);
        ClosableIterator<Molecule> closableIterator = globalizedGraph.find(subject, predicate, AnyObjectNode.ANY_OBJECT_NODE);
        assertTrue(closableIterator.hasNext());
        assertNotNull(closableIterator.next());
        assertFalse(closableIterator.hasNext());

        closableIterator = globalizedGraph.find(subject, AnyPredicateNode.ANY_PREDICATE_NODE, object);
        assertTrue(closableIterator.hasNext());
        assertNotNull(closableIterator.next());
        assertTrue(closableIterator.hasNext());

        closableIterator = globalizedGraph.find(AnySubjectNode.ANY_SUBJECT_NODE, predicate, object);
        assertTrue(closableIterator.hasNext());
        assertNotNull(closableIterator.next());
        assertTrue(closableIterator.hasNext());

    }


}
