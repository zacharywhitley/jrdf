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

package org.jrdf.parser.ntriples.parser;

import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.io.Serializable;

// TODO AN Rename to something a bit more correct - escapes strings.
public final class NTripleUtilImpl implements NTripleUtil, Serializable {
    private static final long serialVersionUID = 4088891841006085382L;
    private static final Pattern LITERAL_ESCAPE_REGEX = Pattern.compile(
        "(\\\\((\\\\)|(\")|(n)|(r)|(t)|(u(\\p{XDigit}{4}))|(U(\\p{XDigit}{8}))))");
    private static final int LITERAL_ESCAPE_INDEX = 0;
    private static final int UNICODE_4DIGIT_INDEX = 9;
    private static final int UNICODE_8DIGIT_INDEX = 11;
    private static final int HEX_RADIX = 16;
    private static final Map<String, String> LITERAL_ESCAPE_LOOKUP = new HashMap<String, String>() {
        private static final long serialVersionUID = 321L;
        {
            put("\\\\", "\\\\");
            put("\\\"", "\\\"");
            put("\\n", "\n");
            put("\\r", "\r");
            put("\\t", "\t");
        }
    };
    private RegexMatcherFactory regexMatcherFactory;

    private NTripleUtilImpl() {
    }

    public NTripleUtilImpl(RegexMatcherFactory regexMatcherFactory) {
        checkNotNull(regexMatcherFactory);
        this.regexMatcherFactory = regexMatcherFactory;
    }

    public String unescapeLiteral(String literal) {
        checkNotNull(literal);
        RegexMatcher matcher = regexMatcherFactory.createMatcher(LITERAL_ESCAPE_REGEX, literal);
        if (!matcher.find()) {
            return literal;
        } else {
            return hasCharactersToEscape(matcher);
        }
    }

    // Can fail on each of these lines when parsing - handle error.
    private String hasCharactersToEscape(RegexMatcher matcher) {
        StringBuffer buffer = new StringBuffer();
        do {
            String escapeChar = matcher.group(LITERAL_ESCAPE_INDEX);
            String escapeValue = LITERAL_ESCAPE_LOOKUP.get(escapeChar);
            if (escapeValue != null) {
                matcher.appendReplacement(buffer, escapeValue);
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
    private void appendUnicode(RegexMatcher matcher, StringBuffer buffer, int group) {
        String unicodeString = matcher.group(group);
        int unicodeValue = Integer.parseInt(unicodeString, HEX_RADIX);
        char[] chars = Character.toChars(unicodeValue);
        matcher.appendReplacement(buffer, String.valueOf(chars));
    }
}
