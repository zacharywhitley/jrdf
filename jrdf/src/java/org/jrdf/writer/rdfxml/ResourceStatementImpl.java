/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.RdfNamespaceMap;
import org.jrdf.writer.WriteException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.PrintWriter;

/**
 * Represents a statement about a resource.
 *
 * @author TurnerRX
 */
public final class ResourceStatementImpl implements ResourceStatement {
    private XMLOutputFactory factory = XMLOutputFactory.newInstance();
    private XMLStreamWriter xmlStreamWriter;
    private Triple triple;
    private RdfNamespaceMap names;
    private BlankNodeRegistry registry;

    public ResourceStatementImpl(RdfNamespaceMap newNames, BlankNodeRegistry newBlankNodeRegistry) {
        checkNotNull(newNames, newBlankNodeRegistry);
        this.names = newNames;
        this.registry = newBlankNodeRegistry;
    }

    public void writeTriple(Triple newTriple, PrintWriter writer) throws WriteException {
        triple = newTriple;
        write(writer);
    }

    public void write(PrintWriter writer) throws WriteException {
        PredicateNode predicate = triple.getPredicate();
        ObjectNode object = triple.getObject();
        try {
            xmlStreamWriter = factory.createXMLStreamWriter(writer);
            write(predicate, object);
            xmlStreamWriter.flush();
        } catch (XMLStreamException e) {
            throw new WriteException(e);
        }
    }


    // TODO AN Replace with visitor.
    private void write(PredicateNode predicate, ObjectNode object) throws WriteException, XMLStreamException {
        if (!(predicate instanceof URIReference)) {
            throw new WriteException("Unknown predicate node type: " + predicate.getClass().getName());
        }
        if (object instanceof URIReference) {
            write((URIReference) predicate, (URIReference) object);
        } else if (object instanceof Literal) {
            write((URIReference) predicate, (Literal) object);
        } else if (object instanceof BlankNode) {
            write((URIReference) predicate, (BlankNode) object);
        } else {
            throw new WriteException("Unknown object node type: " + object.getClass().getName());
        }
    }

    private void write(URIReference predicate, URIReference object) throws WriteException, XMLStreamException {
        xmlStreamWriter.writeStartElement(names.replaceNamespace(predicate));
        xmlStreamWriter.writeAttribute("rdf:resource", object.getURI().toString());
        xmlStreamWriter.writeEndElement();
    }

    private void write(URIReference predicate, Literal literal) throws WriteException, XMLStreamException {
        xmlStreamWriter.writeStartElement(names.replaceNamespace(predicate));
        if (literal.isLanguageLiteral()) {
            xmlStreamWriter.writeAttribute("xml:lang", literal.getLanguage());
        }
        if (literal.isDatatypedLiteral()) {
            xmlStreamWriter.writeAttribute("rdf:datatype", literal.getDatatypeURI().toString());
        }
        xmlStreamWriter.writeCharacters(literal.getLexicalForm());
        xmlStreamWriter.writeEndElement();
    }

    private void write(URIReference predicate, BlankNode bnode) throws WriteException, XMLStreamException {
        xmlStreamWriter.writeStartElement(names.replaceNamespace(predicate));
        xmlStreamWriter.writeAttribute("rdf:nodeID", registry.getNodeId(bnode));
        xmlStreamWriter.writeEndElement();
    }
}
