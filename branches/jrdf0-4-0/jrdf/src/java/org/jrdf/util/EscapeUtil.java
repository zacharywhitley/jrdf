package org.jrdf.util;

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


    private EscapeUtil() {
    }

    /**
     * Escapes a string literal to a string that is N-Triple escaped.
     *
     * @param string a string to escape, never <code>null</code>.
     * @return a version of the <var>string</var> with N-Triples escapes applied.
     * @throws IllegalStateException if there is no handler to perform the relevant match.
     */
    public static String escape(String string) throws IllegalStateException {
        assert null != string;
        Matcher matcher = PATTERN.matcher(string);

        // Try to short-circuit the whole process -- maybe nothing needs escaping?
        if (!matcher.find()) {
            return string;
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
        return stringBuffer.toString();
    }

    /**
     * Depending of the character sequence we're escaping, determine an appropriate replacement.
     *
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
        String escapeString;
        switch (groupString.charAt(0)) {
            case '\t': // tab
                escapeString = "\\\\t";
                break;
            case '\n': // newline
                escapeString = "\\\\n";
                break;
            case '\r': // carriage return
                escapeString = "\\\\r";
                break;
            case '"':  // quote
                escapeString = "\\\\\\\"";
                break;
            case '\\': // backslash
                escapeString = "\\\\\\\\";
                break;
            default:   // other characters use 4-digit hex escapes
                escapeString = format16BitCharacter(groupString);
                assert CHARACTER_LENGTH_16_BIT == escapeString.length();
                assert escapeString.startsWith("\\\\u");
                break;
        }
        return escapeString;
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