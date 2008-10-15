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

import static org.jrdf.query.answer.xml.DatatypeType.NONE;
import static org.jrdf.query.answer.xml.SparqlResultType.UNBOUND;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class NewMultiAnswerXMLStreamQueueWriter extends AbstractXMLStreamWriter implements AnswerXMLWriter {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private SparqlAnswerParser parser;
    private BlockingQueue<InputStream> streamQueue;
    private LinkedHashSet<String> variables;
    private boolean gotVariables;
    private InputStream currentStream;
    private boolean hasMore;

    public NewMultiAnswerXMLStreamQueueWriter(InputStream... streams) throws InterruptedException, XMLStreamException {
        hasMore = false;
        variables = new LinkedHashSet<String>();
        streamQueue = new LinkedBlockingQueue<InputStream>();
        for (InputStream stream : streams) {
            streamQueue.put(stream);
        }
        setupNextParser();
    }

    public void addStream(InputStream stream) throws InterruptedException, XMLStreamException {
        streamQueue.put(stream);
        if (!hasMore) {
            setupNextParser();
        }
    }

    public boolean hasMoreResults() {
        return hasMore;
    }

    public void writeVariables() throws XMLStreamException {
        streamWriter.writeStartElement(HEAD);
        for (String variable : variables) {
            streamWriter.writeStartElement(VARIABLE);
            streamWriter.writeAttribute(NAME, variable);
            streamWriter.writeEndElement();
        }
        streamWriter.writeEndElement();
    }

    public void writeResult() throws XMLStreamException {
        streamWriter.writeStartElement(RESULT);
        while (parser.hasMoreResults()) {
            TypeValue[] results = parser.getResults();
            Iterator<String> currentVariableIterator = variables.iterator();
            for (TypeValue result : results) {
                String currentVariable = currentVariableIterator.next();
                if (!result.getType().equals(UNBOUND)) {
                    writeBinding(result, currentVariable);
                }
            }
        }
        streamWriter.writeEndElement();
        hasMore = hasMore();
    }

    public void setWriter(Writer writer) throws XMLStreamException, IOException {
        if (streamWriter != null) {
            streamWriter.close();
        }
        streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
    }

    public void write(Writer writer) throws XMLStreamException, IOException {
        setWriter(writer);
        write();
    }

    public void close() throws XMLStreamException {
        if (streamWriter != null) {
            try {
                streamWriter.close();
            } catch (XMLStreamException e) {
                ;
            } finally {
                if (parser != null) {
                    parser.close();
                }
                streamQueue.clear();
            }
        }
    }

    private void setupNextParser() throws XMLStreamException {
        currentStream = streamQueue.poll();
        if (currentStream != null) {
            if (parser != null) {
                parser.close();
            }
            parser = new SparqlAnswerParserImpl(INPUT_FACTORY.createXMLStreamReader(currentStream));
            LinkedHashSet<String> newVariables = parser.getVariables();
            if (!gotVariables) {
                variables = newVariables;
                gotVariables = true;
            }
            if (!hasMore) {
                hasMore = hasMore();
            }
        } else {
            parser = null;
        }
    }

    private void writeBinding(TypeValue result, String currentVariable) throws XMLStreamException {
        streamWriter.writeStartElement(BINDING);
        streamWriter.writeAttribute(NAME, currentVariable);
        streamWriter.writeStartElement(result.getType().getXmlElementName());
        if (result.getSuffixType() != NONE) {
            if (result.getSuffixType().equals(DatatypeType.DATATYPE)) {
                streamWriter.writeAttribute(DATATYPE, result.getSuffix());
            } else if (result.getSuffixType().equals(DatatypeType.XML_LANG)) {
                streamWriter.writeAttribute(XML_NS, XML_LANG, result.getSuffix());
            }
        }
        streamWriter.writeCharacters(result.getValue());
        streamWriter.writeEndElement();
        streamWriter.writeEndElement();
    }

    protected void writeAllResults() throws XMLStreamException {
        writeStartResults();
        while (hasMore) {
            writeResult();
        }
        writeEndResults();
    }

    private boolean hasMore() {
        try {
            return parser != null && getToNextStreamResult();
        } catch (XMLStreamException e) {
            return false;
        }
    }

    private boolean getToNextStreamResult() throws XMLStreamException {
        boolean gotNext = false;
        while (parser != null && !gotNext) {
            gotNext = parser.hasMoreResults();
            if (!gotNext) {
                setupNextParser();
            }
        }
        return gotNext;
    }
}