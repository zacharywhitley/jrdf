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

package org.jrdf.parser.bnodefactory;

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactoryImpl;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.map.BdbHandler;
import org.jrdf.map.BdbMapFactory;
import org.jrdf.map.MemMapFactory;
import org.jrdf.map.BdbHandlerImpl;
import org.jrdf.parser.Parser;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.parser.StatementHandler;
import org.jrdf.parser.rdfxml.RdfXmlParser;
import org.jrdf.util.test.MockFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ParserBlankNodeFactoryPerformanceTest extends TestCase {
    private final MockFactory mockFactory = new MockFactory();
    private final String PATH = "C:\\Documents and Settings\\alabri\\Desktop\\pizza.owl";
    private ParserBlankNodeFactory memParserBlankNodeFactory;
    private ParserBlankNodeFactory jeParserBlankNodeFactory;
    private BdbHandler handler;
    private GraphElementFactory graphElementFactory;
    private Graph jrdfGraph;
    private JRDFFactory jrdfFactory;
    private StatementHandler myStatementHandler;

    public void setUp() {
        handler = new BdbHandlerImpl();
        jrdfGraph = mockFactory.createMock(Graph.class);
        myStatementHandler = mockFactory.createMock(StatementHandler.class);
        jrdfFactory = SortedMemoryJRDFFactoryImpl.getFactory();
        jrdfGraph = jrdfFactory.getNewGraph();
        graphElementFactory = jrdfGraph.getElementFactory();
    }
    public void memoryPerformance() throws Exception {
        long startTime = 0;
        long finishTime = 0;
        InputStream stream = null;
        try {
            File rdfXmlFile = new File(PATH);
            stream = new FileInputStream(rdfXmlFile);
            Parser parser = getParserWithMemBlankNode();
            startTime =  System.currentTimeMillis();
            parser.parse(stream, "http://foo/bar");
            //myStatementHandler.flush();
            finishTime = System.currentTimeMillis();
        } finally {
            stream.close();
        }
        System.err.println("Using MemMapFactory:");
        System.err.println("Time Length: " + ((finishTime - startTime)) + "ms");
    }
    public void bdbPerformance() throws Exception {
        long startTime = 0;
        long finishTime = 0;
        InputStream stream = null;
        try {

            File rdfXmlFile = new File(PATH);
            stream = new FileInputStream(rdfXmlFile);
            Parser parser = getParserWithBdbBlankNode();
            startTime =  System.currentTimeMillis();
            parser.parse(stream, "http://foo/bar");
            //myStatementHandler.flush();
            finishTime = System.currentTimeMillis();
        } finally {
            stream.close();
        }
        System.err.println("\nUsing BDBMapFactory:");
        System.err.println("Time Length: " + ((finishTime - startTime)) + "ms");
    }
    private Parser getParserWithMemBlankNode() throws GraphException {
        ParserBlankNodeFactory memParserBlankNodeFactory = new ParserBlankNodeFactoryImpl(new MemMapFactory(), graphElementFactory);
        RdfXmlParser rdfParser = new RdfXmlParser(graphElementFactory, memParserBlankNodeFactory);
        rdfParser.setStatementHandler(myStatementHandler);
        rdfParser.setVerifyData(true);
        rdfParser.setStopAtFirstError(false);
        Parser parser = rdfParser;
        return parser;
    }
    private Parser getParserWithBdbBlankNode() throws GraphException {
        ParserBlankNodeFactory bdbParserBlankNodeFactory =
            new ParserBlankNodeFactoryImpl(new BdbMapFactory(handler, "catalog_name", "database_name"), graphElementFactory);
        RdfXmlParser rdfParser = new RdfXmlParser(graphElementFactory, bdbParserBlankNodeFactory);
        rdfParser.setStatementHandler(myStatementHandler);
        rdfParser.setVerifyData(true);
        rdfParser.setStopAtFirstError(false);
        Parser parser = rdfParser;
        return parser;
    }
}
