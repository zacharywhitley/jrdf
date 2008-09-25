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

package org.jrdf.query;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.ValueOperation;
import static org.jrdf.query.relation.mem.SortedAttributeFactory.DEFAULT_OBJECT_NAME;
import static org.jrdf.query.relation.mem.SortedAttributeFactory.DEFAULT_PREDICATE_NAME;
import static org.jrdf.query.relation.mem.SortedAttributeFactory.DEFAULT_SUBJECT_NAME;

import javax.xml.stream.XMLOutputFactory;
import static javax.xml.stream.XMLOutputFactory.newInstance;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class AnswerXMLStreamWriterImpl implements AnswerXMLWriter {
    private static final String ENCODING_DEFAULT = "UTF-8";
    private static final String VERSION_NUMBER = "1.0";
    private static final XMLOutputFactory FACTORY = newInstance();
    private static final int MAX_RESULT = 200;

    private Set<Attribute> heading;
    private Relation results;
    private XMLStreamWriter streamWriter;
    private PrintWriter printWriter;
    private int counter;

    private AnswerXMLStreamWriterImpl() {
    }

    public AnswerXMLStreamWriterImpl(Set<Attribute> heading, Relation results) {
        this.heading = heading;
        this.results = results;
    }

    public void write(OutputStream stream) throws XMLStreamException {
        printWriter = new PrintWriter(stream);
        counter = 0;
        write(printWriter);
    }

    public void write(Writer writer) throws XMLStreamException {
        printWriter = new PrintWriter(writer);
        try {
            streamWriter = FACTORY.createXMLStreamWriter(printWriter);
            streamWriter.writeStartDocument(ENCODING_DEFAULT, VERSION_NUMBER);
            String target = "type=\"text/xsl\" href=\"" + XSLT_URL_STRING + "\"";
            streamWriter.writeProcessingInstruction("xml-stylesheet", target);
            System.err.println("before writing body");
            writeBody();
            System.err.println("after writing body");
            streamWriter.writeEndDocument();
        } finally {
            streamWriter.flush();
            streamWriter.close();
        }
    }

    private void writeBody() throws XMLStreamException {
        streamWriter.writeStartElement(SPARQL);
        streamWriter.writeAttribute("xmlns", "http://www.w3.org/2005/sparql-results#");
        streamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        streamWriter.writeAttribute("xsi:schemaLocation", "http://www.w3.org/2007/SPARQL/result.xsd");
        writeVariables();
        streamWriter.flush();
        writeResults();
        streamWriter.writeEndElement();
    }

    private void writeResults() throws XMLStreamException {
        streamWriter.writeStartElement(RESULTS);
        if (results != null) {
            SortedSet<Tuple> sortedTuples = results.getSortedTuples();
            for (Tuple tuple : sortedTuples) {
                writeOneResult(tuple);
                counter++;
                if (counter % MAX_RESULT == 0) {
                    streamWriter.flush();
                }
            }
        }
        streamWriter.writeEndElement();
    }

    private void writeOneResult(Tuple tuple) throws XMLStreamException {
        streamWriter.writeStartElement(RESULT);
        final Map<Attribute, ValueOperation> avps = tuple.getAttributeValues();
        for (Attribute headingAttribute : heading) {
            writeOneBinding(avps, headingAttribute);
        }
        streamWriter.writeEndElement();
    }

    private void writeOneBinding(Map<Attribute, ValueOperation> avps, Attribute headingAttribute)
        throws XMLStreamException {
        streamWriter.writeStartElement(BINDING);
        streamWriter.writeAttribute(NAME, getVariableName(headingAttribute));
        final Node node = avps.get(headingAttribute).getValue();
        writeOneNode(node);
        streamWriter.writeEndElement();
    }

    private void writeOneNode(Node node) throws XMLStreamException {
        String nodeType;
        nodeType = getNodeType(node);
        boolean isLiteral = nodeType.equalsIgnoreCase(LITERAL);
        streamWriter.writeStartElement(nodeType);
        String characters;
        if (isLiteral) {
            writeLiteralAttributes(node);
            characters = ((Literal) node).getValue().toString();
        } else {
            characters = node.toString();
        }
        streamWriter.writeCharacters(characters);
        streamWriter.writeEndElement();
    }

    private void writeLiteralAttributes(Node node) throws XMLStreamException {
        final Literal literal = (Literal) node;
        final URI datatypeURI = literal.getDatatypeURI();
        final String language = literal.getLanguage();
        if (datatypeURI != null) {
            streamWriter.writeAttribute(DATATYPE, datatypeURI.toString());
        } else if (language != null || !language.equals("")) {
            streamWriter.writeAttribute(XML_LANG, language);
        }
    }

    private String getNodeType(Node node) {
        String nodeType;
        if (BlankNode.class.isAssignableFrom(node.getClass())) {
            nodeType = BNODE;
        } else if (URIReference.class.isAssignableFrom(node.getClass())) {
            nodeType = URI;
        } else {
            nodeType = LITERAL;
        }
        return nodeType;
    }

    private void writeVariables() throws XMLStreamException {
        streamWriter.writeStartElement(HEAD);
        if (heading != null) {
            for (Attribute attribute : heading) {
                final String variableName = getVariableName(attribute);
                streamWriter.writeStartElement(VARIABLE);
                streamWriter.writeAttribute(NAME, variableName);
                streamWriter.writeEndElement();
            }
        }
        streamWriter.writeEndElement();
    }

    private String getVariableName(Attribute attribute) {
        String name = attribute.getAttributeName().toString();
        if (name.startsWith("?")) {
            name = name.substring(1);
        } else {
            if (name.startsWith(DEFAULT_SUBJECT_NAME)) {
                name = DEFAULT_SUBJECT_NAME;
            } else if (name.startsWith(DEFAULT_PREDICATE_NAME)) {
                name = DEFAULT_PREDICATE_NAME;
            } else if (name.startsWith(DEFAULT_OBJECT_NAME)) {
                name = DEFAULT_OBJECT_NAME;
            }
        }
        return name;
    }
}
