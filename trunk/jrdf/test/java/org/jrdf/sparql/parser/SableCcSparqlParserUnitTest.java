/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.sparql.parser;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expectLastCall;
import org.jrdf.graph.Graph;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.sparql.parser.lexer.LexerException;
import org.jrdf.sparql.parser.node.ASelectQueryStart;
import org.jrdf.sparql.parser.node.EOF;
import org.jrdf.sparql.parser.node.PStart;
import org.jrdf.sparql.parser.node.Start;
import org.jrdf.sparql.parser.node.TBlank;
import org.jrdf.sparql.parser.parser.Parser;
import org.jrdf.sparql.parser.parser.ParserException;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAndEmptyAssertions;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ParameterDefinition;
import org.jrdf.util.test.SparqlQueryTestUtil;

import java.io.IOException;
import java.lang.reflect.Modifier;

public final class SableCcSparqlParserUnitTest extends TestCase {
    private static final String QUERY_BOOK_1_DC_TITLE = SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
    private static final String ERROR_MSG = "Unable to parse query syntax";
    private static final ParserException PARSER_EXECPTION = new ParserException(new TBlank("foo", 1, 1), "bar");
    private static final LexerException LEXER_EXECPTION = new LexerException("foo");
    private static final Exception IO_EXCEPTION = new IOException();
    private static final String[] PARAM_NAMES = {"graph", "queryText"};
    private static final Class[] PARAM_TYPES = {Graph.class, String.class};
    private static final ParameterDefinition BUILD_PARAM_DEFINITION = new ParameterDefinition(PARAM_NAMES, PARAM_TYPES);
    private MockFactory mockFactory = new MockFactory();
    private Graph graph;
    private ParserFactory parserFactory;
    private GraphRelationFactory graphRelationFactory;
    private SortedAttributeFactory attributeFactory;

    public void setUp() {
        graph = mockFactory.createMock(Graph.class);
        parserFactory = mockFactory.createMock(ParserFactory.class);
        graphRelationFactory = mockFactory.createMock(GraphRelationFactory.class);
        attributeFactory = mockFactory.createMock(SortedAttributeFactory.class);
    }

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(SparqlParser.class, SableCcSparqllParser.class);
        checkConstructor(SableCcSparqllParser.class, Modifier.PUBLIC, ParserFactory.class, GraphRelationFactory.class,
            SortedAttributeFactory.class);
    }

    public void testParseQueryFailsWithBadInput() {
        final SparqlParser sableCcSparqlParser = createSableCcSparqlParser();
        checkMethodNullAndEmptyAssertions(sableCcSparqlParser, "parseQuery", BUILD_PARAM_DEFINITION);
    }

    public void testParseQuery() throws Exception {
        final Start start = createStart();
        final Parser parser = createParserReturnStart(start);
        addGetParserExpectationsToParserFactory(parser);
        final SableCcSparqllParser ccSparqlParser = createSableCcSparqlParser();
        mockFactory.replay();
        ccSparqlParser.parseQuery(graph, QUERY_BOOK_1_DC_TITLE);
        mockFactory.verify();
    }

    public void testParseThrowsParserException() throws Exception {
        checkThrowsException(PARSER_EXECPTION, ERROR_MSG + " token: [foo ]");
    }

    public void testParseThrowsLexerException() throws Exception {
        checkThrowsException(LEXER_EXECPTION, ERROR_MSG);
    }

    public void testParseThrowsIOException() throws Exception {
        checkThrowsException(IO_EXCEPTION, ERROR_MSG);
    }

    private Start createStart() {
        Start start = new Start();
        PStart pStart = new ASelectQueryStart();
        EOF eof = new EOF();
        start.setPStart(pStart);
        start.setEOF(eof);
        return start;
    }

    private SableCcSparqllParser createSableCcSparqlParser() {
        return new SableCcSparqllParser(parserFactory, graphRelationFactory, attributeFactory);
    }

    private void checkThrowsException(Exception exception, String errorMsg) throws Exception {
        Parser parser = createParserWithException(exception);
        addGetParserExpectationsToParserFactory(parser);
        final SableCcSparqllParser ccSparqlParser = createSableCcSparqlParser();
        mockFactory.replay();
        AssertThrows.assertThrows(InvalidQuerySyntaxException.class, errorMsg, new AssertThrows.Block() {
            public void execute() throws Throwable {
                ccSparqlParser.parseQuery(graph, QUERY_BOOK_1_DC_TITLE);
            }
        });
        mockFactory.verify();
    }


    private Parser createParserReturnStart(final Start start) throws Exception {
        final Parser mockParser = mockFactory.createMock(Parser.class);
        mockParser.parse();
        expectLastCall().andReturn(start);
        return mockParser;
    }

    private Parser createParserWithException(final Exception exception) throws Exception {
        final Parser mockParser = mockFactory.createMock(Parser.class);
        mockParser.parse();
        expectLastCall().andThrow(exception);
        return mockParser;
    }

    private void addGetParserExpectationsToParserFactory(final Parser parser) {
        parserFactory.getParser(QUERY_BOOK_1_DC_TITLE);
        expectLastCall().andReturn(parser);
        parserFactory.close();
        expectLastCall();
    }
}
