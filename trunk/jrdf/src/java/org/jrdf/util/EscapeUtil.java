package org.jrdf.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility that applies N-Triples escaping.
 *
 * For reference:
 * http://www.echip.com.vn/echiproot/weblh/suutam/2000/uformat.htm
 *
 * @author Simon Raboczi
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public final class EscapeUtil {
    /**
     * A regular expression to pick out characters needing escape from Unicode to
     * ASCII.  Accepts surrogate pairs or all other escaped chars.
     *
     * This is used by the {@link #escape} method.
     */
    private static final Pattern PATTERN = Pattern.compile("\\p{InHighSurrogates}\\p{InLowSurrogates}" +
        "|" +
        "[\\x00-\\x1F\\x22\\\\\\x7F-\\uFFFF]"
    );

    /**
     * The matcher instance used to escape characters from Unicode to ASCII.
     *
     * This is lazily initialized and used by the {@link #escape} method.
     */
    private static Matcher matcher;

    /**
     * Base UTF Code point.
     */
    private static final int UTF_BASE_CODEPOINT = 0x10000;

    /**
     * Shift value for high surrogates.
     */
    private static final int SURROGATE_SHIFT_VALUE = 10;

    /**
     * The mask to get UTF-16 character codes.
     */
    private static final int CHARACTER_CODE_OFFSET = 0x3FF;

    /**
     *  How many characters at a time to decode for 8 bit encoding.
     */
    private static final int CHARACTER_LENGTH_8_BIT = 11;

    /**
     *  How many characters at a time to decode for 16 bit encoding.
     */
    private static final int CHARACTER_LENGTH_16_BIT = 7;


    private EscapeUtil() {
    }

    /**
     * Escapes a string literal to a string that is N-Triple escaped.
     *
     * @param string  a string to escape, never <code>null</code>.
     * @return a version of the <var>string</var> with N-Triples escapes applied.
     */
    public static String escape(String string) {
        assert null != string;

        // Obtain a fresh matcher
        if (null == matcher) {
            // Lazily initialize the matcher
            matcher = PATTERN.matcher(string);
        }
        else {
            // Reuse the existing matcher
            matcher.reset(string);
        }
        assert null != matcher;

        // Try to short-circuit the whole process -- maybe nothing needs escaping?
        if (!matcher.find()) {
            return string;
        }

        // Perform escape character substitutions on each match found by the
        // matcher, accumulating the escaped text into a stringBuffer
        StringBuffer stringBuffer = new StringBuffer();
        do {
            // The escape text with which to replace the current match
            String escapeString;

            // Depending of the character sequence we're escaping, determine an
            // appropriate replacement
            String groupString = matcher.group();
            switch (groupString.length()) {
                case 1: // 16-bit characters requiring escaping
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
                    break;

                case 2: // surrogate pairs are represented as 8-digit hex escapes
                    assert Character.SURROGATE == Character.getType(groupString.charAt(0));
                    assert Character.SURROGATE == Character.getType(groupString.charAt(1));
                    escapeString = format8BitCharacter(groupString);
                    assert CHARACTER_LENGTH_8_BIT == escapeString.length();
                    assert escapeString.startsWith("\\\\U000");
                    break;

                default:
                    throw new Error("Escape sequence " + groupString + " has no handler");
            }
            assert null != escapeString;

            // Having determined an appropriate escapeString, add it to the
            // stringBuffer
            matcher.appendReplacement(stringBuffer, escapeString);
        }
        while (matcher.find());

        // Finish off by appending any remaining text that didn't require escaping,
        // and return the assembled buffer
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static String format16BitCharacter(String groupString) {
        String hexString = Integer.toHexString(groupString.charAt(0)).toUpperCase();
        return "\\\\u0000".substring(0, CHARACTER_LENGTH_16_BIT - hexString.length()) + hexString;
    }

    private static String format8BitCharacter(String groupString) {
        int charValue = getHighSurrogate(groupString) + getLowSurrogate(groupString) + UTF_BASE_CODEPOINT;
        String hexString = Integer.toHexString(charValue).toUpperCase();
        return "\\\\U00000000".substring(0, CHARACTER_LENGTH_8_BIT - hexString.length()) + hexString;
    }

    private static int getHighSurrogate(String groupString) {
        return ((groupString.charAt(0) & CHARACTER_CODE_OFFSET) << SURROGATE_SHIFT_VALUE);
    }

    private static int getLowSurrogate(String groupString) {
        return (groupString.charAt(1) & CHARACTER_CODE_OFFSET);
    }
}