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

import org.codehaus.jackson.JsonParseException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.jrdf.query.answer.SparqlResultType.BOOLEAN;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueArrayFactory;
import org.jrdf.query.answer.TypeValueArrayFactoryImpl;
import org.jrdf.query.answer.TypeValueImpl;
import static org.jrdf.query.answer.json.JsonTestUtil.LINKS;
import static org.jrdf.query.answer.json.JsonTestUtil.NO_LINKS;
import static org.jrdf.query.answer.json.JsonTestUtil.TEST_BINDINGS_1;
import static org.jrdf.query.answer.json.JsonTestUtil.TEST_BINDINGS_2;
import static org.jrdf.query.answer.json.JsonTestUtil.TEST_VARIABLES;
import static org.jrdf.query.answer.json.JsonTestUtil.getFullJsonSelect;
import org.jrdf.util.test.AssertThrows;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.util.Arrays.asList;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

public class SparqlJsonParserImplUnitTest {
    private TypeValueArrayFactory factory = new TypeValueArrayFactoryImpl();

    @Test
    public void simpleBinding() throws Exception {
        final String results = getFullJsonSelect(NO_LINKS, TEST_VARIABLES, TEST_BINDINGS_1, TEST_BINDINGS_2);
        final InputStream inputStream = new ByteArrayInputStream(results.getBytes());
        final SparqlJsonParser jsonParser = new SparqlJsonParserImpl(inputStream);
        final LinkedHashSet<String> vars = new LinkedHashSet<String>(asList("abc", "123", "doh", "ray", "me"));
        assertThat(jsonParser.getLink(), is(Matchers.<String>empty()));
        assertThat(jsonParser.getVariables(), contains(TEST_VARIABLES));
        checkBinding(jsonParser, factory.mapToArray(vars, TEST_BINDINGS_1));
        checkBinding(jsonParser, factory.mapToArray(vars, TEST_BINDINGS_2));
        assertThat(jsonParser.hasNext(), is(false));
        checkThrowsExceptionBeyondResults(jsonParser);
    }

    @Test
    public void withLink() throws Exception {
        final String results = getFullJsonSelect(LINKS, TEST_VARIABLES);
        final InputStream inputStream = new ByteArrayInputStream(results.getBytes());
        final SparqlJsonParser jsonParser = new SparqlJsonParserImpl(inputStream);
        assertThat(jsonParser.getLink(), contains(LINKS));
        assertThat(jsonParser.getVariables(), contains(TEST_VARIABLES));
        assertThat(jsonParser.hasNext(), is(false));
        checkThrowsExceptionBeyondResults(jsonParser);
    }

    @Test
    public void simpleAsk() throws Exception {
        final String emptyHeadAndTrue = "{\"head\": {}, \"boolean\" : true}";
        final TypeValue expectedFalseResult = new TypeValueImpl(BOOLEAN, "true");
        final SparqlJsonParser parser = checkAsk(emptyHeadAndTrue, expectedFalseResult);
        checkThrowsExceptionBeyondResults(parser);
    }

    @Test
    public void askWithVariables() throws Exception {
        final String resultWithLinkVarsAndFalse = "{\"head\":{\"link\":[],\"vars\":[]},\"boolean\":false}";
        final TypeValue expectedFalseResult = new TypeValueImpl(BOOLEAN, "false");
        final SparqlJsonParser parser = checkAsk(resultWithLinkVarsAndFalse, expectedFalseResult);
        checkThrowsExceptionBeyondResults(parser);
    }

    @Test(expected = IllegalStateException.class)
    public void parseableButBadBooleanValueThrowsIllegalState() throws Exception {
        final String resultWithFred = "{\"head\":{\"link\":[],\"vars\":[]},\"boolean\":null}";
        checkAsk(resultWithFred);
    }

    @Test(expected = JsonParseException.class)
    public void unparseableBooleanValueThrowsIllegalState() throws Exception {
        final String resultWithFred = "{\"head\":{\"link\":[],\"vars\":[]},\"boolean\":fred}";
        checkAsk(resultWithFred);
    }

    private SparqlJsonParser checkAsk(final String results, final TypeValue... expectedValues) throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(results.getBytes());
        final SparqlJsonParser jsonParser = new SparqlJsonParserImpl(inputStream);
        assertThat(jsonParser.getLink(), is(Matchers.<String>empty()));
        assertThat(jsonParser.getVariables(), is(Matchers.<String>empty()));
        checkBinding(jsonParser, expectedValues);
        return jsonParser;
    }

    private void checkBinding(final SparqlJsonParser jsonParser, final TypeValue... expectedValues) {
        assertThat(jsonParser.hasNext(), is(true));
        assertThat(jsonParser.hasNext(), is(true));
        assertThat(jsonParser.next(), arrayContaining(expectedValues));
    }

    private void checkThrowsExceptionBeyondResults(final SparqlJsonParser jsonParser) {
        AssertThrows.assertThrows(NoSuchElementException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                jsonParser.next();
            }
        });
    }
}
