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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */
public abstract class AbstractAnswerXMLStreamWriterUnitTest extends TestCase {
    private XMLInputFactory factory = XMLInputFactory.newInstance();
    {
        factory.setProperty(P_INPUT_PARSING_MODE, PARSING_MODE_FRAGMENT);
    }

    protected Writer writer;
    private XMLStreamReader reader;
    protected AnswerXMLWriter xmlWriter;
    protected URL url;
    protected InputStream stream;

    protected void setUp() throws Exception {
        super.setUp();
        writer = new StringWriter();
        url = getClass().getClassLoader().getResource("org/jrdf/query/answer/xml/data/output.xml");
        stream = url.openStream();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        stream.close();
        if (xmlWriter != null) {
            xmlWriter.close();
        }
    }

    public void testVariables() throws Exception {
        Set<String> set = new HashSet<String>();
        for (String v : new String[]{"x", "hpage", "name", "mbox", "age", "blurb", "friend"}) {
            set.add(v);
        }
        Set<String> varSet = getVariables();
        checkVariables(set, varSet);
    }

    protected void checkVariables(Set<String> set, Set<String> varSet) {
        assertEquals(set.size(), varSet.size());
        for (String v : varSet) {
            assertTrue(set.contains(v));
        }
    }

    protected Set<String> getVariables() throws XMLStreamException {
        Set<String> varSet = new HashSet<String>();
        xmlWriter.writeVariables();
        xmlWriter.flush();
        String xml = writer.toString();
        reader = factory.createXMLStreamReader(new StringReader(xml));
        while (reader.hasNext()) {
            int eventType = reader.getEventType();
            switch (eventType) {
                case START_ELEMENT:
                    final String tagName = reader.getLocalName();
                    assertEquals(HEAD, tagName);
                    addVar(varSet);
                    break;
                default:
                    break;
            }
            reader.next();
        }
        return varSet;
    }

    private void addVar(Set<String> variables) throws XMLStreamException {
        int event = reader.next();
        while (reader.hasNext()) {
            switch (event) {
                case START_ELEMENT:
                    final String tagName = reader.getLocalName();
                    assertEquals(VARIABLE, tagName);
                    variables.add(reader.getAttributeValue(null, NAME));
                    break;
                default:
                    break;
            }
            event = reader.next();
        }
    }

    public void testResult() throws Exception {
        final InputStream stream1 = url.openStream();
        int count = 0;
        String[][] pairs = new String[][]{
            new String[]{"x", "r1"},
            new String[]{"hpage", "http://work.example.org/alice/"},
            new String[]{"name", "Alice"},
            new String[]{"mbox", ""},
            new String[]{"friend", "r2"},
            new String[]{"blurb", "<p xmlns=\"http://www.w3.org/1999/xhtml\">My name is <b>alice</b></p>"}
        };
        Map<String, String> map1 = new HashMap<String, String>();
        for (String[] pair : pairs) {
            map1.put(pair[0], pair[1]);
        }
        pairs = new String[][]{
            new String[]{"x", "r2"},
            new String[]{"hpage", "http://work.example.org/bob/"},
            new String[]{"name", "Bob"},
            new String[]{"mbox", "mailto:bob@work.example.org"},
            new String[]{"age", "30"},
            new String[]{"friend", "r1"}
        };
        Map<String, String> map2 = new HashMap<String, String>();
        for (String[] pair : pairs) {
            map2.put(pair[0], pair[1]);
        }
        Map<String, String> actualMap = new HashMap<String, String>();
        xmlWriter.writeStartResults();
        while (xmlWriter.hasMoreResults()) {
            xmlWriter.writeResult();
            count++;
        }
        xmlWriter.writeEndResults();
        xmlWriter.flush();
        String xml = writer.toString();
        assertEquals(2, count);
        int pos = 0;
        reader = factory.createXMLStreamReader(new StringReader(xml));
    loop:
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case START_ELEMENT:
                    String tagName = reader.getLocalName();
                    if (RESULT.equals(tagName)) {
                        actualMap = checkBindings();
                    }
                    break;
                case END_ELEMENT:
                    tagName = reader.getLocalName();
                    if (RESULT.equals(tagName)) {
                        List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
                        maps.add(map1);
                        maps.add(map2);
                        checkMaps(actualMap, pos, maps);
                        pos++;
                    } else if (RESULTS.equals(tagName)) {
                        break loop;
                    }
                    break;
                default:
                    break;
            }
        }
        stream1.close();
    }

    private void checkMaps(Map<String, String> actualMap, int pos, List<Map<String, String>> maps) {
        Map<String, String> map = maps.get(pos);
        assertEquals(map.size(), actualMap.size());
        for (String key : map.keySet()) {
            assertEquals(map.get(key), actualMap.get(key));
        }
    }

    private Map<String, String> checkBindings() throws XMLStreamException {
        Map<String, String> map1 = new HashMap<String, String>();
    loop:
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case START_ELEMENT:
                    String tagName = reader.getLocalName();
                    assertEquals(BINDING, tagName);
                    map1 = checkOneBinding(map1);
                    break;
                case END_ELEMENT:
                    tagName = reader.getLocalName();
                    if (RESULT.equals(tagName)) {
                        break loop;
                    }
                    break;
                default:
                    break;
            }
        }
        return map1;
    }

    private Map<String, String> checkOneBinding(Map<String, String> map) throws XMLStreamException {
        final String varName = reader.getAttributeValue(null, NAME);
    loop:
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case START_ELEMENT:
                    final String varValue = reader.getElementText();
                    map.put(varName, varValue);
                    break;
                case END_ELEMENT:
                    final String tagName = reader.getLocalName();
                    if (BINDING.equals(tagName)) {
                        break loop;
                    }
                    break;
                default:
                    break;
            }
        }
        return map;
    }
}
