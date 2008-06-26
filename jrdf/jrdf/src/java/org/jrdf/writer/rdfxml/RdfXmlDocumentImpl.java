/*
 * $Header$
 * $Revision$
 * $Date$
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

import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.jrdf.writer.RdfNamespaceMap;
import org.jrdf.writer.WriteException;
import org.jrdf.writer.RdfWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Represents an RDF/XML header that includes an XML header and opening RDF
 * element.
 *
 * @author TurnerRX
 */
public class RdfXmlDocumentImpl implements RdfXmlDocument {
    private static final String XML_VERSION = "1.0";
    private static final String DOC_TYPE = "<!DOCTYPE rdf:RDF[" + RdfWriter.NEW_LINE + " ${entities} ]>";
    private static final String XML_ENTITY = "    <!ENTITY ${name} '${uri}'>" + RdfWriter.NEW_LINE;

    /**
     * Character set to be used when writing header
     */
    private final String encoding;

    /**
     * Used to map partial URIs to RDF namespaces
     */
    private final RdfNamespaceMap names;

    /**
     * Writer for the XML document.
     */
    private final XMLStreamWriter xmlStreamWriter;

    /**
     * Constructor. Specifies the character encoding to be used.
     *
     * @param encoding charset
     * @param names the namespace maps (from prefix to full name and back).
     * @param xmlStreamWriter the writer to add new elements to.
     */
    public RdfXmlDocumentImpl(final String encoding, final RdfNamespaceMap names,
        final XMLStreamWriter xmlStreamWriter) {
        checkNotNull(encoding, names, xmlStreamWriter);
        this.encoding = encoding;
        this.names = names;
        this.xmlStreamWriter = xmlStreamWriter;
    }

    public void writeHeader() throws WriteException {
        try {
            xmlStreamWriter.writeStartDocument(encoding, XML_VERSION);
            xmlStreamWriter.writeDTD(writeDocTypeDef());
            xmlStreamWriter.writeStartElement("rdf", "RDF", names.getFullUri("rdf"));
            for (final Entry<String, String> entry : names.getNameEntries()) {
                xmlStreamWriter.writeNamespace(entry.getKey(), entry.getValue());
            }
            xmlStreamWriter.writeCharacters(RdfWriter.NEW_LINE + "    ");
        } catch (XMLStreamException e) {
            throw new WriteException(e);
        }
    }

    public void writeFooter() throws WriteException {
        try {
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
        } catch (XMLStreamException e) {
            throw new WriteException(e);
        }
    }

    /**
     * Writes the document type definition including entities.
     *
     * @return the DOCTYPE entities.
     */
    private String writeDocTypeDef() {
        String docType = DOC_TYPE;
        docType = docType.replaceAll("\\$\\{entities\\}", getEntities());
        return docType;
    }

    /**
     * Returns a list of XML entity declarations for the entries in the
     * namespace map.
     *
     * @return String RDF namespaces as xml entities.
     */
    private String getEntities() {
        final StringBuffer buffer = new StringBuffer();
        final Set<Entry<String, String>> entries = names.getNameEntries();
        for (final Entry<String, String> entry : entries) {
            String entity = XML_ENTITY;
            entity = entity.replaceAll("\\$\\{name\\}", entry.getKey());
            entity = entity.replaceAll("\\$\\{uri\\}", entry.getValue());
            buffer.append(entity);
        }
        return buffer.toString();
    }
}
