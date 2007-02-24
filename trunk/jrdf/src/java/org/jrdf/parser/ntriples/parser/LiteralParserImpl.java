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

package org.jrdf.parser.ntriples.parser;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Literal;
import org.jrdf.parser.ParseException;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiteralParserImpl implements LiteralParser {
    private static final Pattern LANGUAGE_REGEX = Pattern.compile("\\\"([\\x20-\\x7E]*)\\\"" +
            "(" +
            "((\\@(\\p{Lower}+(\\-a-z0-9]+)*))|(\\^\\^\\<([\\x20-\\x7E]+)\\>))?" +
            ").*");
    private static final Pattern LITERAL_ESCAPE_REGEX = Pattern.compile(
            "(\\\\((\\\\)|(\")|(n)|(r)|(t)|(u(\\p{XDigit}{4}))|(U(\\p{XDigit}{8}))))");
    private static final int LITERAL_INDEX = 1;
    private static final int LANGUAGE_INDEX = 5;
    private static final int DATATYPE_INDEX = 8;
    private static final int LITERAL_ESCAPE_INDEX = 0;
    private static final int UNICODE_4DIGIT_INDEX = 9;
    private static final int UNICODE_8DIGIT_INDEX = 11;
    private final GraphElementFactory graphElementFactory;
    private static final int HEX_RADIX = 16;

    public LiteralParserImpl(GraphElementFactory graphElementFactory) {
        this.graphElementFactory = graphElementFactory;
    }

    public Literal parseLiteral(String s) throws GraphElementFactoryException, ParseException {
        Matcher matcher = LANGUAGE_REGEX.matcher(s);
        if (matcher.matches()) {
            String literal = unescapeLiteral(matcher.group(LITERAL_INDEX));
            String language = matcher.group(LANGUAGE_INDEX);
            String datatype = matcher.group(DATATYPE_INDEX);
            if (language != null) {
                return graphElementFactory.createLiteral(literal, language);
            } else if (datatype != null) {
                return graphElementFactory.createLiteral(literal, URI.create(datatype));
            } else {
                return graphElementFactory.createLiteral(literal);
            }
        } else {
            return null;
        }
    }

    private String unescapeLiteral(String literal) {
        Matcher matcher = LITERAL_ESCAPE_REGEX.matcher(literal);
        if (!matcher.find()) {
            return literal;
        } else {
            return hasCharactersToEscape(matcher);
        }
    }

    // Can fail on each of these lines when parsing - handle error.
    private String hasCharactersToEscape(Matcher matcher) {
        StringBuffer buffer = new StringBuffer();
        do {
            String escapeChar = matcher.group(LITERAL_ESCAPE_INDEX);
            if (escapeChar.equals("\\\\")) {
                matcher.appendReplacement(buffer, "\\\\");
            } else if (escapeChar.equals("\\\"")) {
                matcher.appendReplacement(buffer, "\"");
            } else if (escapeChar.equals("\\n")) {
                matcher.appendReplacement(buffer, "\n");
            } else if (escapeChar.equals("\\r")) {
                matcher.appendReplacement(buffer, "\r");
            } else if (escapeChar.equals("\\t")) {
                matcher.appendReplacement(buffer, "\t");
            } else if (escapeChar.startsWith("\\u")) {
                appendUnicode(matcher, buffer, UNICODE_4DIGIT_INDEX);
            } else if (escapeChar.startsWith("\\U")) {
                appendUnicode(matcher, buffer, UNICODE_8DIGIT_INDEX);
            }
        } while (matcher.find());
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    // Can fail on each of these lines when parsing - handle error.
    private void appendUnicode(Matcher matcher, StringBuffer buffer, int group) {
        String unicodeString = matcher.group(group);
        int unicodeValue = Integer.parseInt(unicodeString, HEX_RADIX);
        char[] chars = Character.toChars(unicodeValue);
        matcher.appendReplacement(buffer, String.valueOf(chars));
    }
}
