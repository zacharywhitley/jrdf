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

package org.jrdf.query.answer.json.parser;

import org.codehaus.jackson.JsonParser;
import static org.codehaus.jackson.JsonToken.END_OBJECT;
import static org.codehaus.jackson.JsonToken.FIELD_NAME;
import static org.codehaus.jackson.JsonToken.START_OBJECT;
import static org.jrdf.query.answer.SparqlProtocol.DATATYPE;
import static org.jrdf.query.answer.SparqlProtocol.JSON_XML_LANG;
import static org.jrdf.query.answer.SparqlProtocol.TYPE;
import static org.jrdf.query.answer.SparqlProtocol.VALUE;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueFactory;
import org.jrdf.query.answer.TypeValueFactoryImpl;

import java.io.IOException;
import java.util.Map;

public class SparqlJsonResultParserImpl implements SparqlJsonResultParser {
    private static final String CANNOT_PARSE = "Cannot parse token: ";
    private final TypeValueFactory typeValueFactory = new TypeValueFactoryImpl();
    private final JsonParser parser;
    private String type;
    private String value;
    private String datatype;
    private String xmlLang;

    public SparqlJsonResultParserImpl(JsonParser newParser) {
        this.parser = newParser;
    }

    public void getOneBinding(Map<String, TypeValue> variableToValue) throws IOException {
        variableToValue.put(getVariable(), getTypeValue());
    }

    private String getVariable() throws IOException {
        if (parser.getCurrentToken() == FIELD_NAME) {
            return parser.getCurrentName();
        }
        throw new IllegalStateException(CANNOT_PARSE + parser.getText());
    }

    private TypeValue getTypeValue() throws IOException {
        if (parser.nextToken() == START_OBJECT) {
            return parseObject();
        }
        throw new IllegalStateException(CANNOT_PARSE + parser.getText());
    }

    private TypeValue parseObject() throws IOException {
        type = "";
        value = "";
        datatype = null;
        xmlLang = null;
        while (parser.nextToken() != END_OBJECT) {
            if (parser.getCurrentToken() == FIELD_NAME) {
                parseType();
                parseValue();
                parseDatatype();
                parseXmlLang();
            }
        }
        return typeValueFactory.createTypeValue(type, value, datatype, xmlLang);
    }

    private void parseType() throws IOException {
        if (parser.getCurrentName().equals(TYPE)) {
            parser.nextToken();
            type = parser.getText();
        }
    }

    private void parseValue() throws IOException {
        if (parser.getCurrentName().equals(VALUE)) {
            parser.nextToken();
            value = parser.getText();
        }
    }

    private void parseDatatype() throws IOException {
        if (parser.getCurrentName().equals(DATATYPE)) {
            parser.nextToken();
            datatype = parser.getText();
        }
    }

    private void parseXmlLang() throws IOException {
        if (parser.getCurrentName().equals(JSON_XML_LANG)) {
            parser.nextToken();
            xmlLang = parser.getText();
        }
    }
}