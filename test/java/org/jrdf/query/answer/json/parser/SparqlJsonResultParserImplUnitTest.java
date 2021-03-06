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

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.query.answer.SparqlResultType;
import static org.jrdf.query.answer.SparqlResultType.BLANK_NODE;
import static org.jrdf.query.answer.SparqlResultType.LITERAL;
import static org.jrdf.query.answer.SparqlResultType.TYPED_LITERAL;
import static org.jrdf.query.answer.SparqlResultType.URI_REFERENCE;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueImpl;
import org.jrdf.vocabulary.RDF;
import org.jrdf.vocabulary.XSD;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.HashMap;

public class SparqlJsonResultParserImplUnitTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private static final String CANNOT_PARSE = "Cannot parse token: ";

    @Test
    public void uriBinding() throws Exception {
        checkParser("\"hpage\" : {\"type\": \"uri\", \"value\": \"http://example.org/alice\" }", "hpage",
            new TypeValueImpl(URI_REFERENCE, "http://example.org/alice"));
    }

    @Test
    public void untypedLiteralBinding() throws Exception {
        checkParser("\"name\" : {\"type\": \"literal\", \"value\": \"Alice\" }", "name",
            new TypeValueImpl(LITERAL, "Alice"));
    }

    @Test
    public void typedLiteralBinding() throws Exception {
        checkParser("\"ray\" :{\"value\":\"123\",\"type\":\"typed-literal\"," +
            "\"datatype\":\"http://www.w3.org/2001/XMLSchema#int\"}", "ray",
            new TypeValueImpl(TYPED_LITERAL, "123", true, XSD.INT.toString()));
    }

    @Test
    public void xmlTypedLiteralBinding() throws Exception {
        checkParser("\"name\" : {\"type\": \"typed-literal\", " +
            "\"value\": \"<p xmlns=\\\"http://www.w3.org/1999/xhtml\\\">My name is <b>alice</b></p>\"," +
            "\"datatype\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral\" }", "name",
            new TypeValueImpl(TYPED_LITERAL,
                "<p xmlns=\"http://www.w3.org/1999/xhtml\">My name is <b>alice</b></p>",
                true, RDF.XML_LITERAL.toString()));
    }

    @Test
    public void languageLiteralBinding() throws Exception {
        checkParser("\"name\" : {\"type\": \"literal\", \"value\": \"Bob\",\"xml:lang\": \"en\" }", "name",
            new TypeValueImpl(SparqlResultType.LITERAL, "Bob", false, "en"));
    }

    @Test
    public void bnodeBinding() throws Exception {
        checkParser("\"x\" : {\"type\": \"bnode\", \"value\": \"r1\" }", "x", new TypeValueImpl(BLANK_NODE, "r1"));
    }

    @Test
    public void valueTypeInsteadOfTypeValueBinding() throws Exception {
        checkParser("\"x\" : {\"value\": \"r1\", \"type\": \"bnode\"  }", "x", new TypeValueImpl(BLANK_NODE, "r1"));
    }

    @Test
    public void missingTypeMeansUnbound() throws Exception {
        checkParser("\"name\" : {\"value\": \"Alice\" }", "name", new TypeValueImpl());
    }

    @Test
    public void missingValueMeansUnbound() throws Exception {
        checkParser("\"name\" : {\"type\": \"bnode\" }", "name", new TypeValueImpl(BLANK_NODE, ""));
    }

    @Test
    public void missingdValueAndType() throws Exception {
        checkParser("\"name\" : { }", "name", new TypeValueImpl());
    }

    @Test
    public void arrayInsteadOfObjectThrowsException() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(CANNOT_PARSE + "[");
        checkParser("\"name\" : [{}] ", "name", new TypeValueImpl());
    }

    @Test
    public void emptyThrowsAnException() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(CANNOT_PARSE + "}");
        checkParser("", "", new TypeValueImpl());
    }

    private void checkParser(final String jsonBinding, final String variable, final TypeValue expectedValue)
        throws IOException {
        // Create something that the parser will successfully parse.
        final JsonFactory jsonFactory = new JsonFactory();
        final JsonParser parser = jsonFactory.createJsonParser("{" + jsonBinding + "}");
        final SparqlJsonResultParser jsonParser = new SparqlJsonResultParserImpl(parser);
        final HashMap<String, TypeValue> binding = new HashMap<String, TypeValue>();
        parser.nextToken();
        parser.nextToken();
        jsonParser.getOneBinding(binding);
        assertThat(binding.containsKey(variable), is(true));
        assertThat(binding.get(variable), equalTo(expectedValue));
    }
}
