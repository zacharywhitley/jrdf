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
package org.jrdf.writer.rdfxml;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Resource;
import org.jrdf.graph.URIReference;
import static org.jrdf.util.param.ParameterUtil.*;
import org.jrdf.vocabulary.RDF;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.RdfNamespaceMap;
import org.jrdf.writer.RdfWriter;
import org.jrdf.writer.WriteException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Represents a statement about a resource.
 *
 * @author TurnerRX
 * @author Andrew Newman
 */
public final class PredicateObjectWriterImpl implements PredicateObjectWriter {
    private final RdfNamespaceMap names;
    private final BlankNodeRegistry registry;
    private final XmlLiteralWriter xmlLiteralWriter;
    private XMLStreamWriter xmlStreamWriter;
    private Exception exception;

    public PredicateObjectWriterImpl(final RdfNamespaceMap newNames, final BlankNodeRegistry newBlankNodeRegistry,
        final XMLStreamWriter newXmlStreamWriter, final XmlLiteralWriter xmlLiteralWriter) {
        checkNotNull(newNames, newBlankNodeRegistry, newXmlStreamWriter, xmlLiteralWriter);
        this.xmlLiteralWriter = xmlLiteralWriter;
        this.names = newNames;
        this.registry = newBlankNodeRegistry;
        this.xmlStreamWriter = newXmlStreamWriter;
    }

    public void writePredicateObject(final PredicateNode predicate, final ObjectNode object) throws WriteException {
        checkNotNull(predicate, object);
        try {
            writePredicate(predicate);
            writeObject(object);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters(RdfWriter.NEW_LINE);
            xmlStreamWriter.flush();
        } catch (Exception e) {
            exception = null;
            throw new WriteException(e);
        }
    }

    public void visitBlankNode(BlankNode blankNode) {
        checkNotNull(blankNode);
        try {
            xmlStreamWriter.writeAttribute("rdf:nodeID", registry.getNodeId(blankNode));
        } catch (XMLStreamException e) {
            exception = e;
        }
    }

    public void visitURIReference(URIReference uriReference) {
        checkNotNull(uriReference);
        try {
            xmlStreamWriter.writeAttribute("rdf:resource", uriReference.getURI().toString());
        } catch (XMLStreamException e) {
            exception = e;
        }
    }

    public void visitLiteral(Literal literal) {
        checkNotNull(literal);
        try {
            if (literal.isDatatypedLiteral() && literal.getDatatypeURI().equals(RDF.XML_LITERAL)) {
                xmlLiteralWriter.write(literal);
            } else {
                if (literal.isDatatypedLiteral()) {
                    xmlStreamWriter.writeAttribute("rdf:datatype", literal.getDatatypeURI().toString());
                } else if (literal.isLanguageLiteral()) {
                    xmlStreamWriter.writeAttribute("xml:lang", literal.getLanguage());
                }
                xmlStreamWriter.writeCharacters(literal.getLexicalForm());
            }
        } catch (XMLStreamException e) {
            exception = e;
        }
    }

    public void visitNode(Node node) {
        checkNotNull(node);
        exception = new WriteException("Unknown object node type: " + node.getClass().getName());
    }

    public void visitResource(Resource resource) {
        checkNotNull(resource);
        exception = new WriteException("Unknown object node type: " + resource.getClass().getName());
    }

    private void writePredicate(PredicateNode predicate) throws WriteException, XMLStreamException {
        if (!(predicate instanceof URIReference)) {
            throw new WriteException("Unknown predicate node type: " + predicate.getClass().getName());
        }
        String resourceName = names.replaceNamespace((URIReference) predicate);
        xmlStreamWriter.writeStartElement(resourceName);
    }

    private void writeObject(ObjectNode object) throws Exception {
        object.accept(this);
        if (exception != null) {
            throw exception;
        }
    }
}