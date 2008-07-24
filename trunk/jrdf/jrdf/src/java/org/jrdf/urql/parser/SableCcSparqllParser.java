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

package org.jrdf.urql.parser;

import org.jrdf.graph.Graph;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.Query;
import org.jrdf.query.relation.mem.AttributeValuePairHelper;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.urql.analysis.SparqlAnalyser;
import org.jrdf.urql.analysis.SparqlAnalyserImpl;
import org.jrdf.urql.builder.TripleBuilder;
import org.jrdf.urql.builder.TripleBuilderImpl;
import org.jrdf.urql.parser.lexer.LexerException;
import org.jrdf.urql.parser.node.Start;
import org.jrdf.urql.parser.parser.Parser;
import org.jrdf.urql.parser.parser.ParserException;
import static org.jrdf.util.param.ParameterUtil.checkNotEmptyString;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.io.IOException;

public final class SableCcSparqllParser implements SparqlParser {
    private static final String INVALID_QUERY_MESSAGE = "Unable to parse query syntax";
    private final AttributeValuePairHelper avpHelper;
    private final SortedAttributeFactory sortedAttributeFactory;
    private ParserFactory parserFactory;
    private GraphRelationFactory graphRelationFactory;

    public SableCcSparqllParser(ParserFactory parserFactory, GraphRelationFactory graphRelationFactory,
        AttributeValuePairHelper avpHelper, SortedAttributeFactory sortedAttributeFactory) {
        this.parserFactory = parserFactory;
        this.graphRelationFactory = graphRelationFactory;
        this.avpHelper = avpHelper;
        this.sortedAttributeFactory = sortedAttributeFactory;
    }

    /**
     * Parses a textual query into a {@link org.jrdf.query.Query} object.
     *
     * @param queryText The textual query to applyAnalyser.
     * @return A query object representing the <var>queryText</var>, will never be <code>null</code>.
     * @throws InvalidQuerySyntaxException If the syntax of the <code>query</code> is incorrect.
     */
    public Query parseQuery(Graph graph, String queryText) throws InvalidQuerySyntaxException {
        checkNotNull(graph);
        checkNotEmptyString("queryText", queryText);
        System.err.println("Query text: [" + queryText + "]");
        Parser parser = parserFactory.getParser(queryText);
        Start start = tryParse(parser);
        return analyseQuery(graph, start);
    }

    private Start tryParse(Parser parser) throws InvalidQuerySyntaxException {
        try {
            return parser.parse();
        } catch (ParserException e) {
            e.printStackTrace();
            throw new InvalidQuerySyntaxException(INVALID_QUERY_MESSAGE + " token: [" + e.getToken() + "]", e);
        } catch (LexerException e) {
            throw new InvalidQuerySyntaxException(INVALID_QUERY_MESSAGE, e);
        } catch (IOException e) {
            throw new InvalidQuerySyntaxException(INVALID_QUERY_MESSAGE, e);
        } finally {
            parserFactory.close();
        }
    }

    private Query analyseQuery(Graph graph, Start start) throws InvalidQuerySyntaxException {
        TripleBuilder builder = new TripleBuilderImpl(graph, avpHelper, sortedAttributeFactory);
        SparqlAnalyser analyser = new SparqlAnalyserImpl(builder, graph, graphRelationFactory);
        start.apply(analyser);
        try {
            return analyser.getQuery();
        } catch (ParserException e) {
            throw new InvalidQuerySyntaxException(INVALID_QUERY_MESSAGE + " token: " + e.getToken(), e);
        }
    }
}