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

package org.jrdf.restlet.server;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Resource;
import org.jrdf.graph.Value;
import static org.jrdf.parser.Reader.parseNTriples;
import org.jrdf.restlet.ConfigurableRestletResource;
import org.jrdf.restlet.server.local.WebInterfaceGraphApplication;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.Models;
import org.jrdf.util.ModelsImpl;
import static org.jrdf.util.ModelsImpl.JRDF_NAMESPACE;
import org.restlet.Context;
import org.restlet.data.MediaType;
import static org.restlet.data.MediaType.TEXT_HTML;
import org.restlet.data.Request;
import org.restlet.data.Response;
import static org.restlet.data.Status.SERVER_ERROR_INTERNAL;
import static org.restlet.data.Status.SUCCESS_OK;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import static java.net.URI.create;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class GraphsResource extends ConfigurableRestletResource {
    private static final URI NAME = create(JRDF_NAMESPACE + "name");
    private static final URI ID = create(JRDF_NAMESPACE + "id");
    private static final DirectoryHandler HANDLER = BaseGraphApplication.getHandler();
    private Set<Resource> resources;
    private WebInterfaceGraphApplication application;
    private Configuration freemarkerConfig;

    @Override
    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
        String path = this.getRequest().getResourceRef().toString();
        if (!path.endsWith("/")) {
            path += "/";
        }
        refreshGraphsModel();
    }

    private void refreshGraphsModel() {
        final File file = new File(HANDLER.getDir(), "graphs.nt");
        final Graph modelsGraph = parseNTriples(file);
        final Models model = new ModelsImpl(modelsGraph);
        this.resources = model.getResources();
    }

    public void setGraphApplication(WebInterfaceGraphApplication newApplication) {
        this.application = newApplication;
    }

    public void setFreemarkerConfig(final Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public boolean allowPost() {
        return false;
    }

    @Override
    public void handleGet() {
        final String ss = getRequest().getResourceRef().getPath();
        if (ss != null && !"/graphs".equals(ss)) {
            getResponse().redirectPermanent("/graphs");
        } else {
            try {
                Map<String, String> map = populateIdNameMap();
                getResponse().setEntity(constructRepresentation(map));
                getResponse().setStatus(SUCCESS_OK);
            } catch (Exception e) {
                getResponse().setStatus(SERVER_ERROR_INTERNAL, e);
            }
        }
    }

    @Override
    public Representation represent(Variant variant) throws ResourceException {
        Map<String, String> map = populateIdNameMap();
        Representation rep = null;
        try {
            rep = constructRepresentation(map);
            getResponse().setStatus(SUCCESS_OK);
        } catch (IOException e) {
            getResponse().setStatus(SERVER_ERROR_INTERNAL, e, e.getMessage());
        }
        return rep;
    }

    private Map<String, String> populateIdNameMap() throws ResourceException {
        refreshGraphsModel();
        Map<String, String> idNameMap = new HashMap<String, String>();
        try {
            for (Resource resource : resources) {
                String id = getStringValue(resource, ID);
                String name = getStringValue(resource, NAME);
                idNameMap.put(id, name);
            }
        } catch (Exception e) {
            throw new ResourceException(SERVER_ERROR_INTERNAL, e);
        }
        return idNameMap;
    }

    private Representation constructRepresentation(Map<String, String> map) throws IOException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("dirName", application.getGraphsDir());
        root.put("graphs", map);
        root.put("rand", Math.random());
        Template template = freemarkerConfig.getTemplate("graphsPage.ftl");
        return new TemplateRepresentation(template, root, TEXT_HTML);
    }

    private String getStringValue(Resource resource, URI pred) throws GraphException {
        final ClosableIterator<ObjectNode> iterator = resource.getObjects(pred);
        try {
            String name = "";
            if (iterator.hasNext()) {
                name = ((Value) iterator.next()).getValue().toString();
            }
            return name;
        } finally {
            iterator.close();
        }
    }
}
