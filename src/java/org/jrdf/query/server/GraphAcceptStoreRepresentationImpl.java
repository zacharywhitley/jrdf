/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 *  ====================================================================
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
 */

package org.jrdf.query.server;

import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.parser.RdfReader;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;

import java.io.IOException;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import static org.jrdf.query.server.GraphRequestParameters.GRAPH_NAME_IN;
import static org.restlet.data.MediaType.APPLICATION_RDF_XML;
import static org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST;
import static org.restlet.data.Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE;
import static org.restlet.data.Status.SUCCESS_CREATED;

public class GraphAcceptStoreRepresentationImpl implements GraphAcceptStoreRepresentation {
    private GraphApplication graphApplication;

    public void setGraphApplication(GraphApplication newGraphApplication) {
        this.graphApplication = newGraphApplication;
    }

    public Status acceptRepresentation(Request request) throws ResourceException {
        final Graph graph = getGraph(request);
        return tryParseAndAddToGraph(request.getEntity(), graph);
    }

    public Status storeRepresentation(Request request) throws ResourceException {
        final Graph graph = getGraph(request);
        removeIfThereAreExistingTriples(graph);
        return tryParseAndAddToGraph(request.getEntity(), graph);
    }

    private Graph getGraph(Request newRequest) {
        final String graphName = GRAPH_NAME_IN.getValue(newRequest);
        return graphApplication.getGraph(graphName);
    }

    private void removeIfThereAreExistingTriples(Graph graph) {
        if (graph.getNumberOfTriples() != 0) {
            graph.remove(graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator());
        }
    }

    private Status tryParseAndAddToGraph(Representation entity, Graph graph) {
        if (entity.getMediaType().equals(APPLICATION_RDF_XML)) {
            return tryParseRdfXml(entity, graph);
        } else {
            return CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE;
        }
    }

    private Status tryParseRdfXml(Representation entity, Graph graph) {
        try {
            new RdfReader(graph, new MemMapFactory()).parseRdfXml(entity.getStream());
            return SUCCESS_CREATED;
        } catch (IOException e) {
            return CLIENT_ERROR_BAD_REQUEST;
        }
    }
}
