/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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

package org.jrdf.parser.ntriples;

import org.jrdf.graph.Graph;
import org.jrdf.parser.ParseErrorListener;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.parser.RDFEventReader;
import org.jrdf.parser.RDFInputFactory;
import org.jrdf.parser.ntriples.parser.BlankNodeParser;
import org.jrdf.parser.ntriples.parser.BlankNodeParserImpl;
import org.jrdf.parser.ntriples.parser.LiteralParser;
import org.jrdf.parser.ntriples.parser.LiteralParserImpl;
import org.jrdf.parser.ntriples.parser.ObjectParser;
import org.jrdf.parser.ntriples.parser.ObjectParserImpl;
import org.jrdf.parser.ntriples.parser.PredicateParser;
import org.jrdf.parser.ntriples.parser.PredicateParserImpl;
import org.jrdf.parser.ntriples.parser.SubjectParser;
import org.jrdf.parser.ntriples.parser.SubjectParserImpl;
import org.jrdf.parser.ntriples.parser.URIReferenceParser;
import org.jrdf.parser.ntriples.parser.URIReferenceParserImpl;
import org.jrdf.parser.ntriples.parser.LiteralUtil;
import org.jrdf.parser.ntriples.parser.LiteralUtilImpl;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;
import org.jrdf.util.boundary.RegexMatcherFactory;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

/**
 * Class description goes here.
 */
public class NTriplesRDFInputFactoryImpl implements RDFInputFactory {
    private static final RDFInputFactory FACTORY = new NTriplesRDFInputFactoryImpl();
    private static final RegexMatcherFactory REGEX_MATCHER_FACTORY = new RegexMatcherFactoryImpl();
    private SubjectParser subjectParser;
    private PredicateParser predicateParser;
    private ObjectParser objectParser;

    public RDFEventReader createRDFEventReader(InputStream stream, URI baseURI, Graph graph,
            ParserBlankNodeFactory blankNodeFactory) {
        init(graph, blankNodeFactory);
        return new NTriplesEventReader(stream, baseURI, graph.getTripleFactory(), subjectParser, predicateParser,
                objectParser, REGEX_MATCHER_FACTORY);
    }

    public RDFEventReader createRDFEventReader(Reader reader, URI baseURI, Graph graph,
            ParserBlankNodeFactory blankNodeFactory) {
        init(graph, blankNodeFactory);
        return new NTriplesEventReader(reader, baseURI, graph.getTripleFactory(), subjectParser, predicateParser,
                objectParser, REGEX_MATCHER_FACTORY);
    }

    public ParseErrorListener getRDFReporter() {
        return null;
    }

    public static RDFInputFactory newInstance() {
        return FACTORY;
    }

    public static RDFInputFactory newInstance(String factoryId, ClassLoader classLoader) {
        throw new UnsupportedOperationException();
    }

    private void init(Graph graph, ParserBlankNodeFactory blankNodeFactory) {
        URIReferenceParser referenceParser = new URIReferenceParserImpl(graph.getElementFactory());
        BlankNodeParser blankNodeParser = new BlankNodeParserImpl(blankNodeFactory);
        RegexMatcherFactory matcherFactory = new RegexMatcherFactoryImpl();
        LiteralUtil literalUtil = new LiteralUtilImpl(matcherFactory);
        LiteralParser literalParser = new LiteralParserImpl(graph.getElementFactory(), matcherFactory, literalUtil);
        subjectParser = new SubjectParserImpl(referenceParser, blankNodeParser);
        predicateParser = new PredicateParserImpl(referenceParser);
        objectParser = new ObjectParserImpl(referenceParser, blankNodeParser, literalParser);
    }
}
