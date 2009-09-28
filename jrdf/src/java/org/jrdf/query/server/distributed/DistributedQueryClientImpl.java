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

package org.jrdf.query.server.distributed;

import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.SparqlStreamingAnswerFactory;
import org.jrdf.query.answer.SparqlStreamingAnswerFactoryImpl;
import org.jrdf.query.answer.StreamingAnswerSparqlParserImpl;
import org.jrdf.query.answer.StreamingAnswerSparqlParser;
import org.jrdf.query.client.CallableQueryClient;
import org.jrdf.query.client.CallableQueryClientImpl;
import org.jrdf.query.client.QueryClient;
import org.jrdf.query.client.ServerPort;
import org.jrdf.query.client.SparqlAnswerHandler;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import javax.xml.stream.XMLStreamException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author Yuan-Fang Li
 * @version :$
 */
public class DistributedQueryClientImpl implements QueryClient {
    private static final SparqlStreamingAnswerFactory SPARQL_ANSWER_STREAMING_FACTORY =
        new SparqlStreamingAnswerFactoryImpl();
    private ExecutorService executor;
    private Collection<CallableQueryClient> queryClients;
    private Set<Future<Answer>> answers;
    private StreamingAnswerSparqlParser multiAnswerParser;

    public DistributedQueryClientImpl(final Collection<ServerPort> servers, final SparqlAnswerHandler answerHandler)
        throws XMLStreamException, InterruptedException {
        this.queryClients = new LinkedList<CallableQueryClient>();
        for (final ServerPort server : servers) {
            final CallableQueryClient client = new CallableQueryClientImpl(server, answerHandler);
            this.queryClients.add(client);
        }
        this.executor = new ScheduledThreadPoolExecutor(servers.size());
        this.multiAnswerParser = new StreamingAnswerSparqlParserImpl();
    }

    public void setQuery(final String endPoint, final String queryString, final Map<String, String> ext) {
        for (final QueryClient queryClient : queryClients) {
            queryClient.setQuery(endPoint, queryString, ext);
        }
    }

    public Answer executeQuery() {
        checkNotNull(queryClients);
        this.answers = new HashSet<Future<Answer>>();
        executeQuries();
        aggregateResults();
        return SPARQL_ANSWER_STREAMING_FACTORY.createStreamingAnswer(multiAnswerParser);
    }

    public Answer executeQuery(final String endPoint, final String queryString, final Map<String, String> ext) {
        setQuery(endPoint, queryString, ext);
        return executeQuery();
    }

    public void cancelExecution() {
        for (final Future<Answer> future : answers) {
            cancelExecution(future);
        }
    }

    private void aggregateResults() {
        final long start = System.currentTimeMillis();
        for (final Future<Answer> future : answers) {
            try {
                final Answer answer = future.get();
                multiAnswerParser.addAnswer(answer);
            } catch (Exception e) {
                cancelExecution(future);
            }
        }
        System.out.println("Distributed querying time: " + (System.currentTimeMillis() - start));
    }

    private void executeQuries() {
        for (final CallableQueryClient queryClient : queryClients) {
            final Future<Answer> future = executor.submit(queryClient);
            answers.add(future);
        }
    }

    private void cancelExecution(final Future<Answer> future) {
        future.cancel(true);
    }
}