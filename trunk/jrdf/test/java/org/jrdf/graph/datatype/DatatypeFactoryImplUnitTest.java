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

package org.jrdf.graph.datatype;

import org.jrdf.vocabulary.XSD;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DatatypeFactoryImplUnitTest {
    private static final int TEN_SECONDS = 10000;
    private static final Date TEN_SECONDS_AFTER_UNIX_EPOCH = new Date(TEN_SECONDS);
    private static final String NORMAL_STRING = "This is a normal string";
    private static final String TEN_SECOND_AFTER_UNIX_EPOCH_STRING = "1970-01-01T10:00:10.000+10:00";
    private static final String HAPPY_NEW_YEAR = "2000-01-01T00:00:00.000+10:00";
    private static final String COFFEE_TIME = "09:30:00.000+10:00";
    private static final String DURATION_1 = "P0Y0M0DT0H10M0S";
    private static final String DURATION_2 = "-P120D";
    private static final String G_MONTH_DAY_STR = "--01-21";
    private static final String G_YEAR_MONTH_STR = "1999-06";
    private static final String G_YEAR_STR = "2007";
    private static final String G_DAY_STR = "---28";
    private static final String G_MONTH_STR_1 = "--12";
    private static final String G_MONTH_STR_2 = "--12+10:00";
    private static final String G_MONTH_STR_3 = "--08-04:00";
    private static final String URI_STR = "http://foo/bar#Litral";
    private static final String ZERO = "0";
    private static final String POSITIVE_ZERO = "+0";
    private static final String NEGATIVE_ZERO = "-0";
    private static final String ONE = "1";
    private static final String POSITIVE_ONE = "+1";
    private static final String NEGATIVE_ONE = "-1";
    private DatatypeFactory datatypeFactory = DatatypeFactoryImpl.getInstance();

    @Before
    public void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+10:00"));
    }

    @Test
    public void testCreatingCalendar() {
        Calendar year2000 = new GregorianCalendar(2000, 0, 1);
        DatatypeValue value = datatypeFactory.createValue(year2000);
        assertThat(value.getLexicalForm(), equalTo(HAPPY_NEW_YEAR));
        URI uri = datatypeFactory.getObjectDatatypeURI(year2000);
        assertThat(uri, equalTo(XSD.DATE_TIME));
    }

    public void testCreatingDateTimeValue() {
        DatatypeValue value = datatypeFactory.createValue(TEN_SECONDS_AFTER_UNIX_EPOCH);
        assertThat(value.getLexicalForm(), equalTo(TEN_SECOND_AFTER_UNIX_EPOCH_STRING));
        URI uri = datatypeFactory.getObjectDatatypeURI(TEN_SECONDS_AFTER_UNIX_EPOCH);
        assertThat(uri, equalTo(XSD.DATE_TIME));
    }

    public void testStringToTime() {
        DatatypeValue value = datatypeFactory.createValue(XSD.TIME, COFFEE_TIME);
        assertThat(value.getLexicalForm(), equalTo(COFFEE_TIME));
    }

    public void testStringToGYearMonth() {
        checkWrongStringValueAndCreate(G_YEAR_MONTH_STR, XSD.G_YEAR_MONTH);
    }

    public void testStringToGYear() {
        checkWrongStringValueAndCreate(G_YEAR_STR, XSD.G_YEAR);
    }

    public void testStringToGMonthDay() {
        checkWrongStringValueAndCreate(G_MONTH_DAY_STR, XSD.G_MONTH_DAY);
    }

    public void testGDay() {
        checkWrongStringValueAndCreate(G_DAY_STR, XSD.G_DAY);
    }

    public void testGMonth() {
        checkWrongStringValueAndCreate(G_MONTH_STR_1, XSD.G_MONTH);
        checkWrongStringValueAndCreate(G_MONTH_STR_2, XSD.G_MONTH);
        checkWrongStringValueAndCreate(G_MONTH_STR_3, XSD.G_MONTH);
    }

    public void testDate() {
        DatatypeValue value = datatypeFactory.createValue(new Date(TEN_SECONDS));
        assertThat(value.getLexicalForm(), equalTo(TEN_SECOND_AFTER_UNIX_EPOCH_STRING));
    }

    public void testSQLDate() {
        DatatypeValue value = datatypeFactory.createValue(new java.sql.Date(TEN_SECONDS));
        assertThat(value.getLexicalForm(), equalTo(TEN_SECOND_AFTER_UNIX_EPOCH_STRING));
    }

    public void testAnyURI() throws Exception {
        DatatypeValue value = datatypeFactory.createValue(XSD.ANY_URI, URI_STR);
        assertThat(value.getLexicalForm(), equalTo(URI_STR));
        final URI uri = new URI(URI_STR);
        AnyURIValue anyUriValue1 = new AnyURIValue(uri);
        assertThat(value.equals(anyUriValue1), is(true));
    }

    public void testNonNegativeIntegerValue() {
        //test different correct format
        checkWrongStringValueAndCreate(ZERO, XSD.NON_NEGATIVE_INTEGER);
        checkWrongStringValueAndCreate(POSITIVE_ZERO, XSD.NON_NEGATIVE_INTEGER);
        checkWrongStringValueAndCreate(NEGATIVE_ZERO, XSD.NON_NEGATIVE_INTEGER);
        checkWrongStringValueAndCreate(ONE, XSD.NON_NEGATIVE_INTEGER);
        checkWrongStringValueAndCreate(POSITIVE_ONE, XSD.NON_NEGATIVE_INTEGER);
        //test wrong format
        checkStringIsWrongFormat(NEGATIVE_ONE, XSD.NON_NEGATIVE_INTEGER);
    }

    public void testNonPositiveIntegerValue() {
        //test different correct format
        checkWrongStringValueAndCreate(ZERO, XSD.NON_POSITIVE_INTEGER);
        checkWrongStringValueAndCreate(POSITIVE_ZERO, XSD.NON_POSITIVE_INTEGER);
        checkWrongStringValueAndCreate(NEGATIVE_ZERO, XSD.NON_POSITIVE_INTEGER);
        checkWrongStringValueAndCreate(NEGATIVE_ONE, XSD.NON_POSITIVE_INTEGER);
        checkWrongStringValueAndCreate("-2164654654", XSD.NON_POSITIVE_INTEGER);

        //test wrong format
        checkStringIsWrongFormat(POSITIVE_ONE, XSD.NON_POSITIVE_INTEGER);
        checkStringIsWrongFormat(ONE, XSD.NON_POSITIVE_INTEGER);
    }

    public void testQName() {
        checkCreatingValue("bar", XSD.Q_NAME);
        checkCreatingValue("foo:bar", XSD.Q_NAME);
        checkEmptyString(XSD.Q_NAME);
    }

    public void testDuration() {
        checkCreatingValue(DURATION_1, XSD.DURATION);
        checkCreatingValue(DURATION_2, XSD.DURATION);
        checkEmptyString(XSD.DURATION);
    }

    private void checkWrongStringValueAndCreate(String strToParse, URI uri) {
        checkStringIsWrongFormat(NORMAL_STRING, uri);
        checkEmptyString(uri);
        checkCreatingValue(strToParse, uri);
    }

    private void checkStringIsWrongFormat(String wrongFormatedString, URI correctURIFormat) {
        DatatypeValue value = datatypeFactory.createValue(correctURIFormat, wrongFormatedString);
        assertThat("Should fail for having wrong format", datatypeFactory.correctValueType(value, correctURIFormat),
            is(false));
    }

    private void checkEmptyString(URI uri) {
        checkStringIsWrongFormat("", uri);
    }

    private void checkCreatingValue(String strToParse, URI uri) {
        DatatypeValue value = datatypeFactory.createValue(uri, strToParse);
        assertThat("Should parse correctly with expected value, got: " + value.getClass(),
            datatypeFactory.correctValueType(value, uri), is(true));
        assertThat(value.getLexicalForm(), equalTo(strToParse));
    }
}
