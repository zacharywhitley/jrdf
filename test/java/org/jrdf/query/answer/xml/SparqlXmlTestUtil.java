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

package org.jrdf.query.answer.xml;

import static com.ctc.wstx.api.WstxInputProperties.PARSING_MODE_FRAGMENT;
import static com.ctc.wstx.api.WstxInputProperties.P_INPUT_PARSING_MODE;
import static junit.framework.Assert.assertEquals;
import org.jrdf.query.answer.SparqlProtocol;

import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SparqlXmlTestUtil {
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();
    {
        FACTORY.setProperty(P_INPUT_PARSING_MODE, PARSING_MODE_FRAGMENT);
    }

    private SparqlXmlTestUtil() {
    }

    public static void checkNumberOfResults(final int expectedNumber, final SparqlXmlWriter xmlWriter)
        throws Exception {
        int count = 0;
        xmlWriter.writeStartResults();
        while (xmlWriter.hasMoreResults()) {
            xmlWriter.writeResult();
            count++;
        }
        xmlWriter.writeEndResults();
        xmlWriter.flush();
        assertEquals(expectedNumber, count);
    }

    public static void checkContentsOfResults(final List<Map<String, String>> expectedResults,
        final Writer actualResults) throws Exception {
        final XMLStreamReader reader = FACTORY.createXMLStreamReader(new StringReader(actualResults.toString()));
        Map<String, String> actualResultsMap = new HashMap<String, String>();
        int pos = 0;
        while (reader.hasNext() && !endOfResults(reader)) {
            final int eventType = reader.next();
            if (eventType == START_ELEMENT) {
                actualResultsMap = createActualResultsMap(reader);
            } else if (eventType == END_ELEMENT) {
                if (SparqlProtocol.RESULT.equals(reader.getLocalName())) {
                    checkMaps(expectedResults.get(pos), actualResultsMap);
                    pos++;
                }
            }
        }
    }

    private static Map<String, String> createActualResultsMap(final XMLStreamReader reader) throws Exception {
        Map<String, String> actualResultsMap = new HashMap<String, String>();
        if (SparqlProtocol.RESULT.equals(reader.getLocalName())) {
            actualResultsMap = createBindings(reader);
        }
        return actualResultsMap;
    }

    private static Map<String, String> createBindings(final XMLStreamReader reader) throws Exception {
        final Map<String, String> newMap = new HashMap<String, String>();
        while (reader.hasNext() && !endOfResult(reader)) {
            if (reader.next() == START_ELEMENT) {
                assertEquals(SparqlProtocol.BINDING, reader.getLocalName());
                addBinding(newMap, reader);
            }
        }
        return newMap;
    }

    private static void addBinding(final Map<String, String> map, final XMLStreamReader reader) throws Exception {
        while (reader.hasNext() && !endOfBinding(reader)) {
            if (reader.next() == START_ELEMENT) {
                map.put(reader.getAttributeValue(null, SparqlProtocol.NAME), reader.getElementText());
            }
        }
    }

    private static void checkMaps(final Map<String, String> expectedValues, final Map<String, String> actualValues) {
        assertEquals(expectedValues.size(), actualValues.size());
        for (final String key : expectedValues.keySet()) {
            assertEquals(expectedValues.get(key), actualValues.get(key));
        }
    }

    private static boolean endOfResults(final XMLStreamReader reader) {
        return (reader.getEventType() == END_ELEMENT) && (SparqlProtocol.RESULTS.equals(reader.getLocalName()));
    }

    private static boolean endOfResult(final XMLStreamReader reader) {
        return (reader.getEventType() == END_ELEMENT) && (SparqlProtocol.RESULT.equals(reader.getLocalName()));
    }

    private static boolean endOfBinding(final XMLStreamReader reader) {
        return (reader.getEventType() == END_ELEMENT) && (SparqlProtocol.BINDING.equals(reader.getLocalName()));
    }
}