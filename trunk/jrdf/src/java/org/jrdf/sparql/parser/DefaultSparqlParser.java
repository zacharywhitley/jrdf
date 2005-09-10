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

import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.Query;
import org.jrdf.sparql.analysis.DefaultSparqlAnalyser;
import org.jrdf.sparql.analysis.SparqlAnalyser;
import org.jrdf.sparql.parser.lexer.Lexer;
import org.jrdf.sparql.parser.lexer.LexerException;
import org.jrdf.sparql.parser.node.Start;
import org.jrdf.sparql.parser.parser.Parser;
import org.jrdf.sparql.parser.parser.ParserException;
import org.jrdf.util.param.ParameterUtil;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

/**
 * Default implementation of a {@link SparqlParser}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class DefaultSparqlParser implements SparqlParser {

    // FIXME TJA: Test drive out throwing of InvalidQuerySyntaxException.

    private static final String INVALID_QUERY_MESSAGE = "Unable to parse query syntax";
    private static final int PUSHBACK_BUFFER_SIZE = 256;
    private SparqlAnalyser analyser = new DefaultSparqlAnalyser();

    /**
     * Parses a textual query into a {@link org.jrdf.query.Query} object.
     *
     * @param queryText The textual query to applyAnalyser.
     * @return A query object representing the <var>queryText</var>, will never be <code>null</code>.
     * @throws InvalidQuerySyntaxException If the syntax of the <code>query</code> is incorrect.
     */
    public Query parseQuery(String queryText) throws InvalidQuerySyntaxException {
        ParameterUtil.checkNotEmptyString("queryText", queryText);
        Parser parser = createParser(queryText);
        Start syntaxTree = parseQuerySyntax(parser);
        applyAnalyser(syntaxTree);
        return analyser.getQuery();
    }

    private Start parseQuerySyntax(Parser parser) throws InvalidQuerySyntaxException {
        try {
            return parser.parse();
        }
        catch (ParserException e) {
            throw new InvalidQuerySyntaxException(INVALID_QUERY_MESSAGE, e);
        }
        catch (LexerException e) {
            throw new InvalidQuerySyntaxException(INVALID_QUERY_MESSAGE, e);
        }
        catch (IOException e) {
            throw new InvalidQuerySyntaxException(INVALID_QUERY_MESSAGE, e);
        }
    }

    private Parser createParser(String queryText) {
        PushbackReader reader = createPushbackReader(queryText);
        return new Parser(new Lexer(reader));
    }

    private PushbackReader createPushbackReader(String queryText) {
        return new PushbackReader(new StringReader(queryText), PUSHBACK_BUFFER_SIZE);
    }

    private void applyAnalyser(Start start) {
        start.apply(analyser);
    }
}
