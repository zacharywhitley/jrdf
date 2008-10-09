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

package org.jrdf.restlet.server.local;

import org.jrdf.PersistentGlobalJRDFFactory;
import org.jrdf.PersistentGlobalJRDFFactoryImpl;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.query.Answer;
import org.jrdf.query.xml.AnswerXMLWriter;
import org.jrdf.query.QueryFactory;
import org.jrdf.query.QueryFactoryImpl;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.restlet.server.BaseGraphApplication;
import org.jrdf.restlet.server.GraphsResource;
import org.jrdf.urql.UrqlConnectionImpl;
import org.jrdf.urql.builder.QueryBuilder;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.resource.ResourceException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;

public class WebInterfaceGraphApplication extends BaseGraphApplication {
    private static final String LOCAL_SERVER = "127.0.0.1";
    /**
     * The max no. of rows of answers that will be transfomred into xml.
     */
    public static final int MAX_ROWS = 1000;

    private static final PersistentGlobalJRDFFactory FACTORY = PersistentGlobalJRDFFactoryImpl.getFactory(HANDLER);
    private static final QueryFactory QUERY_FACTORY = new QueryFactoryImpl();
    private static final QueryEngine QUERY_ENGINE = QUERY_FACTORY.createQueryEngine();
    private static final QueryBuilder BUILDER = QUERY_FACTORY.createQueryBuilder();

    private final String[] serverAddresses;
    private UrqlConnectionImpl urqlConnection;
    private Answer answer;
    private AnswerXMLWriter xmlWriter;
    private boolean tooManyRows;

    public WebInterfaceGraphApplication() {
        this.urqlConnection = new UrqlConnectionImpl(BUILDER, QUERY_ENGINE);
        serverAddresses = new String[]{LOCAL_SERVER};
    }

    public WebInterfaceGraphApplication(String[] serverAddresses) {
        this.urqlConnection = new UrqlConnectionImpl(BUILDER, QUERY_ENGINE);
        this.serverAddresses = serverAddresses;
    }

    @Override
    public synchronized Restlet createRoot() {
        Router router = new Router(getContext());
        router.attach("/graphs", GraphsResource.class);
        router.attach("/graphs/{graph}", LocalGraphResource.class);
        router.attachDefault(GraphsResource.class);
        return router;
    }

    public void close() {
        FACTORY.close();
    }

    public MoleculeGraph getGraph(String name) {
        return FACTORY.getGraph(name);
    }

    public MoleculeGraph getGraph() {
        return FACTORY.getGraph();
    }

    public String[] getServers() {
        return serverAddresses;
    }

    public void answerQuery(String graphName, String queryString) throws ResourceException {
        try {
            final MoleculeGraph graph = getGraph(graphName);
            answer = urqlConnection.executeQuery(graph, queryString);
            tooManyRows = answer.numberOfTuples() > MAX_ROWS;
        } catch (Exception e) {
            throw new ResourceException(e);
        }
    }

    public long getTimeTaken() {
        return answer.getTimeTaken();
    }

    public boolean isTooManyRows() {
        return tooManyRows;
    }

    public AnswerXMLWriter getAnswerXMLWriter(Writer writer) throws XMLStreamException, IOException {
        if (tooManyRows) {
            xmlWriter = answer.getXMLWriter(writer, MAX_ROWS);
        } else {
            xmlWriter = answer.getXMLWriter(writer);
        }
        return xmlWriter;
    }
}
