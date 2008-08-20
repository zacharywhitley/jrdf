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

package org.jrdf.parser.rdfxml;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.collection.MapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.parser.ParserBlankNodeFactory;
import static org.jrdf.parser.ParserTestUtil.checkNegativeRdfTestParseException;
import static org.jrdf.parser.ParserTestUtil.checkPositiveNtNtTest;
import static org.jrdf.parser.ParserTestUtil.checkPositiveNtRdfTest;
import org.jrdf.parser.bnodefactory.ParserBlankNodeFactoryImpl;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// TODO 1 AN Doesn't do graph equality for blank nodes.
// TODO 2 AN Doesn't test for correctly created graphs - li's by themselves.
public class RdfXmlIntegrationTest extends TestCase {
    private static final TestJRDFFactory TEST_JRDF_FACTORY = TestJRDFFactory.getFactory();

    private static final Map<String, String> POSITIVE_RDFXML_TESTS = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            put("jrdf/test001.nt", "jrdf/test001.rdf");
            put("rdfcore/amp-in-url/test001.nt", "rdfcore/amp-in-url/test001.rdf");
            put("rdfcore/datatypes/test001.nt", "rdfcore/datatypes/test001.rdf");
            put("rdfcore/datatypes/test002.nt", "rdfcore/datatypes/test002.rdf");
            put("rdfcore/rdf-charmod-literals/test001.nt", "rdfcore/rdf-charmod-literals/test001.rdf");
            put("rdfcore/rdf-charmod-uris/test001.nt", "rdfcore/rdf-charmod-uris/test001.rdf");
            put("rdfcore/rdf-charmod-uris/test002.nt", "rdfcore/rdf-charmod-uris/test002.rdf");
            put("rdfcore/rdf-containers-syntax-vs-schema/test001.nt", "rdfcore/rdf-containers-syntax-vs-schema/test001.rdf");
            put("rdfcore/rdf-containers-syntax-vs-schema/test002.nt", "rdfcore/rdf-containers-syntax-vs-schema/test002.rdf");
            put("rdfcore/rdf-containers-syntax-vs-schema/test003.nt", "rdfcore/rdf-containers-syntax-vs-schema/test003.rdf");
            put("rdfcore/rdf-containers-syntax-vs-schema/test004.nt", "rdfcore/rdf-containers-syntax-vs-schema/test004.rdf");
            put("rdfcore/rdf-containers-syntax-vs-schema/test006.nt", "rdfcore/rdf-containers-syntax-vs-schema/test006.rdf");
            put("rdfcore/rdf-containers-syntax-vs-schema/test007.nt", "rdfcore/rdf-containers-syntax-vs-schema/test007.rdf");
            put("rdfcore/rdf-containers-syntax-vs-schema/test008.nt", "rdfcore/rdf-containers-syntax-vs-schema/test008.rdf");
            put("rdfcore/rdf-element-not-mandatory/test001.nt", "rdfcore/rdf-element-not-mandatory/test001.rdf");
            put("rdfcore/rdf-ns-prefix-confusion/test0001.nt", "rdfcore/rdf-ns-prefix-confusion/test0001.rdf");
            put("rdfcore/rdf-ns-prefix-confusion/test0003.nt", "rdfcore/rdf-ns-prefix-confusion/test0003.rdf");
            put("rdfcore/rdf-ns-prefix-confusion/test0004.nt", "rdfcore/rdf-ns-prefix-confusion/test0004.rdf");
            put("rdfcore/rdf-ns-prefix-confusion/test0005.nt", "rdfcore/rdf-ns-prefix-confusion/test0005.rdf");
            put("rdfcore/rdf-ns-prefix-confusion/test0006.nt", "rdfcore/rdf-ns-prefix-confusion/test0006.rdf");
            put("rdfcore/rdf-ns-prefix-confusion/test0009.nt", "rdfcore/rdf-ns-prefix-confusion/test0009.rdf");
            put("rdfcore/rdf-ns-prefix-confusion/test0010.nt", "rdfcore/rdf-ns-prefix-confusion/test0010.rdf");
            put("rdfcore/rdf-ns-prefix-confusion/test0011.nt", "rdfcore/rdf-ns-prefix-confusion/test0011.rdf");
            put("rdfcore/rdf-ns-prefix-confusion/test0012.nt", "rdfcore/rdf-ns-prefix-confusion/test0012.rdf");
            put("rdfcore/rdf-ns-prefix-confusion/test0013.nt", "rdfcore/rdf-ns-prefix-confusion/test0013.rdf");
            put("rdfcore/rdf-ns-prefix-confusion/test0014.nt", "rdfcore/rdf-ns-prefix-confusion/test0014.rdf");
            put("rdfcore/rdfms-difference-between-ID-and-about/test1.nt", "rdfcore/rdfms-difference-between-ID-and-about/test1.rdf");
            put("rdfcore/rdfms-difference-between-ID-and-about/test2.nt", "rdfcore/rdfms-difference-between-ID-and-about/test2.rdf");
            put("rdfcore/rdfms-difference-between-ID-and-about/test3.nt", "rdfcore/rdfms-difference-between-ID-and-about/test3.rdf");
            put("rdfcore/rdfms-duplicate-member-props/test001.nt", "rdfcore/rdfms-duplicate-member-props/test001.rdf");
            put("rdfcore/rdfms-empty-property-elements/test001.nt", "rdfcore/rdfms-empty-property-elements/test001.rdf");
            put("rdfcore/rdfms-empty-property-elements/test002.nt", "rdfcore/rdfms-empty-property-elements/test002.rdf");
            put("rdfcore/rdfms-empty-property-elements/test003.nt", "rdfcore/rdfms-empty-property-elements/test003.rdf");
            put("rdfcore/rdfms-empty-property-elements/test004.nt", "rdfcore/rdfms-empty-property-elements/test004.rdf");
            put("rdfcore/rdfms-empty-property-elements/test005.nt", "rdfcore/rdfms-empty-property-elements/test005.rdf");
            put("rdfcore/rdfms-empty-property-elements/test006.nt", "rdfcore/rdfms-empty-property-elements/test006.rdf");
            put("rdfcore/rdfms-empty-property-elements/test007.nt", "rdfcore/rdfms-empty-property-elements/test007.rdf");
            put("rdfcore/rdfms-empty-property-elements/test008.nt", "rdfcore/rdfms-empty-property-elements/test008.rdf");
            put("rdfcore/rdfms-empty-property-elements/test009.nt", "rdfcore/rdfms-empty-property-elements/test009.rdf");
            put("rdfcore/rdfms-empty-property-elements/test010.nt", "rdfcore/rdfms-empty-property-elements/test010.rdf");
            put("rdfcore/rdfms-empty-property-elements/test011.nt", "rdfcore/rdfms-empty-property-elements/test011.rdf");
            put("rdfcore/rdfms-empty-property-elements/test012.nt", "rdfcore/rdfms-empty-property-elements/test012.rdf");
            put("rdfcore/rdfms-empty-property-elements/test013.nt", "rdfcore/rdfms-empty-property-elements/test013.rdf");
            put("rdfcore/rdfms-empty-property-elements/test014.nt", "rdfcore/rdfms-empty-property-elements/test014.rdf");
            put("rdfcore/rdfms-empty-property-elements/test015.nt", "rdfcore/rdfms-empty-property-elements/test015.rdf");
            put("rdfcore/rdfms-empty-property-elements/test016.nt", "rdfcore/rdfms-empty-property-elements/test016.rdf");
            put("rdfcore/rdfms-empty-property-elements/test017.nt", "rdfcore/rdfms-empty-property-elements/test017.rdf");
            put("rdfcore/rdfms-identity-anon-resources/test001.nt", "rdfcore/rdfms-identity-anon-resources/test001.rdf");
            put("rdfcore/rdfms-identity-anon-resources/test002.nt", "rdfcore/rdfms-identity-anon-resources/test002.rdf");
            put("rdfcore/rdfms-identity-anon-resources/test003.nt", "rdfcore/rdfms-identity-anon-resources/test003.rdf");
            put("rdfcore/rdfms-identity-anon-resources/test004.nt", "rdfcore/rdfms-identity-anon-resources/test004.rdf");
            put("rdfcore/rdfms-identity-anon-resources/test005.nt", "rdfcore/rdfms-identity-anon-resources/test005.rdf");
            put("rdfcore/rdfms-not-id-and-resource-attr/test001.nt", "rdfcore/rdfms-not-id-and-resource-attr/test001.rdf");
        }
    };

    private static final Map<String, String> POSITIVE_NTRIPLE_TESTS = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            put("rdfcore/datatypes/test003a.nt", "rdfcore/datatypes/test003b.nt");
            put("rdfcore/datatypes/test003b.nt", "rdfcore/datatypes/test003a.nt");
            put("rdfcore/datatypes/test005a.nt", "rdfcore/datatypes/test005b.nt");
            put("rdfcore/datatypes/test008a.nt", "rdfcore/datatypes/test008b.nt");
            put("rdfcore/datatypes/test011a.nt", "rdfcore/datatypes/test011b.nt");
        }
    };

    private static final Set<String> NEGATIVE_TESTS = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
        {
            add("rdfcore/rdf-containers-syntax-vs-schema/error001.rdf");
            add("rdfcore/rdf-containers-syntax-vs-schema/error002.rdf");
            add("rdfcore/rdfms-abouteach/error001.rdf");
            add("rdfcore/rdfms-abouteach/error002.rdf");
            add("rdfcore/rdfms-difference-between-ID-and-about/error1.rdf");
        }
    };

    public void testPositiveRdfXmlTests() throws Exception {
        for (String ntriplesFile : POSITIVE_RDFXML_TESTS.keySet()) {
            final String rdfFile = POSITIVE_RDFXML_TESTS.get(ntriplesFile);
            final URL expectedFile = getClass().getClassLoader().getResource("rdf-tests/" + ntriplesFile);
            final URL actualFile = getClass().getClassLoader().getResource("rdf-tests/" + rdfFile);
            Graph graph = TEST_JRDF_FACTORY.getGraph();
            MapFactory creator = new MemMapFactory();
            ParserBlankNodeFactory nodeFactory = new ParserBlankNodeFactoryImpl(creator, graph.getElementFactory());
            checkPositiveNtRdfTest(expectedFile, actualFile, "http://www.w3.org/2000/10/rdf-tests/" + rdfFile,
                graph, nodeFactory);
        }
    }

    public void testPositiveNTriplesTests() throws Exception {
        for (String actualName :  POSITIVE_NTRIPLE_TESTS.keySet()) {
            final URL actualFile = getClass().getClassLoader().getResource("rdf-tests/" + actualName);
            final String expectedName = POSITIVE_NTRIPLE_TESTS.get(actualName);
            final URL expectedFile = getClass().getClassLoader().getResource("rdf-tests/" + expectedName);
            Graph graph = TEST_JRDF_FACTORY.getGraph();
            MapFactory creator = new MemMapFactory();
            ParserBlankNodeFactory nodeFactory = new ParserBlankNodeFactoryImpl(creator, graph.getElementFactory());
            checkPositiveNtNtTest(expectedFile,  actualFile, "http://example.org", graph, nodeFactory);
        }
    }

    public void testNegativeTests() throws Exception {
        for (String rdfFile : NEGATIVE_TESTS) {
            final URL errorFile = getClass().getClassLoader().getResource("rdf-tests/" + rdfFile);
            Graph graph = TEST_JRDF_FACTORY.getGraph();
            MapFactory creator = new MemMapFactory();
            ParserBlankNodeFactory nodeFactory = new ParserBlankNodeFactoryImpl(creator, graph.getElementFactory());
            checkNegativeRdfTestParseException(errorFile, graph, nodeFactory);
        }
    }
}
