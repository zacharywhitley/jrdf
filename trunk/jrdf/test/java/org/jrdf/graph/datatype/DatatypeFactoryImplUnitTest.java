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
import java.util.Date;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.util.Calendar;

public class DatatypeFactoryImplUnitTest extends TestCase {
    private static final int TEN_SECONDS = 10000;
    private static final Date TEN_SECONDS_AFTER_UNIX_EPOCH = new Date(TEN_SECONDS);
    private static final String TEN_SECOND_AFTER_UNIX_EPOCH_STRING = "1970-01-01T10:00:10.000+10:00";
    private static final String HAPPY_NEW_YEAR = "2000-01-01T00:00:00.000+10:00";
    private static final String JAN_21ST = "01-21";
    private DatatypeFactory datatypeFactory = DatatypeFactoryImpl.getInstance();

    public void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+10:00"));
    }

    public void testCreatingDateTimeValue() {
        Value value = datatypeFactory.createValue(TEN_SECONDS_AFTER_UNIX_EPOCH);
        assertEquals(TEN_SECOND_AFTER_UNIX_EPOCH_STRING, value.getLexicalForm());
        URI uri = datatypeFactory.getObjectDatatypeURI(TEN_SECONDS_AFTER_UNIX_EPOCH);
        assertEquals(XSD.DATE_TIME, uri);
    }

    public void testCreatingCalendar() {
        Calendar year2000 = new GregorianCalendar(2000, 0, 1);
        Value value = datatypeFactory.createValue(year2000);
        assertEquals(HAPPY_NEW_YEAR, value.getLexicalForm());
        URI uri = datatypeFactory.getObjectDatatypeURI(year2000);
        assertEquals(XSD.DATE_TIME, uri);
    }

    public void testStringToGMonthDay() {
        Value value = datatypeFactory.createValue(JAN_21ST, XSD.G_MONTH_DAY);
        assertEquals(JAN_21ST, value.getLexicalForm());
    }
}
