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

package org.jrdf.query.server;

import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.AskAnswer;
import org.jrdf.query.answer.SelectAnswer;
import static org.jrdf.query.server.GraphResourceRequestParameters.MAX_ROWS_IN;
import static org.jrdf.query.server.GraphResourceRequestParameters.QUERY_IN;
import static org.jrdf.query.server.GraphResourceRequestParameters.GRAPH_IN;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import static org.restlet.data.Status.SERVER_ERROR_INTERNAL;
import static org.restlet.data.Status.SUCCESS_OK;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version : $Id: $
 */
public class GraphResource extends ConfigurableRestletResource {
    private static final long DEFAULT_MAX_ROWS = 1000;
    private static final String GRAPH = "graphName";
    private static final String GRAPH_REF = "graphRef";
    private static final String TIME_TAKEN = "timeTaken";
    private static final String MAX_ROWS = "maxRows";
    private static final String TOO_MANY_ROWS = "tooManyRows";
    private static final String ANSWER = "answer";
    private String graphName;
    private String queryString;
    private long maxRows;
    private GraphApplication graphApplication;
    private GraphStoreRepresentation graphRepresentation;

    @Override
    public void init(final Context context, final Request request, final Response response) {
        super.init(context, request, response);
        setModifiable(true);
    }

    public void setGraphApplication(GraphApplication newGraphApplication) {
        this.graphApplication = newGraphApplication;
    }

    public void setGraphRepresentation(GraphStoreRepresentation newGraphRepresentation) {
        this.graphRepresentation = newGraphRepresentation;
    }

    @Override
    public void storeRepresentation(Representation entity) throws ResourceException {
        final Status status = graphRepresentation.storeRepresentation(getRequest());
        getResponse().setStatus(status);
    }

    @Override
    public void acceptRepresentation(Representation entity) throws ResourceException {
        final Status status = graphRepresentation.acceptRepresentation(getRequest());
        getResponse().setStatus(status);
    }

//    @Override
//    public void removeRepresentations() throws ResourceException {
//        final Status status = graphRepresentation.removeRepresentations(getRequest());
//        getResponse().setStatus(status);
//    }

    @Override
    public Representation represent(Variant variant) {
        Representation rep = null;
        try {
            graphName = GRAPH_IN.getValue(getRequest());
            maxRows = getMaxRows();
            queryString = QUERY_IN.getValue(getRequest());
            if (queryString == null) {
                rep = nonQueryRepresentation(variant);
            } else {
                rep = queryRepresentation(variant);
            }
            getResponse().setStatus(SUCCESS_OK);
        } catch (Exception e) {
            e.printStackTrace();
            getResponse().setStatus(SERVER_ERROR_INTERNAL, e, e.getMessage().replace("\n", ""));
        }
        return rep;
    }

    private Long getMaxRows() {
        Long rows;
        try {
            final String tmpMaxRows = (String) MAX_ROWS_IN.getValue(getRequest());
            if (tmpMaxRows != null) {
                rows = Long.parseLong(tmpMaxRows);
            } else {
                rows = DEFAULT_MAX_ROWS;
            }
        } catch (NumberFormatException e) {
            rows = DEFAULT_MAX_ROWS;
        }
        return rows;
    }

    private Representation nonQueryRepresentation(Variant variant) throws IOException {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put(GRAPH, graphName);
        dataModel.put(GRAPH_REF, graphApplication.getGraph(graphName));
        return createTemplateRepresentation(variant.getMediaType(), dataModel);
    }

    private Representation queryRepresentation(Variant variant) throws ResourceException {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        Answer answer = graphApplication.answerQuery(graphName, queryString, maxRows);
        dataModel.put(GRAPH, graphName);
        dataModel.put(MAX_ROWS, graphApplication.getMaxRows());
        dataModel.put(TIME_TAKEN, graphApplication.getTimeTaken());
        dataModel.put(TOO_MANY_ROWS, graphApplication.isTooManyRows());
        dataModel.put(ANSWER, answer);
        if (answer instanceof SelectAnswer) {
            dataModel.put("answerType", "select");
        } else if (answer instanceof AskAnswer) {
            dataModel.put("answerType", "ask");
        }
        return createTemplateRepresentation(variant.getMediaType(), dataModel);
    }
}