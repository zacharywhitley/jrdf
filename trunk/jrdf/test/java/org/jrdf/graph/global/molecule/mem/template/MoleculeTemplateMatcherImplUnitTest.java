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
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.global.BlankNodeImpl;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Yuan-Fang Li
 * @version $Revision:$
 */
public class MoleculeTemplateMatcherImplUnitTest extends TestCase {
    private final NodeTypeComparator typeComparator = new NodeTypeComparatorImpl();
    private final LocalizedNodeComparator localNodeComparator = new LocalizedNodeComparatorImpl();
    private final BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localNodeComparator);
    private final NodeComparator nodeComparator = new NodeComparatorImpl(typeComparator, blankNodeComparator);
    private final TripleComparator comparator = new TripleComparatorImpl(nodeComparator);
    private final static JRDFFactory FACTORY = SortedMemoryJRDFFactory.getFactory();

    private Graph graph;
    private GraphElementFactory eFac;
    private MoleculeTemplate template;
    private SubjectNode s1, s2;
    private PredicateNode p1, p2, p3;
    private ObjectNode o1, o2, o3;
    private TriplePattern tp1, tp2, tp3, tp4;
    private BlankNode b1, b2, b3, b4;
    private Triple t1, t2, t3, t4;
    private MoleculeTemplateMatcher matcher;
    private Set<Triple> set;
    private List<Triple> list;

    public void setUp() throws Exception {
        super.setUp();
        graph = FACTORY.getNewGraph();
        eFac = graph.getElementFactory();
        s1 = eFac.createURIReference(URI.create("urn:s1"));
        s2 = eFac.createURIReference(URI.create("urn:s2"));
        o1 = eFac.createURIReference(URI.create("urn:o1"));
        o2 = eFac.createURIReference(URI.create("urn:o2"));
        o3 = eFac.createURIReference(URI.create("urn:o3"));
        p1 = eFac.createURIReference(URI.create("urn:p1"));
        p2 = eFac.createURIReference(URI.create("urn:p2"));
        p3 = eFac.createURIReference(URI.create("urn:p3"));
        b1 = new BlankNodeImpl();
        b2 = new BlankNodeImpl();
        b3 = new BlankNodeImpl();
        b4 = new BlankNodeImpl();
        template = new MoleculeTemplateImpl(null, comparator);
    }

    public void tearDown() {
        if (set != null) {
            set.clear();
        }
        if (list != null) {
            list.clear();
        }
    }

    public void testOneSimpleTPMatching() throws Exception {
        tp1 = new TriplePattern(s1, p1, o1);
        addTPsToTemplate(tp1);
        Triple triple = new TripleImpl(s1, p1, o1);
        set = addTriplesToSet(triple);
        matcher = template.matcher(set);
        list = matcher.matches();
        assertEquals("1 triple", 1, list.size());
        assertEquals("Right triple", triple, list.get(0));
    }

    private void addTPsToTemplate(TriplePattern... tps) throws Exception {
        for (TriplePattern t1 : tps) {
            template.addRootTriple(t1);
        }
    }

    private Set<Triple> addTriplesToSet(Triple... triples) {
        set = new HashSet<Triple>();
        for (Triple triple : triples) {
            set.add(triple);
        }
        return set;
    }

    public void testOneAnyTPMatching() throws Exception {
        tp1 = new TriplePattern(ANY_SUBJECT_NODE, p1, o1);
        addTPsToTemplate(tp1);
        t1 = new TripleImpl(s1, p1, o1);
        set = addTriplesToSet(t1);
        matcher = template.matcher(set);
        list = matcher.matches();
        assertEquals("1 triple", 1, list.size());
        assertEquals("Right t1", t1, list.get(0));
    }

    public void test2LvlTPNotMatch() throws Exception {
        tp1 = new TriplePattern(ANY_SUBJECT_NODE, p1, o1);
        tp2 = new TriplePattern((SubjectNode) o1, p2, o2);
        MoleculeTemplate sub = new MoleculeTemplateImpl(null, comparator);
        sub.addRootTriple(tp2);
        template.add(tp1, sub);
        t1 = new TripleImpl(s2, p1, o1);
        t2 = new TripleImpl(s1, p2, o2);
        set = addTriplesToSet(t1, t2);
        matcher = template.matcher(set);
        list = matcher.matches();
        assertEquals("Not match", null, list);
    }

    public void test2LvlTPNotMatch1() throws Exception {
        tp1 = new TriplePattern(ANY_SUBJECT_NODE, p1, o1);
        tp2 = new TriplePattern((SubjectNode) o1, p2, o2);
        MoleculeTemplate sub = new MoleculeTemplateImpl(null, comparator);
        sub.addRootTriple(tp2);
        template.add(tp1, sub);
        t1 = new TripleImpl(s2, p1, o1);
        set = addTriplesToSet(t1);
        matcher = template.matcher(set);
        list = matcher.matches();
        assertEquals("Not match", null, list);
    }

    public void test2LvlTPMatch() throws Exception {
        tp1 = new TriplePattern(ANY_SUBJECT_NODE, p1, o1);
        tp2 = new TriplePattern((SubjectNode) o1, p2, o2);
        MoleculeTemplate sub = new MoleculeTemplateImpl(null, comparator);
        sub.addRootTriple(tp2);
        template.add(tp1, sub);
        t1 = new TripleImpl(s2, p1, o1);
        t2 = new TripleImpl((SubjectNode) o1, p2, o2);
        set = addTriplesToSet(t1, t2);
        matcher = template.matcher(set);
        list = matcher.matches();
        checkTripleList(list, t1, t2);
    }

    public void test3LvlChainTPMatch() throws Exception {
        tp1 = new TriplePattern(ANY_SUBJECT_NODE, p1, o1);
        tp2 = new TriplePattern((SubjectNode) o2, p2, ANY_OBJECT_NODE);
        tp3 = new TriplePattern((SubjectNode) o1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        MoleculeTemplate sub = new MoleculeTemplateImpl(null, comparator);
        MoleculeTemplate sub1 = new MoleculeTemplateImpl(null, comparator);
        sub.addRootTriple(tp2);
        sub1.add(tp3, sub);
        template.add(tp1, sub1);
        t1 = new TripleImpl(s1, p1, o1);
        t2 = new TripleImpl((SubjectNode) o1, p2, o2);
        t3 = new TripleImpl((SubjectNode) o2, p2, o1);
        set = addTriplesToSet(t1, t2, t3);
        matcher = template.matcher(set);
        list = matcher.matches();
        checkTripleList(list, t1, t2, t3);
        assertEquals("Set empty", 0, set.size());
    }

    public void test3LvlChainExtraTripleMatch() throws Exception {
        tp1 = new TriplePattern(ANY_SUBJECT_NODE, p1, o1);
        tp2 = new TriplePattern((SubjectNode) o2, p2, ANY_OBJECT_NODE);
        tp3 = new TriplePattern((SubjectNode) o1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        MoleculeTemplate sub = new MoleculeTemplateImpl(null, comparator);
        MoleculeTemplate sub1 = new MoleculeTemplateImpl(null, comparator);
        sub.addRootTriple(tp2);
        sub1.add(tp3, sub);
        template.add(tp1, sub1);
        t1 = new TripleImpl(s1, p1, o1);
        t2 = new TripleImpl((SubjectNode) o1, p2, o2);
        t3 = new TripleImpl((SubjectNode) o2, p2, o1);
        t4 = new TripleImpl(s1, p2, o2);
        set = addTriplesToSet(t4, t1, t2, t3);
        matcher = template.matcher(set);
        list = matcher.matches();
        checkTripleList(list, t1, t2, t3);
        assertEquals("Set left 1", 1, set.size());
        assertEquals("t4", t4, set.iterator().next());
    }

    public void test3LvlCircular() throws Exception {
        tp1 = new TriplePattern(s1, ANY_PREDICATE_NODE, o1);
        tp2 = new TriplePattern((SubjectNode) o1, p2, o2);
        tp3 = new TriplePattern((SubjectNode) o2, ANY_PREDICATE_NODE, (ObjectNode) s1);
        MoleculeTemplate sub = new MoleculeTemplateImpl(null, comparator);
        MoleculeTemplate sub1 = new MoleculeTemplateImpl(null, comparator);
        sub.add(tp2, sub1);
        sub1.addRootTriple(tp3);
        template.add(tp1, sub);
        t1 = new TripleImpl(s1, p1, o1);
        t2 = new TripleImpl((SubjectNode) o1, p2, o2);
        t3 = new TripleImpl((SubjectNode) o2, p1, (ObjectNode) s1);
        set = addTriplesToSet(t1, t2, t3);
        matcher = template.matcher(set);
        list = matcher.matches();
        checkTripleList(list, t1, t2, t3);
    }

    public void test3LvlChain() throws Exception {
        tp1 = new TriplePattern(s1, p1, o1);
        tp2 = new TriplePattern((SubjectNode) o1, p1, o2);
        tp3 = new TriplePattern((SubjectNode) o2, p1, (ObjectNode) s2);
        MoleculeTemplate sub = new MoleculeTemplateImpl(null, comparator);
        MoleculeTemplate sub1 = new MoleculeTemplateImpl(null, comparator);
        sub.add(tp2, sub1);
        sub1.addRootTriple(tp3);
        template.add(tp1, sub);
        t1 = new TripleImpl(s1, p1, o1);
        t2 = new TripleImpl((SubjectNode) o1, p1, o2);
        t3 = new TripleImpl((SubjectNode) o2, p1, (ObjectNode) s2);
        set = addTriplesToSet(t1, t2, t3);
        matcher = template.matcher(set);
        list = matcher.matches();
        checkTripleList(list, t1, t2, t3);
    }

    public void test3LvlPPI() throws Exception {
        // interaction hasParticipant interactor
        tp1 = new TriplePattern(b1, p1, b2);
        tp2 = new TriplePattern(b1, p1, b3);
        // interactor hasReference ref
        tp3 = new TriplePattern(b2, p2, o1);
        tp4 = new TriplePattern(b3, p2, o2);
        // ref hasID id
        TriplePattern tp5 = new TriplePattern((SubjectNode) o1, p3, o3);
        TriplePattern tp6 = new TriplePattern((SubjectNode) o2, p3, (ObjectNode) s2);
        MoleculeTemplate sub1 = new MoleculeTemplateImpl(null, comparator);
        MoleculeTemplate sub2 = new MoleculeTemplateImpl(null, comparator);
        MoleculeTemplate sub3 = new MoleculeTemplateImpl(null, comparator);
        MoleculeTemplate sub4 = new MoleculeTemplateImpl(null, comparator);
        sub3.addRootTriple(tp5);
        sub4.addRootTriple(tp6);
        sub1.add(tp3, sub3);
        sub2.add(tp4, sub4);
        template.add(tp1, sub1);
        template.add(tp2, sub2);
        System.err.println(template.toString());
    }

    private void checkTripleList(List<Triple> list, Triple... triples) {
        assertTrue("Not null", list != null && triples != null);
        assertEquals("Same no.", triples.length, list.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals("Triple " + i + " equals", triples[i], list.get(i));
        }
    }
}
