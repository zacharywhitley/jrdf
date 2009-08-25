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

import static org.jrdf.util.EqualsUtil.isNull;
import static org.jrdf.util.EqualsUtil.sameReference;

public final class ServerPort {
    private static final int PRIME = 31;
    private int port;
    private String hostname;

    public static ServerPort createServerPort(String serverPort, int defaultPort) {
        return new ServerPort(serverPort, defaultPort);
    }

    private ServerPort(String serverPort, int defaultPort) {
        String[] hostPort = serverPort.split(":");
        if (hostPort.length == 1) {
            this.port = defaultPort;
            this.hostname = serverPort;
        } else if (hostPort.length == 2) {
            this.port = Integer.parseInt(hostPort[1]);
            this.hostname = hostPort[0];
        } else {
            throw new IllegalArgumentException("Unable to parse for hostname and port: " + serverPort);
        }
    }

    public int getPort() {
        return port;
    }

    public String getHostname() {
        return hostname;
    }

    @Override
    public int hashCode() {
        int result = port;
        result = PRIME * result + (hostname != null ? hostname.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (sameReference(this, obj)) {
            return true;
        }
        try {
            ServerPort serverPort = (ServerPort) obj;
            return comparePort(serverPort) && compareHost(serverPort);
        } catch (ClassCastException cce) {
            return false;
        }
    }

    private boolean comparePort(ServerPort serverPort) {
        return port == serverPort.getPort();
    }

    private boolean compareHost(ServerPort serverPort) {
        return ((hostname != null && hostname.equals(serverPort.getHostname()) ||
            (hostname == null && serverPort.getHostname() == null)));
    }
}
