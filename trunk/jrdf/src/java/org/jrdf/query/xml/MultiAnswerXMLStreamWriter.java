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

package org.jrdf.query.xml;

import static com.ctc.wstx.api.WstxInputProperties.PARSING_MODE_DOCUMENTS;
import static com.ctc.wstx.api.WstxInputProperties.P_INPUT_PARSING_MODE;

import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class MultiAnswerXMLStreamWriter extends AbstractXMLStreamWriter implements AnswerXMLWriter, Runnable {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    {
        INPUT_FACTORY.setProperty(P_INPUT_PARSING_MODE, PARSING_MODE_DOCUMENTS);
    }

    private InputStream inputStream;
    private XMLStreamReader parser;
    private boolean hasMore;
    private Set<String> variables;
    private boolean gotVariables;
    private int currentEvent;

    public MultiAnswerXMLStreamWriter(InputStream inputStream) throws XMLStreamException {
        this.inputStream = inputStream;
        variables = new HashSet<String>();
        createParser();
    }

    public void run() {
        try {
            if (parser == null) {
                createParser();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createParser() throws XMLStreamException {
        parser = INPUT_FACTORY.createXMLStreamReader(this.inputStream);
    }

    public boolean hasMoreResults() {
        try {
            hasMore = getToNextResult();
        } catch (XMLStreamException e) {
            hasMore = false;
        }
        return hasMore;
    }

    public void write(Writer writer) throws XMLStreamException {
        streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
        write();
    }

    public void writeResult() throws XMLStreamException {
        streamWriter.writeStartElement(RESULT);
        while (parser.hasNext()) {
            currentEvent = parser.getEventType();
            if (currentEvent == START_ELEMENT && BINDING.equals(parser.getLocalName())) {
                writeOneBinding();
            } else if (currentEvent == END_ELEMENT && RESULT.equals(parser.getLocalName())) {
                break;
            }
            parser.next();
        }
        streamWriter.writeEndElement();
        hasMore = getToNextResult();
    }

    public void writeVariables() throws XMLStreamException {
        if (!gotVariables) {
            gotVariables = getVariables();
        }
        streamWriter.writeStartElement(HEAD);
        for (String variable : variables) {
            streamWriter.writeStartElement(VARIABLE);
            streamWriter.writeAttribute(NAME, variable);
            streamWriter.writeEndElement();
        }
        streamWriter.writeEndElement();
    }

    public void setWriter(Writer writer) throws XMLStreamException, IOException {
        if (streamWriter != null) {
            streamWriter.close();
        }
        streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
    }

    public void write() throws XMLStreamException {
        writeStartDocument();
        writeVariables();
        writeAllResults();
        writeEndDocument();
    }

    public void addStream(InputStream stream) throws InterruptedException, XMLStreamException {
        throw new UnsupportedOperationException("Cannot add stream to this writer.");
    }

    protected void writeAllResults() throws XMLStreamException {
        writeStartResults();
        while (hasMoreResults()) {
            writeResult();
        }
        writeEndResults();
    }

    private void writeOneBinding() throws XMLStreamException {
        streamWriter.writeStartElement(BINDING);
        String variableName = parser.getAttributeValue(null, NAME);
        streamWriter.writeAttribute(NAME, variableName);
        currentEvent = parser.next();
        writeOneNode();
        streamWriter.writeEndElement();
    }

    private void writeOneNode() throws XMLStreamException {
        String tagName = parser.getLocalName();
        streamWriter.writeStartElement(tagName);
        if (LITERAL.equals(tagName)) {
            String datatype = parser.getAttributeValue(null, DATATYPE);
            if (datatype != null) {
                streamWriter.writeAttribute(DATATYPE, datatype);
            }
            String language = parser.getAttributeValue(null, XML_LANG);
            if (language != null) {
                streamWriter.writeAttribute(XML_LANG, language);
            }
        }
        final String text = parser.getElementText();
        streamWriter.writeCharacters(text);
        streamWriter.writeEndElement();
    }

    private boolean getVariables() throws XMLStreamException {
        while (parser.hasNext()) {
            currentEvent = parser.getEventType();
            if (currentEvent == START_ELEMENT && VARIABLE.equals(parser.getLocalName())) {
                addVariable();
            } else if (currentEvent == END_ELEMENT) {
                if (HEAD.equals(parser.getLocalName())) {
                    return true;
                }
            }
            currentEvent = parser.next();
        }
        return false;
    }

    private void addVariable() {
        assert currentEvent == START_ELEMENT;
        variables.add(parser.getAttributeValue(null, NAME));
    }

    private boolean getToNextResult() throws XMLStreamException {
        while (parser.hasNext()) {
            int eventType = parser.getEventType();
            if (eventType == START_ELEMENT) {
                final String tagName = parser.getLocalName();
                if (RESULT.equals(tagName)) {
                    return true;
                } else if (!gotVariables && VARIABLE.equals(tagName)) {
                    gotVariables = getVariables();
                }
            }
            parser.next();
        }
        return false;
    }

    public void close() throws XMLStreamException, IOException {
        try {
            if (parser != null) {
                parser.close();
            }
            if (streamWriter != null) {
                streamWriter.close();
            }
        } catch (XMLStreamException e) {
            ;
        }
    }
}
