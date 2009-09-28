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

package org.jrdf.query.client;

import org.jrdf.query.answer.Answer;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.restlet.Client;
import static org.restlet.data.Method.GET;
import static org.restlet.data.Protocol.HTTP;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version :$
 */
public final class QueryClientImpl implements QueryClient {
    private final Client client;
    private final ServerPort serverPort;
    private final SparqlAnswerHandler answerHandler;
    private Request request;

    public QueryClientImpl(final ServerPort serverPort, final SparqlAnswerHandler answerHandler) {
        this.serverPort = serverPort;
        this.answerHandler = answerHandler;
        this.client = new Client(HTTP);
    }

    public void setQuery(final String endPoint, final String queryString, final Map<String, String> ext) {
        final HashMap<String, String> map = new HashMap<String, String>(ext);
        map.put("query", queryString);
        final String requestURL = makeRequestString(endPoint, map);
        request = new Request(GET, requestURL);
        answerHandler.setAcceptedMediaTypes(request);
    }

    public Answer executeQuery() {
        return answerHandler.getAnswer(getRepresentation());
    }

    public Answer executeQuery(final String endPoint, final String queryString, final Map<String, String> ext) {
        setQuery(endPoint, queryString, ext);
        return executeQuery();
    }

    public String toString() {
        return request.toString();
    }

    private Representation getRepresentation() {
        checkNotNull(client, request);
        answerHandler.setAcceptedMediaTypes(request);
        final Response response = client.handle(request);
        final Status status = response.getStatus();
        if (status.isSuccess()) {
            return response.getEntity();
        } else {
            return handleFail(status);
        }
    }

    private Representation handleFail(Status status) {
        if (status.getThrowable() != null) {
            throw new RuntimeException(status.getThrowable());
        } else {
            throw new RuntimeException(status.getDescription() + " (" + status.getCode() + ") from: " +
                request.getResourceRef());
        }
    }

    private String makeRequestString(final String graphName, final Map<String, String> queryParameters) {
        final Reference ref = new Reference(HTTP.getSchemeName(), serverPort.getHostname(), serverPort.getPort(),
            graphName, null, null);
        for (final String key : queryParameters.keySet()) {
            ref.addQueryParameter(key, queryParameters.get(key));
        }
        return ref.toString();
    }
}