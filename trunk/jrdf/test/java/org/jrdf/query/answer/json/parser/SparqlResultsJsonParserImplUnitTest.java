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
import static org.codehaus.jackson.JsonToken.END_ARRAY;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayContaining;
import static org.jrdf.query.answer.SparqlResultType.BLANK_NODE;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueFactory;
import org.jrdf.query.answer.TypeValueImpl;
import org.jrdf.util.test.MockFactory;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class SparqlResultsJsonParserImplUnitTest {
    private final MockFactory mockFactory = new MockFactory();
    private Iterator<TypeValue[]> mockIterator;
    private JsonParser mockJsonParser;
    private TypeValueFactory mockTypeValueFactory;
    private LinkedHashSet<String> variables = new LinkedHashSet<String>();

    @Before
    @SuppressWarnings({ "unchecked" })
    public void setUp() throws Exception {
        mockJsonParser = mockFactory.createMock(JsonParser.class);
        mockTypeValueFactory = mockFactory.createMock(TypeValueFactory.class);
        mockIterator = mockFactory.createMock(Iterator.class);
    }

    @Test
    public void closeClosesParser() throws Exception {
        expect(mockJsonParser.nextToken()).andReturn(END_ARRAY);
        mockJsonParser.close();
        expectLastCall();

        mockFactory.replay();
        final SparqlResultsJsonParser jsonParser =
            new SparqlResultsJsonParserImpl(variables, mockJsonParser);
        final boolean closedOkay = jsonParser.close();

        mockFactory.verify();
        assertThat(closedOkay, is(true));
    }

    @Test(expected = RuntimeException.class)
    public void closeWithException() throws Exception {
        expect(mockJsonParser.nextToken()).andReturn(END_ARRAY);
        mockJsonParser.close();
        expectLastCall().andThrow(new IOException());

        mockFactory.replay();
        final SparqlResultsJsonParser jsonParser =
            new SparqlResultsJsonParserImpl(variables, mockJsonParser);
        jsonParser.close();

        mockFactory.verify();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeThrowsException() throws Exception {
        final SparqlResultsJsonParser jsonParser =
            new SparqlResultsJsonParserImpl(variables, mockJsonParser);
        jsonParser.remove();
    }

    @Test
    public void someSimpleBindings() throws Exception {
        String results = "[" +
            "{\"friend\" : {\"type\": \"bnode\", \"value\": \"r2\"}}," +
            "{\"friend\" : {\"type\": \"bnode\", \"value\": \"r3\"}}" +
            "]";
        final JsonFactory jsonFactory = new JsonFactory();
        final JsonParser parser = jsonFactory.createJsonParser(results);
        variables.add("friend");
        // Skip start of array
        parser.nextToken();
        final SparqlResultsJsonParser jsonParser = new SparqlResultsJsonParserImpl(variables, parser);
        assertThat(jsonParser.hasNext(), is(true));
        assertThat(jsonParser.next(), arrayContaining(new TypeValue[]{new TypeValueImpl(BLANK_NODE, "r2")}));
        assertThat(jsonParser.hasNext(), is(true));
        assertThat(jsonParser.next(), arrayContaining(new TypeValue[]{new TypeValueImpl(BLANK_NODE, "r3")}));
    }

    @Test(expected = RuntimeException.class)
    public void bindingWithParseErrorThrowsRuntime() throws Exception {
        String results = "[{\"friend\" : {\"type\": \"bnode\", \"value\": \"r2\"},},]";
        final JsonFactory jsonFactory = new JsonFactory();
        final JsonParser parser = jsonFactory.createJsonParser(results);
        variables.add("friend");
        // Skip start of array
        parser.nextToken();
        final SparqlResultsJsonParser jsonParser = new SparqlResultsJsonParserImpl(variables, parser);
        jsonParser.next();
    }
}
