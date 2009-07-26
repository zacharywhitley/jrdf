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
import static org.jrdf.query.answer.SparqlProtocol.BNODE;
import static org.jrdf.query.answer.SparqlProtocol.LITERAL;
import static org.jrdf.query.answer.SparqlProtocol.TYPE;
import static org.jrdf.query.answer.SparqlProtocol.URI;
import static org.jrdf.query.answer.SparqlProtocol.VALUE;
import org.jrdf.query.answer.xml.SparqlResultType;
import static org.jrdf.query.answer.xml.SparqlResultType.BLANK_NODE;
import static org.jrdf.query.answer.xml.SparqlResultType.URI_REFERENCE;
import org.jrdf.query.answer.xml.TypeValue;
import org.jrdf.query.answer.xml.TypeValueImpl;

import java.io.IOException;
import java.util.Map;

public class SparqlAnswerResultJsonParserImpl {
    private final JsonParser parser;

    public SparqlAnswerResultJsonParserImpl(JsonParser parser) {
        this.parser = parser;
    }

    public void getOneBinding(Map<String, TypeValue> variableToValue) throws IOException {
        variableToValue.put(getVariable(), getTypeValue());
    }

    private String getVariable() throws IOException {
        if (parser.nextToken() == START_OBJECT) {
            if (parser.nextToken() == FIELD_NAME) {
                return parser.getCurrentName();
            }
        }
        throw new IllegalStateException("Cannot parse: " + parser.getText());
    }

    private TypeValue getTypeValue() throws IOException {
        TypeValue typeValue = new TypeValueImpl();
        if (parser.nextToken() == START_OBJECT) {
            String type = "";
            String value = "";
            while (parser.nextToken() != END_OBJECT) {
                if (parser.getCurrentToken() == FIELD_NAME) {
                    if (parser.getCurrentName().equals(TYPE)) {
                        parser.nextToken();
                        type = parser.getText();
                    }
                    if (parser.getCurrentName().equals(VALUE)) {
                        parser.nextToken();
                        value = parser.getText();
                    }
                }
            }
            typeValue = createTypeValue(type, value);
        }
        return typeValue;
    }

    private TypeValue createTypeValue(final String type, final String value) {
        TypeValue typeValue = new TypeValueImpl();
        if (URI.equals(type)) {
            typeValue = createURI(value);
        } else if (LITERAL.equals(type)) {
            typeValue = createLiteral(value);
        } else if (BNODE.equals(type)) {
            typeValue = createBNode(value);
        }
        return typeValue;
    }

    private TypeValue createURI(final String value) {
        return new TypeValueImpl(URI_REFERENCE, value);
    }

    private TypeValue createLiteral(final String value) {
        return new TypeValueImpl(SparqlResultType.LITERAL, value);
    }

    private TypeValue createBNode(final String value) {
        return new TypeValueImpl(BLANK_NODE, value);
    }
}
