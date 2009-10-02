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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import org.jrdf.query.answer.TypeValueFactoryImpl;
import static org.jrdf.query.answer.xml.parser.SparqlXmlParserImplUnitTest.EXPECTED_VARIABLES;
import static org.jrdf.query.answer.xml.parser.SparqlXmlParserImplUnitTest.ROW_1;
import static org.jrdf.query.answer.xml.parser.SparqlXmlParserImplUnitTest.ROW_2;
import org.jrdf.query.answer.xml.parser.SparqlXmlResultsParser;
import org.jrdf.query.answer.xml.parser.SparqlXmlResultsParserImpl;
import org.jrdf.query.client.XmlSparqlAnswerHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static javax.xml.stream.XMLInputFactory.newInstance;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

public class SparqlMultiXmlStreamQueueWriterIntegrationTest {
    private static final SparqlXmlStreamWriterTestUtil TEST_UTIL = new SparqlXmlStreamWriterTestUtil();
    private InputStream stream1, stream2;
    private XMLStreamReader streamReader;
    private SparqlXmlWriter xmlWriter;
    private Writer writer = new StringWriter();

    @Before
    public void setUp() throws Exception {
        stream1 = TEST_UTIL.getData().openStream();
        stream2 = TEST_UTIL.getData().openStream();
        xmlWriter = new SparqlMultiXmlStreamQueueWriter(new XmlSparqlAnswerHandler(), writer, stream1);
    }

    @After
    public void tearDown() throws Exception {
        stream1.close();
        stream2.close();
    }

    @Test
    public void testVariables() throws Exception {
        final Set<String> set = new HashSet<String>(asList("x", "hpage", "name", "mbox", "age", "blurb", "friend"));
        TEST_UTIL.checkVariables(set, TEST_UTIL.getVariables(xmlWriter, writer));
    }

    @Test
    public void testResults() throws Exception {
        TEST_UTIL.checkResult(TEST_UTIL.getData(), writer, xmlWriter);
    }

    @Test
    public void testIncomingStreams() throws Exception {
        int count = 0;
        xmlWriter.writeStartResults();
        while (xmlWriter.hasMoreResults()) {
            count++;
            xmlWriter.writeResult();
        }
        assertThat(count, is(2));
        checkTwoResults();
        assertThat(xmlWriter.hasMoreResults(), is(false));
        ((SparqlMultiXmlStreamWriter) xmlWriter).addStream(stream2);
        assertThat(xmlWriter.hasMoreResults(), is(true));
        while (xmlWriter.hasMoreResults()) {
            count++;
            xmlWriter.writeResult();
        }
        xmlWriter.writeEndResults();
        assertThat(count, is(4));
        checkFourResults();
    }

    @Test
    public void test2Streams() throws Exception {
        int count = 0;
        ((SparqlMultiXmlStreamWriter) xmlWriter).addStream(stream2);
        xmlWriter.writeStartResults();
        while (xmlWriter.hasMoreResults()) {
            count++;
            xmlWriter.writeResult();
        }
        assertThat(count, is(4));
        xmlWriter.writeEndResults();
        checkFourResults();
    }

    @Test
    public void test2StreamsConstructor() throws Exception {
        stream1.close();
        stream1 = TEST_UTIL.getData().openStream();
        xmlWriter.close();
        writer = new StringWriter();
        xmlWriter = new SparqlMultiXmlStreamQueueWriter(new XmlSparqlAnswerHandler(), writer, stream1, stream2);
        int count = 0;
        xmlWriter.writeStartResults();
        while (xmlWriter.hasMoreResults()) {
            count++;
            xmlWriter.writeResult();
        }
        assertThat(count, is(4));
        xmlWriter.writeEndResults();
        checkFourResults();
    }

    private void checkTwoResults() throws Exception {
        SparqlXmlResultsParser parser = getParser();
        assertThat(parser.getResults(EXPECTED_VARIABLES), arrayContaining(ROW_1));
        streamReader.next();
        assertThat(parser.getResults(EXPECTED_VARIABLES), arrayContaining(ROW_2));
    }

    private void checkFourResults() throws Exception {
        SparqlXmlResultsParser parser = getParser();
        assertThat(parser.getResults(EXPECTED_VARIABLES), arrayContaining(ROW_1));
        streamReader.next();
        assertThat(parser.getResults(EXPECTED_VARIABLES), arrayContaining(ROW_2));
        streamReader.next();
        assertThat(parser.getResults(EXPECTED_VARIABLES), arrayContaining(ROW_1));
        streamReader.next();
        assertThat(parser.getResults(EXPECTED_VARIABLES), arrayContaining(ROW_2));
    }

    private SparqlXmlResultsParser getParser() throws XMLStreamException {
        String result = writer.toString();
        InputStream stringStream = new ByteArrayInputStream(result.getBytes());
        streamReader = newInstance().createXMLStreamReader(stringStream);
        return new SparqlXmlResultsParserImpl(streamReader, new TypeValueFactoryImpl());
    }
}