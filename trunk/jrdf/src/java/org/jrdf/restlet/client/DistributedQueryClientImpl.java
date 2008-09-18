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

import org.jrdf.restlet.server.Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class DistributedQueryClientImpl implements DistributedQueryClient {
    private GraphClient[] clientImpls;
    private String[] serverAddresses;
    private int length;
    private ExecutorService executor;

    public DistributedQueryClientImpl(String... servers) {
        serverAddresses = servers;
        length = serverAddresses.length;
        executor = new ScheduledThreadPoolExecutor(length);
        clientImpls = new GraphClientImpl[length];
        for (int i = 0; i < length; i++) {
            clientImpls[i] = new GraphClientImpl(serverAddresses[i], Server.PORT);
        }
    }

    public String postQuery(String graphName, String queryString) {
        try {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                clientImpls[i].constructPostQuery(graphName, queryString);
            }
            for (int i = 0; i < length; i++) {
                System.err.println("Starting client: " + i);
                Future<String> future = executor.submit(clientImpls[i]);
                while (!future.isDone()) {
                    String answer = future.get(2, TimeUnit.SECONDS);
                    builder.append(answer);
                }
            }
            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            throw new RuntimeException("Empty server list.");
        }
        String[] servers = new String[args.length - 1];
        System.arraycopy(args, 1, servers, 0, args.length - 1);
        DistributedQueryClient client = new DistributedQueryClientImpl(servers);
        final String queryString = "select ?s where { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> " +
                "<http://www.biopax.org/release/biopax-level2.owl#physicalEntity> . }";
        client.postQuery("perstMoleculeGraph", queryString);
    }
}
