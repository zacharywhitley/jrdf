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

package org.jrdf.parser.ntriples;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.Parser;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.parser.StatementHandler;
import org.jrdf.parser.StatementHandlerConfiguration;
import org.jrdf.parser.StatementHandlerException;
import org.jrdf.parser.ntriples.parser.URIReferenceParserImpl;
import org.jrdf.parser.ntriples.parser.URIReferenceParser;
import org.jrdf.parser.ntriples.parser.SubjectParserImpl;
import org.jrdf.parser.ntriples.parser.SubjectParser;
import org.jrdf.parser.ntriples.parser.PredicateParserImpl;
import org.jrdf.parser.ntriples.parser.PredicateParser;
import org.jrdf.parser.ntriples.parser.ObjectParserImpl;
import org.jrdf.parser.ntriples.parser.ObjectParser;
import org.jrdf.parser.ntriples.parser.BlankNodeParserImpl;
import org.jrdf.parser.ntriples.parser.BlankNodeParser;
import org.jrdf.parser.ntriples.parser.LiteralParserImpl;
import org.jrdf.parser.mem.ParserBlankNodeFactoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NTripleParser implements Parser, StatementHandlerConfiguration {
    private static final int SUBJECT_GROUP = 1;
    private static final int PREDICATE_GROUP = 2;
    private static final int OBJECT_GROUP = 3;
    private static final Pattern COMMENT_REGEX = Pattern.compile("\\p{Blank}*#([\\x20-\\x7E[^\\n\\r]])*");
    private static final Pattern TRIPLE_REGEX = Pattern.compile("\\p{Blank}*" +
        "(\\<[\\x20-\\x7E]+\\>|_:\\p{Alpha}\\p{Alnum}*)\\p{Blank}+" +
        "(\\<[\\x20-\\x7E]+\\>)\\p{Blank}+" +
        "(\\<[\\x20-\\x7E]+\\>|_:\\p{Alpha}\\p{Alnum}*\\\"[\\x20-\\x7E]+\\\"|[\\x20-\\x7E]+)\\p{Blank}*" +
        "\\.\\p{Blank}*");

    private final GraphElementFactory graphElementFactory;
    private final ParserBlankNodeFactory parserBlankNodeFactory;
    private final URIReferenceParser uriReferenceParser;
    private final BlankNodeParser blankNodeParser;
    private final LiteralParserImpl literalParser;
    private final SubjectParser subjectParser;
    private final PredicateParser predicateParser;
    private final ObjectParser objectParser;
    private StatementHandler sh;
    private LineNumberReader bufferedReader;

    /**
     * Creates a parser with an in memory blank node map.
     *
     * @param graphElementFactory used to create new triples.
     * @throws GraphException
     */
    public NTripleParser(GraphElementFactory graphElementFactory) {
        this(graphElementFactory, new ParserBlankNodeFactoryImpl(graphElementFactory));
    }

    public NTripleParser(GraphElementFactory graphElementFactory,
        ParserBlankNodeFactory parserBlankNodeFactory) {
        this.graphElementFactory = graphElementFactory;
        this.parserBlankNodeFactory = parserBlankNodeFactory;
        this.uriReferenceParser = new URIReferenceParserImpl(graphElementFactory);
        this.blankNodeParser = new BlankNodeParserImpl(graphElementFactory, parserBlankNodeFactory);
        this.literalParser = new LiteralParserImpl(graphElementFactory);
        this.subjectParser = new SubjectParserImpl(uriReferenceParser, blankNodeParser);
        this.predicateParser = new PredicateParserImpl(uriReferenceParser);
        this.objectParser = new ObjectParserImpl(uriReferenceParser, blankNodeParser, literalParser);
    }

    public void setStatementHandler(StatementHandler sh) {
        this.sh = sh;
    }

    public void parse(InputStream in, String baseURI) throws IOException, ParseException, StatementHandlerException {
        parse(new InputStreamReader(in), baseURI);
    }

    public void parse(Reader reader, String baseURI) throws IOException, ParseException, StatementHandlerException {
        bufferedReader = new LineNumberReader(reader);
        String line;
        Matcher tripleRegexMatcher;
        while ((line = bufferedReader.readLine()) != null) {
            if (!COMMENT_REGEX.matcher(line).matches()) {
                tripleRegexMatcher = TRIPLE_REGEX.matcher(line);
                if (tripleRegexMatcher.matches()) {
                    try {
                        parseTriple(tripleRegexMatcher);
                    } catch (GraphElementFactoryException e) {
                        new GraphException(e);
                    }
                }
            }
        }
    }

    private void parseTriple(Matcher tripleRegexMatcher)
        throws GraphElementFactoryException, ParseException, StatementHandlerException {
        SubjectNode subject = subjectParser.parseSubject(tripleRegexMatcher.group(SUBJECT_GROUP));
        PredicateNode predicate = predicateParser.parsePredicate(tripleRegexMatcher.group(PREDICATE_GROUP));
        ObjectNode object = objectParser.parseObject(tripleRegexMatcher.group(OBJECT_GROUP));
        if (subject != null && predicate != null && object != null) {
            sh.handleStatement(subject, predicate, object);
        }
    }
}
