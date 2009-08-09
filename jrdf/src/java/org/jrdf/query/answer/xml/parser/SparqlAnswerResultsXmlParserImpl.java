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

import org.jrdf.query.answer.SparqlProtocol;
import static org.jrdf.query.answer.SparqlResultType.BOOLEAN;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueArrayFactory;
import org.jrdf.query.answer.TypeValueArrayFactoryImpl;
import org.jrdf.query.answer.TypeValueFactory;
import org.jrdf.query.answer.TypeValueImpl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class SparqlAnswerResultsXmlParserImpl implements SparqlAnswerResultsXmlParser {
    private XMLStreamReader parser;
    private SparqlAnswerResultXmlParser resultParser;
    private TypeValueArrayFactory arrayFactory = new TypeValueArrayFactoryImpl();

    public SparqlAnswerResultsXmlParserImpl(final XMLStreamReader newParser, final TypeValueFactory typeValueFactory) {
        this.parser = newParser;
        this.resultParser = new SparqlAnswerResultXmlParserImpl(parser, typeValueFactory);
    }

    public TypeValue[] getResults(LinkedHashSet<String> variables) {
        Map<String, TypeValue> variableToValue;
        try {
            variableToValue = parseAllResults();
        } catch (XMLStreamException e) {
            variableToValue = new HashMap<String, TypeValue>();
        }
        return arrayFactory.mapToArray(variables, variableToValue);
    }

    private Map<String, TypeValue> parseAllResults() throws XMLStreamException {
        Map<String, TypeValue> variableToValue = new HashMap<String, TypeValue>();
        int currentEvent = parser.getEventType();
        while (parser.hasNext() && !endOfResult(currentEvent)) {
            if (startOfBinding(currentEvent)) {
                resultParser.getOneBinding(variableToValue);
            } else if (startOfBoolean(currentEvent)) {
                variableToValue.put("", new TypeValueImpl(BOOLEAN, parser.getElementText()));
            }
            currentEvent = parser.next();
        }
        return variableToValue;
    }

    private boolean startOfBoolean(int currentEvent) {
        return currentEvent == START_ELEMENT && SparqlProtocol.BOOLEAN.equals(parser.getLocalName());
    }

    private boolean startOfBinding(int currentEvent) {
        return currentEvent == START_ELEMENT && SparqlProtocol.BINDING.equals(parser.getLocalName());
    }

    private boolean endOfResult(int currentEvent) {
        return currentEvent == END_ELEMENT && SparqlProtocol.RESULT.equals(parser.getLocalName());
    }
}
