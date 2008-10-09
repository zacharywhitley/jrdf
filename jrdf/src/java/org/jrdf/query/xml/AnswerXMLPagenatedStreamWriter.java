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
import org.jrdf.util.EmptyClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class AnswerXMLPagenatedStreamWriter extends AbstractXMLStreamWriter implements AnswerXMLWriter {
    private Set<Attribute> heading;
    private Relation results;
    private Iterator<Tuple> tupleIterator;
    private Tuple currentTuple;
    private int maxRows;
    private int count;

    private AnswerXMLPagenatedStreamWriter() {
    }

    public AnswerXMLPagenatedStreamWriter(Set<Attribute> heading, Relation results, Writer writer)
        throws XMLStreamException {
        this.heading = heading;
        this.results = results;
        if (results != null) {
            tupleIterator = this.results.getSortedTuples().iterator();
            maxRows = results.getTuples().size();
        } else {
            tupleIterator = new EmptyClosableIterator<Tuple>();
            maxRows = 0;
        }
        streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
        count = 0;
    }

    public AnswerXMLPagenatedStreamWriter(Set<Attribute> heading, Relation results, Writer writer, int maxRows)
        throws XMLStreamException {
        this(heading, results, writer);
        this.maxRows = maxRows;
    }

    public void setWriter(Writer writer) throws XMLStreamException, IOException {
        close();
        streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
    }

    public boolean hasMoreResults() {
        return tupleIterator.hasNext() && count < maxRows;
    }

    public void write() throws XMLStreamException {
        checkNotNull(streamWriter);
        doWrite();
    }

    public void write(Writer writer) throws XMLStreamException {
        streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
        doWrite();
    }

    public void addStream(InputStream stream) throws InterruptedException, XMLStreamException {
        throw new UnsupportedOperationException("Cannot add a stream to this writer.");
    }

    private void doWrite() throws XMLStreamException {
        writeStartDocument();
        writeVariables();
        writeAllResults();
        writeEndDocument();
    }

    protected void writeAllResults() throws XMLStreamException {
        writeStartResults();
        while (hasMoreResults()) {
            writeResult();
        }
        writeEndResults();
        streamWriter.flush();
    }

    public void writeResult() throws XMLStreamException {
        if (tupleIterator.hasNext()) {
            currentTuple = tupleIterator.next();
            streamWriter.writeStartElement(RESULT);
            final Map<Attribute, ValueOperation> avps = currentTuple.getAttributeValues();
            for (Attribute headingAttribute : heading) {
                writeOneBinding(avps, headingAttribute);
            }
            streamWriter.writeEndElement();
            count++;
        }
    }

    private void writeOneBinding(Map<Attribute, ValueOperation> avps, Attribute headingAttribute)
        throws XMLStreamException {
        streamWriter.writeStartElement(BINDING);
        streamWriter.writeAttribute(NAME, getVariableName(headingAttribute));
        final Node node = avps.get(headingAttribute).getValue();
        writeOneNode(node);
        streamWriter.writeEndElement();
    }

    public void writeVariables() throws XMLStreamException {
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
