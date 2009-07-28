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

import org.jrdf.query.answer.AnswerType;
import static org.jrdf.query.answer.AnswerType.ASK;
import static org.jrdf.query.answer.AnswerType.SELECT;
import static org.jrdf.query.answer.AnswerType.UNKNOWN;
import static org.jrdf.query.answer.SparqlProtocol.BOOLEAN;
import static org.jrdf.query.answer.SparqlProtocol.HEAD;
import static org.jrdf.query.answer.SparqlProtocol.NAME;
import static org.jrdf.query.answer.SparqlProtocol.RESULT;
import static org.jrdf.query.answer.SparqlProtocol.RESULTS;
import static org.jrdf.query.answer.SparqlProtocol.VARIABLE;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueFactoryImpl;

import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.LinkedHashSet;

// TODO AN/YF Refactor out the tryGetVariables
// TODO AN Missing link support for head tag.
// TODO AN/YF - Turn into extension of closableiterator?
public class SparqlAnswerXmlParserImpl implements SparqlAnswerXmlParser {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private final XMLStreamReader parser;
    private final SparqlAnswerResultsXmlParser resultsParser;
    private LinkedHashSet<String> variables = new LinkedHashSet<String>();
    private boolean hasMore;
    private boolean finishedVariableParsing;
    private AnswerType answerType = UNKNOWN;

    public SparqlAnswerXmlParserImpl(InputStream stream) throws XMLStreamException {
        this.parser = INPUT_FACTORY.createXMLStreamReader(stream);
        this.resultsParser = new SparqlAnswerResultsXmlParserImpl(parser, new TypeValueFactoryImpl());
        this.finishedVariableParsing = false;
        this.hasMore = false;
        parseHeadElement();
    }

    public LinkedHashSet<String> getVariables() {
        if (answerType == SELECT) {
            return variables;
        } else {
            throw new UnsupportedOperationException("Cannot get variables for non-SLECT queries.");
        }
    }

    public AnswerType getAnswerType() throws XMLStreamException {
        return answerType;
    }

    public boolean hasMoreResults() {
        try {
            hasMore = hasNextResult();
        } catch (XMLStreamException e) {
            hasMore = false;
        }
        return hasMore;
    }

    public boolean getAskResult() throws XMLStreamException {
        if (answerType != ASK) {
            throw new UnsupportedOperationException("Cannot get boolean result for non-ASK queries.");
        }
        int eventType = parser.getEventType();
        while (parser.hasNext()) {
            if (startOfElement(eventType, BOOLEAN)) {
                eventType = parser.next();
                if (eventType == CHARACTERS) {
                    hasMore = false;
                    return Boolean.parseBoolean(parser.getText());
                }
            }
            eventType = parser.next();
        }
        throw new XMLStreamException("Cannot find boolean result value.");
    }

    public TypeValue[] getResults() {
        return resultsParser.getResults(variables);
    }

    public void close() {
        try {
            parser.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseHeadElement() throws XMLStreamException {
        int eventType = parser.getEventType();
        while (parser.hasNext()) {
            if (startOfElement(eventType, BOOLEAN)) {
                finishedVariableParsing = true;
                hasMore = true;
                answerType = ASK;
                break;
            } else if (startOfElement(eventType, VARIABLE) || startOfElement(eventType, RESULTS)) {
                tryGetVariables();
                answerType = SELECT;
                break;
            }
            eventType = parser.next();
        }
    }

    private boolean hasNextResult() throws XMLStreamException {
        while (parser.hasNext()) {
            int eventType = parser.getEventType();
            if (startOfElement(eventType, RESULT) || startOfElement(eventType, BOOLEAN)) {
                return true;
            }
            eventType = parser.next();
        }
        return false;
    }

    private void tryGetVariables() throws XMLStreamException {
        if (!finishedVariableParsing) {
            int currentEvent = parser.getEventType();
            while (parser.hasNext()) {
                if (startOfElement(currentEvent, VARIABLE)) {
                    String variableName = parser.getAttributeValue(null, NAME);
                    variables.add(variableName);
                } else if (endOfElement(currentEvent, HEAD) || startOfElement(currentEvent, RESULTS)) {
                    break;
                }
                currentEvent = parser.next();
            }
            finishedVariableParsing = true;
        }
    }

    private boolean startOfElement(int currentEvent, String tagName) {
        return currentEvent == START_ELEMENT && tagName.equals(parser.getLocalName());
    }

    private boolean endOfElement(int currentEvent, String tagName) {
        return currentEvent == END_ELEMENT && tagName.equals(parser.getLocalName());
    }
}
