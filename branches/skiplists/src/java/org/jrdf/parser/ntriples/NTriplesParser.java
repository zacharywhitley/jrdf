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

import org.jrdf.graph.Triple;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.Parser;
import org.jrdf.parser.StatementHandler;
import org.jrdf.parser.StatementHandlerConfiguration;
import org.jrdf.parser.StatementHandlerException;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.regex.Pattern;

public class NTriplesParser implements Parser, StatementHandlerConfiguration {
    private static final Pattern COMMENT_REGEX = Pattern.compile("\\p{Blank}*#([\\x20-\\x7E[^\\n\\r]])*");
    private static final Pattern TRIPLE_REGEX = Pattern.compile("\\p{Blank}*" +
        "(\\<([\\x20-\\x7E]+?)\\>|_:((\\p{Alpha}\\p{Alnum}*?)))\\p{Blank}+" +
        "(\\<([\\x20-\\x7E]+?)\\>)\\p{Blank}+" +
        "(\\<([\\x20-\\x7E]+?)\\>|_:((\\p{Alpha}\\p{Alnum}*?))|((([\\x20-\\x7E]+?))))\\p{Blank}*" +
        "\\.\\p{Blank}*");

    private final TripleParser tripleParser;
    private final RegexMatcherFactory regexMatcherFactory;
    private StatementHandler sh;

    public NTriplesParser(TripleParser tripleFactory, RegexMatcherFactory newRegexFactory) {
        this.tripleParser = tripleFactory;
        this.regexMatcherFactory = newRegexFactory;
    }

    public void setStatementHandler(StatementHandler statementHandler) {
        this.sh = statementHandler;
    }

    public void parse(InputStream in, String baseURI) throws IOException, ParseException, StatementHandlerException {
        parse(new InputStreamReader(in), baseURI);
    }

    public void parse(Reader reader, String baseURI) throws IOException, ParseException, StatementHandlerException {
        LineNumberReader bufferedReader = new LineNumberReader(reader);
        String line;
        RegexMatcher tripleRegexMatcher;
        while ((line = bufferedReader.readLine()) != null) {
            RegexMatcher commentMatcher = regexMatcherFactory.createMatcher(COMMENT_REGEX, line);
            if (!commentMatcher.matches()) {
                tripleRegexMatcher = regexMatcherFactory.createMatcher(TRIPLE_REGEX, line);
                if (tripleRegexMatcher.matches()) {
                    parseTriple(tripleRegexMatcher);
                }
            }
        }
    }

    private void parseTriple(RegexMatcher tripleRegexMatcher) throws StatementHandlerException {
        Triple triple = tripleParser.parseTriple(tripleRegexMatcher);
        sh.handleStatement(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }
}