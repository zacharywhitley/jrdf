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

package org.jrdf.query.client;

import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.SparqlStreamingAnswer;
import org.jrdf.query.answer.xml.SparqlAnswerParserStream;
import org.jrdf.query.answer.xml.SparqlAnswerParserStreamImpl;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class NewDistributedQueryClientImpl implements GraphQueryClient {
    private ExecutorService executor;
    private Collection<CallableGraphQueryClient> queryClients;
    private Collection<String> serverAddresses;
    private Set<Future<InputStream>> set;
    private SparqlAnswerParserStream xmlWriter;

    public NewDistributedQueryClientImpl(Collection<String> servers)
        throws XMLStreamException, InterruptedException {
        this.serverAddresses = servers;
        this.queryClients = new LinkedList<CallableGraphQueryClient>();
        for (String server : serverAddresses) {
            queryClients.add(new GraphClientImpl(server));
        }
        executor = new ScheduledThreadPoolExecutor(serverAddresses.size());
        xmlWriter = new SparqlAnswerParserStreamImpl();
    }


    public Answer getAnswer() {
        return new SparqlStreamingAnswer(xmlWriter);
    }

    public void postDistributedServer(String action, String servers) throws MalformedURLException {
    }

    public void getQuery(String graphName, String queryString, String noRows) {
        try {
            for (GraphQueryClient queryClient : queryClients) {
                queryClient.getQuery(graphName, queryString, noRows);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream executeQuery() throws IOException {
        checkNotNull(queryClients);
        set = new HashSet<Future<InputStream>>();
        executeQuries();
        aggregateResults();
        return null;
    }

    private void aggregateResults() {
        for (Future<InputStream> future : set) {
            try {
                final InputStream stream = future.get();
                xmlWriter.addStream(stream);
            } catch (Exception e) {
                cancelExecution(future);
            }
        }
    }

    private void executeQuries() {
        for (CallableGraphQueryClient queryClient : queryClients) {
            Future<InputStream> future = executor.submit(queryClient);
            set.add(future);
        }
    }

    public void cancelExecution() {
        for (Future<InputStream> future : set) {
            cancelExecution(future);
        }
    }

    private void cancelExecution(Future<InputStream> future) {
        future.cancel(true);
    }
}