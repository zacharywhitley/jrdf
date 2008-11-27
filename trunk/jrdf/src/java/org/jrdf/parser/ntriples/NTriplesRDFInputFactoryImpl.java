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

package org.jrdf.parser.ntriples;

import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.parser.ParseErrorListener;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.parser.RDFEventReader;
import org.jrdf.parser.RDFInputFactory;
import org.jrdf.parser.bnodefactory.ParserBlankNodeFactoryImpl;
import org.jrdf.parser.ntriples.parser.BlankNodeParser;
import org.jrdf.parser.ntriples.parser.LiteralParser;
import org.jrdf.parser.ntriples.parser.NodeMaps;
import org.jrdf.parser.ntriples.parser.NodeMapsImpl;
import org.jrdf.parser.ntriples.parser.NodeParsersFactory;
import org.jrdf.parser.ntriples.parser.NodeParsersFactoryImpl;
import org.jrdf.parser.ntriples.parser.RegexTripleParser;
import org.jrdf.parser.ntriples.parser.RegexTripleParserImpl;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.parser.ntriples.parser.TripleParserImpl;
import org.jrdf.parser.ntriples.parser.URIReferenceParser;
import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

public class NTriplesRDFInputFactoryImpl implements RDFInputFactory {
    private static final RDFInputFactory FACTORY = new NTriplesRDFInputFactoryImpl();
    private static final RegexMatcherFactory REGEX_MATCHER_FACTORY = new RegexMatcherFactoryImpl();
    private URIReferenceParser uriReferenceParser;
    private BlankNodeParser blankNodeParser;
    private LiteralParser literalParser;
    private RegexTripleParser regexTripleFactory;

    public RDFEventReader createRDFEventReader(InputStream stream, URI baseURI, Graph graph) {
        ParserBlankNodeFactory parserBlankNodeFactory = new ParserBlankNodeFactoryImpl(new MemMapFactory(),
                graph.getElementFactory());
        return createRDFEventReader(stream, baseURI, graph, parserBlankNodeFactory);
    }

    public RDFEventReader createRDFEventReader(Reader reader, URI baseURI, Graph graph) {
        ParserBlankNodeFactory parserBlankNodeFactory = new ParserBlankNodeFactoryImpl(new MemMapFactory(),
                graph.getElementFactory());
        return createRDFEventReader(reader, baseURI, graph, parserBlankNodeFactory);
    }

    public RDFEventReader createRDFEventReader(InputStream stream, URI baseURI, Graph graph,
        ParserBlankNodeFactory blankNodeFactory) {
        init(graph, blankNodeFactory);
        TripleParser tripleParser = new TripleParserImpl(REGEX_MATCHER_FACTORY, blankNodeParser, regexTripleFactory);
        return new NTriplesEventReader(stream, baseURI, tripleParser);
    }

    public RDFEventReader createRDFEventReader(Reader reader, URI baseURI, Graph graph,
        ParserBlankNodeFactory blankNodeFactory) {
        init(graph, blankNodeFactory);
        TripleParser tripleParser = new TripleParserImpl(REGEX_MATCHER_FACTORY, blankNodeParser, regexTripleFactory);
        return new NTriplesEventReader(reader, baseURI, tripleParser);
    }

    public ParseErrorListener getRDFReporter() {
        return null;
    }

    public static RDFInputFactory newInstance() {
        return FACTORY;
    }

    private void init(Graph graph, ParserBlankNodeFactory blankNodeFactory) {
        final NodeParsersFactory parsersFactory = new NodeParsersFactoryImpl(graph, new MemMapFactory());
        uriReferenceParser = parsersFactory.getUriReferenceParser();
        blankNodeParser = parsersFactory.getBlankNodeParserWithFactory(blankNodeFactory);
        literalParser = parsersFactory.getLiteralParser();
        final NodeMaps nodeMaps = new NodeMapsImpl(uriReferenceParser, blankNodeParser, literalParser);
        regexTripleFactory = new RegexTripleParserImpl(REGEX_MATCHER_FACTORY, graph.getTripleFactory(), nodeMaps);
    }
}
