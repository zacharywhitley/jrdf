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

import static org.jrdf.query.MediaTypeExtensions.*;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.SparqlStreamingAnswer;
import static org.jrdf.query.client.ServerPort.*;
import static org.jrdf.util.param.ParameterUtil.*;
import org.restlet.Client;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import static org.restlet.data.Method.*;
import org.restlet.data.Preference;
import static org.restlet.data.Protocol.*;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class QueryClientImpl implements CallableGraphQueryClient {
    private static final int DEFAULT_PORT = 8182;
    private Client client;
    private Request request;
    private ServerPort serverPort;

    public QueryClientImpl(String server) {
        this.serverPort = createServerPort(server, DEFAULT_PORT);
        this.client = new Client(HTTP);
    }

    public InputStream call() throws Exception {
        return getRepresentation().getStream();
    }

    public void getQuery(String graphName, String queryString, String noRows) {
        request = prepareGetRequest(graphName, queryString);
    }

    public Answer executeQuery() throws IOException {
        return tryGetAnswer(getRepresentation());
    }

    private Representation getRepresentation() {
        checkNotNull(client, request);
        setAcceptedMediaTypes(request);
        Response response = client.handle(request);
        final Status status = response.getStatus();
        if (status.isSuccess()) {
            return response.getEntity();
        } else {
            throw new RuntimeException(status.getThrowable());
        }
    }

    protected Request prepareGetRequest(String graphName, String queryString) {
        String requestURL = makeRequestString(graphName, queryString);
        Request request = new Request(GET, requestURL);
        setAcceptedMediaTypes(request);
        return request;
    }

    private Answer tryGetAnswer(Representation output) throws IOException {
        try {
            return new SparqlStreamingAnswer(output.getStream());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private void setAcceptedMediaTypes(Request theRequest) {
        ClientInfo clientInfo = theRequest.getClientInfo();
        List<Preference<MediaType>> preferenceList = new ArrayList<Preference<MediaType>>();
        preferenceList.add(new Preference<MediaType>(APPLICATION_SPARQL));
        clientInfo.setAcceptedMediaTypes(preferenceList);
    }

    private String makeRequestString(String graphName, String queryString) {
        Reference ref = new Reference(HTTP.getSchemeName(), serverPort.getHostname(), serverPort.getPort(),
            "/graphs/" + graphName, null, null);
        ref.addQueryParameter("query", queryString);
        return ref.toString();
    }
}
