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

import org.jrdf.query.server.ConfigurableRestletResource;
import org.jrdf.query.server.FreemarkerRepresentationFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import static org.restlet.data.Status.SUCCESS_OK;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version :$
 */
public class ServersResource extends ConfigurableRestletResource {
    private static final int LOCAL_PORT_NUMBER = 8182;
    private static final String DEFAULT_PORT_STRING = "defaultPort";
    private DistributedQueryGraphApplication application;
    private FreemarkerRepresentationFactory factory;
    /**
     * The name of the radio button for action.
     */
    public static final String ACTION = "action";
    /**
     * The form name for the servers.
     */
    public static final String SERVERS_STRING = "serversString";

    /**
     * The form name for the port number.
     */
    public static final String PORT_STRING = "port";

    private static final String SERVER_LIST = "serverList";

    public void setDistributedQueryGraphApplication(DistributedQueryGraphApplication newApplication) {
        this.application = newApplication;
    }

    public void setResultRepresentation(FreemarkerRepresentationFactory newFactory) {
        this.factory = newFactory;
    }

    public boolean allowGet() {
        return true;
    }

    public boolean allowPost() {
        return true;
    }

    public Representation represent(Variant variant) {
        Representation rep = null;
        try {
            Map<String, Object> dataModel = new HashMap<String, Object>();
            dataModel.put(DEFAULT_PORT_STRING, Integer.toString(LOCAL_PORT_NUMBER));
            dataModel.put(ACTION, ACTION);
            final String[] servers = application.getServers();
            dataModel.put(SERVER_LIST, servers);
            return createTemplateRepresentation(variant.getMediaType(), dataModel);
        } catch (Exception e) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e, e.getMessage().replace("\n", ""));
        }
        return rep;
    }

    public void acceptRepresentation(Representation representation) throws ResourceException {
        checkNotNull(representation);
        Form form = new Form(representation);
        final String servers = form.getFirstValue(SERVERS_STRING);
        final String action = form.getFirstValue("action");
        processServerList(servers, action);
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put(SERVER_LIST, application.getServers());
        Representation rep = factory.createRepresentation(MediaType.TEXT_HTML, dataModel);
        getResponse().setEntity(rep);
        getResponse().setStatus(SUCCESS_OK);
    }

    private void processServerList(String servers, String action) {
        checkNotNull(servers, action);
        final String[] strings = servers.split("[\\s,;]");
        for (String server : strings) {
            if (server.length() > 0) {
                processServerString(server, action);
            }
        }
    }

    private void processServerString(String server, String action) {
        if ("add".equalsIgnoreCase(action)) {
            application.addServers(server);
        } else if ("remove".equalsIgnoreCase(action)) {
            application.removeServers(server);
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Unknown action on server list: " + action);
        }
    }
}