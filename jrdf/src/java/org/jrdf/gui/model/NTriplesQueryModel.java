/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.gui.model;

import org.jrdf.collection.MapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.GraphFactory;
import org.jrdf.parser.GraphStatementHandler;
import org.jrdf.parser.line.LineHandler;
import org.jrdf.parser.line.LineHandlerFactory;
import org.jrdf.parser.line.LineParser;
import org.jrdf.parser.line.LineParserImpl;
import org.jrdf.parser.n3.N3ParserFactoryImpl;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.answer.Answer;
import org.jrdf.urql.UrqlConnection;
import static org.jrdf.util.EscapeURL.toEscapedString;

import java.net.URL;

public class NTriplesQueryModel implements QueryModel {
    private final GraphFactory graphFactory;
    private final UrqlConnection connection;
    private final MapFactory mapFactory;
    private Graph graph;

    public NTriplesQueryModel(final GraphFactory newGraphFactory, final UrqlConnection newConnection,
        final MapFactory newMapFactory) {
        graphFactory = newGraphFactory;
        connection = newConnection;
        mapFactory = newMapFactory;
    }

    public Graph loadModel(URL url) {
        try {
            graph = graphFactory.getGraph();
            parse(graph, url);
            return graph;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Answer performQuery(String query) throws GraphException, InvalidQuerySyntaxException {
        return connection.executeQuery(graph, query);
    }

    // TODO N3 Changes - detect RDF type based on file extension and mime type.
    private void parse(Graph graph, URL url) throws Exception {
        graph.clear();
        LineHandlerFactory parserFactory = new N3ParserFactoryImpl();
        LineHandler nTriplesParser = parserFactory.createParser(graph, mapFactory);
        final LineParser parser = new LineParserImpl(nTriplesParser);
        parser.setStatementHandler(new GraphStatementHandler(graph));
        parser.parse(url.openStream(), toEscapedString(url));
    }
}
