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

import static org.jrdf.restlet.server.distributed.DistributedQueryResource.PORT_STRING;
import static org.jrdf.restlet.server.distributed.DistributedQueryResource.ACTION;
import org.jrdf.restlet.server.distributed.DistributedQueryResource;
import static org.jrdf.restlet.server.local.LocalQueryServer.PORT;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.restlet.Client;
import org.restlet.data.Form;
import static org.restlet.data.Protocol.HTTP;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.Representation;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class GraphClientImpl extends BaseClientImpl implements CallableGraphQueryClient {
    private Client client;
    protected Request request;
    protected String answer;

    public GraphClientImpl(String server, int portNumber) {
        super(portNumber, server);
        client = new Client(HTTP);
    }

    public String call() throws Exception {
        return executeQuery();
    }

    public void postQuery(String graphName, String queryString, String noRows) throws IOException {
        request = preparePostRequest(graphName, queryString, noRows);
    }

    public void postDistributedServer(int port, String action, String servers) throws MalformedURLException {
        checkNotNull(port, servers);
        Form form = new Form();
        form.add(PORT_STRING, Integer.toString(port));
        form.add(ACTION, action);
        form.add(DistributedQueryResource.SERVERS_STRING, servers);
        Representation representation = form.getWebRepresentation();
        URL url = new URL(HTTP.getSchemeName(), serverString, serverPort, "/");
        String requestURL = url.toString();
        Request request = new Request(Method.POST, requestURL, representation);
        Response response = client.handle(request);
        if (!response.getStatus().isSuccess()) {
            System.err.println("Error: " + response.getStatus().toString());
            throw new RuntimeException(response.getStatus().toString());
        }
    }

    public String executeQuery() throws IOException {
        checkNotNull(client, request);
        Response response = client.handle(request);
        final Status status = response.getStatus();
        if (status.isSuccess()) {
            Representation output = response.getEntity();
            final String answer = output.getText();
            System.err.println("answer = " + answer);
            return answer;
        } else {
            System.err.println("Error: " + status.toString());
            throw new RuntimeException(status.getThrowable());
        }
    }

    public static void main(String[] args) throws Exception {
        CallableGraphQueryClient queryClient = new GraphClientImpl("127.0.0.1", PORT);
        queryClient.postQuery("foo", "SELECT * WHERE { ?s ?p ?o. }", "all");
        String answer = queryClient.call();
        System.err.println("Answer = " + answer);
    }
}
