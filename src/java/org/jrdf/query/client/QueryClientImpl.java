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
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class QueryClientImpl implements QueryClient {
    private final URI endPointUri;
    private Client client;
    private final SparqlAnswerHandler answerHandler;
    private Request request;

    public QueryClientImpl(final URI newEndPointUri, final SparqlAnswerHandler newAnswerHandler) {
        checkNotNull(newEndPointUri, newAnswerHandler);
        this.endPointUri = newEndPointUri;
        this.client = new Client(newEndPointUri.getScheme());
        this.answerHandler = newAnswerHandler;
    }

    public void setQuery(final String sparqlQuery, final Map<String, String> queryExtensions) {
        checkNotNull(sparqlQuery, queryExtensions);
        final HashMap<String, String> map = new HashMap<String, String>(queryExtensions);
        map.put("query", sparqlQuery);
        request = new Request(GET, createReferenceFromQuery(map));
        answerHandler.setAcceptedMediaTypes(request);
    }

    public Answer executeQuery() {
        if (request == null) {
            throw new IllegalStateException("No query to execute, call setQuery first");
        }
        return answerHandler.getAnswer(getRepresentation());
    }

    public Answer executeQuery(final String queryString, final Map<String, String> ext) {
        checkNotNull(queryString, ext);
        setQuery(queryString, ext);
        return executeQuery();
    }

    public String toString() {
        return request.getClientInfo().getAddress();
    }

    private Reference createReferenceFromQuery(final Map<String, String> queryParameters) {
        validateEndPointUri();
        final Reference ref = new Reference(endPointUri.getScheme(), endPointUri.getHost(), endPointUri.getPort(),
            endPointUri.getPath(), null, null);
        for (final Map.Entry<String, String> entry : queryParameters.entrySet()) {
            ref.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return ref;
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

    private void validateEndPointUri() {
        if (!endPointUri.isAbsolute()) {
            throw new IllegalArgumentException("The SPARQL end point must have a scheme.  Given URI: " + endPointUri);
        }
    }

    private Representation handleFail(final Status status) {
        final Throwable throwable = status.getThrowable();
        if (throwable != null) {
            throw new RuntimeException(throwable);
        } else {
            throw new RuntimeException(status.getDescription() + " (" + status.getCode() + ") from: " +
                request.getResourceRef());
        }
    }
}