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

package org.jrdf.graph.global.molecule.mem;

import junit.framework.TestCase;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.global.molecule.FindEntryNode;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1B1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1B2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R2B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R3B1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R3B2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R3B4;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R3R3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B4R2B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B5R1B4;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B5R2B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.GRAPH;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R1R1B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R1R1R1;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.assertThrows;

public class FindEntryNodeImplUnitTest extends TestCase {
    private FindEntryNode finder = new FindEntryNodeImpl();

    public void setUp() throws Exception {
        GRAPH.remove(GRAPH.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator());
    }

    public void testSingleNode() throws Exception {
        GRAPH.add(B1R1B2);
        findAndCheck(B1R1B2, B1R1B2);
    }

    public void testSingleNode2() throws Exception {
        GRAPH.add(B1R1B1);
        findAndCheck(B1R1B1, B1R1B1);
    }

    public void testSingleNodeNotBlank() throws Exception {
        GRAPH.add(R1R1R1);
        findAndCheck(R1R1R1, R1R1R1);
    }

    public void testSingleNodeWrongStart() throws Exception {
        GRAPH.add(B1R1B2);
        assertThrows(GraphException.class, "Cannot find triple: " + B2R2B3, new AssertThrows.Block() {
            public void execute() throws Throwable {
                findAndCheck(B2R2B3, B2R2B3);
            }
        });
    }

    public void testSingleNodeWrongStart2() throws Exception {
        GRAPH.add(B1R1B2);
        assertThrows(GraphException.class, "Cannot find triple: " + B3R3B1, new AssertThrows.Block() {
            public void execute() throws Throwable {
                findAndCheck(B3R3B1, B3R3B1);
            }
        });
    }

    public void testTwoNodes() throws Exception {
        GRAPH.add(B1R1B2, B1R1B3);
        findAndCheck(B1R1B2, B1R1B2);
    }

    public void testTwoNodes2() throws Exception {
        GRAPH.add(B1R1B2, B1R1B3);
        findAndCheck(B1R1B3, B1R1B3);
    }

    public void testThreeNodes() throws Exception {
        GRAPH.add(B1R1B2, R1R1B3, B2R2B3);
        findAndCheck(B2R2B3, B1R1B2);
    }

    public void testThreeLevels() throws Exception {
        GRAPH.add(B1R1B2, B2R2B3, B3R3R3);
        findAndCheck(B3R3R3, B1R1B2);
    }

    public void testThreeLevelsAllBlank() throws Exception {
        GRAPH.add(B1R1B2, B2R2B3, B3R3B4);
        findAndCheck(B3R3B4, B1R1B2);
    }

    public void testThreeLevels2() throws Exception {
        GRAPH.add(B1R1B2, B2R2B3, B2R1R1, B3R3R3);
        findAndCheck(B3R3R3, B1R1B2);
    }

    public void testThreeLevelsWithDummies() throws Exception {
        GRAPH.add(B1R1B2, B2R2B3, B4R2B3, B5R2B3, B3R3B4);
        findAndCheck(B3R3B4, B1R1B2);
    }

    public void testThreeLevelsWithDummies2() throws Exception {
        GRAPH.add(B5R1B4, B4R2B3, B1R2B3, B2R2B3, B3R3B2);
        findAndCheck(B3R3B2, B5R1B4);
    }

    public void testThreeLevelsStartAtStart() throws Exception {
        GRAPH.add(B1R1B2, B2R2B3, B4R2B3, B5R2B3, B3R3B4);
        findAndCheck(B1R1B2, B1R1B2);
    }

    public void testThreeLevelsStartAtLevel2() throws Exception {
        GRAPH.add(B1R1B2, B2R2B3, B4R2B3, B5R2B3, B3R3B4);
        findAndCheck(B2R2B3, B1R1B2);
    }

    private void findAndCheck(Triple tripleToStartFrom, Triple tripleToFind) throws GraphException {
        Triple triple = finder.find(GRAPH, tripleToStartFrom);
        assertEquals(tripleToFind, triple);
    }
}
