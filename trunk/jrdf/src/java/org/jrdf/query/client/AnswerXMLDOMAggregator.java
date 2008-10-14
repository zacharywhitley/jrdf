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

package org.jrdf.query.client;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.NAME;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.RESULT;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.RESULTS;
import static org.jrdf.query.answer.xml.AnswerXMLWriter.VARIABLE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class AnswerXMLDOMAggregator implements AnswerXMLAggregator {
    private String content;
    protected Document document;
    protected Document newDoc;

    public void aggregate(String xml) throws IOException, SAXException {
        if (content == null) {
            content = xml;
        } else {
            DOMParser parser = new DOMParser();
            DOMParser newParser = new DOMParser();
            parser.parse(new InputSource(new StringReader(content)));
            document = parser.getDocument();
            newParser.parse(new InputSource(new StringReader(xml)));
            newDoc = newParser.getDocument();
            content = mergeDocs();
            newDoc = null;
        }
    }

    public String getXML() {
        return content;
    }

    private String mergeDocs() throws IOException {
        final NodeList list = document.getElementsByTagName(VARIABLE);
        final NodeList newList = newDoc.getElementsByTagName(VARIABLE);
        if (!checkList(list, newList)) {
            throw new RuntimeException("Incompatible variable lists");
        }
        addResults();
        StringWriter writer = serializeToString();
        return writer.toString();
    }

    private StringWriter serializeToString() throws IOException {
        OutputFormat of = new OutputFormat("XML", "UTF-8", true);
        XMLSerializer serializer = new XMLSerializer();
        serializer.setOutputFormat(of);
        StringWriter writer = new StringWriter();
        serializer.setOutputCharStream(writer);
        serializer.serialize(document);
        return writer;
    }

    private void addResults() {
        final NodeList results = document.getElementsByTagName(RESULTS);
        Element resultsElement = (Element) results.item(0);
        final NodeList results2 = newDoc.getElementsByTagName(RESULT);
        for (int i = 0; i < results2.getLength(); i++) {
            Element result = (Element) results2.item(i);
            Node node = document.importNode(result, true);
            resultsElement.appendChild(node);
        }
    }

    private boolean checkList(NodeList list1, NodeList list2) {
        String[] vars1 = getVariables(list1, NAME);
        String[] vars2 = getVariables(list2, NAME);
        Arrays.sort(vars1);
        Arrays.sort(vars2);
        return Arrays.equals(vars1, vars2);
    }

    private String[] getVariables(NodeList list, String attName) {
        String[] result = new String[list.getLength()];
        for (int i = 0; i < list.getLength(); i++) {
            final Element node = (Element) list.item(i);
            result[i] = node.getAttribute(attName);
        }
        return result;
    }
}
