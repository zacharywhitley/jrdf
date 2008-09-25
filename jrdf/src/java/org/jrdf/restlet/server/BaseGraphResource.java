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
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import static org.jrdf.query.AnswerXMLWriter.XSLT_URL_STRING;
import static org.jrdf.restlet.server.local.LocalQueryServer.SPARQL_XML_RESULT_MEDIA_TYPE_STRING;
import org.restlet.data.MediaType;
import static org.restlet.data.MediaType.TEXT_HTML;
import static org.restlet.data.MediaType.TEXT_XML;
import static org.restlet.data.Status.SERVER_ERROR_INTERNAL;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class BaseGraphResource extends Resource {
    /**
     * The form name for the query string.
     */
    public static final String QUERY_STRING = "queryString";
    /**
     * The name of the radio button.
     */
    public static final String FORMAT = "format";
    /**
     * The sparql query result format in XML.
     */
    public static final String FORMAT_XML = "xml";
    /**
     * The sparql query result format in HTML.
     */
    public static final String FORMAT_HTML = "html";
    private static final TransformerFactory TRANSFORM_FACTORY = TransformerFactory.newInstance();
    protected String graphName;
    protected String format;

    public BaseGraphResource(org.restlet.Context context, org.restlet.data.Request request,
                             org.restlet.data.Response response) {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.valueOf(SPARQL_XML_RESULT_MEDIA_TYPE_STRING)));
        getVariants().add(new Variant(TEXT_HTML));
        format = BaseGraphResource.FORMAT_XML;
        graphName = (String) this.getRequest().getAttributes().get("graph");
    }

    @Override
    public boolean allowGet() {
        return true;
    }

    @Override
    public boolean allowPost() {
        return true;
    }

    protected Representation renderResult(String format, String xmlString) throws TransformerException {
        Representation rep;
        if (FORMAT_HTML.equalsIgnoreCase(format)) {
            String transformedXMLString = doXSLTTransformation(xmlString);
            rep = new StringRepresentation(transformedXMLString, TEXT_HTML);
        } else {
            rep = new StringRepresentation(xmlString, TEXT_XML);
        }
        return rep;
    }

    private String doXSLTTransformation(String xmlString) throws TransformerException {
        StringWriter writer;
        Source xmlSource = new StreamSource(new StringReader(xmlString));
        Source xsltSource = new StreamSource(XSLT_URL_STRING);
        writer = new StringWriter();
        Result result = new StreamResult(writer);
        Transformer transformer = TRANSFORM_FACTORY.newTransformer(xsltSource);
        transformer.transform(xmlSource, result);
        final String transformedXMLString = writer.toString();
        return transformedXMLString;
    }

    @Override
    public Representation represent(Variant variant) {
        Representation rep = null;
        try {
            Configuration cfg = new Configuration();
            final String curDir = System.getProperty("user.dir");
            final File dir = new File(curDir);
            System.err.println("dir = " + dir.getCanonicalPath());
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            cfg.setDirectoryForTemplateLoading(dir);
            Template template = cfg.getTemplate("queryPage.ftl");
            rep = new TemplateRepresentation(template, TEXT_HTML);
        } catch (IOException e) {
            getResponse().setStatus(SERVER_ERROR_INTERNAL, e);
        }
        return rep;
    }

    protected void constructAnswerRepresentation(String format, String xmlString) throws TransformerException {
        Representation rep = renderResult(format, xmlString);
        getResponse().setEntity(rep);
    }
}
