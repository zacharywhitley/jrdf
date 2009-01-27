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

package org.jrdf.writer.ntriples;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.Resource;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TypedNodeVisitable;
import org.jrdf.graph.URIReference;
import org.jrdf.util.ClosableIterable;
import org.jrdf.writer.WriteException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class NTriplesWriterImpl implements NTriplesWriter {
    private static final String SPACE = " ";
    private static final String END_OF_TRIPLE = SPACE + "." + NEW_LINE;
    private PrintWriter printWriter;
    private WriteException exception;

    public void write(Graph graph, OutputStream stream) throws WriteException, GraphException {
        final OutputStreamWriter writer = new OutputStreamWriter(stream);
        try {
            write(graph, writer);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new WriteException(e);
            }
        }
    }

    public void write(Graph graph, Writer writer) throws WriteException, GraphException {
        printWriter = new PrintWriter(writer);
        try {
            write(graph, (String) null);
        } finally {
            printWriter.close();
        }
    }

    private void write(Graph graph, String encoding) throws GraphException, WriteException {
        ClosableIterable<Triple> triples = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        try {
            for (Triple triple : triples) {
                printTriple(triple);
            }
        } finally {
            triples.iterator().close();
        }
    }

    private void printTriple(Triple triple) throws WriteException {
        printNode(SPACE, triple.getSubject());
        printNode(SPACE, triple.getPredicate());
        printNode(END_OF_TRIPLE, triple.getObject());
    }

    private void printNode(String s, TypedNodeVisitable typedNodeVisitable) throws WriteException {
        typedNodeVisitable.accept(this);
        if (exception != null) {
            throw exception;
        }
        printWriter.write(s);
    }

    public void visitBlankNode(BlankNode blankNode) {
        printWriter.write("_:a" + blankNode.toString().replace("#", "").replace("-", ""));
    }

    public void visitURIReference(URIReference uriReference) {
        printWriter.write("<" + uriReference.getURI() + ">");
    }

    public void visitLiteral(Literal literal) {
        printWriter.write(literal.getEscapedForm());
    }

    public void visitNode(Node node) {
        unknownType(node);
    }

    public void visitResource(Resource resource) {
        unknownType(resource);
    }

    private void unknownType(final Node node) {
        exception = new WriteException("Unknown node type: " + node.getClass().getName());
    }
}
