/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.graph.local.index.operation.mem;

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.operation.Comparison;

import java.net.URI;

// TODO (AN) Add tests for isGrounded - return false when there are blank nodes.
// TODO (AN) Add tests for areIsomorphic.

/**
 * Integration tests {@link org.jrdf.graph.local.index.index.operation.mem.ComparisonImpl}.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class ComparisonImplIntegrationTest extends TestCase {
    private static final URI URI1 = URI.create("http://foo/bar");
    private static final URI URI2 = URI.create("http://foo/bar/baz");
    private static final JRDFFactory FACTORY = TestJRDFFactory.getFactory();

    public void testMemGraphEquality() throws Exception {
        checkGraph(URI1, URI1, true);
        checkGraph(URI2, URI2, true);
        checkGraph(URI1, URI2, false);
    }

    private void checkGraph(URI resource1, URI resource2, boolean areEqual) throws Exception {
        Graph graph1 = FACTORY.getNewGraph();
        addTriple(graph1, resource1);
        Graph graph2 = FACTORY.getNewGraph();
        addTriple(graph2, resource2);
        Comparison comparison = new ComparisonImpl();
        assertEquals(areEqual, comparison.groundedGraphsAreEqual(graph1, graph2));
    }

    private void addTriple(Graph graph, URI uri) throws Exception {
        URIReference resource = graph.getElementFactory().createURIReference(uri);
        Triple triple = graph.getTripleFactory().createTriple(resource, resource, resource);
        graph.add(triple);
    }
}
