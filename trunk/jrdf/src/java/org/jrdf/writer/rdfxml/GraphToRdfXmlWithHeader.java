/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 *  ====================================================================
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
 */

package org.jrdf.writer.rdfxml;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.IteratorStack;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.RdfNamespaceMap;
import org.jrdf.writer.WriteException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;

import static org.jrdf.graph.AnyTriple.ANY_TRIPLE;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

public class GraphToRdfXmlWithHeader implements GraphToRdfXml {
    private static final XMLOutputFactory FACTORY = XMLOutputFactory.newInstance();
    private BlankNodeRegistry blankNodeRegistry;
    private RdfNamespaceMap names;

    public GraphToRdfXmlWithHeader(final BlankNodeRegistry newBlankNodeRegistry, final RdfNamespaceMap newNames) {
        checkNotNull(newBlankNodeRegistry, newNames);
        this.blankNodeRegistry = newBlankNodeRegistry;
        this.names = newNames;
    }

    /**
     * Writes the graph contents to the writer, including the specified encoding
     * in the XML header.
     *
     * @param graph Graph to be written.
     * @param encoding String XML encoding attribute.
     * @param writer
     * @throws GraphException If the graph cannot be read.
     * @throws WriteException If the contents could not be written
     */
    public void write(Graph graph, String encoding, Writer writer) throws GraphException, WriteException,
        XMLStreamException, IOException {
        try {
            resetRegistryAndNames(graph);
            XMLStreamWriter xmlStreamWriter = createXmlStreamWriter(writer);
            RdfXmlDocument document = createRdfXmlDocument(encoding, xmlStreamWriter);
            document.writeHeader();
            writeStatements(graph, document);
            document.writeFooter();
        } finally {
            if (writer != null) {
                writer.flush();
            }
        }
    }

    private void resetRegistryAndNames(Graph graph) {
        blankNodeRegistry.clear();
        names.reset();
        names.load(graph);
    }

    private XMLStreamWriter createXmlStreamWriter(Writer writer) {
        // TODO AN - Remove this is a hack!!!!
        XMLStreamWriter xmlStreamWriter;
        try {
            xmlStreamWriter = FACTORY.createXMLStreamWriter(writer);
        } catch (XMLStreamException e) {
            throw new WriteException(e);
        }
        return xmlStreamWriter;
    }

    private RdfXmlDocument createRdfXmlDocument(String encoding, XMLStreamWriter xmlStreamWriter) {
        RdfXmlHeaderFooter headerFooter = new RdfXmlHeaderFooterImpl(encoding, names, xmlStreamWriter);
        ResourceWriterImpl resourceWriter = new ResourceWriterImpl(names, blankNodeRegistry, xmlStreamWriter,
            new XmlLiteralWriterImpl(xmlStreamWriter));
        return new RdfXmlDocumentImpl(headerFooter, resourceWriter);
    }

    /**
     * Writes all statements in the Graph to the writer.
     *
     * @param graph  Graph containing statements.
     * @param document
     * @throws GraphException If the graph cannot be read.
     * @throws WriteException If the statements could not be written.
     */
    private void writeStatements(final Graph graph, final RdfXmlDocument document) throws GraphException,
        WriteException, XMLStreamException {
        final ClosableIterator<Triple> iter = graph.find(ANY_TRIPLE).iterator();
        try {
            final IteratorStack<Triple> stack = new IteratorStack<Triple>(iter);
            while (stack.hasNext()) {
                final Triple currentTriple = stack.pop();
                document.setTriple(currentTriple);
                document.writeStart();
                document.writeNestedStatements(stack);
                document.writeEnd();
            }
        } finally {
            iter.close();
        }
    }
}
