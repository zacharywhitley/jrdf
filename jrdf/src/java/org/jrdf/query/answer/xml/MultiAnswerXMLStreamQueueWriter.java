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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class MultiAnswerXMLStreamQueueWriter extends AbstractXMLStreamWriter implements AnswerXMLWriter {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private XMLEventReader parser;
    private BlockingQueue<InputStream> streamQueue;
    private Set<String> variables;
    private boolean gotVariables;
    private InputStream currentStream;
    private boolean hasMore;

    public MultiAnswerXMLStreamQueueWriter(InputStream... streams) throws InterruptedException, XMLStreamException {
        hasMore = false;
        variables = new HashSet<String>();
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
        if (!gotVariables) {
            getVariables();
        }
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
        while (parser.hasNext()) {
            final XMLEvent event = parser.nextEvent();
            if (event.isStartElement()) {
                final String tagName = getElementTagName(event);
                if (BINDING.equals(tagName)) {
                    writeOneBinding(event);
                }
            } else if (event.isEndElement()) {
                final String tagName = getElementTagName(event);
                if (RESULT.equals(tagName)) {
                    break;
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
            }
        }
        if (parser != null) {
            parser.close();
        }
        streamQueue.clear();
    }

    private void setupNextParser() throws XMLStreamException {
        currentStream = streamQueue.poll();
        if (currentStream != null) {
            getNextStreamParser();
            gotVariables = getVariables();
            if (!hasMore) {
                hasMore = hasMore();
            }
        } else {
            parser = null;
        }
    }

    private boolean hasMore() {
        try {
            return parser != null && getToNextStreamResult();
        } catch (XMLStreamException e) {
            return false;
        }
    }

    private void getNextStreamParser() throws XMLStreamException {
        if (parser != null) {
            parser.close();
        }
        parser = INPUT_FACTORY.createXMLEventReader(currentStream);
    }

    private boolean getVariables() throws XMLStreamException {
        boolean gotVar = false;
        while (parser != null && parser.hasNext()) {
            final XMLEvent event = parser.nextEvent();
            if (event.isStartElement()) {
                final String tagName = getElementTagName(event);
                if (VARIABLE.equals(tagName)) {
                    Attribute attr = ((StartElement) event).getAttributeByName(new QName(NAME));
                    final String name = attr.getValue();
                    variables.add(name);
                    gotVar = true;
                }
            } else if (event.isEndElement()) {
                final String tagName = getElementTagName(event);
                if (!VARIABLE.equals(tagName)) {
                    break;
                }
            }
        }
        return gotVar;
    }

    private String getElementTagName(XMLEvent e) {
        assert e.isStartElement() || e.isEndElement();
        if (e.isStartElement()) {
            return ((StartElement) e).getName().getLocalPart();
        } else {
            return ((EndElement) e).getName().getLocalPart();
        }
    }

    private boolean getToNextResult() throws XMLStreamException {
        while (parser.hasNext()) {
            final XMLEvent event = parser.nextEvent();
            if (event.isStartElement()) {
                final String tagName = getElementTagName(event);
                if (RESULT.equals(tagName)) {
                    return true;
                } else if (!gotVariables && HEAD.equals(tagName)) {
                    gotVariables = getVariables();
                }
            } else if (event.isEndElement()) {
                final String tagName = getElementTagName(event);
                if (RESULTS.equals(tagName) || SPARQL.equals(tagName)) {
                    break;
                }
            }
        }
        return false;
    }

    private boolean getToNextStreamResult() throws XMLStreamException {
        boolean gotNext = false;
        while (parser != null && !gotNext) {
            gotNext = getToNextResult();
            if (!gotNext) {
                setupNextParser();
            }
        }
        /*while ((parser != null) && !(gotNext = getToNextResult())) {
            setupNextParser();
        }*/
        return gotNext;
    }

    protected void writeAllResults() throws XMLStreamException {
        writeStartResults();
        while (hasMore) {
            writeResult();
        }
        writeEndResults();
    }

    private void writeOneBinding(XMLEvent event) throws XMLStreamException {
        streamWriter.writeStartElement(BINDING);
        Attribute nameAttr = ((StartElement) event).getAttributeByName(new QName(NAME));
        String varName = nameAttr.getValue();
        streamWriter.writeAttribute(NAME, varName);
        writeCurrentNode();
        streamWriter.writeEndElement();

    }

    private void writeCurrentNode() throws XMLStreamException {
        while (parser.hasNext()) {
            final XMLEvent event = parser.nextEvent();
            if (event.isStartElement()) {
                String tagName = getElementTagName(event);
                streamWriter.writeStartElement(tagName);
                if (LITERAL.equals(tagName)) {
                    writeLiteralAttributes((StartElement) event);
                }
            } else if (event.isCharacters()) {
                streamWriter.writeCharacters(((Characters) event).getData());
            } else if (event.isEndElement()) {
                if (BINDING.equals(this.getElementTagName(event))) {
                    break;
                } else {
                    streamWriter.writeEndElement();
                }
            }
        }
    }

    private void writeLiteralAttributes(StartElement event) throws XMLStreamException {
        final Attribute datatype = event.getAttributeByName(new QName(DATATYPE));
        if (datatype != null) {
            streamWriter.writeAttribute(DATATYPE, datatype.getValue());
        }
        final Attribute lang = event.getAttributeByName(new QName(XML_LANG));
        if (lang != null) {
            streamWriter.writeAttribute(XML_LANG, lang.getValue());
        }
    }
}
