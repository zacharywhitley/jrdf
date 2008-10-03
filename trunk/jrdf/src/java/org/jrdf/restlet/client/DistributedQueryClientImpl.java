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

package org.jrdf.restlet.client;

import org.jrdf.util.param.ParameterUtil;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class DistributedQueryClientImpl implements GraphQueryClient {
    private ExecutorService executor;
    private Collection<CallableGraphQueryClient> queryClients;
    private Collection<String> serverAddresses;
    private int localPort;
    private AnswerXMLAggregator aggregator;
    private Set<Future<String>> set;

    public DistributedQueryClientImpl(int localPort, Collection<String> servers) {
        this.localPort = localPort;
        serverAddresses = servers;
        queryClients = new LinkedList<CallableGraphQueryClient>();
        for (String server : serverAddresses) {
            queryClients.add(new GraphClientImpl(server, this.localPort));
        }
        executor = new ScheduledThreadPoolExecutor(serverAddresses.size());
        aggregator = new AnswerXMLDOMAggregator();
    }

    public void postDistributedServer(int port, String action, String servers) throws MalformedURLException {
    }

    public void postQuery(String graphName, String queryString, String noRows) {
        try {
            for (GraphQueryClient queryClient : queryClients) {
                queryClient.postQuery(graphName, queryString, noRows);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String executeQuery() throws IOException {
        ParameterUtil.checkNotNull(queryClients);
        StringBuilder builder = new StringBuilder();
        set = new HashSet<Future<String>>();
        try {
            executeQuries();
            aggregateResults();
            builder.append(aggregator.getXML());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    private void aggregateResults() throws InterruptedException, ExecutionException, IOException, SAXException {
        for (Future<String> future : set) {
            final String xmlAnswer = future.get();
            aggregator.aggregate(xmlAnswer);
        }
    }

    private void executeQuries() {
        for (CallableGraphQueryClient queryClient : queryClients) {
            System.err.println("Starting client: " + queryClient.toString());
            Future<String> future = executor.submit(queryClient);
            set.add(future);
        }
    }

    public void cancelExecution() {
        for (Future<String> future : set) {
            future.cancel(true);
        }
    }
}
