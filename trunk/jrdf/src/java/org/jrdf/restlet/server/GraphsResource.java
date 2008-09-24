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

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import static org.jrdf.parser.Reader.parseNTriples;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.Models;
import org.jrdf.util.ModelsImpl;
import static org.jrdf.util.ModelsImpl.JRDF_NAMESPACE;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import static org.restlet.data.Status.SERVER_ERROR_INTERNAL;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import java.io.File;
import java.net.URI;
import static java.net.URI.create;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class GraphsResource extends Resource {
    private static final URI NAME = create(JRDF_NAMESPACE + "name");
    private static final URI ID = create(JRDF_NAMESPACE + "id");

    private Models model;
    private Set<org.jrdf.graph.Resource> resources;
    private DirectoryHandler handler;
    protected String path;

    public GraphsResource(Context context, Request request, Response response) {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
        path = this.getRequest().getResourceRef().toString();
        if (!path.endsWith("/")) {
            path += "/";
        }
        handler = BaseGraphApplication.getHandler();
        File file = new File(handler.getDir(), "graphs.nt");
        System.err.println("Graphs dir = " + file.getPath());
        Graph modelsGraph = parseNTriples(file);
        model = new ModelsImpl(modelsGraph);
        resources = model.getResources();
    }

    @Override
    public boolean allowGet() {
        return true;
    }

    @Override
    public Representation represent(Variant variant) throws ResourceException {
        Map<String, String> map = populateIdNameMap();
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
            String name = map.get(key);
            builder.append("ID = " + key + ",\t" + "Name = " + path + name + "\n");
        }
        return new StringRepresentation(builder, MediaType.TEXT_PLAIN);
    }

    private Map<String, String> populateIdNameMap() throws ResourceException {
        Map<String, String> idNameMap = new HashMap<String, String>();
        try {
            for (org.jrdf.graph.Resource resource : resources) {
                String name = getStringValue(resource, NAME);
                String id = getStringValue(resource, ID);
                idNameMap.put(id, name);
            }
        } catch (Exception e) {
            throw new ResourceException(SERVER_ERROR_INTERNAL, e);
        }
        return idNameMap;
    }

    private String getStringValue(org.jrdf.graph.Resource resource, URI pred) throws GraphException {
        final ClosableIterator<ObjectNode> iterator = resource.getObjects(pred);
        try {
            String name = "";
            if (iterator.hasNext()) {
                name = ((Literal) iterator.next()).getValue().toString();
            }
            return name;
        } finally {
            iterator.close();
        }
    }
}
