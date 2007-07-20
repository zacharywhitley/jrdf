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
import org.jrdf.writer.RdfWriter;
import org.jrdf.writer.WriteException;
import org.jrdf.writer.RdfNamespaceMap;

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
    /**
     * PrintWriter output. Caller is responsible for closing stream.
     */
    private PrintWriter printWriter;

    /**
     * Current triple off the stack.
     */
    private Triple currentTriple;

    /**
     * The current subject node.
     */
    private SubjectNode currentSubject;

    /**
     * Used to track blank nodes.
     */
    private BlankNodeRegistry blankNodeRegistry;

    /**
     * Containing mappings between partial URIs and namespaces.
     */
    private RdfNamespaceMap names;

    public RdfXmlWriter(BlankNodeRegistry blankNodeRegistry, RdfNamespaceMap names) {
        this.blankNodeRegistry = blankNodeRegistry;
        this.names = names;
    }

    public void write(Graph graph, OutputStream stream) throws IOException, WriteException, GraphException {
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        write(graph, writer);
    }

    public void write(Graph graph, Writer writer) throws IOException, WriteException, GraphException {
        printWriter = new PrintWriter(writer);
        write(graph, (String) null);
    }

    /**
     * Writes the graph contents to the writer, including the specified encoding
     * in the XML header.
     *
     * @param graph    Graph to be written.
     * @param encoding String XML encoding attribute.
     * @throws IOException    If the graph contents cannot be written to the output.
     * @throws GraphException If the graph cannot be read.
     * @throws WriteException If the contents could not be written
     */
    private void write(Graph graph, String encoding) throws IOException, GraphException,
            WriteException {
        try {
            // Initialize values.
            blankNodeRegistry.clear();
            names.reset();
            names.load(graph);

            // header
            RdfXmlHeader header = new RdfXmlHeader(encoding, names);
            header.write(printWriter);

            // body
            writeStatements(graph);

            // footer
            RdfXmlFooter footer = new RdfXmlFooter();
            footer.write(printWriter);
        } finally {
            if (printWriter != null) {
                printWriter.flush();
            }
        }
    }

    /**
     * Writes all statements in the Graph to the writer.
     *
     * @param graph  Graph containing statements.
     * @throws GraphException If the graph cannot be read.
     * @throws IOException    If the statements cannot be written.
     * @throws WriteException If the statements could not be written.
     */
    private void writeStatements(Graph graph) throws GraphException, IOException, WriteException {
        // get all statements
        // TODO - ensure these statements are ordered.
        // write one subject at a time
        ClosableIterator<Triple> iter = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        try {
            IteratorStack<Triple> stack = new IteratorStack<Triple>(iter);
            while (stack.hasNext()) {
                writeSubject(stack);
            }
        } finally {
            iter.close();
        }
    }

    /**
     * Writes a Resource with all its statements to the writer.
     *
     * @param stack    IteratorStack<Triple>
     * @throws IOException    If an IOException is encountered while writing the subject.
     * @throws WriteException If the subject could not be written
     */
    private void writeSubject(IteratorStack<Triple> stack) throws IOException, WriteException {
        currentTriple = stack.pop();
        currentSubject = currentTriple.getSubject();
        writeHeader();
        writeStatements(stack);
        writeFooter();
    }

    private void writeHeader() throws IOException, WriteException {
        ResourceHeader header = new ResourceHeader(currentSubject, blankNodeRegistry);
        header.write(printWriter);
    }

    private void writeStatements(IteratorStack<Triple> stack) throws IOException, WriteException {
        // write statements
        ResourceStatement statement = new ResourceStatement(names, blankNodeRegistry);
        statement.setAndWriteTriple(currentTriple, printWriter);
        while (stack.hasNext()) {
            currentTriple = stack.pop();
            // Have we run out of the same subject - if so push it back on an stop iterating.
            if (!currentSubject.equals(currentTriple.getSubject())) {
                stack.push(currentTriple);
                break;
            }
            statement.setAndWriteTriple(currentTriple, printWriter);
        }
    }

    private void writeFooter() throws IOException, WriteException {
        ResourceFooter footer = new ResourceFooter(currentSubject);
        footer.write(printWriter);
    }
}
