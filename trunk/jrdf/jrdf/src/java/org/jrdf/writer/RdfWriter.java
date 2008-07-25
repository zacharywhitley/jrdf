/*
 * $Header$
 * $Revision: 2216 $
 * $Date: 2008-07-02 11:15:22 +1000 (Wed, 02 Jul 2008) $
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
package org.jrdf.writer;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * General interface for RDF Writer.
 *
 * @author TurnerRX
 * @version $Id$
 */
public interface RdfWriter {
    /**
     * XML's new line character (LF).
     */
    String NEW_LINE = "\n";

    /**
     * Writes the Graph contents to the OutputStream.
     *
     * @param graph  Graph input
     * @param stream OutputStream output. Caller is responsible for closing stream.
     * @throws WriteException If the writer encounters an unrecoverable error.
     * @throws GraphException If an exception occurrs while reading the graph.
     * @throws IOException    If output cannot be written.
     */
    void write(Graph graph, OutputStream stream) throws WriteException, GraphException, IOException, XMLStreamException;

    /**
     * Writes the Graph contents to the Writer.
     *
     * @param graph  Graph input
     * @param writer Writer output. Caller is responsible for closing writers.
     * @throws WriteException If the writer encounters an unrecoverable error.
     * @throws GraphException If an exception occurrs while reading the graph.
     * @throws IOException    If output cannot be written.
     */
    void write(Graph graph, Writer writer) throws WriteException, GraphException, IOException, XMLStreamException;
}