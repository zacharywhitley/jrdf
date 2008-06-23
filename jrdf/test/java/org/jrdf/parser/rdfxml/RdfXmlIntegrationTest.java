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
import org.jrdf.graph.Graph;
import org.jrdf.map.MapFactory;
import org.jrdf.map.MemMapFactory;
import org.jrdf.parser.ParserBlankNodeFactory;
import static org.jrdf.parser.ParserTestUtil.checkNegativeRdfTestParseException;
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

    private static final Map<String, String[]> POSITIVE_TESTS = new HashMap<String, String[]>() {
        {
            put("rdf-tests/jrdf/test001.nt", new String[]{"rdf-tests/jrdf/test001.rdf"});
            put("rdf-tests/amp-in-url/test001.nt", new String[]{"rdf-tests/amp-in-url/test001.rdf"});
            put("rdf-tests/datatypes/test001.nt", new String[]{"rdf-tests/datatypes/test001.rdf"});
            put("rdf-tests/datatypes/test002.nt", new String[]{"rdf-tests/datatypes/test002.rdf"});
            put("rdf-tests/rdf-charmod-literals/test001.nt", new String[]{"rdf-tests/rdf-charmod-literals/test001.rdf"});
            put("rdf-tests/rdf-charmod-uris/test001.nt", new String[]{"rdf-tests/rdf-charmod-uris/test001.rdf"});
            put("rdf-tests/rdf-charmod-uris/test002.nt", new String[]{"rdf-tests/rdf-charmod-uris/test002.rdf"});
            put("rdf-tests/rdf-containers-syntax-vs-schema/test001.nt", new String[]{"rdf-tests/rdf-containers-syntax-vs-schema/test001.rdf"});
            //1 put("rdf-tests/rdf-containers-syntax-vs-schema/test002.nt", "rdf-tests/rdf-containers-syntax-vs-schema/test002.rdf");
            //2 put("rdf-tests/rdf-containers-syntax-vs-schema/test001-8.nt", new String[]{"rdf-tests/rdf-containers-syntax-vs-schema/test001-8.rdf"});
            put("rdf-tests/rdf-element-not-mandatory/test001.nt", new String[]{"rdf-tests/rdf-element-not-mandatory/test001.rdf"});
            put("rdf-tests/rdfms-difference-between-ID-and-about/test1.nt", new String[]{
                "rdf-tests/rdfms-difference-between-ID-and-about/test1.rdf",
                    "http://www.w3.org/2000/10/rdf-tests/rdfcore/rdfms-difference-between-ID-and-about/test1.rdf"});
            put("rdf-tests/rdfms-difference-between-ID-and-about/test2.nt", new String[]{
                "rdf-tests/rdfms-difference-between-ID-and-about/test2.rdf",
                    "http://www.w3.org/2000/10/rdf-tests/rdfcore/rdfms-difference-between-ID-and-about/test2.rdf"});
            put("rdf-tests/rdfms-difference-between-ID-and-about/test3.nt", new String[]{
                "rdf-tests/rdfms-difference-between-ID-and-about/test3.rdf",
                    "http://www.w3.org/2000/10/rdf-tests/rdfcore/rdfms-difference-between-ID-and-about/test3.rdf"});
            put("rdf-tests/rdfms-duplicate-member-props/test001.nt", new String[]{"rdf-tests/rdfms-duplicate-member-props/test001.rdf"});
        }
    };
    private static final Set<String> NEGATIVE_TESTS = new HashSet<String>() {
        {
            //2 add("rdf-tests/rdf-containers-syntax-vs-schema/error001.rdf");
            //2 add("rdf-tests/rdf-containers-syntax-vs-schema/error002.rdf");
            add("rdf-tests/rdfms-abouteach/error001.rdf");
            add("rdf-tests/rdfms-abouteach/error002.rdf");
            add("rdf-tests/rdfms-difference-between-ID-and-about/error1.rdf");
        }
    };

    public void testPositiveTests() throws Exception {
        for (String ntriplesFile : POSITIVE_TESTS.keySet()) {
            final String[] data = POSITIVE_TESTS.get(ntriplesFile);
            final String rdfFile = data[0];
            final URL expectedFile = getClass().getClassLoader().getResource(ntriplesFile);
            final URL actualFile = getClass().getClassLoader().getResource(rdfFile);
            Graph graph = TEST_JRDF_FACTORY.getNewGraph();
            MapFactory creator = new MemMapFactory();
            ParserBlankNodeFactory nodeFactory = new ParserBlankNodeFactoryImpl(creator, graph.getElementFactory());
            if (data.length == 2) {
                checkPositiveNtRdfTest(expectedFile, actualFile, data[1], graph, nodeFactory);
            } else {
                checkPositiveNtRdfTest(expectedFile, actualFile, "http://example.org/", graph, nodeFactory);
            }
        }
    }

    public void testNegativeTests() throws Exception {
        for (String rdfFile : NEGATIVE_TESTS) {
            final URL errorFile = getClass().getClassLoader().getResource(rdfFile);
            Graph graph = TEST_JRDF_FACTORY.getNewGraph();
            MapFactory creator = new MemMapFactory();
            ParserBlankNodeFactory nodeFactory = new ParserBlankNodeFactoryImpl(creator, graph.getElementFactory());
            checkNegativeRdfTestParseException(errorFile, graph, nodeFactory);
        }
    }
}
