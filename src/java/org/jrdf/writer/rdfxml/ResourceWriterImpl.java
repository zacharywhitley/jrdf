/*
 * $Header$
 * $Revision: 2077 $
 * $Date: 2008-06-04 11:57:01 +1000 (Wed, 04 Jun 2008) $
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

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.Resource;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.util.IteratorStack;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.RdfNamespaceMap;
import org.jrdf.writer.WriteException;
import org.jrdf.writer.RdfWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Represents an RDF/XML header for a given resource.
 *
 * @author TurnerRX
 * @author Andrew Newman
 */
public class ResourceWriterImpl implements ResourceWriter {
    private final BlankNodeRegistry registry;
    private final XMLStreamWriter xmlStreamWriter;
    private final PredicateObjectWriter statement;
    private Triple currentTriple;
    private Exception exception;

    public ResourceWriterImpl(final RdfNamespaceMap names, final BlankNodeRegistry newRegistry,
        final XMLStreamWriter newXmlStreamWriter, XmlLiteralWriter xmlLiteralWriter) {
        checkNotNull(names, newRegistry, newXmlStreamWriter);
        this.registry = newRegistry;
        this.xmlStreamWriter = newXmlStreamWriter;
        this.statement = new PredicateObjectWriterImpl(names, registry, xmlStreamWriter, xmlLiteralWriter);
    }

    public void setTriple(final Triple triple) {
        this.currentTriple = triple;
    }

    public void writeStart() throws WriteException {
        try {
            xmlStreamWriter.writeStartElement("rdf:Description");
            currentTriple.getSubject().accept(this);
            xmlStreamWriter.writeCharacters(RdfWriter.NEW_LINE + "    ");
            xmlStreamWriter.flush();
            if (exception != null) {
                throw exception;
            }
        } catch (Exception e) {
            exception = null;
            throw new WriteException(e);
        }
    }

    public void writeNestedStatements(final IteratorStack<Triple> stack) throws WriteException, XMLStreamException {
        statement.writePredicateObject(currentTriple.getPredicate(), currentTriple.getObject());
        while (stack.hasNext()) {
            SubjectNode currentSubject = currentTriple.getSubject();
            currentTriple = stack.pop();
            // Have we run out of the same subject - if so push it back on an stop iterating.
            if (!currentSubject.equals(currentTriple.getSubject())) {
                stack.push(currentTriple);
                break;
            }
            xmlStreamWriter.writeCharacters("    ");
            statement.writePredicateObject(currentTriple.getPredicate(), currentTriple.getObject());
        }
    }

    public void writeEnd() throws WriteException {
        try {
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters(RdfWriter.NEW_LINE);
            xmlStreamWriter.flush();
        } catch (XMLStreamException e) {
            throw new WriteException(e);
        }
    }

    public void visitBlankNode(final BlankNode blankNode) {
        try {
            xmlStreamWriter.writeAttribute("rdf:nodeID", registry.getNodeId(blankNode));
        } catch (XMLStreamException e) {
            exception = e;
        }
    }

    public void visitURIReference(final URIReference uriReference) {
        try {
            xmlStreamWriter.writeAttribute("rdf:about", uriReference.getURI().toString());
        } catch (XMLStreamException e) {
            exception = e;
        }
    }

    public void visitLiteral(final Literal literal) {
        unknownType(literal);
    }

    public void visitNode(final Node node) {
        unknownType(node);
    }

    public void visitResource(Resource resource) {
        unknownType(resource);
    }

    private void unknownType(final Node node) {
        exception = new WriteException("Unknown SubjectNode type: " + node.getClass().getName());
    }
}
