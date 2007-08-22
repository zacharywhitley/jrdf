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

package org.jrdf.graph.datatype;

import junit.framework.TestCase;
import org.jrdf.vocabulary.XSD;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DatatypeFactoryImplUnitTest extends TestCase {
    private static final int TEN_SECONDS = 10000;
    private static final Date TEN_SECONDS_AFTER_UNIX_EPOCH = new Date(TEN_SECONDS);
    private static final String STR = "This is a normal string";
    private static final String TEN_SECOND_AFTER_UNIX_EPOCH_STRING = "1970-01-01T10:00:10.000+10:00";
    private static final String HAPPY_NEW_YEAR = "2000-01-01T00:00:00.000+10:00";
    private static final String COFFEE_TIME = "09:30:00.000+10:00";
    private static final String G_MONTH_DAY_STR = "--01-21";
    private static final String G_YEAR_MONTH_STR = "1999-06";
    private static final String G_YEAR_STR = "2007";
    private static final String G_DAY_STR = "---28";
    private static final String G_MONTH_STR = "--12";
    private static final String URI_STR = "http://foo/bar#Litral";
    private DatatypeFactory datatypeFactory = DatatypeFactoryImpl.getInstance();

    public void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+10:00"));
    }

    public void testCreatingCalendar() {
        Calendar year2000 = new GregorianCalendar(2000, 0, 1);
        Value value = datatypeFactory.createValue(year2000);
        assertEquals(HAPPY_NEW_YEAR, value.getLexicalForm());
        URI uri = datatypeFactory.getObjectDatatypeURI(year2000);
        assertEquals(XSD.DATE_TIME, uri);
    }

    public void testCreatingDateTimeValue() {
        Value value = datatypeFactory.createValue(TEN_SECONDS_AFTER_UNIX_EPOCH);
        assertEquals(TEN_SECOND_AFTER_UNIX_EPOCH_STRING, value.getLexicalForm());
        URI uri = datatypeFactory.getObjectDatatypeURI(TEN_SECONDS_AFTER_UNIX_EPOCH);
        assertEquals(XSD.DATE_TIME, uri);
    }

    public void testStringToTime() {
        Value value = datatypeFactory.createValue(COFFEE_TIME, XSD.TIME);
        assertEquals(COFFEE_TIME, value.getLexicalForm());
    }

    public void testStringToGYearMonth() {
        testCreatingValue(G_YEAR_MONTH_STR, XSD.G_YEAR_MONTH);
    }

    public void testStringToGYear() {
        testCreatingValue(G_YEAR_STR, XSD.G_YEAR);
    }

    public void testStringToGMonthDay() {
        testCreatingValue(G_MONTH_DAY_STR, XSD.G_MONTH_DAY);
    }

    public void testGDay() {
        testCreatingValue(G_DAY_STR, XSD.G_DAY);
    }

    public void testGMonth() {
        testCreatingValue(G_MONTH_STR, XSD.G_MONTH);
    }

    public void testAnyURI() throws Exception {
        Value value = datatypeFactory.createValue(URI_STR, XSD.ANY_URI);
        assertEquals(URI_STR, value.getLexicalForm());
        final URI uri = new URI(URI_STR);
        AnyURIValue anyUriValue1 = new AnyURIValue(uri);
        assertTrue(value.equals(anyUriValue1));
    }

    private void testCreatingValue(String strToParse, URI uri) {
        Value value = datatypeFactory.createValue(STR, uri);
        assertFalse("Should fall back to XSD String but isn't what we expected to be created",
            datatypeFactory.correctValueType(value, uri));
        value = datatypeFactory.createValue(strToParse, uri);
        assertTrue("Should parse correctly with expected value", datatypeFactory.correctValueType(value, uri));
        assertEquals(strToParse, value.getLexicalForm());
    }
}
