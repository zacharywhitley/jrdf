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

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.IteratorStack;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.RdfNamespaceMap;
import org.jrdf.writer.RdfWriter;
import org.jrdf.writer.WriteException;
import org.jrdf.writer.mem.BlankNodeRegistryImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Writes Graph contents in RDF/XML format.
 *
 * @author TurnerRX
 */
public class RdfXmlWriter implements RdfWriter {

    public void write(Graph graph, OutputStream stream) throws IOException, WriteException, GraphException {
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        write(graph, writer);
    }

    public void write(Graph graph, Writer writer) throws IOException, WriteException, GraphException {
        PrintWriter printWriter = new PrintWriter(writer);
        write(graph, printWriter, null);
    }

    /**
     * Writes the graph contents to the writer, including the specified encoding
     * in the XML header.
     *
     * @param graph    Graph to be written.
     * @param writer   PrintWriter output. Caller is responsible for closing stream.
     * @param encoding String XML encoding attribute.
     * @throws IOException    If the graph contents cannot be written to the output.
     * @throws GraphException If the graph cannot be read.
     * @throws WriteException If the contents could not be written
     */
    private void write(Graph graph, PrintWriter writer, String encoding) throws IOException, GraphException,
            WriteException {
        try {
            // load namespaces
            RdfNamespaceMap names = new RdfNamespaceMap();
            names.load(graph);
            // header
            RdfXmlHeader header = new RdfXmlHeader(encoding, names);
            header.write(writer);
            // body
            writeStatements(graph, writer, names);
            // footer
            RdfXmlFooter footer = new RdfXmlFooter();
            footer.write(writer);
        } finally {
            if (writer != null) {
                writer.flush();
            }
        }
    }

    /**
     * Writes all statements in the Graph to the writer.
     *
     * @param graph  Graph containing statements.
     * @param writer PrintWriter output
     * @param names  RdfNamespaceMap containgin mappings between partial URIs and namespaces.
     * @throws GraphException If the graph cannot be read.
     * @throws IOException    If the statements cannot be written.
     * @throws WriteException If the statements could not be written.
     */
    private void writeStatements(Graph graph, PrintWriter writer, RdfNamespaceMap names) throws GraphException,
            IOException, WriteException {
        ClosableIterator<Triple> iter = null;
        try {
            BlankNodeRegistry registry = new BlankNodeRegistryImpl();
            // get all statements
            // TODO - ensure these statements are ordered.
            iter = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE,
                    ANY_OBJECT_NODE);
            // write one subject at a time
            IteratorStack<Triple> stack = new IteratorStack<Triple>(iter);
            while (iter.hasNext()) {
                writeSubject(stack, writer, names, registry);
            }
        } finally {
            if (iter != null) {
                iter.close();
            }
        }
    }

    /**
     * Writes a Resource with all its statements to the writer.
     *
     * @param stack    IteratorStack<Triple>
     * @param writer   PrintWriter output
     * @param names    RdfNamespaceMap containing mappings between partial URIs and
     *                 namespaces.
     * @param registry BlankNodeRegistry Used to track blank nodes.
     * @throws IOException    If an IOException is encountered while writing the subject.
     * @throws WriteException If the subject could not be written
     */
    private void writeSubject(IteratorStack<Triple> stack, PrintWriter writer, RdfNamespaceMap names,
                              BlankNodeRegistry registry) throws IOException, WriteException {
        if (!stack.hasNext()) {
            return;
        }
        // init
        Triple triple = stack.pop();
        SubjectNode subject = triple.getSubject();
        // write header
        ResourceHeader header = new ResourceHeader(subject, registry);
        header.write(writer);
        // write statements
        ResourceStatement statement = new ResourceStatement(names, registry);
        statement.setTriple(triple);
        statement.write(writer);
        while (stack.hasNext()) {
            triple = stack.pop();
            // put it back if it is not the right subject
            if (!subject.equals(triple.getSubject())) {
                stack.push(triple);
                break;
            }
            statement.setTriple(triple);
            statement.write(writer);
        }

        // write footer
        ResourceFooter footer = new ResourceFooter(subject);
        footer.write(writer);
    }

}
