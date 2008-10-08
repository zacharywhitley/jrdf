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

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public abstract class AbstractXMLStreamWriter implements AnswerXMLWriter {
    protected static final String ENCODING_DEFAULT = "UTF-8";
    protected static final String VERSION_NUMBER = "1.0";
    protected static final XMLOutputFactory OUTPUT_FACTORY = javax.xml.stream.XMLOutputFactory.newInstance();
    protected XMLStreamWriter streamWriter;

    public void writeStartDocument() throws XMLStreamException {
        streamWriter.writeStartDocument(ENCODING_DEFAULT, VERSION_NUMBER);
        String target = "type=\"text/xsl\" href=\"" + XSLT_URL_STRING + "\"";
        streamWriter.writeProcessingInstruction("xml-stylesheet", target);

        streamWriter.writeStartElement(SPARQL);
        streamWriter.writeAttribute("xmlns", SPARQL_NS);
        streamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        streamWriter.writeAttribute("xsi:schemaLocation", "http://www.w3.org/2007/SPARQL/result.xsd");
    }

    public void writeEndDocument() throws XMLStreamException {
        streamWriter.writeEndElement();
        streamWriter.writeEndDocument();
    }

    public void writeStartResults() throws XMLStreamException {
        streamWriter.writeStartElement(RESULTS);
    }

    public void writeEndResults() throws XMLStreamException {
        streamWriter.writeEndElement();
    }

    public void close() throws XMLStreamException, IOException {
        if (streamWriter != null) {
            streamWriter.flush();
            streamWriter.close();
        }
    }

    public void flush() throws XMLStreamException {
        streamWriter.flush();
    }
}
