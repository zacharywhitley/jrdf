/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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
 */

package org.jrdf.sparql.parser;

import junit.framework.TestCase;
import org.easymock.IMocksControl;
import org.jrdf.graph.Graph;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.sparql.builder.TripleBuilder;
import org.jrdf.sparql.parser.lexer.LexerException;
import org.jrdf.sparql.parser.node.AQueryStart;
import org.jrdf.sparql.parser.node.EOF;
import org.jrdf.sparql.parser.node.PStart;
import org.jrdf.sparql.parser.node.Start;
import org.jrdf.sparql.parser.node.TBlank;
import org.jrdf.sparql.parser.parser.Parser;
import org.jrdf.sparql.parser.parser.ParserException;
import org.jrdf.sparql.analysis.VariableCollector;
import org.jrdf.util.param.ParameterTestUtil;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.MockTestUtil;
import org.jrdf.util.test.SparqlQueryTestUtil;

import java.io.IOException;
import java.lang.reflect.Modifier;

/**
 * Unit test for {@link SableCcSparqlParser}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class SableCcSparqlParserUnitTest extends TestCase {

    private static final String QUERY_BOOK_1_DC_TITLE = SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
    private static final Graph GRAPH = MockTestUtil.createMock(Graph.class);
    private static final VariableCollector VARIABLE_COLLECTOR = MockTestUtil.createMock(VariableCollector.class);
    private static final ParserFactory PARSER_FACTORY = MockTestUtil.createMock(ParserFactory.class);
    private static final TripleBuilder TRIPLE_BUILDER = MockTestUtil.createMock(TripleBuilder.class);
    private static final String QUERY_TEXT_MESSAGE = "queryText cannot be null";
    private static final String QUERY_TEXT_EMPTY = "queryText cannot be the empty string";
    private static final String GRAPH_MESSAGE = "graph cannot be null";
    private static final String ERROR_MSG = "Unable to parse query syntax";
    private static final ParserException PARSER_EXECPTION = new ParserException(new TBlank("foo", 1,1), "bar");
    private static final LexerException LEXER_EXECPTION = new LexerException("foo");
    private static final Exception IO_EXCEPTION = new IOException();

    private MockFactory mockFactory;

    public void setUp() {
        mockFactory = new MockFactory();
    }

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(SparqlParser.class, SableCcSparqlParser.class);
        checkConstructor(SableCcSparqlParser.class, Modifier.PUBLIC, ParserFactory.class, TripleBuilder.class);
    }

    public void testParseQueryFailsWithBadInput() {
        checkBadInput(GRAPH, ParameterTestUtil.NULL_STRING, QUERY_TEXT_MESSAGE);
        checkBadInput(GRAPH, ParameterTestUtil.EMPTY_STRING, QUERY_TEXT_EMPTY);
        checkBadInput(GRAPH, ParameterTestUtil.SINGLE_SPACE, QUERY_TEXT_EMPTY);
        checkBadInput(null, ParameterTestUtil.NON_EMPTY_STRING, GRAPH_MESSAGE);
    }

    public void testParseQuery() throws Exception {
        Start start = createStart();
        Parser parser = createParser(start);
        ParserFactory parserFactory = createParserFactory(parser);
        SableCcSparqlParser sableCcSparqlParser = createSableCcSparqlParser(parserFactory, TRIPLE_BUILDER,
                VARIABLE_COLLECTOR);
        mockFactory.replay();
        sableCcSparqlParser.parseQuery(GRAPH, QUERY_BOOK_1_DC_TITLE);
        mockFactory.verify();
    }

    public void testParseThrowsParserException() throws Exception {
        checkThrowsException(PARSER_EXECPTION);
    }

    public void testParseThrowsLexerException() throws Exception {
        checkThrowsException(LEXER_EXECPTION);
    }

    public void testParseThrowsIOException() throws Exception {
        checkThrowsException(IO_EXCEPTION);
    }

    private Start createStart() {
        Start start = new Start();
        PStart pStart = new AQueryStart();
        EOF eof = new EOF();
        start.setPStart(pStart);
        start.setEOF(eof);
        return start;
    }

    @SuppressWarnings({ "unchecked" })
    private Parser createParser(Start start) throws Exception {
        IMocksControl control = mockFactory.createControl();
        Parser parser = control.createMock(Parser.class);
        parser.parse();
        control.andReturn(start);
        return parser;
    }

    private Parser createParser(Exception exception) throws Exception {
        IMocksControl control = mockFactory.createControl();
        Parser parser = control.createMock(Parser.class);
        parser.parse();
        control.andThrow(exception);
        return parser;
    }


    @SuppressWarnings({ "unchecked" })
    private ParserFactory createParserFactory(Parser parser) {
        IMocksControl control = mockFactory.createControl();
        ParserFactory parserFactory = control.createMock(ParserFactory.class);
        parserFactory.getParser(QUERY_BOOK_1_DC_TITLE);
        control.andReturn(parser);
        return parserFactory;
    }

    private void checkBadInput(final Graph graph, final String query, String errorMessage) {
        final SparqlParser sableCcSparqlParser = createSableCcSparqlParser(PARSER_FACTORY, TRIPLE_BUILDER,
                VARIABLE_COLLECTOR);
        AssertThrows.assertThrows(IllegalArgumentException.class, errorMessage, new AssertThrows.Block() {
            public void execute() throws Throwable {
                sableCcSparqlParser.parseQuery(graph, query);
            }
        });
    }

    private SableCcSparqlParser createSableCcSparqlParser(ParserFactory parserFactory, TripleBuilder tripleBuilder,
            VariableCollector variableCollector) {
        return new SableCcSparqlParser(parserFactory, tripleBuilder);

    }

    private void checkThrowsException(Exception exception)
            throws Exception {
        Parser parser = createParser(exception);
        ParserFactory parserFactory = createParserFactory(parser);
        final SableCcSparqlParser sableCcSparqlParser =
                createSableCcSparqlParser(parserFactory, TRIPLE_BUILDER, VARIABLE_COLLECTOR);
        mockFactory.replay();
        AssertThrows.assertThrows(InvalidQuerySyntaxException.class, ERROR_MSG, new AssertThrows.Block() {
            public void execute() throws Throwable {
                sableCcSparqlParser.parseQuery(GRAPH, QUERY_BOOK_1_DC_TITLE);
            }
        });
        mockFactory.verify();
    }
}
