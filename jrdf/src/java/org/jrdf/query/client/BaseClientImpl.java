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

import static org.jrdf.query.MediaTypeExtensions.APPLICATION_SPARQL;
import static org.jrdf.query.server.BaseGraphResource.FORMAT;
import static org.jrdf.query.server.BaseGraphResource.FORMAT_XML;
import static org.jrdf.query.server.BaseGraphResource.NO_ROWS;
import static org.jrdf.query.server.BaseGraphResource.QUERY_STRING;
import org.restlet.data.ClientInfo;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import static org.restlet.data.Method.GET;
import static org.restlet.data.Method.POST;
import org.restlet.data.Preference;
import static org.restlet.data.Protocol.HTTP;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.resource.Representation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Arrays;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public abstract class BaseClientImpl implements GraphQueryClient {
    private static final String NEW_LINE = System.getProperty("line.separator");

    protected int serverPort;
    protected String serverString;

    public BaseClientImpl(int portNumber, String server) {
        serverPort = portNumber;
        serverString = server;
    }

    protected Request prepareGetRequest(String graphName, String queryString, String noRows) {
        Representation representation = new Form().getWebRepresentation();
        String requestURL = makeRequestString(graphName, queryString);
        Request request = new Request(GET, requestURL, representation);
        setAcceptedMediaTypes(request);
        return request;
    }

    protected Request preparePostRequest(String graphName, String queryString, String noRows)
        throws MalformedURLException {
        Form form = new Form();
        form.add(QUERY_STRING, queryString);
        form.add(FORMAT, FORMAT_XML);
        form.add(NO_ROWS, noRows);
        Representation representation = form.getWebRepresentation();
        String requestURL = makeRequestString(graphName);
        return new Request(POST, requestURL, representation);
    }

    private void setAcceptedMediaTypes(Request request) {
        ClientInfo clientInfo = new ClientInfo();
        Preference<MediaType> preference = new Preference<MediaType>(APPLICATION_SPARQL);
        clientInfo.setAcceptedMediaTypes(Arrays.<Preference<MediaType>>asList(preference));
        request.setClientInfo(clientInfo);
    }

    private String makeRequestString(String graphName) {
        Reference ref = new Reference(HTTP.getSchemeName(), serverString, serverPort, "/graphs/" + graphName, null,
            null);
        return ref.toString();
    }

    private String makeRequestString(String graphName, String queryString) {
        String query = "queryString=" + queryString;
        Reference ref = new Reference(HTTP.getSchemeName(), serverString, serverPort, "/graphs/" + graphName, query,
            null);
        return ref.toString();
    }

    public static String readFromInputStream(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader din = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = din.readLine()) != null) {
                sb.append(line + NEW_LINE);
            }
        } finally {
            stream.close();
        }
        return sb.toString();
    }

}
