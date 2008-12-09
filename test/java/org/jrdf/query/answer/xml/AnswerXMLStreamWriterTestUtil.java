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
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Resource;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.HEAD;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.NAME;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.VARIABLE;

import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import static java.net.URI.create;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AnswerXMLStreamWriterTestUtil {
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

    public AnswerXMLStreamWriterTestUtil() {
    }

    public URL getData() {
        return getClass().getClassLoader().getResource("org/jrdf/query/answer/xml/data/output.xml");
    }

    public void createTestGraph(Graph newGraph) throws Exception {
        final GraphElementFactory elementFactory = newGraph.getElementFactory();
        Resource b1 = elementFactory.createResource(create("urn:s1"));
        Resource b2 = elementFactory.createResource(create("urn:s2"));
        Resource b3 = elementFactory.createResource(create("urn:s3"));
        b1.addValue(create("urn:p1"), "l1");
        b2.addValue(create("urn:p2"), "l2");
        b3.addValue(create("urn:p3"), "l3");
    }

    public Set<String> getVariables(final AnswerXMLWriter xmlWriter, final Writer writer) throws Exception {
        xmlWriter.writeHead();
        xmlWriter.flush();
        final Set<String> varSet = new HashSet<String>();
        final String xml = writer.toString();
        final XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
        while (reader.hasNext()) {
            if (reader.getEventType() == START_ELEMENT) {
                assertEquals(HEAD, reader.getLocalName());
                addVar(varSet, reader);
            }
            reader.next();
        }
        return varSet;
    }

    private void addVar(final Set<String> variables, final XMLStreamReader reader) throws Exception {
        reader.next();
        while (reader.hasNext()) {
            if (reader.getEventType() == START_ELEMENT) {
                assertEquals(VARIABLE, reader.getLocalName());
                variables.add(reader.getAttributeValue(null, NAME));
            }
            reader.next();
        }
    }

    public void checkVariables(Set<String> set, Set<String> varSet) {
        assertEquals(set.size(), varSet.size());
        for (final String v : varSet) {
            assertTrue(set.contains(v));
        }
    }

    public void checkResult(final URL url, final Writer writer, final AnswerXMLWriter xmlWriter) throws Exception {
        final InputStream resultStream = url.openStream();
        try {
            final List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
            maps.add(RESULT1);
            maps.add(RESULT2);
            AnswerXMLTestUtil.checkNumberOfResults(maps.size(), xmlWriter);
            AnswerXMLTestUtil.checkContentsOfResults(maps, writer);
        } finally {
            resultStream.close();
        }
    }

    public String readStreamIntoString(final InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        stream.close();
        return sb.toString();
    }

    private static Map<String, String> addToMap(final String[][] result) {
        final Map<String, String> map = new HashMap<String, String>();
        for (final String[] pair : result) {
            map.put(pair[0], pair[1]);
        }
        return map;
    }
}
