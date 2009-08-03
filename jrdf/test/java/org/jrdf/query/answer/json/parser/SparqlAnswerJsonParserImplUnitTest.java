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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueArrayFactory;
import org.jrdf.query.answer.TypeValueArrayFactoryImpl;
import static org.jrdf.query.answer.json.JsonTestUtil.NO_LINKS;
import static org.jrdf.query.answer.json.JsonTestUtil.TEST_BINDINGS_1;
import static org.jrdf.query.answer.json.JsonTestUtil.TEST_BINDINGS_2;
import static org.jrdf.query.answer.json.JsonTestUtil.TEST_VARIABLES;
import static org.jrdf.query.answer.json.JsonTestUtil.getFullJsonDocument;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import static java.util.Arrays.asList;
import java.util.LinkedHashSet;

public class SparqlAnswerJsonParserImplUnitTest {
    private TypeValueArrayFactory factory = new TypeValueArrayFactoryImpl();

    @Test
    public void simpleBinding() throws Exception {
        String results = getFullJsonDocument(NO_LINKS, TEST_VARIABLES, TEST_BINDINGS_1, TEST_BINDINGS_2);
        final byte[] bytes = results.getBytes();
        final SparqlAnswerJsonParser jsonParser = new SparqlAnswerJsonParserImpl(new ByteArrayInputStream(bytes));
        final LinkedHashSet<String> vars = new LinkedHashSet<String>();
        vars.addAll(asList("abc", "123", "doh", "ray", "me"));
        assertThat(jsonParser.getVariables(), equalTo(vars));
        testABinding(jsonParser, factory.mapToArray(vars, TEST_BINDINGS_1));
        testABinding(jsonParser, factory.mapToArray(vars, TEST_BINDINGS_2));
        assertThat(jsonParser.hasNext(), is(false));
    }

    @Test
    public void withLink() throws Exception {
        String results = "{\"head\": { \"link\": [ \"http://www.w3.org/TR/rdf-sparql-XMLres/example.rq\" ]," +
            " \"vars\": [ \"x\", \"hpage\", \"name\", \"mbox\", \"age\", \"blurb\", \"friend\" ] },\n" +
            " \"results\": { \"bindings\": [ {} ] }}";
        final byte[] bytes = results.getBytes();
        final SparqlAnswerJsonParser jsonParser = new SparqlAnswerJsonParserImpl(new ByteArrayInputStream(bytes));
        final LinkedHashSet<String> vars = new LinkedHashSet<String>();
        vars.addAll(asList("x", "hpage", "name", "mbox", "age", "blurb", "friend"));
        final LinkedHashSet<String> links = new LinkedHashSet<String>();
        links.addAll(asList("http://www.w3.org/TR/rdf-sparql-XMLres/example.rq"));
        assertThat(jsonParser.getVariables(), equalTo(vars));
        assertThat(jsonParser.getLink(), equalTo(links));
        assertThat(jsonParser.hasNext(), is(false));
    }

    private void testABinding(SparqlAnswerJsonParser jsonParser, final TypeValue[] expectedValues) {
        assertThat(jsonParser.hasNext(), is(true));
        assertThat(jsonParser.next(), equalTo(expectedValues));
    }
}
