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

import junit.framework.TestCase;
import static org.jrdf.query.answer.xml.parser.SparqlAnswerXmlParserImplUnitTest.EXPECTED_VARIABLES;
import static org.jrdf.query.answer.xml.parser.SparqlAnswerXmlParserImplUnitTest.ROW_1;
import static org.jrdf.query.answer.xml.parser.SparqlAnswerXmlParserImplUnitTest.ROW_2;
import static org.jrdf.query.answer.xml.parser.SparqlAnswerXmlParserImplUnitTest.checkRow;
import org.jrdf.query.answer.xml.parser.SparqlAnswerResultsXmlParser;
import org.jrdf.query.answer.xml.parser.SparqlAnswerResultsXmlParserImpl;

import static javax.xml.stream.XMLInputFactory.newInstance;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;
import java.util.HashSet;
import static java.util.Arrays.asList;

public class MultiAnswerXmlStreamQueueWriterIntegrationTest extends TestCase {
    private static final AnswerXmlStreamWriterTestUtil TEST_UTIL = new AnswerXmlStreamWriterTestUtil();
    private InputStream stream1, stream2;
    private XMLStreamReader streamReader;
    private AnswerXmlWriter xmlWriter;
    private Writer writer = new StringWriter();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        stream1 = TEST_UTIL.getData().openStream();
        stream2 = TEST_UTIL.getData().openStream();
        xmlWriter = new MultiAnswerXmlStreamQueueWriter(writer, stream1);
    }

    @Override
    protected void tearDown() throws Exception {
        stream1.close();
        stream2.close();
    }

    public void testVariables() throws Exception {
        final Set<String> set = new HashSet<String>(asList("x", "hpage", "name", "mbox", "age", "blurb", "friend"));
        TEST_UTIL.checkVariables(set, TEST_UTIL.getVariables(xmlWriter, writer));
    }

    public void testResults() throws Exception {
        TEST_UTIL.checkResult(TEST_UTIL.getData(), writer, xmlWriter);
    }

    public void testIncomingStreams() throws Exception {
        int count = 0;
        xmlWriter.writeStartResults();
        while (xmlWriter.hasMoreResults()) {
            count++;
            xmlWriter.writeResult();
        }
        assertEquals(2, count);
        checkTwoResults(writer);
        assertFalse(xmlWriter.hasMoreResults());
        ((MultiAnswerXmlStreamWriter) xmlWriter).addStream(stream2);
        assertTrue(xmlWriter.hasMoreResults());
        while (xmlWriter.hasMoreResults()) {
            count++;
            xmlWriter.writeResult();
        }
        xmlWriter.writeEndResults();
        assertEquals(4, count);
        checkFourResults(writer);
    }

    public void test2Streams() throws Exception {
        int count = 0;
        ((MultiAnswerXmlStreamWriter) xmlWriter).addStream(stream2);
        xmlWriter.writeStartResults();
        while (xmlWriter.hasMoreResults()) {
            count++;
            xmlWriter.writeResult();
        }
        assertEquals(4, count);
        xmlWriter.writeEndResults();
        checkFourResults(writer);
    }

    public void test2StreamsConstructor() throws Exception {
        stream1.close();
        stream1 = TEST_UTIL.getData().openStream();
        xmlWriter.close();
        writer = new StringWriter();
        xmlWriter = new MultiAnswerXmlStreamQueueWriter(writer, stream1, stream2);
        int count = 0;
        xmlWriter.writeStartResults();
        while (xmlWriter.hasMoreResults()) {
            count++;
            xmlWriter.writeResult();
        }
        assertEquals(4, count);
        xmlWriter.writeEndResults();
        checkFourResults(writer);
    }

    private void checkTwoResults(Writer writer) throws Exception {
        SparqlAnswerResultsXmlParser parser = getParser(writer);
        checkRow(parser.getResults(EXPECTED_VARIABLES), ROW_1);
        streamReader.next();
        checkRow(parser.getResults(EXPECTED_VARIABLES), ROW_2);
    }

    private void checkFourResults(Writer writer) throws Exception {
        SparqlAnswerResultsXmlParser parser = getParser(writer);
        checkRow(parser.getResults(EXPECTED_VARIABLES), ROW_1);
        streamReader.next();
        checkRow(parser.getResults(EXPECTED_VARIABLES), ROW_2);
        streamReader.next();
        checkRow(parser.getResults(EXPECTED_VARIABLES), ROW_1);
        streamReader.next();
        checkRow(parser.getResults(EXPECTED_VARIABLES), ROW_2);
    }

    private SparqlAnswerResultsXmlParser getParser(Writer writer) throws XMLStreamException {
        String result = writer.toString();
        InputStream stringStream = new ByteArrayInputStream(result.getBytes());
        streamReader = newInstance().createXMLStreamReader(stringStream);
        return new SparqlAnswerResultsXmlParserImpl(streamReader, new TypeValueFactoryImpl());
    }
}