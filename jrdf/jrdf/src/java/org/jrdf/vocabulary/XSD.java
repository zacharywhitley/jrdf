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

package org.jrdf.vocabulary;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * A set of constants for the standard XSD vocabulary.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class XSD extends Vocabulary {

    /**
     * Allow newer compiled version of the stub to operate when changes
     * have not occurred with the class.
     * NOTE : update this serialVersionUID when a method or a public member is
     * deleted.
     */
    private static final long serialVersionUID = -6831645627526760837L;

    /**
     * The URI of the XSD name space.
     */
    public static final URI BASE_URI;

    /**
     * URI of the string type.
     */
    public static final URI STRING;

    /**
     * URI of the boolean type.
     */
    public static final URI BOOLEAN;

    /**
     * URI of the decimal type.
     */
    public static final URI DECIMAL;

    /**
     * URI of the float type.
     */
    public static final URI FLOAT;

    /**
     * URI of the double type.
     */
    public static final URI DOUBLE;

    /**
     * URI of the duration type.
     */
    public static final URI DURATION;

    /**
     * URI of the dateTime type.
     */
    public static final URI DATE_TIME;

    /**
     * URI of the time type.
     */
    public static final URI TIME;

    /**
     * URI of the date type.
     */
    public static final URI DATE;

    /**
     * URI for the Gregorian year and month data type.
     */
    public static final URI G_YEAR_MONTH;

    /**
     * URI for the Gregorian year.
     */
    public static final URI G_YEAR;

    /**
     * URI for the Gregorian month and day.
     */
    public static final URI G_MONTH_DAY;

    /**
     * URI for the Gregorian day.
     */
    public static final URI G_DAY;

    /**
     * URI for the Gregorian month.
     */
    public static final URI G_MONTH;

    /**
     * URI for the hex binary date type.
     */
    public static final URI HEX_BINARY;

    /**
     * URI for the base64 binary data type.
     */
    public static final URI BASE_64_BINARY;

    /**
     * URI of the anyURI type.
     */
    public static final URI ANY_URI;

    /**
     * QName data type.
     */
    public static final URI Q_NAME;

    /**
     * Notation data type.
     */
    public static final URI NOTATION;

    /**
     * Integer data type - whole numbers (no factions), no limit in size.
     */
    public static final URI INTEGER;

    /**
     * Long data type.
     */
    public static final URI LONG;

    /**
     * Integer data type.
     */
    public static final URI INT;

    /**
     * NonPositiveInteger data type.
     */
    public static final URI NON_POSITIVE_INTEGER;

    /**
     * NonNegativeInteger data type.
     */
    public static final URI NON_NEGATIVE_INTEGER;
    /**

     * Short data type.
     */
    public static final URI SHORT;

    /**
     * Byte data type.
     */
    public static final URI BYTE;

    /**
     * The set of all decimal and derived data types.
     */
    public static final Set<URI> DECIMALS = new HashSet<URI>();

    /**
     * The set of all string and derived data types.
     */
    public static final Set<URI> STRINGS = new HashSet<URI>();

    static {
        try {
            BASE_URI = new URI("http://www.w3.org/2001/XMLSchema#");

            // Primitive data types.
            STRING = new URI(BASE_URI + "string");
            BOOLEAN = new URI(BASE_URI + "boolean");
            DECIMAL = new URI(BASE_URI + "decimal");
            FLOAT = new URI(BASE_URI + "float");
            DOUBLE = new URI(BASE_URI + "double");
            DURATION = new URI(BASE_URI + "duration");
            DATE_TIME = new URI(BASE_URI + "dateTime");
            TIME = new URI(BASE_URI + "time");
            DATE = new URI(BASE_URI + "date");
            G_YEAR_MONTH = new URI(BASE_URI + "gYearMonth");
            G_YEAR = new URI(BASE_URI + "gYear");
            G_MONTH_DAY = new URI(BASE_URI + "gMonthDay");
            G_DAY = new URI(BASE_URI + "gDay");
            G_MONTH = new URI(BASE_URI + "gMonth");
            HEX_BINARY = new URI(BASE_URI + "hexBinary");
            BASE_64_BINARY = new URI(BASE_URI + "base64Binary");
            ANY_URI = new URI(BASE_URI + "anyURI");
            Q_NAME = new URI(BASE_URI + "QName");
            NOTATION = new URI(BASE_URI + "NOTATION");
            NON_POSITIVE_INTEGER = new URI(BASE_URI + "nonPositiveInteger");
            NON_NEGATIVE_INTEGER = new URI(BASE_URI + "nonNegativeInteger");

            // Derived data types.
            INTEGER = new URI(BASE_URI + "integer");
            LONG = new URI(BASE_URI + "long");
            INT = new URI(BASE_URI + "int");
            SHORT = new URI(BASE_URI + "short");
            BYTE = new URI(BASE_URI + "byte");

            // Add base
            RESOURCES.add(STRING);
            RESOURCES.add(BOOLEAN);
            RESOURCES.add(DECIMAL);
            RESOURCES.add(FLOAT);
            RESOURCES.add(DOUBLE);
            RESOURCES.add(DURATION);
            RESOURCES.add(DATE_TIME);
            RESOURCES.add(TIME);
            RESOURCES.add(DATE);
            RESOURCES.add(G_YEAR_MONTH);
            RESOURCES.add(G_YEAR);
            RESOURCES.add(G_MONTH_DAY);
            RESOURCES.add(G_DAY);
            RESOURCES.add(G_MONTH);
            RESOURCES.add(HEX_BINARY);
            RESOURCES.add(BASE_64_BINARY);
            RESOURCES.add(ANY_URI);
            RESOURCES.add(Q_NAME);
            RESOURCES.add(NOTATION);

            // Add dervied
            RESOURCES.add(INTEGER);
            RESOURCES.add(LONG);
            RESOURCES.add(INT);
            RESOURCES.add(SHORT);
            RESOURCES.add(BYTE);
            RESOURCES.add(NON_POSITIVE_INTEGER);
            RESOURCES.add(NON_NEGATIVE_INTEGER);

            // Add all decimal types
            DECIMALS.add(DECIMAL);
            DECIMALS.add(INTEGER);
            DECIMALS.add(LONG);
            DECIMALS.add(INT);
            DECIMALS.add(SHORT);
            DECIMALS.add(BYTE);
            DECIMALS.add(NON_POSITIVE_INTEGER);
            DECIMALS.add(NON_NEGATIVE_INTEGER);

            // Add all string types
            STRINGS.add(STRING);
        } catch (URISyntaxException use) {

            // This should never happen.
            throw new ExceptionInInitializerError("Failed to create required URIs");
        }
    }
}
