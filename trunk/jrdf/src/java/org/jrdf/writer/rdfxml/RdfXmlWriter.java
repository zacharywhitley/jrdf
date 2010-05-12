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

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.RdfNamespaceMap;
import org.jrdf.writer.RdfWriter;
import org.jrdf.writer.WriteException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import static org.jrdf.util.param.ParameterUtil.checkNotNull;

/**
 * Writes Graph contents in RDF/XML format.
 *
 * @author TurnerRX
 */
public class RdfXmlWriter implements RdfWriter {
    private static final String ENCODING_DEFAULT = "UTF-8";
    private OutputStreamWriter streamWriter;
    private PrintWriter printWriter;
    private GraphToRdfXml graphToRdfXml;

    public RdfXmlWriter(BlankNodeRegistry newBlankNodeRegistry, RdfNamespaceMap newNames) {
        checkNotNull(newBlankNodeRegistry, newNames);
        this.graphToRdfXml = new GraphToRdfXmlWithHeader(newBlankNodeRegistry, newNames);
    }

    public void write(Graph graph, OutputStream stream) throws GraphException {
        streamWriter = new OutputStreamWriter(stream);
        write(graph, streamWriter);
    }

    public void write(Graph graph, Writer writer) throws GraphException {
        write(graph, writer, null);
    }

    public void write(Graph graph, Writer writer, String encoding) throws GraphException {
        try {
            tryWrite(graph, writer, encoding);
        } catch (XMLStreamException e) {
            throw new WriteException(e);
        } catch (IOException e) {
            throw new WriteException(e);
        }
    }

    public void close() throws WriteException {
        try {
            printWriter.close();
        } finally {
            try {
                if (streamWriter != null) {
                    streamWriter.close();
                }
            } catch (IOException e) {
                throw new WriteException(e);
            }
        }
    }

    private void tryWrite(Graph graph, Writer writer, String encoding) throws XMLStreamException, IOException {
        printWriter = new PrintWriter(writer);
        encoding = (encoding == null) ? ENCODING_DEFAULT : encoding;
        graphToRdfXml.write(graph, encoding, printWriter);
    }
}
