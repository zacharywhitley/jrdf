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

import org.jrdf.query.xml.AnswerXMLWriter;
import org.jrdf.query.client.DistributedQueryClientImpl;
import org.jrdf.query.client.GraphQueryClient;
import org.jrdf.query.server.BaseGraphApplication;
import org.jrdf.query.server.ListGraphsResource;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.resource.ResourceException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class DistributedQueryGraphApplication extends BaseGraphApplication {
    private static final int PORT_NUMBER = 8182;
    private Set<String> servers;
    private int portNumber = PORT_NUMBER;
    private GraphQueryClient client;
    private AnswerXMLWriter xmlWriter;
    private static final int INVALID_TIME_TAKEN = -1;

    public DistributedQueryGraphApplication() {
        servers = new HashSet<String>();
    }

    public void addServers(String... servers) {
        for (String server : servers) {
            this.servers.add(server);
        }
    }

    public void removeServers(String... servers) {
        for (String server : servers) {
            this.servers.remove(server);
        }
    }

    public Collection<String> getServers() {
        return servers;
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

    public void setPort(int port) {
        portNumber = port;
    }

    public int getPort() {
        return portNumber;
    }

    public synchronized Restlet createRoot() {
        Router router = new Router(getContext());
        router.attach("/", DistributedQueryResource.class);
        router.attach("/graphs", ListGraphsResource.class);
        router.attach("/graphs/{graph}", DistributedGraphResource.class);
        router.attachDefault(DistributedQueryResource.class);
        return router;
    }

    public AnswerXMLWriter getAnswerXMLWriter(Writer writer) throws XMLStreamException, IOException {
        xmlWriter = ((DistributedQueryClientImpl) client).getXMLWriter(writer);
        return xmlWriter;
    }

    public long getTimeTaken() {
        return INVALID_TIME_TAKEN;
    }
}
