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

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.jrdf.query.server.ConfigurableRestletResource;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.restlet.data.Form;
import static org.restlet.data.MediaType.TEXT_HTML;
import org.restlet.data.Status;
import static org.restlet.data.Status.SUCCESS_OK;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class ServersResource extends ConfigurableRestletResource {
    private static final int PORT_NUMBER = 8182;
    private static final String DEFAULT_PORT_STRING = "defaultPort";
    private DistributedQueryGraphApplication application;
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

    public void setDistributedQueryGraphApplication(DistributedQueryGraphApplication newApplication) {
        this.application = newApplication;
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
            Configuration cfg = new Configuration();
            File resourcesDir = new File(new File(System.getProperty("user.dir")), "resources");
            cfg.setDirectoryForTemplateLoading(resourcesDir);
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            Map<String, String> root = new HashMap<String, String>();
            root.put(DEFAULT_PORT_STRING, Integer.toString(PORT_NUMBER));
            root.put(ACTION, "action");
            Template template = cfg.getTemplate("distributedStartPage.ftl");
            rep = new TemplateRepresentation(template, root, TEXT_HTML);
        } catch (IOException e) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
        }
        return rep;
    }

    public void acceptRepresentation(Representation representation) throws ResourceException {
        checkNotNull(representation);
        Form form = new Form(representation);
        final String servers = form.getFirstValue(SERVERS_STRING);
        final String action = form.getFirstValue("action");
        processServerList(servers, action);
        Representation rep = new StringRepresentation("Servers updated successfully", TEXT_HTML);
        getResponse().setEntity(rep);
        getResponse().setStatus(SUCCESS_OK);
    }

    private void processServerList(String servers, String action) {
        checkNotNull(servers, action);
        StringTokenizer tokenizer = new StringTokenizer(servers);
        if ("add".equalsIgnoreCase(action)) {
            while (tokenizer.hasMoreTokens()) {
                final String server = tokenizer.nextToken();
                application.addServers(server);
            }
        } else if ("remove".equalsIgnoreCase(action)) {
            while (tokenizer.hasMoreTokens()) {
                final String server = tokenizer.nextToken();
                application.removeServers(server);
            }
        }
    }
}