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

package org.jrdf.query.answer.xml

import junit.framework.TestCase
import org.jrdf.PersistentGlobalJRDFFactory
import org.jrdf.PersistentGlobalJRDFFactoryImpl
import org.jrdf.TestJRDFFactory
import org.jrdf.graph.Graph
import org.jrdf.query.answer.Answer
import org.jrdf.query.answer.SelectAnswer
import static org.jrdf.query.answer.SparqlResultType.LITERAL
import static org.jrdf.query.answer.SparqlResultType.URI_REFERENCE
import org.jrdf.sparql.SparqlConnection
import org.jrdf.util.DirectoryHandler
import org.jrdf.util.TempDirectoryHandler
import static org.jrdf.util.test.SetUtil.asSet
import org.jrdf.query.answer.TypeValueImpl
import org.jrdf.sparql.SparqlConnection

class SparqlSelectXmlWriterNewIntegrationTest extends GroovyTestCase {

  private static final DirectoryHandler HANDLER = new TempDirectoryHandler()
    private static final TestJRDFFactory TEST_FACTORY = TestJRDFFactory.getFactory()
    private static final PersistentGlobalJRDFFactory FACTORY = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER)
    private static final String QUERY = "SELECT ?s ?p ?o WHERE {?s ?p ?o .}"
    private static final SparqlXmlStreamWriterTestUtil TEST_UTIL = new SparqlXmlStreamWriterTestUtil()
    private static final def VARIABLES = asSet(["s", "p", "o"])
    public static final def RESULT1 = [
        "s": new TypeValueImpl(URI_REFERENCE, "urn:s1"),
        "p": new TypeValueImpl(URI_REFERENCE, "urn:p1"),
        "o": new TypeValueImpl(LITERAL, "\"l1\"")
    ]
    public static final def RESULT2 = [
        "s": new TypeValueImpl(URI_REFERENCE, "urn:s2"),
        "p": new TypeValueImpl(URI_REFERENCE, "urn:p2"),
        "o": new TypeValueImpl(LITERAL, "\"l2\"")
    ]
    public static final def RESULT3 = [
        "s", new TypeValueImpl(URI_REFERENCE, "urn:s3"),
        "p", new TypeValueImpl(URI_REFERENCE, "urn:p3"),
        "o", new TypeValueImpl(LITERAL, "\"l3\""),
    ];
    private SparqlConnection sparqlConnection
    private Graph graph
    private SparqlXmlWriter xmlWriter
    private Writer resultsWriter
    private Answer answer

    @Override
    public void setUp() {
        HANDLER.removeDir()
        HANDLER.makeDir()
        FACTORY.refresh()
        TEST_FACTORY.refresh()
        resultsWriter = new StringWriter()
        sparqlConnection = TEST_FACTORY.newSparqlConnection
        graph = FACTORY.newGraph
        TEST_UTIL.createTestGraph(graph)
        answer = sparqlConnection.executeQuery(graph, QUERY)
        xmlWriter = new SparqlSelectXmlWriter(resultsWriter, answer.getVariableNames(),
                answer.columnValuesIterator(), answer.numberOfTuples())
    }

    @Override
    public void tearDown() throws Exception {
        if (xmlWriter != null) {
            xmlWriter.close()
        }
        graph.close()
        HANDLER.removeDir()
    }

    public void testVariables() throws Exception {
        xmlWriter.writeFullDocument();
        xmlWriter.flush();
        def slurper =  new XmlSlurper()
        def sparql = slurper.parseText(resultsWriter.toString())
        def head = sparql.head
        assert 1 == head.size() : "Should have a head element"
        def allResults = sparql.results
        assert 1 == allResults.size() : "Should have a results element"
        def variables = head.variable
        checkVariables(VARIABLES, variables)
        def listOfResults = []
        def results = sparql.results.result
        results.each { result ->
            def bindingResults = [:]
            result.binding.each { binding ->
                //println "binding " + (binding.uri == "")
                bindingResults.(binding.@name) = binding.text()
            }
            listOfResults.add(bindingResults)
        }
        //println("Results " + listOfResults)
    }

    private def checkVariables(def expectedVariablesAsString, def actualVariablesAsAttributes) {
        List actualVarsAsString = actualVariablesAsAttributes.collect {
            it.@name.text()
        }
        assert expectedVariablesAsString == asSet(actualVarsAsString)
    }
}
