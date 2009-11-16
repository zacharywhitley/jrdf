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

package org.jrdf;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.query.server.SpringLocalServer;
import org.jrdf.query.client.CallableQueryClient;
import org.jrdf.query.client.CallableQueryClientImpl;
import org.jrdf.query.client.SparqlAnswerHandlerFactory;
import org.jrdf.query.client.SparqlAnswerHandlerFactoryImpl;
import org.jrdf.query.client.SparqlAnswerHandler;
import org.jrdf.query.answer.Answer;
import org.junit.Test;

import java.net.URI;
import static java.util.Collections.EMPTY_MAP;

/**
 * Class description goes here.
 */
public class FooTe {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler("perstMoleculeGraph");
    private static final PersistentGlobalJRDFFactory FACTORY = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
    private static final String SELECT_QUERY_STRING = "SELECT * WHERE { ?s ?p ?o. }";
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final String GRAPH_PATH = "/graph/";
    private static final String GRAPH = "foo";
    private static final URI LOCAL_SERVER_END_POINT = URI.create("http://" + LOCAL_HOST + ":" + 8182 + GRAPH_PATH +
        GRAPH);
    private static final SparqlAnswerHandlerFactory HANDLER_FACTORY = new SparqlAnswerHandlerFactoryImpl();
    private static final SparqlAnswerHandler ANSWER_HANDLER = HANDLER_FACTORY.createSparqlAnswerHandlerFactory();

    @Test
    public void testCreateGraph() {
        HANDLER.removeDir();
        final MoleculeGraph graph = FACTORY.getGraph("foo");
        GraphElementFactory elementFactory = graph.getElementFactory();
        final URIReference p = elementFactory.createURIReference(URI.create("urn:p"));
        final BlankNode b1 = elementFactory.createBlankNode();
        final BlankNode b2 = elementFactory.createBlankNode();
        graph.add(b1, p, b1);
        graph.add(b2, p, b2);
        for (Triple triple : graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE)) {
            System.err.println("Triple " + triple);
        }

    }

    @Test
    public void testStartServer() throws Exception {
        HANDLER.removeDir();
        final MoleculeGraph graph = FACTORY.getGraph("foo");
        GraphElementFactory elementFactory = graph.getElementFactory();
        final URIReference p = elementFactory.createURIReference(URI.create("urn:p"));
        final BlankNode b1 = elementFactory.createBlankNode();
        final BlankNode b2 = elementFactory.createBlankNode();
        graph.add(b1, p, b1);
        graph.add(b2, p, b2);
        for (Triple triple : graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE)) {
            System.err.println("Triple " + triple);
        }
        SpringLocalServer localQueryServer;
        localQueryServer = new SpringLocalServer();
        localQueryServer.start();
        CallableQueryClient queryClient = new CallableQueryClientImpl(LOCAL_SERVER_END_POINT, ANSWER_HANDLER);
        Answer answer = queryClient.executeQuery(SELECT_QUERY_STRING, EMPTY_MAP);
        localQueryServer.stop();
        Thread.sleep(360000);
    }

    @Test
    public void testOnly() throws Exception {
        SpringLocalServer localQueryServer;
        localQueryServer = new SpringLocalServer();
        localQueryServer.start();
        Thread.sleep(360000);
    }
}
