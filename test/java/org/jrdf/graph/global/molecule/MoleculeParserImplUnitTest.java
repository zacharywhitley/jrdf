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
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import org.jrdf.parser.Parser;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.graph.global.GlobalizedGraph;
import org.jrdf.graph.global.TripleImpl;
import org.jrdf.graph.global.URIReferenceImpl;
import org.jrdf.util.EscapeURL;
import org.jrdf.vocabulary.RDF;

import java.net.URI;
import java.net.URL;

public class MoleculeParserImplUnitTest extends TestCase {
    private static final int NUMBER_OF_MOLECULES_IN_PIZZA = 1384;
    private static final int NUMBER_OF_TRIPLES_IN_PIZZA = 2332;
    private static final String BASE_URI = "http://example.org";
    private final JRDFFactory factory = SortedMemoryJRDFFactory.getFactory();
    private final Graph jrdfGraph = factory.getNewGraph();
    private MoleculeParser moleculeParser;
    private final URIReferenceImpl BANGLADESH_URI =
        new URIReferenceImpl(URI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#Bangladesh"));
    private final URIReferenceImpl AMERICAN_URI =
        new URIReferenceImpl(URI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#American"));
    private final URIReferenceImpl TYPE_URI = new URIReferenceImpl(RDF.TYPE);
    private final URIReferenceImpl CLASS_URI = new URIReferenceImpl(URI.create("http://www.w3.org/2002/07/owl#Class"));
    private final URL resource = getClass().getResource("/org/jrdf/example/pizza.rdf");

    public void setUp() throws Exception {
        // Work out why we need to do this.
        jrdfGraph.clear();
        moleculeParser = new MoleculeParserImpl(jrdfGraph);
    }

    public void testParse() throws Exception {
        moleculeParser.parse(resource.openStream(), BASE_URI);
    }

    public void testNormalParser() throws Exception {
        Parser parser = new GraphRdfXmlParser(jrdfGraph);
        parser.parse(resource.openStream(), EscapeURL.toEscapedString(resource));
    }

    public void testGetGlobalizedGraph() throws Exception {
        moleculeParser.parse(resource.openStream(), EscapeURL.toEscapedString(resource));
        GlobalizedGraph globalizedGraph = moleculeParser.getGlobalizedGraph();
        assertEquals(NUMBER_OF_TRIPLES_IN_PIZZA, globalizedGraph.getNumberOfTriples());
        assertEquals(NUMBER_OF_MOLECULES_IN_PIZZA, globalizedGraph.getNumberOfMolecules());

        Triple triple = new TripleImpl(AMERICAN_URI, TYPE_URI, CLASS_URI);
        boolean result = globalizedGraph.contains(triple);
        assertTrue(result);

        triple = new TripleImpl(BANGLADESH_URI, TYPE_URI, CLASS_URI);
        result = globalizedGraph.contains(triple.getSubject(), triple.getPredicate(), triple.getObject());
        assertFalse(result);
    }
}
