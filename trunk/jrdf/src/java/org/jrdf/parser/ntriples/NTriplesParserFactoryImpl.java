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

package org.jrdf.parser.ntriples;

import org.jrdf.graph.Graph;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.parser.ntriples.parser.BlankNodeParser;
import org.jrdf.parser.ntriples.parser.BlankNodeParserImpl;
import org.jrdf.parser.ntriples.parser.LiteralMatcher;
import org.jrdf.parser.ntriples.parser.LiteralParser;
import org.jrdf.parser.ntriples.parser.LiteralParserImpl;
import org.jrdf.parser.ntriples.parser.NTripleUtil;
import org.jrdf.parser.ntriples.parser.NTripleUtilImpl;
import org.jrdf.parser.ntriples.parser.RegexLiteralMatcher;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.parser.ntriples.parser.TripleParserImpl;
import org.jrdf.parser.ntriples.parser.URIReferenceParser;
import org.jrdf.parser.ntriples.parser.URIReferenceParserImpl;
import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;

import java.util.regex.Pattern;

public class NTriplesParserFactoryImpl implements NTriplesParserFactory {
    private static final Pattern TRIPLE_REGEX = Pattern.compile("\\p{Blank}*" +
        "(\\<([\\x20-\\x7E]+?)\\>|_:((\\p{Alpha}\\p{Alnum}*?)))\\p{Blank}+" +
        "(\\<([\\x20-\\x7E]+?)\\>)\\p{Blank}+" +
        "(\\<([\\x20-\\x7E]+?)\\>|_:((\\p{Alpha}\\p{Alnum}*?))|((([\\x20-\\x7E]+?))))\\p{Blank}*" +
        "\\.\\p{Blank}*");

    public NTriplesParser createParser(Graph newGraph, ParserBlankNodeFactory parserBlankNodeFactory) {
        BlankNodeParser blankNodeParser = new BlankNodeParserImpl(parserBlankNodeFactory);
        RegexMatcherFactory matcherFactory = new RegexMatcherFactoryImpl();
        NTripleUtil literalUtil = new NTripleUtilImpl(matcherFactory);
        LiteralMatcher literalMatcher = new RegexLiteralMatcher(matcherFactory, literalUtil);
        LiteralParser literalParser = new LiteralParserImpl(newGraph.getElementFactory(), literalMatcher);
        URIReferenceParser uriReferenceParser = new URIReferenceParserImpl(newGraph.getElementFactory(), literalUtil);
        TripleParser tripleParser = new TripleParserImpl(uriReferenceParser, blankNodeParser, literalParser,
            newGraph.getTripleFactory());
        return new NTriplesParser(new CommentsParserImpl(matcherFactory),
            new TriplesParserImpl(tripleParser, matcherFactory, TRIPLE_REGEX));
    }
}
