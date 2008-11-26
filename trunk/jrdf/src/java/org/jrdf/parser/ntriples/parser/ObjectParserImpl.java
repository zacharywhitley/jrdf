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

package org.jrdf.parser.ntriples.parser;

import org.jrdf.graph.ObjectNode;
import org.jrdf.parser.ParseException;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;

public final class ObjectParserImpl implements ObjectParser {
    private static final Pattern REGEX = compile(
        "(<([\\x20-\\x7E]+?)>|_:(\\p{Alpha}[\\x20-\\x7E]*?)|([\\x20-\\x7E]+?))");
    private static final int LINE_GROUP = 0;
    private static final int URI_GROUP = 2;
    private static final int BLANK_NODE_GROUP = 3;
    private static final int LITERAL_GROUP = 4;
    private final RegexMatcherFactory factory;
    private final URIReferenceParser uriReferenceParser;
    private final BlankNodeParser blankNodeParser;
    private final LiteralParser literalParser;

    public ObjectParserImpl(RegexMatcherFactory newFactory, URIReferenceParser newUriReferenceParser,
        BlankNodeParser newBlankNodeParser, LiteralParser newLiteralParser) {
        checkNotNull(newFactory, newUriReferenceParser, newBlankNodeParser, newLiteralParser);
        factory = newFactory;
        uriReferenceParser = newUriReferenceParser;
        blankNodeParser = newBlankNodeParser;
        literalParser = newLiteralParser;
    }

    public ObjectNode parseNode(final CharSequence line) throws ParseException {
        checkNotNull(line);
        final RegexMatcher regexMatcher = factory.createMatcher(REGEX, line);
        if (regexMatcher.matches()) {
            return parseObject(regexMatcher);
        } else {
            throw new IllegalArgumentException("Couldn't match line: " + line);
        }
    }

    private ObjectNode parseObject(final RegexMatcher matcher) throws ParseException {
        if (matcher.group(URI_GROUP) != null) {
            return uriReferenceParser.parseURIReference(matcher.group(URI_GROUP));
        } else if (matcher.group(BLANK_NODE_GROUP) != null) {
            return blankNodeParser.parseBlankNode(matcher.group(BLANK_NODE_GROUP));
        } else if (matcher.group(LITERAL_GROUP) != null) {
            return literalParser.parseLiteral(matcher.group(LITERAL_GROUP));
        } else {
            throw new ParseException("Failed to parse line: " + matcher.group(LINE_GROUP), 1);
        }
    }
}
