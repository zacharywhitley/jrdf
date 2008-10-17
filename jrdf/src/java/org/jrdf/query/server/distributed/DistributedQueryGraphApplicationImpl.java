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

package org.jrdf.query.server.distributed;

import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.xml.AnswerXMLWriter;
import org.jrdf.query.client.DistributedQueryClientImpl;
import org.jrdf.query.client.GraphQueryClient;
import org.jrdf.query.client.NewDistributedQueryClientImpl;
import org.jrdf.query.server.GraphApplication;
import org.restlet.resource.ResourceException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class DistributedQueryGraphApplicationImpl implements DistributedQueryGraphApplication {
    private static final int PORT_NUMBER = 8182;
    private static final int INVALID_TIME_TAKEN = -1;
    private Set<String> servers;
    private int portNumber = PORT_NUMBER;
    private GraphQueryClient client;
    private AnswerXMLWriter xmlWriter;
    private GraphApplication application;

    public DistributedQueryGraphApplicationImpl(GraphApplication newApplication) {
        this.application = newApplication;
        this.servers = new HashSet<String>();
    }

    public void addServers(String... servers) {
        this.servers.addAll(asList(servers));
    }

    public void removeServers(String... servers) {
        this.servers.removeAll(asList(servers));
    }

    public String[] getServers() {
        return servers.toArray(new String[servers.size()]);
    }

    public Answer answerQuery2(String graphName, String queryString) throws ResourceException {
        answerQuery(graphName, queryString);
        return ((NewDistributedQueryClientImpl) client).getAnswer();
    }

    public void setPort(int port) {
        portNumber = port;
    }

    public int getPort() {
        return portNumber;
    }

    public String getGraphsDir() {
        return application.getGraphsDir();
    }

    public String getFormat() {
        return application.getFormat();
    }

    public void setFormat(String format) {
        application.setFormat(format);
    }

    public String getMaxRows() {
        return application.getMaxRows();
    }

    public void setMaxRows(String rows) {
        application.setMaxRows(rows);
    }

    public void close() {
        application.close();
    }

    public MoleculeGraph getGraph(String name) {
        return application.getGraph(name);
    }

    public MoleculeGraph getGraph() {
        return application.getGraph();
    }

    public void answerQuery(String graphName, String queryString) throws ResourceException {
        try {
            if (client == null) {
                client = new DistributedQueryClientImpl(portNumber, servers);
            }
            client.postQuery(graphName, queryString, null);
            client.executeQuery();
        } catch (Exception e) {
            throw new ResourceException(e);
        }
    }

    public AnswerXMLWriter getAnswerXMLWriter(Writer writer) throws XMLStreamException, IOException {
        xmlWriter = ((DistributedQueryClientImpl) client).getXMLWriter(writer);
        return xmlWriter;
    }

    public long getTimeTaken() {
        return INVALID_TIME_TAKEN;
    }

    public boolean isTooManyRows() {
        return application.isTooManyRows();
    }
}