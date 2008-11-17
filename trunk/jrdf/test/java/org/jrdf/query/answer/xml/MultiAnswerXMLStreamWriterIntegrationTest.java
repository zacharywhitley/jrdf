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

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringWriter;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class MultiAnswerXMLStreamWriterIntegrationTest extends AbstractAnswerXMLStreamWriterIntegrationTest {

    protected void setUp() throws Exception {
        super.setUp();
        xmlWriter = new MultiAnswerXMLStreamWriter(stream);
        xmlWriter.setWriter(writer);
    }

    protected void tearDown() throws Exception {
        xmlWriter.close();
    }

    public void testIncomingStream() throws IOException, XMLStreamException, InterruptedException {
        PipedOutputStream outputStream = new PipedOutputStream();
        PipedInputStream inputStream = new PipedInputStream(outputStream);
        long start = System.currentTimeMillis();
        RunnableStreamWriter sWriter = new RunnableStreamWriter(outputStream, 2);
        Thread thread = new Thread(sWriter);
        thread.start();

        xmlWriter = new MultiAnswerXMLStreamWriter(inputStream);
        writer = new StringWriter();
        xmlWriter.setWriter(writer);
        Thread wThread = new Thread((Runnable) xmlWriter);
        wThread.start();

        int count = 0;
        xmlWriter.writeStartResults();
        while (xmlWriter.hasMoreResults()) {
            count++;
            xmlWriter.writeResult();
        }
        xmlWriter.writeEndResults();
        assertEquals(4, count);
        inputStream.close();
        wThread.join();
        thread.join();
    }

    class RunnableStreamWriter implements Runnable {
        private InputStream stream;
        private OutputStream outputStream;
        private int loops;

        RunnableStreamWriter(OutputStream outuputStream, int count) {
            this.outputStream = outuputStream;
            this.loops = count;
        }

        void writeFromMultipleStreams() throws IOException, InterruptedException {
            for (int i = 0; i < loops; i++) {
                writeStream();
                if (i == loops - 1) {
                    synchronized (this) {
                        this.wait(1000);
                    }
                }
            }
            outputStream.close();
        }

        void writeStream() throws IOException {
            stream = getClass().getClassLoader().getResource("org/jrdf/query/answer/xml/data/output.xml").openStream();
            int read = stream.read();
            while (read != -1) {
                outputStream.write(read);
                read = stream.read();
            }
            stream.close();
        }

        public void run() {
            try {
                writeFromMultipleStreams();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void testMultipleInputs() throws XMLStreamException, IOException {
        String xml = readStreamIntoString();
        xml += xml;
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        writer = new StringWriter();
        xmlWriter = new MultiAnswerXMLStreamWriter(inputStream);
        xmlWriter.setWriter(writer);
        int count = 0;
        xmlWriter.writeStartResults();
        while (xmlWriter.hasMoreResults()) {
            count++;
            xmlWriter.writeResult();
        }
        xmlWriter.writeEndResults();
        assertEquals(4, count);
        xmlWriter.flush();
        inputStream.close();
    }

    private String readStreamIntoString() throws IOException {
        final InputStream stream1 = url.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream1));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        stream1.close();
        return sb.toString();
    }
}
