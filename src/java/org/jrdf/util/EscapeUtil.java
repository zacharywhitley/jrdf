/*
 * $Header$
 * $Revision$
 * $Date$
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
package org.jrdf.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO  Write a direct unit test for this instead of relying on AbstractLiteralUnitTest.

/**
 * A utility that applies N-Triples escaping.
 * <p/>
 * For reference:
 * http://www.echip.com.vn/echiproot/weblh/suutam/2000/uformat.htm
 *
 * @author Simon Raboczi
 * @author Andrew Newman
 * @version $Revision$
 */
public final class EscapeUtil {
    /**
     * A regular expression to pick out characters needing escape from Unicode to
     * ASCII.  Accepts surrogate pairs or all other escaped chars.
     * <p/>
     * This is used by the {@link #escape} method.
     */
    private static final Pattern PATTERN = Pattern.compile("[\uD800\uDC00-\uDBFF\uDFFF]" +
        "|" +
        "[\\x00-\\x1F\\x22\\\\\\x7F-\\uFFFF]");

    /**
     * How many characters at a time to decode for 8 bit encoding.
     */
    private static final int CHARACTER_LENGTH_8_BIT = 11;

    /**
     * How many characters at a time to decode for 16 bit encoding.
     */
    private static final int CHARACTER_LENGTH_16_BIT = 7;

    /**
     * Look up for escaping characters.
     */
    private static final Map<Character, String> CHAR_ESCAPE_LOOKUP = new HashMap<Character, String>() {
        private static final long serialVersionUID = 321L;

        {
            put('\t', "\\\\t");
            put('\n', "\\\\n");
            put('\r', "\\\\r");
            put('"', "\\\\\\\"");
            put('\\', "\\\\\\\\");
        }
    };


    private EscapeUtil() {
    }

    /**
     * Escapes a string literal to a string that is N-Triple escaped.
     *
     * @param string a string to escape, never <code>null</code>.
     * @return a version of the <var>string</var> with N-Triples escapes applied.
     * @throws IllegalStateException if there is no handler to perform the relevant match.
     */
    public static StringBuffer escape(String string) throws IllegalStateException {
        assert null != string;
        Matcher matcher = PATTERN.matcher(string);

        // Try to short-circuit the whole process -- maybe nothing needs escaping?
        if (!matcher.find()) {
            return new StringBuffer(string);
        }

        // Perform escape character substitutions on each match found by the
        // matcher, accumulating the escaped text into a stringBuffer
        StringBuffer stringBuffer = new StringBuffer();
        do {
            // The escape text with which to replace the current match
            matcher.appendReplacement(stringBuffer, getReplacementString(matcher));
        }
        while (matcher.find());

        // Finish off by appending any remaining text that didn't require escaping,
        // and return the assembled buffer
        matcher.appendTail(stringBuffer);
        return stringBuffer;
    }

    /**
     * Depending of the character sequence we're escaping, determine an appropriate replacement.
     *
     * @param matcher Used to match values.
     * @return escaped string value.
     * @throws IllegalStateException if there is no handler to perform the relevant match.
     */
    private static String getReplacementString(Matcher matcher) throws IllegalStateException {
        String escapeString;
        String groupString = matcher.group();

        int numMatches = groupString.length();
        if (numMatches == 1) {
            escapeString = escape16Bit(groupString);
        } else if (numMatches == 2) {
            escapeString = escape8Bit(groupString);
        } else {
            throw new IllegalStateException("Escape sequence " + groupString + " has no handler");
        }
        assert null != escapeString;
        return escapeString;
    }

    private static String escape16Bit(String groupString) {
        char firstChar = groupString.charAt(0);
        if (CHAR_ESCAPE_LOOKUP.keySet().contains(firstChar)) {
            return CHAR_ESCAPE_LOOKUP.get(firstChar);
        } else {
            return format16BitCharacter(groupString);
        }
    }

    private static String escape8Bit(String groupString) {
        assert Character.SURROGATE == Character.getType(groupString.charAt(0));
        assert Character.SURROGATE == Character.getType(groupString.charAt(1));

        String escapeString = format8BitCharacter(groupString);

        assert CHARACTER_LENGTH_8_BIT == escapeString.length();
        assert escapeString.startsWith("\\\\U000");

        return escapeString;
    }

    private static String format16BitCharacter(String groupString) {
        String hexString = Integer.toHexString(groupString.charAt(0)).toUpperCase();
        return "\\\\u0000".substring(0, CHARACTER_LENGTH_16_BIT - hexString.length()) + hexString;
    }

    private static String format8BitCharacter(String groupString) {
        int charValue = Character.codePointAt(groupString, 0);
        String hexString = Integer.toHexString(charValue).toUpperCase();
        return "\\\\U00000000".substring(0, CHARACTER_LENGTH_8_BIT - hexString.length()) + hexString;
    }
}