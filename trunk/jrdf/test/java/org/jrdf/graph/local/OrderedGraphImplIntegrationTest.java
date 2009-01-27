/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.graph.local;

import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import org.jrdf.graph.AbstractGraphIntegrationTest;
import org.jrdf.graph.Graph;

/**
 * Implementation of {@link org.jrdf.graph.AbstractGraphIntegrationTest} test case.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public class OrderedGraphImplIntegrationTest extends AbstractGraphIntegrationTest {
    private static final JRDFFactory FACTORY = SortedMemoryJRDFFactory.getFactory();

    /**
     * Create a graph implementation.
     *
     * @return A new GraphImplUnitTest.
     */
    public Graph newGraph() throws Exception {
        return FACTORY.getNewGraph();
    }

//    //TODO: Create a disk based serialization test.
//
//    /**
//     * Implementation method for testing serialization of the graph.
//     *
//     * @throws Exception When a problem is found.
//     */
//    public void testSerializing() throws Exception {
//        // populate the graph
//        graph.add(blank1, ref1, blank2);
//        graph.add(blank1, ref2, blank2);
//        graph.add(blank1, ref1, l1);
//        graph.add(blank1, ref1, l2);
//        graph.add(blank2, ref1, blank2);
//        graph.add(blank2, ref2, blank2);
//        graph.add(blank2, ref1, l1);
//        graph.add(blank2, ref1, l2);
//        graph.add(blank2, ref1, l2);
//        graph.add(ref1, ref1, ref1);
//
//        // check that the graph is as expected
//        assertEquals(9, graph.getNumberOfTriples());
//
//        // create an in-memory output stream
//        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
//        ObjectOutputStream os = new ObjectOutputStream(outputBytes);
//
//        // write the graph
//        os.writeObject(graph);
//
//        // read a new graph back in
//        ByteArrayInputStream inputBytes = new ByteArrayInputStream(outputBytes.toByteArray());
//        ObjectInputStream is = new ObjectInputStream(inputBytes);
//
//        // read the graph
//        Graph graph2 = (Graph) is.readObject();
//
//        ref3 = graph2.getElementFactory().createURIReference(ref1.getURI());
//        Literal l3 = graph2.getElementFactory().createLiteral(l1.getLexicalForm());
//
//        // test that the graphs are equivalent
//        assertEquals(graph.getNumberOfTriples(), graph2.getNumberOfTriples());
//        assertTrue(graph2.contains(blank1, ref1, blank2));
//        assertTrue(graph2.contains(blank1, ref2, blank2));
//        assertTrue(graph2.contains(blank1, ref1, l1));
//        assertTrue(graph2.contains(blank1, ref1, l2));
//        assertTrue(graph2.contains(blank2, ref1, blank2));
//        assertTrue(graph2.contains(blank2, ref2, blank2));
//        assertTrue(graph2.contains(blank2, ref1, l1));
//        assertTrue(graph2.contains(blank2, ref3, l2));
//        assertTrue(graph2.contains(blank1, ref3, l3));
//        assertTrue(graph2.contains(ref1, ref1, ref1));
//        assertTrue(graph2.contains(ANY_SUBJECT_NODE, ref1, ANY_OBJECT_NODE));
//        assertTrue(graph2.contains(ref3, ref3, ref3));
//        assertTrue(graph2.contains(ANY_SUBJECT_NODE, ref3, ANY_OBJECT_NODE));
//        assertTrue(graph2.contains(ANY_SUBJECT_NODE, ref3, l3));
//    }
}
