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

package org.jrdf.query.client;

import static org.jrdf.query.MediaTypeExtensions.APPLICATION_SPARQL_XML;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.SparqlStreamingAnswerFactory;
import org.jrdf.query.answer.SparqlStreamingAnswerFactoryImpl;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.restlet.Client;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import static org.restlet.data.Method.GET;
import org.restlet.data.Preference;
import static org.restlet.data.Protocol.HTTP;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuan-Fang Li
 * @version :$
 */
public final class QueryClientImpl implements CallableGraphQueryClient {
    private static final SparqlStreamingAnswerFactory SPARQL_ANSWER_STREAMING_FACTORY =
        new SparqlStreamingAnswerFactoryImpl();
    private final Client client;
    private final ServerPort serverPort;
    private Request request;

    public QueryClientImpl(final ServerPort serverPort) {
        this.serverPort = serverPort;
        this.client = new Client(HTTP);
    }

    public void setQuery(String graphName, String queryString, long noRows) {
        String requestURL = makeRequestString(graphName, queryString, noRows);
        request = new Request(GET, requestURL);
        setAcceptedMediaTypes();
    }

    public Answer executeQuery() {
        return tryGetAnswer(getRepresentation());
    }

    public Answer executeQuery(String graphName, String queryString, long noRows) {
        setQuery(graphName, queryString, noRows);
        return executeQuery();
    }

    public InputStream call() throws Exception {
        return getRepresentation().getStream();
    }

    public String toString() {
        return serverPort.getHostname() + ":" + serverPort.getPort();
    }

    private Representation getRepresentation() {
        checkNotNull(client, request);
        setAcceptedMediaTypes();
        Response response = client.handle(request);
        final Status status = response.getStatus();
        if (status.isSuccess()) {
            return response.getEntity();
        } else {
            status.getThrowable().printStackTrace();
            throw new RuntimeException(status.getThrowable());
        }
    }

    private Answer tryGetAnswer(Representation output) {
        try {
            return SPARQL_ANSWER_STREAMING_FACTORY.createStreamingAnswer(output.getStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setAcceptedMediaTypes() {
        ClientInfo clientInfo = request.getClientInfo();
        List<Preference<MediaType>> preferenceList = new ArrayList<Preference<MediaType>>();
        preferenceList.add(new Preference<MediaType>(APPLICATION_SPARQL_XML));
        clientInfo.setAcceptedMediaTypes(preferenceList);
    }

    private String makeRequestString(String graphName, String queryString, long noRows) {
        Reference ref = new Reference(HTTP.getSchemeName(), serverPort.getHostname(), serverPort.getPort(),
            "/graphs/" + graphName, null, null);
        ref.addQueryParameter("query", queryString);
        ref.addQueryParameter("maxRows", Long.toString(noRows));
        return ref.toString();
    }
}
