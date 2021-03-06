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

package org.jrdf.query.answer.xml.parser;

import org.jrdf.query.answer.SparqlParser;
import static org.jrdf.query.answer.SparqlProtocol.BOOLEAN;
import static org.jrdf.query.answer.SparqlProtocol.HEAD;
import static org.jrdf.query.answer.SparqlProtocol.HREF;
import static org.jrdf.query.answer.SparqlProtocol.LINK;
import static org.jrdf.query.answer.SparqlProtocol.NAME;
import static org.jrdf.query.answer.SparqlProtocol.RESULT;
import static org.jrdf.query.answer.SparqlProtocol.RESULTS;
import static org.jrdf.query.answer.SparqlProtocol.VARIABLE;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueFactoryImpl;

import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

public class SparqlXmlParserImpl implements SparqlParser {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private final XMLStreamReader parser;
    private final SparqlXmlResultsParser resultsParser;
    private LinkedHashSet<String> variables = new LinkedHashSet<String>();
    private LinkedHashSet<String> links = new LinkedHashSet<String>();
    private boolean hasMore;
    private boolean finishedVariableParsing;

    public SparqlXmlParserImpl(final InputStream stream) {
        this.parser = tryGetXmlParser(stream);
        this.resultsParser = new SparqlXmlResultsParserImpl(parser, new TypeValueFactoryImpl());
        this.finishedVariableParsing = false;
        this.hasMore = false;
        tryGetVariables();
    }

    public LinkedHashSet<String> getVariables() {
        return variables;
    }

    public LinkedHashSet<String> getLink() {
        return links;
    }

    public boolean hasNext() {
        try {
            hasMore = hasNextResult();
        } catch (RuntimeException e) {
            hasMore = false;
        }
        return hasMore;
    }

    public TypeValue[] next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more results available");
        }
        return resultsParser.getResults(variables);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean close() {
        try {
            parser.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private XMLStreamReader tryGetXmlParser(final InputStream stream) {
        try {
            return INPUT_FACTORY.createXMLStreamReader(stream);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasNextResult() {
        while (tryHasNext()) {
            final int eventType = parser.getEventType();
            if (startOfElement(eventType, RESULT) || startOfElement(eventType, BOOLEAN)) {
                return true;
            }
            tryNext();
        }
        return false;
    }

    private void tryGetVariables() {
        if (!finishedVariableParsing) {
            int currentEvent = parser.getEventType();
            while (tryHasNext() && !(endOfElement(currentEvent, HEAD) || startOfElement(currentEvent, RESULTS))) {
                checkElementType(currentEvent);
                currentEvent = tryNext();
            }
            finishedVariableParsing = true;
        }
    }

    private int tryNext() {
        try {
            return parser.next();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean tryHasNext() {
        try {
            return parser.hasNext();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean endOfElement(final int currentEvent, final String tagName) {
        return currentEvent == END_ELEMENT && tagName.equals(parser.getLocalName());
    }

    private void checkElementType(final int currentEvent) {
        if (startOfElement(currentEvent, VARIABLE)) {
            addAttributeToSet(NAME, variables);
        } else if (startOfElement(currentEvent, LINK)) {
            addAttributeToSet(HREF, links);
        }
    }

    private boolean startOfElement(final int currentEvent, final String tagName) {
        return currentEvent == START_ELEMENT && tagName.equals(parser.getLocalName());
    }

    private void addAttributeToSet(final String attributeName, final LinkedHashSet<String> setToAddTo) {
        setToAddTo.add(parser.getAttributeValue(null, attributeName));
    }
}
