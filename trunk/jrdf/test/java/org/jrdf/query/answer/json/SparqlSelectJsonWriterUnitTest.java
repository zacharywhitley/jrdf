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
import static org.easymock.EasyMock.expectLastCall;
import org.jrdf.query.answer.SelectAnswer;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueArrayFactory;
import org.jrdf.query.answer.TypeValueArrayFactoryImpl;
import static org.jrdf.query.answer.json.JsonTestUtil.NO_LINKS;
import static org.jrdf.query.answer.json.JsonTestUtil.NO_VARIABLES;
import static org.jrdf.query.answer.json.JsonTestUtil.TEST_BINDINGS_1;
import static org.jrdf.query.answer.json.JsonTestUtil.TEST_BINDINGS_2;
import static org.jrdf.query.answer.json.JsonTestUtil.TEST_BINDINGS_3;
import static org.jrdf.query.answer.json.JsonTestUtil.TEST_VARIABLES;
import static org.jrdf.query.answer.json.JsonTestUtil.*;
import org.jrdf.util.test.MockFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

public class SparqlSelectJsonWriterUnitTest {
    private TypeValueArrayFactory factory = new TypeValueArrayFactoryImpl();
    private final MockFactory mockFactory = new MockFactory();
    private SelectAnswer selectAnswer;
    private Iterator<TypeValue[]> mockIterator;
    private StringWriter stringWriter;
    private Writer mockWriter;

    @SuppressWarnings({ "unchecked" })
    @Before
    public void setUp() throws Exception {
        selectAnswer = mockFactory.createMock(SelectAnswer.class);
        mockIterator = mockFactory.createMock(Iterator.class);
        mockWriter = mockFactory.createMock(Writer.class);
        stringWriter = new StringWriter();
    }

    @Test
    public void creationAndClosing() throws Exception {
        mockWriter.flush();
        expectLastCall();
        mockWriter.close();
        expectLastCall();

        mockFactory.replay();
        final SparqlJsonWriter writer = new SparqlSelectJsonWriter(mockWriter, NO_LINKS, NO_VARIABLES, mockIterator,
            0L);
        writer.flush();
        writer.close();

        mockFactory.verify();
    }

    @Test(expected = JSONException.class)
    public void wrapIOExceptionOnFlush() throws Exception {
        expect(selectAnswer.columnValuesIterator()).andReturn(mockIterator);
        expect(selectAnswer.numberOfTuples()).andReturn(0L);
        mockWriter.flush();
        expectLastCall().andThrow(new IOException());

        mockFactory.replay();
        final SparqlJsonWriter writer = new SparqlSelectJsonWriter(mockWriter, NO_LINKS, NO_VARIABLES, mockIterator,
            0L);
        writer.flush();

        mockFactory.verify();
    }

    @Test(expected = JSONException.class)
    public void wrapIOExceptionOnClose() throws Exception {
        expect(selectAnswer.columnValuesIterator()).andReturn(mockIterator);
        expect(selectAnswer.numberOfTuples()).andReturn(0L);
        mockWriter.close();
        expectLastCall().andThrow(new IOException());

        mockFactory.replay();
        final SparqlJsonWriter writer = new SparqlSelectJsonWriter(mockWriter, NO_LINKS, NO_VARIABLES, mockIterator,
            0L);
        writer.close();

        mockFactory.verify();
    }

    @Test
    public void emptyAnswer() throws Exception {
        checkAnswer(NO_LINKS, NO_VARIABLES, 0);
    }

    @Test
    public void noResults() throws Exception {
        checkAnswer(NO_LINKS, TEST_VARIABLES, 0);
    }

    @Test
    public void hasResults() throws Exception {
        checkAnswer(NO_LINKS, TEST_VARIABLES, -1, TEST_BINDINGS_1, TEST_BINDINGS_2, TEST_BINDINGS_3);
    }

    @Test
    public void maxRows() throws Exception {
        checkAnswer(NO_LINKS, TEST_VARIABLES, 1, TEST_BINDINGS_1, TEST_BINDINGS_2, TEST_BINDINGS_3);
    }

    public void checkAnswer(final String[] links, final String[] variables, final int numberOfResults,
        final Map<String, TypeValue>... bindings) throws JSONException {
        setupExpectationsForResults(variables, numberOfResults == -1 ? bindings.length : numberOfResults, bindings);
        mockFactory.replay();

        final SparqlJsonWriter writer = new SparqlSelectJsonWriter(stringWriter, links, variables, mockIterator,
            numberOfResults);
        writer.writeFullDocument();
        mockFactory.verify();

        final JSONObject head = new JSONObject(stringWriter.toString()).getJSONObject("head");
        checkJSONStringArrayValues(head, "vars", variables);
        final JSONObject results = new JSONObject(stringWriter.toString()).getJSONObject("results");
        checkBindings(results.getJSONArray("bindings"), numberOfResults, bindings);
    }

    private void setupExpectationsForResults(final String[] variables, final int numberOfResults,
        final Map<String, TypeValue>... results) {
        // Number of loops around the iterator
        if (numberOfResults > 0) {
            expect(mockIterator.hasNext()).andReturn(true).times(numberOfResults);
        }
        expect(mockIterator.hasNext()).andReturn(false).once();
        // Number of results
        for (int i = 0; i < numberOfResults; i++) {
            expect(mockIterator.next()).andReturn(factory.mapToArray(variables, results[i]));
        }
    }
}