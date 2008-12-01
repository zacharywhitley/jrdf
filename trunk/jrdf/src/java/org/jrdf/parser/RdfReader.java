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

package org.jrdf.parser;

import org.jrdf.MemoryJRDFFactory;
import org.jrdf.collection.MapFactory;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.parser.line.GraphLineParser;
import org.jrdf.parser.line.LineHandler;
import org.jrdf.parser.line.LineHandlerFactory;
import org.jrdf.parser.n3.N3ParserFactory;
import org.jrdf.parser.ntriples.NTriplesParserFactory;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A simple factory for creating graphs given a file - everything is in memory by default.  Or a new MapFactory and
 * JRDF Factory can be given to make it persistent.
 */
public final class RdfReader {
    private LineHandlerFactory ntriplesFactory = new NTriplesParserFactory();
    private LineHandlerFactory n3Factory = new N3ParserFactory();
    private Graph graph = MemoryJRDFFactory.getFactory().getNewGraph();
    private MapFactory mapFactory = new MemMapFactory();

    public RdfReader() {
    }

    public RdfReader(final Graph newGraph, final MapFactory newMapFactory) {
        graph = newGraph;
        mapFactory = newMapFactory;
    }

    public Graph parseNTriples(final File file) {
        return parseNTriples(getInputStream(file));
    }

    public Graph parseNTriples(final InputStream stream) {
        final LineHandler lineHandler = ntriplesFactory.createParser(graph, mapFactory);
        final GraphLineParser lineParser = new GraphLineParser(graph, lineHandler);
        tryParse(lineParser, stream);
        return graph;
    }

    public Graph parseN3(final File file) {
        return parseN3(getInputStream(file));
    }

    public Graph parseN3(final InputStream stream) {
        final LineHandler lineHandler = n3Factory.createParser(graph, mapFactory);
        final GraphLineParser lineParser = new GraphLineParser(graph, lineHandler);
        tryParse(lineParser, stream);
        return graph;
    }

    public Graph parseRdfXml(final File file) {
        return parseRdfXml(getInputStream(file));
    }

    public Graph parseRdfXml(final InputStream stream) {
        final Parser parser = new GraphRdfXmlParser(graph, mapFactory);
        tryParse(parser, stream);
        return graph;
    }

    private InputStream getInputStream(final File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return new ByteArrayInputStream("".getBytes());
        }
    }

    private void tryParse(Parser parser, InputStream stream) {
        try {
            parser.parse(stream, "http://jrdf.sf.net/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
