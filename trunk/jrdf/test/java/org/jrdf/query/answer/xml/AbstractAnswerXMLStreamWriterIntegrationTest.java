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

package org.jrdf.query.answer.xml;

import static com.ctc.wstx.api.WstxInputProperties.PARSING_MODE_FRAGMENT;
import static com.ctc.wstx.api.WstxInputProperties.P_INPUT_PARSING_MODE;
import junit.framework.TestCase;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.BINDING;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.HEAD;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.NAME;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.RESULT;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.RESULTS;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.VARIABLE;

import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractAnswerXMLStreamWriterIntegrationTest extends TestCase {
    private XMLInputFactory factory = XMLInputFactory.newInstance();
    {
        factory.setProperty(P_INPUT_PARSING_MODE, PARSING_MODE_FRAGMENT);
    }
    private static final String[][] VALUES_1 = new String[][]{
        new String[]{"x", "r1"},
        new String[]{"hpage", "http://work.example.org/alice/"},
        new String[]{"name", "Alice"},
        new String[]{"mbox", ""},
        new String[]{"friend", "r2"},
        new String[]{"blurb", "<p xmlns=\"http://www.w3.org/1999/xhtml\">My name is <b>alice</b></p>"}
    };
    private static final String[][] VALUES_2 = new String[][]{
        new String[]{"x", "r2"},
        new String[]{"hpage", "http://work.example.org/bob/"},
        new String[]{"name", "Bob"},
        new String[]{"mbox", "mailto:bob@work.example.org"},
        new String[]{"age", "30"},
        new String[]{"friend", "r1"}
    };
    private static final Map<String, String> RESULT1 = addToMap(VALUES_1);
    private static final Map<String, String> RESULT2 = addToMap(VALUES_2);
    private XMLStreamReader reader;
    protected Writer writer;
    protected AnswerXMLWriter xmlWriter;
    protected URL url;
    protected InputStream stream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        writer = new StringWriter();
        url = getClass().getClassLoader().getResource("org/jrdf/query/answer/xml/data/output.xml");
        stream = url.openStream();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        stream.close();
        if (xmlWriter != null) {
            xmlWriter.close();
        }
    }

    public void testVariables() throws Exception {
        final Set<String> set = new HashSet<String>(asList("x", "hpage", "name", "mbox", "age", "blurb", "friend"));
        checkVariables(set, getVariables());
    }

    protected void checkVariables(Set<String> set, Set<String> varSet) {
        assertEquals(set.size(), varSet.size());
        for (final String v : varSet) {
            assertTrue(set.contains(v));
        }
    }

    protected Set<String> getVariables() throws XMLStreamException {
        Set<String> varSet = new HashSet<String>();
        xmlWriter.writeHead();
        xmlWriter.flush();
        final String xml = writer.toString();
        reader = factory.createXMLStreamReader(new StringReader(xml));
        while (reader.hasNext()) {
            if (reader.getEventType() == START_ELEMENT) {
                assertEquals(HEAD, reader.getLocalName());
                addVar(varSet);
            }
            reader.next();
        }
        return varSet;
    }

    private void addVar(final Set<String> variables) throws XMLStreamException {
        reader.next();
        while (reader.hasNext()) {
            if (reader.getEventType() == START_ELEMENT) {
                assertEquals(VARIABLE, reader.getLocalName());
                variables.add(reader.getAttributeValue(null, NAME));
            }
            reader.next();
        }
    }

    public void testResult() throws Exception {
        final InputStream resultStream = url.openStream();
        try {
            final List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
            maps.add(RESULT1);
            maps.add(RESULT2);
            checkNumberOfResults(maps.size());
            checkContentsOfResults(maps);
        } finally {
            resultStream.close();
        }
    }

    private void checkNumberOfResults(final int expectedNumber) throws XMLStreamException {
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

    private void checkContentsOfResults(List<Map<String, String>> maps) throws XMLStreamException {
        reader = factory.createXMLStreamReader(new StringReader(writer.toString()));
        Map<String, String> actualResultsMap = new HashMap<String, String>();
        int pos = 0;
        while (reader.hasNext() && !endOfResults()) {
            final int eventType = reader.next();
            if (eventType == START_ELEMENT) {
                actualResultsMap = createActualResultsMap();
            } else if (eventType == END_ELEMENT) {
                if (RESULT.equals(reader.getLocalName())) {
                    checkMaps(maps.get(pos), actualResultsMap);
                    pos++;
                }
            }
        }
    }

    private Map<String, String> createActualResultsMap() throws XMLStreamException {
        Map<String, String> actualResultsMap = new HashMap<String, String>();
        if (RESULT.equals(reader.getLocalName())) {
            actualResultsMap = createBindings();
        }
        return actualResultsMap;
    }

    private Map<String, String> createBindings() throws XMLStreamException {
        final Map<String, String> newMap = new HashMap<String, String>();
        while (reader.hasNext() && !endOfResult()) {
            if (reader.next() == START_ELEMENT) {
                assertEquals(BINDING, reader.getLocalName());
                addBinding(newMap);
            }
        }
        return newMap;
    }

    private void addBinding(final Map<String, String> map) throws XMLStreamException {
        while (reader.hasNext() && !endOfBinding()) {
            if (reader.next() == START_ELEMENT) {
                map.put(reader.getAttributeValue(null, NAME), reader.getElementText());
            }
        }
    }

    private void checkMaps(final Map<String, String> expectedValues, final Map<String, String> actualValues) {
        assertEquals(expectedValues.size(), actualValues.size());
        for (final String key : expectedValues.keySet()) {
            assertEquals(expectedValues.get(key), actualValues.get(key));
        }
    }

    private boolean endOfResults() {
        return (reader.getEventType() == END_ELEMENT) && (RESULTS.equals(reader.getLocalName()));
    }

    private boolean endOfResult() {
        return (reader.getEventType() == END_ELEMENT) && (RESULT.equals(reader.getLocalName()));
    }

    private boolean endOfBinding() {
        return (reader.getEventType() == END_ELEMENT) && (BINDING.equals(reader.getLocalName()));
    }

    private static Map<String, String> addToMap(final String[][] result) {
        final Map<String, String> map = new HashMap<String, String>();
        for (final String[] pair : result) {
            map.put(pair[0], pair[1]);
        }
        return map;
    }
}
