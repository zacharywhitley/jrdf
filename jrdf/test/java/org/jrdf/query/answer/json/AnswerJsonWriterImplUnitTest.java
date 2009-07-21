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

package org.jrdf.query.answer.json;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.jrdf.query.answer.SelectAnswer;
import static org.jrdf.query.answer.xml.SparqlResultType.BLANK_NODE;
import static org.jrdf.query.answer.xml.SparqlResultType.LITERAL;
import static org.jrdf.query.answer.xml.SparqlResultType.TYPED_LITERAL;
import static org.jrdf.query.answer.xml.SparqlResultType.URI_REFERENCE;
import org.jrdf.query.answer.xml.TypeValue;
import org.jrdf.query.answer.xml.TypeValueImpl;
import org.jrdf.util.test.MockFactory;
import org.jrdf.vocabulary.XSD;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AnswerJsonWriterImplUnitTest {
    private static final String[] NO_VARIABLES = {};
    private static final String[] NO_BINDINGS = {};
    private static final String[] TEST_VARIABLES = {"abc", "123", "doh", "ray", "me"};
    private static final Map<String, TypeValue> TEST_BINDINGS_1 = new HashMap<String, TypeValue>() {
        {
            put("abc", new TypeValueImpl(BLANK_NODE, "r1"));
            put("123", new TypeValueImpl(URI_REFERENCE, "http://work.example.org/alice/"));
            put("doh", new TypeValueImpl(LITERAL, "a deer"));
            put("ray", new TypeValueImpl(TYPED_LITERAL, "123", true, XSD.INT.toString()));
            put("me", new TypeValueImpl(LITERAL, ""));
        }
    };
    private final MockFactory mockFactory = new MockFactory();
    private SelectAnswer selectAnswer;
    private Iterator<TypeValue[]> mockIterator;
    private StringWriter stringWriter;

    @SuppressWarnings({ "unchecked" })
    @Before
    public void createMocks() throws Exception {
        selectAnswer = mockFactory.createMock(SelectAnswer.class);
        mockIterator = mockFactory.createMock(Iterator.class);
        stringWriter = new StringWriter();
    }

    @Test
    public void creation() throws Exception {
        expect(selectAnswer.columnValuesIterator()).andReturn(mockIterator);
        expect(selectAnswer.numberOfTuples()).andReturn(0L);

        mockFactory.replay();
        new AnswerJsonWriterImpl(stringWriter, selectAnswer);

        mockFactory.verify();
    }

    @Test
    public void emptyAnswer() throws Exception {
        expect(mockIterator.hasNext()).andReturn(false);
        expect(selectAnswer.columnValuesIterator()).andReturn(mockIterator);
        expect(selectAnswer.numberOfTuples()).andReturn(0L);
        expect(selectAnswer.getVariableNames()).andReturn(NO_VARIABLES);

        mockFactory.replay();
        final AnswerJsonWriter writer = new AnswerJsonWriterImpl(stringWriter, selectAnswer);
        writer.writeFullDocument();

        mockFactory.verify();
    }

    @Test
    public void noResults() throws Exception {
        expect(mockIterator.hasNext()).andReturn(false);
        expect(selectAnswer.columnValuesIterator()).andReturn(mockIterator);
        expect(selectAnswer.numberOfTuples()).andReturn(0L);
        expect(selectAnswer.getVariableNames()).andReturn(TEST_VARIABLES);

        mockFactory.replay();
        final AnswerJsonWriter writer = new AnswerJsonWriterImpl(stringWriter, selectAnswer);
        writer.writeFullDocument();

        mockFactory.verify();
        final JSONObject head = new JSONObject(stringWriter.toString()).getJSONObject("head");
        checkJSONStringArrayValues(head, "vars", TEST_VARIABLES);
        final JSONObject results = new JSONObject(stringWriter.toString()).getJSONObject("results");
        checkJSONStringArrayValues(results, "bindings", NO_BINDINGS);
    }


    @Test
    public void oneResult() throws Exception {
        expect(mockIterator.hasNext()).andReturn(true).times(2);
        expect(mockIterator.hasNext()).andReturn(false).once();
        expect(mockIterator.next()).andReturn(new TypeValue[]{
            TEST_BINDINGS_1.get(TEST_VARIABLES[0]),
            TEST_BINDINGS_1.get(TEST_VARIABLES[1]),
            TEST_BINDINGS_1.get(TEST_VARIABLES[2]),
            TEST_BINDINGS_1.get(TEST_VARIABLES[3])});
        expect(selectAnswer.columnValuesIterator()).andReturn(mockIterator);
        expect(selectAnswer.numberOfTuples()).andReturn(1L);
        expect(selectAnswer.getVariableNames()).andReturn(TEST_VARIABLES).anyTimes();

        mockFactory.replay();
        final AnswerJsonWriter writer = new AnswerJsonWriterImpl(stringWriter, selectAnswer);
        writer.writeFullDocument();

        mockFactory.verify();
        final JSONObject head = new JSONObject(stringWriter.toString()).getJSONObject("head");
        checkJSONStringArrayValues(head, "vars", TEST_VARIABLES);
        final JSONObject results = new JSONObject(stringWriter.toString()).getJSONObject("results");
        checkBindings(results.getJSONArray("bindings"), TEST_BINDINGS_1);
    }

    private void checkJSONStringArrayValues(final JSONObject jsonObject, final String arrayName,
        final String[] expectedValues) throws JSONException {
        final JSONArray vars = jsonObject.getJSONArray(arrayName);
        assertThat(vars, notNullValue());
        assertThat(vars.length(), equalTo(expectedValues.length));
        int counter = 0;
        for (String value : expectedValues) {
            assertThat(vars.getString(counter++), equalTo(value));
        }
    }

    private void checkBindings(final JSONArray bindings, Map<String, TypeValue>... allResults) throws JSONException {
        for (int i = 0; i < bindings.length(); i++) {
            final JSONObject aBinding = bindings.getJSONObject(i);
            final Map<String, TypeValue> expectedBindings = allResults[i];
            assertThat(aBinding, notNullValue());
            for (String variable : TEST_VARIABLES) {
                if (aBinding.has(variable)) {
                    final JSONObject aVariable = aBinding.getJSONObject(variable);
                    assertThat(aVariable, notNullValue());
                    final String type = aVariable.getString("type");
                    final String value = aVariable.getString("value");
                    assertThat(type, equalTo(expectedBindings.get(variable).getType().toString()));
                    assertThat(value, equalTo(expectedBindings.get(variable).getValue()));
                }
            }
        }
    }
}
