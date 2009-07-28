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

import static org.jrdf.query.answer.SparqlProtocol.BINDING;
import static org.jrdf.query.answer.SparqlProtocol.DATATYPE;
import static org.jrdf.query.answer.SparqlProtocol.HEAD;
import static org.jrdf.query.answer.SparqlProtocol.NAME;
import static org.jrdf.query.answer.SparqlProtocol.RESULT;
import static org.jrdf.query.answer.SparqlProtocol.RESULTS;
import static org.jrdf.query.answer.SparqlProtocol.SPARQL;
import static org.jrdf.query.answer.SparqlProtocol.SPARQL_NS;
import static org.jrdf.query.answer.SparqlProtocol.VARIABLE;
import static org.jrdf.query.answer.SparqlProtocol.XSLT_URL_STRING;
import static org.jrdf.query.answer.DatatypeType.NONE;
import static org.jrdf.query.answer.SparqlResultType.UNBOUND;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.DatatypeType;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.XML_NS_PREFIX;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;

/**
 * @author Yuan-Fang Li
 * @version :$
 */
public abstract class AbstractXmlStreamWriter implements AnswerXmlWriter {
    private static final String ENCODING_DEFAULT = "UTF-8";
    private static final String VERSION_NUMBER = "1.0";
    private static final XMLOutputFactory OUTPUT_FACTORY = javax.xml.stream.XMLOutputFactory.newInstance();
    protected XMLStreamWriter streamWriter;

    protected void createXmlStreamWriter(Writer writer) throws XMLStreamException {
        this.streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
    }

    public void writeFullDocument() throws XMLStreamException {
        checkNotNull(streamWriter);
        writeStartDocument();
        writeHead();
        writeAllResults();
        writeEndDocument();
    }

    public void writeStartDocument() throws XMLStreamException {
        streamWriter.writeStartDocument(ENCODING_DEFAULT, VERSION_NUMBER);
        String target = "type=\"text/xsl\" href=\"" + XSLT_URL_STRING + "\"";
        streamWriter.writeProcessingInstruction("xml-stylesheet", target);
        streamWriter.writeStartElement(SPARQL);
        streamWriter.writeDefaultNamespace(SPARQL_NS);
        streamWriter.writeNamespace("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
        streamWriter.writeNamespace("schemaLocation", "http://www.w3.org/2007/SPARQL/result.xsd");
    }

    protected void writeHead(String[] variables) throws XMLStreamException {
        streamWriter.writeStartElement(HEAD);
        for (String variable : variables) {
            streamWriter.writeStartElement(VARIABLE);
            streamWriter.writeAttribute(NAME, variable);
            streamWriter.writeEndElement();
        }
        streamWriter.writeEndElement();
    }

    protected void writeResult(String[] currentVariables, final TypeValue[] results) throws XMLStreamException {
        streamWriter.writeStartElement(RESULT);
        int index = 0;
        for (TypeValue result : results) {
            writeBinding(result, currentVariables[index]);
            index++;
        }
        streamWriter.writeEndElement();
    }

    protected void writeBinding(TypeValue result, String currentVariable) throws XMLStreamException {
        if (!result.getType().equals(UNBOUND)) {
            realWriteBinding(result, currentVariable);
        }
    }

    protected void realWriteBinding(TypeValue result, String currentVariable) throws XMLStreamException {
        streamWriter.writeStartElement(BINDING);
        streamWriter.writeAttribute(NAME, currentVariable);
        streamWriter.writeStartElement(result.getType().getXmlElementName());
        if (result.getSuffixType() != NONE) {
            if (result.getSuffixType().equals(DatatypeType.DATATYPE)) {
                streamWriter.writeAttribute(DATATYPE, result.getSuffix());
            } else if (result.getSuffixType().equals(DatatypeType.XML_LANG)) {
                streamWriter.writeAttribute(XML_NS_PREFIX + ":lang", result.getSuffix());
            }
        }
        streamWriter.writeCharacters(result.getValue());
        streamWriter.writeEndElement();
        streamWriter.writeEndElement();
    }

    protected void writeAllResults() throws XMLStreamException {
        writeStartResults();
        while (hasMoreResults()) {
            writeResult();
        }
        writeEndResults();
        flush();
    }

    public void writeStartResults() throws XMLStreamException {
        streamWriter.writeStartElement(RESULTS);
    }

    public void writeEndResults() throws XMLStreamException {
        streamWriter.writeEndElement();
    }

    public void writeEndDocument() throws XMLStreamException {
        streamWriter.writeEndElement();
        streamWriter.writeEndDocument();
    }

    public void flush() throws XMLStreamException {
        streamWriter.flush();
    }

    public void close() throws XMLStreamException {
        if (streamWriter != null) {
            flush();
            try {
                streamWriter.close();
            } catch (XMLStreamException e) {
                // We did our best.
                ;
            }
        }
    }
}
