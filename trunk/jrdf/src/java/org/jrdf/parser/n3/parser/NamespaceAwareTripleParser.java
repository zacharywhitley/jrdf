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

package org.jrdf.parser.n3.parser;

import org.jrdf.graph.Triple;
import org.jrdf.parser.ParseException;
import static org.jrdf.parser.n3.parser.NamespaceAwareNodeMaps.OBJECT_REGEX;
import static org.jrdf.parser.n3.parser.NamespaceAwareNodeMaps.PREDICATE_REGEX;
import static org.jrdf.parser.n3.parser.NamespaceAwareNodeMaps.SUBJECT_REGEX;
import org.jrdf.parser.ntriples.parser.BlankNodeParser;
import org.jrdf.parser.ntriples.parser.RegexTripleParser;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.regex.Pattern;

public class NamespaceAwareTripleParser implements TripleParser {
    private static final Pattern TRIPLE_REGEX = Pattern.compile("\\p{Blank}*" +
        "(<(.+?)>|((.+?):(.+?))|_:(.+?))\\p{Blank}+" +
        "(<(.+?)>|((.+?):(.+?)))\\p{Blank}+" +
        "(<(.+?)>|((.+?):(.+?))|_:(.+?)|(.+?))\\p{Blank}*\\.\\p{Blank}*");
    private static final int SUBJECT_GROUP = 1;
    private static final int PREDICATE_GROUP = 7;
    private static final int OBJECT_GROUP = 12;
    private final RegexMatcherFactory regexMatcherFactory;
    private final BlankNodeParser blankNodeParser;
    private final RegexTripleParser tripleParser;

    public NamespaceAwareTripleParser(final RegexMatcherFactory newRegexFactory,
        final BlankNodeParser newBlankNodeParser, final RegexTripleParser newTripleParser) {
        checkNotNull(newRegexFactory, newBlankNodeParser, newTripleParser);
        regexMatcherFactory = newRegexFactory;
        blankNodeParser = newBlankNodeParser;
        tripleParser = newTripleParser;
    }

    // This is a duplicate with TripleParserImpl - but it will stop being a duplicate when all the features of N3 are
    // implemented (such as blank nodes, short hand for predicates, etc).
    public Triple parseTriple(final CharSequence line) {
        try {
            final RegexMatcher regexMatcher = regexMatcherFactory.createMatcher(TRIPLE_REGEX, line);
            if (regexMatcher.matches()) {
                final RegexMatcher matcher1 = tripleParser.createMatcher(regexMatcher, SUBJECT_REGEX, SUBJECT_GROUP);
                final RegexMatcher matcher2 = tripleParser.createMatcher(regexMatcher, PREDICATE_REGEX,
                    PREDICATE_GROUP);
                final RegexMatcher matcher3 = tripleParser.createMatcher(regexMatcher, OBJECT_REGEX, OBJECT_GROUP);
                return tripleParser.parseTripleLine(matcher1, matcher2, matcher3);
            }
            return null;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        blankNodeParser.clear();
    }
}