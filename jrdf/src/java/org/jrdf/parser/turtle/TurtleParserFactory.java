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

package org.jrdf.parser.turtle;

import org.jrdf.collection.MapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.parser.NamespaceListener;
import org.jrdf.parser.line.LineHandler;
import org.jrdf.parser.line.LineHandlerFactory;
import org.jrdf.parser.line.TriplesParserImpl;
import org.jrdf.parser.mem.MemNamespaceListener;
import org.jrdf.parser.ntriples.parser.CommentsParserImpl;
import org.jrdf.parser.ntriples.parser.NodeMaps;
import org.jrdf.parser.ntriples.parser.RegexTripleParser;
import org.jrdf.parser.ntriples.parser.RegexTripleParserImpl;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.parser.turtle.parser.NamespaceAwareNodeMaps;
import org.jrdf.parser.turtle.parser.NamespaceAwareNodeParsersFactory;
import org.jrdf.parser.turtle.parser.NamespaceAwareNodeParsersFactoryImpl;
import org.jrdf.parser.turtle.parser.NamespaceAwareTripleParser;
import org.jrdf.parser.turtle.parser.PrefixParserImpl;
import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;

public class TurtleParserFactory implements LineHandlerFactory {
    public LineHandler createParser(final Graph newGraph, final MapFactory mapFactory) {
        final RegexMatcherFactory matcherFactory = new RegexMatcherFactoryImpl();
        final NamespaceListener listener = new MemNamespaceListener();
        final NamespaceAwareNodeParsersFactory parsersFactory = new NamespaceAwareNodeParsersFactoryImpl(newGraph,
            mapFactory, matcherFactory, listener);
        final NodeMaps nodeMaps = new NamespaceAwareNodeMaps(parsersFactory.getUriReferenceParser(),
            parsersFactory.getBlankNodeParser(), parsersFactory.getLiteralParser());
        final RegexTripleParser parser = new RegexTripleParserImpl(matcherFactory, newGraph.getTripleFactory(),
            nodeMaps);
        final TripleParser tripleParser = new NamespaceAwareTripleParser(matcherFactory,
            parsersFactory.getBlankNodeParser(), parser);
        return new TurtleParser(new CommentsParserImpl(matcherFactory),
            new PrefixParserImpl(matcherFactory, listener),
            new TriplesParserImpl(tripleParser));
    }
}
