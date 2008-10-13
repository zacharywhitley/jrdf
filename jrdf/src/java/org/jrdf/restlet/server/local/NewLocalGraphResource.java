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

package org.jrdf.restlet.server.local;

import static freemarker.ext.dom.NodeModel.parse;
import org.jrdf.query.xml.AnswerXMLWriter;
import static org.jrdf.restlet.server.local.GraphApplicationImpl.DEFAULT_MAX_ROWS;
import org.jrdf.restlet.ConfigurableRestletResource;
import static org.restlet.data.Status.SERVER_ERROR_INTERNAL;
import static org.restlet.data.Status.SUCCESS_OK;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
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

public class NewLocalGraphResource extends ConfigurableRestletResource {
    private static final String GRAPH_VALUE = "graph";
    private static final String GRAPH_NAME = "graphName";
    private AnswerXMLWriter xmlWriter;
    private NewLocalGraphResourceDoer doer;
    private String graphName;

    public void setDoer(NewLocalGraphResourceDoer newDoer) {
        this.doer = newDoer;
    }

    @Override
    public Representation represent(Variant variant) {
        Representation rep = null;
        try {
            String getNext = getRequest().getResourceRef().getQueryAsForm().getFirstValue("next");
            graphName = (String) this.getRequest().getAttributes().get(GRAPH_VALUE);
            if (getNext == null || !getNext.equalsIgnoreCase("true")) {
                rep = queryPageRepresentation(variant);
            } else {
                rep = queryResultRepresentation(variant, doer.getMaxRows());
            }
            getResponse().setStatus(SUCCESS_OK);
        } catch (Exception e) {
            getResponse().setStatus(SERVER_ERROR_INTERNAL, e, e.getMessage());
        }
        return rep;
    }

    protected Representation queryPageRepresentation(Variant variant) throws IOException {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put(GRAPH_NAME, graphName);
        return createTemplateRepresentation(variant.getMediaType(), dataModel);
    }

    private Representation queryResultRepresentation(Variant variant, String noRows) throws ResourceException {
        try {
            StringWriter writer = new StringWriter();
            xmlWriter = doer.getAnswerXMLWriter(writer);
            String xmlString = getXMLString(noRows, writer);
            writer.close();
            xmlWriter.close();
            return constructHTMLAnswerRep(variant, xmlString);
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

    private Representation constructHTMLAnswerRep(Variant variant, String answerXML) throws SAXException, IOException,
        ParserConfigurationException {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("doc", parse(new InputSource(new StringReader(answerXML))));
        dataModel.put(GRAPH_NAME, graphName);
        dataModel.put("timeTaken", doer.getTimeTaken());
        dataModel.put("hasMore", xmlWriter.hasMoreResults());
        dataModel.put("tooManyRows", doer.isTooManyRows());
        dataModel.put("maxRows", DEFAULT_MAX_ROWS);
        return createTemplateRepresentation(variant.getMediaType(), dataModel);
    }
}