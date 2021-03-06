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

package org.jrdf.query.server.local;

import org.jrdf.PersistentGlobalJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactoryImpl;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.server.GraphApplication;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.util.DirectoryHandler;
import org.restlet.Application;
import org.restlet.resource.ResourceException;

public class GraphApplicationImpl extends Application implements GraphApplication {
    private final DirectoryHandler handler;
    private final PersistentGlobalJRDFFactory factory;
    private final SparqlConnection sparqlConnection;
    private Answer answer;
    private boolean tooManyRows;
    private long maxRows;

    public GraphApplicationImpl(DirectoryHandler newHandler, SparqlConnection newConnection) {
        this.handler = newHandler;
        this.factory = PersistentGlobalJRDFFactoryImpl.getFactory(handler);
        this.sparqlConnection = newConnection;
    }

    public boolean hasGraph(String name) {
        return factory.hasGraph(name);
    }

    public MoleculeGraph getGraph(String name) {
        return factory.getGraph(name);
    }

    public MoleculeGraph getGraph() {
        return factory.getGraph();
    }

    public Answer answerQuery(String graphName, String queryString, long newMaxRows) throws ResourceException {
        final MoleculeGraph graph = getGraph(graphName);
        try {
            this.answer = sparqlConnection.executeQuery(graph, queryString);
            this.maxRows = newMaxRows;
            this.tooManyRows = answer.numberOfTuples() > newMaxRows;
            return answer;
        } catch (Exception e) {
            throw new ResourceException(e);
        } finally {
            graph.close();
        }
    }

    public long getTimeTaken() {
        return answer.getTimeTaken();
    }

    public boolean isTooManyRows() {
        return tooManyRows;
    }

    public void setMaxRows(long newMaxRows) {
        this.maxRows = newMaxRows;
    }

    public long getMaxRows() {
        return maxRows;
    }

    public String getGraphsDir() {
        return handler.getDir().getName();
    }
}
