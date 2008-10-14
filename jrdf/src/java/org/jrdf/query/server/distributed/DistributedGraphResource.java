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

import static freemarker.ext.dom.NodeModel.parse;
import org.jrdf.query.xml.AnswerXMLWriter;
import org.jrdf.query.server.BaseGraphResource;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Form;
import static org.restlet.data.MediaType.TEXT_XML;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import static org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST;
import static org.restlet.data.Status.SERVER_ERROR_INTERNAL;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class DistributedGraphResource extends BaseGraphResource {
    private DistributedQueryGraphApplicationImpl application;
    private AnswerXMLWriter xmlWriter;

    public DistributedGraphResource(Context context, Request request, Response response) {
        super(context, request, response);
        application = (DistributedQueryGraphApplicationImpl) Application.getCurrent();
    }

    public boolean allowGet() {
        return true;
    }

    public boolean allowPost() {
        return true;
    }

    public void acceptRepresentation(Representation representation) throws ResourceException {
        try {
            Form form = new Form(representation);
            String queryString = form.getFirstValue(QUERY_STRING);
            String newFormat = form.getFirstValue("format");
            String format = (newFormat == null) ? FORMAT_XML : newFormat;
            String noRows = form.getFirstValue("noRows");
            processQueryInputs(queryString, format, noRows);
            Representation rep = constructRepresentation(format, noRows);
            getResponse().setEntity(rep);
            getResponse().setStatus(Status.SUCCESS_OK);
        } catch (IllegalArgumentException e) {
            getResponse().setStatus(CLIENT_ERROR_BAD_REQUEST, e);
        } catch (Exception e) {
            getResponse().setStatus(SERVER_ERROR_INTERNAL, e);
        }
    }

    private void processQueryInputs(String queryString, String format, String noRows) throws ResourceException {
        application.setFormat(format);
        application.setMaxRows(noRows);
        application.answerQuery(graphName, queryString);
    }

    private Representation constructRepresentation(String format, String noRows) throws ResourceException {
        StringWriter writer = new StringWriter();
        try {
            xmlWriter = application.getAnswerXMLWriter(writer);
            String xmlString = getXMLString(noRows, writer);
            writer.close();
            xmlWriter.close();
            return getAnswerRep(format, xmlString);
        } catch (Exception e) {
            throw new ResourceException(e);
        }
    }

    private String getXMLString(String noRows, StringWriter writer) throws XMLStreamException {
        String  xmlString;
        try {
            int maxRows = Integer.parseInt(noRows);
            generatePartialXMLString(maxRows);
        } catch (NumberFormatException e) {
            xmlWriter.write();
        }
        xmlString = writer.toString();
        return xmlString;
    }

    private void generatePartialXMLString(int maxRows) throws XMLStreamException {
        int count = 0;
        xmlWriter.writeStartDocument();
        xmlWriter.writeVariables();
        xmlWriter.writeStartResults();
        while (xmlWriter.hasMoreResults() && count < maxRows) {
            xmlWriter.writeResult();
            count++;
        }
        xmlWriter.writeEndResults();
        xmlWriter.writeEndDocument();
    }

    private Representation getAnswerRep(String format, String answerXML) throws SAXException,
            IOException, ParserConfigurationException {
        Representation rep;
        if (format.equalsIgnoreCase(FORMAT_XML)) {
            rep = new StringRepresentation(answerXML, TEXT_XML);
        } else {
            rep = constructHTMLAnswerRep(answerXML);
        }
        return rep;
    }

    private Representation constructHTMLAnswerRep(String answerXML)
        throws SAXException, IOException, ParserConfigurationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("doc", parse(new InputSource(new StringReader(answerXML))));
        root.put(GRAPH_NAME, graphName);
        root.put("timeTaken", application.getTimeTaken());
        root.put("hasMore", xmlWriter.hasMoreResults());
//        root.put("tooManyRows", application.isTooManyRows());
//        root.put("maxRows", MAX_ROWS);
        return getRepresentation(root, "query-html.ftl");
    }
}
