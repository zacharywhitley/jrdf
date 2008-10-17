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

import org.jrdf.query.ConfigurableRestletResource;
import static org.jrdf.query.server.local.GraphApplicationImpl.DEFAULT_MAX_ROWS;
import static org.restlet.data.Status.SERVER_ERROR_INTERNAL;
import static org.restlet.data.Status.SUCCESS_OK;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @author Andrew Newman
 * @version :$
 */

public class DistributedGraphResource extends ConfigurableRestletResource {
    private static final String GRAPH_VALUE = "graph";
    private static final String GRAPH_NAME = "graphName";
    private DistributedQueryGraphApplication graphApplication;
    private String graphName;
    private String queryString;

    public void setDistributeQueryGraphApplication(DistributedQueryGraphApplication newApplication) {
        this.graphApplication = newApplication;
    }

    @Override
    public Representation represent(Variant variant) {
        Representation rep = null;
        try {
            graphName = (String) getRequest().getAttributes().get(GRAPH_VALUE);
            if (getRequest().getResourceRef().hasQuery()) {
                queryString = getRequest().getResourceRef().getQueryAsForm().getFirst("queryString").getValue();
            }
            if (queryString == null) {
                rep = queryPageRepresentation(variant);
            } else {
                rep = queryResultRepresentation(variant);
            }
            getResponse().setStatus(SUCCESS_OK);
        } catch (Exception e) {
            getResponse().setStatus(SERVER_ERROR_INTERNAL, e, e.getMessage().replace("\n", ""));
        }
        return rep;
    }

    private Representation queryPageRepresentation(Variant variant) {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put(GRAPH_NAME, graphName);
        return createTemplateRepresentation(variant.getMediaType(), dataModel);
    }

    private Representation queryResultRepresentation(Variant variant) throws ResourceException {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("queryString", queryString);
        dataModel.put("answer", graphApplication.answerQuery2(graphName, queryString));
        dataModel.put(GRAPH_NAME, graphName);
        dataModel.put("timeTaken", graphApplication.getTimeTaken());
        dataModel.put("tooManyRows", graphApplication.isTooManyRows());
        dataModel.put("maxRows", DEFAULT_MAX_ROWS);
        return createTemplateRepresentation(variant.getMediaType(), dataModel);
    }
}