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

package org.jrdf.writer.rdfxml;

import org.jrdf.graph.Literal;
import static org.jrdf.util.param.StringUtil.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringReader;

/**
 * Class description goes here.
 */
public class XmlLiteralWriterImpl implements XmlLiteralWriter {
    private final XMLStreamWriter xmlStreamWriter;

    public XmlLiteralWriterImpl(final XMLStreamWriter xmlStreamWriter) {
        this.xmlStreamWriter = xmlStreamWriter;
    }

    public void write(Literal literal) throws XMLStreamException {
        xmlStreamWriter.writeAttribute("rdf:parseType", "Literal");
        final XMLInputFactory eventFactoryIn = XMLInputFactory.newInstance();
        final StringReader reader = new StringReader(literal.getLexicalForm());
        final XMLStreamReader streamReader = eventFactoryIn.createXMLStreamReader(reader);
        while (streamReader.hasNext()) {
            streamReader.next();
            translateAndWrite(streamReader, xmlStreamWriter);
        }
    }

    private void translateAndWrite(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
            handleStartElementAndAttributes(reader, writer);
        } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
            handleEndElement(writer);
        } else if (reader.getEventType() == XMLStreamConstants.PROCESSING_INSTRUCTION) {
            handleProcessingInstruction(reader, writer);
        } else if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
            handleCharacters(reader, writer);
        } else if (reader.getEventType() == XMLStreamConstants.COMMENT) {
            handleComment(reader, writer);
        }
    }

    private void handleStartElementAndAttributes(XMLStreamReader reader, XMLStreamWriter writer)
        throws XMLStreamException {
        handleStartElement(reader, writer);
        handleAttributes(reader, writer);
    }

    private void handleStartElement(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        String prefix = toEmpty(reader.getPrefix());
        for (int i = 0; i < reader.getNamespaceCount(); i++) {
            writer.writeNamespace(reader.getNamespacePrefix(i), toEmpty(reader.getNamespaceURI(i)));
        }
        writer.writeStartElement(prefix, reader.getLocalName(), toEmpty(reader.getNamespaceURI()));
    }

    private void handleAttributes(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (reader.getAttributeNamespace(i) != null) {
                if (reader.getAttributePrefix(i) != null) {
                    writer.writeAttribute(reader.getAttributePrefix(i), reader.getAttributeNamespace(i),
                            reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                } else {
                    writer.writeAttribute(reader.getAttributeNamespace(i), reader.getAttributeLocalName(i),
                            reader.getAttributeValue(i));
                }
            } else {
                writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            }
        }
    }

    private void handleEndElement(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }

    private void handleProcessingInstruction(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeProcessingInstruction(reader.getPITarget(), reader.getPIData());
    }

    private void handleCharacters(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        if (!reader.getText().equals("\n")) {
            writer.writeCharacters(reader.getText());
        }
    }

    private void handleComment(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeComment(reader.getText());
    }
}
