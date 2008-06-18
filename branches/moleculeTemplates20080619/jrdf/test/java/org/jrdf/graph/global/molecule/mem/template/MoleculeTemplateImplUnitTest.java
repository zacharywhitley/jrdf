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

package org.jrdf.graph.global.molecule.mem.template;

import junit.framework.TestCase;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.TripleComparator;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.GRAPH;
import org.jrdf.graph.global.molecule.TriplePattern;
import org.jrdf.graph.local.BlankNodeComparator;
import org.jrdf.graph.local.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.LocalizedNodeComparator;
import org.jrdf.graph.local.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.NodeComparatorImpl;
import org.jrdf.graph.local.TripleComparatorImpl;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Yuan-Fang Li
 * @version $Revision:$
 */
public class MoleculeTemplateImplUnitTest extends TestCase {
    private final NodeTypeComparator typeComparator = new NodeTypeComparatorImpl();
    private final LocalizedNodeComparator localNodeComparator = new LocalizedNodeComparatorImpl();
    private final BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localNodeComparator);
    private final NodeComparator nodeComparator = new NodeComparatorImpl(typeComparator, blankNodeComparator);
    private final TripleComparator tripleComparator = new TripleComparatorImpl(nodeComparator);

    private MoleculeTemplate molTemp;
    private GraphElementFactory eFac;

    private SubjectNode s0;
    private SubjectNode s1;
    private SubjectNode s2;
    private ObjectNode o0;
    private ObjectNode o1;
    private ObjectNode o2;
    private PredicateNode p1;
    private PredicateNode p2;

    public void setUp() throws Exception {
        super.setUp();
        eFac = GRAPH.getElementFactory();
        s0 = eFac.createBlankNode();
        s1 = eFac.createURIReference(URI.create("urn:s1"));
        s2 = eFac.createURIReference(URI.create("urn:s2"));
        o0 = eFac.createBlankNode();
        o1 = eFac.createURIReference(URI.create("urn:o1"));
        o2 = eFac.createURIReference(URI.create("urn:o2"));
        p1 = eFac.createURIReference(URI.create("urn:p1"));
        p2 = eFac.createURIReference(URI.create("urn:p2"));
        molTemp = new MoleculeTemplateImpl(GRAPH, tripleComparator);
    }

    public void testInsertHeadTriple() throws Exception {
        PredicateNode pNode = eFac.createURIReference(URI.create("urn:p1"));
        TriplePattern pattern = new TriplePattern(ANY_SUBJECT_NODE, pNode, ANY_OBJECT_NODE);
        molTemp.setHeadTriple(pattern);
        TriplePattern head = molTemp.getHeadTriple();
        assertTrue("Same triple pattern", pattern.equals(head));
        TriplePattern pattern1 = new TriplePattern(ANY_SUBJECT_NODE, pNode, ANY_OBJECT_NODE);
        assertTrue("same triple pattern also", head.equals(pattern1));
        PredicateNode pNode1 = eFac.createURIReference(URI.create("urn:p2"));
        TriplePattern pattern2 = new TriplePattern(ANY_SUBJECT_NODE, pNode1, ANY_OBJECT_NODE);
        assertFalse("diff triple pattern", head.equals(pattern2));
        try {
            molTemp.setHeadTriple(pattern2);
        } catch (Exception e) {
            System.err.println("exception thrown.");
        }
    }

    public void testRootTriples() throws Exception {
        TriplePattern t1 = new TriplePattern(s1, p1, o1);
        TriplePattern t2 = new TriplePattern(s2, p2, o2);
        molTemp.addRootTriple(t1, t2);
        Set<TriplePattern> roots = molTemp.getRootTriples();
        checkRoots(roots, t1, t2);
        TriplePattern t3 = new TriplePattern(s1, p2, o2);
        molTemp.addRootTriple(t3);
        roots = molTemp.getRootTriples();
        checkRoots(roots, t1, t2, t3);
    }

    public void test2LvlTemplate() throws Exception {
        TriplePattern t0 = new TriplePattern(s1, p2, (ObjectNode) s0);
        TriplePattern t1 = new TriplePattern(s0, p1, o1);
        TriplePattern t2 = new TriplePattern(s2, p2, o2);
        MoleculeTemplate sub1 = new MoleculeTemplateImpl(GRAPH, tripleComparator);
        sub1.setHeadTriple(t1);
        molTemp.add(t0, sub1);
        molTemp.addRootTriple(t2);
        assertTrue("head ok", t0.equals(molTemp.getHeadTriple()));
        checkRoots(molTemp.getRootTriples(), t0, t2);
        List<MoleculeTemplate> subs = molTemp.getSubMoleculeTemplate(t0);
        assertEquals("1 sub molecule", 1, subs.size());
        checkRoots(subs.get(0).getRootTriples(), t1);
    }

    public void testSimpleRemove() throws Exception {
        TriplePattern t0 = new TriplePattern(s1, p2, (ObjectNode) s0);
        TriplePattern t1 = new TriplePattern(s0, p1, o1);
        TriplePattern t2 = new TriplePattern(s2, p2, o2);
        molTemp.addRootTriple(t0, t1, t2);
        MoleculeTemplate sub1 = new MoleculeTemplateImpl(GRAPH, tripleComparator);
        molTemp.add(t0, sub1);
        TriplePattern t4 = new TriplePattern(s1, p2, (ObjectNode) s0);
        molTemp.remove(t4);
        checkRoots(molTemp.getRootTriples(), t1, t2);
    }

    public void testComplexRemove() throws Exception {
        TriplePattern t0 = new TriplePattern(s1, p2, (ObjectNode) s0);
        TriplePattern t1 = new TriplePattern(s0, p1, o1);
        TriplePattern t2 = new TriplePattern(s2, p2, o2);
        molTemp.addRootTriple(t0, t1);
        molTemp.remove(t1);
        molTemp.addRootTriple(t2);
        checkRoots(molTemp.getRootTriples(), t0, t2);
    }

    public void testIterator() throws Exception {
        TriplePattern t0 = new TriplePattern(s1, p2, o1);
        TriplePattern t1 = new TriplePattern(s0, p1, o1);
        TriplePattern t2 = new TriplePattern(s2, p2, o2);
        TriplePattern t3 = new TriplePattern(s2, p2, o1);
        Iterator<TriplePattern> iterator = molTemp.iterator();
        assertFalse("No element", iterator.hasNext());
        molTemp.setHeadTriple(t0);
        MoleculeTemplate mt1 = new MoleculeTemplateImpl(GRAPH, tripleComparator);
        //mt1.addRootTriple(t1);
        MoleculeTemplate mt2 = new MoleculeTemplateImpl(GRAPH, tripleComparator);
        mt2.addRootTriple(t3);
        mt1.add(t1, mt2);
        molTemp.add(t2, mt1);
        molTemp.addRootTriple(t3);
        iterator = molTemp.iterator();
        checkRootsAsIterator(iterator, t0, t2, t1, t3, t3);
    }

    private void checkRoots(Set<TriplePattern> roots, TriplePattern... triples) {
        assertEquals("Same no. of triples", triples.length, roots.size());
        checkRootsAsIterator(roots.iterator(), triples);
    }

    private void checkRootsAsIterator(Iterator<TriplePattern> rootIter, TriplePattern... triples) {
        int length = 0;
        while (rootIter.hasNext()) {
            rootIter.next();
            length++;
        }
        assertEquals("Same no. of triples", triples.length, length);
        int i = 0;
        while (rootIter.hasNext()) {
            TriplePattern trip = rootIter.next();
            assertTrue("Same triple", trip.equals(triples[i++]));
        }
    }
}
