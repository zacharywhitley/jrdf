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

package org.jrdf.query.answer.xml.parser;

import org.apache.tools.ant.filters.StringInputStream;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.jrdf.query.answer.SparqlResultType.BLANK_NODE;
import static org.jrdf.query.answer.SparqlResultType.LITERAL;
import static org.jrdf.query.answer.SparqlResultType.URI_REFERENCE;
import static org.jrdf.query.answer.SparqlResultType.TYPED_LITERAL;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueFactory;
import org.jrdf.query.answer.TypeValueFactoryImpl;
import org.jrdf.query.answer.TypeValueImpl;
import org.jrdf.query.answer.SparqlResultType;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.HashMap;

public class SparqlXmlResultParserUnitTest {
    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();

    @Test
    public void bnodeBinding() throws Exception {
        checkParser("<binding name=\"x\"><bnode>r1</bnode></binding>", "x", new TypeValueImpl(BLANK_NODE, "r1"));
    }

    @Test
    public void uriBinding() throws Exception {
        checkParser("<binding name=\"hpage\"><uri>http://work.example.org/alice/</uri></binding>", "hpage",
            new TypeValueImpl(URI_REFERENCE, "http://work.example.org/alice/"));
    }

    @Test
    public void untypedLiteralBinding() throws Exception {
        checkParser("<binding name=\"name\"><literal>Alice</literal></binding>", "name",
            new TypeValueImpl(LITERAL, "Alice"));
    }

    @Test
    public void typedLiteralBinding() throws Exception {
        checkParser("<binding name=\"blurb\"><literal datatype=\"" +
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral\">&lt;p xmlns=\"http://www.w3.org/1999/xhtml\"&gt;" +
            "My name is &lt;b&gt;alice&lt;/b&gt;&lt;/p&gt;</literal></binding>", "blurb",
            new TypeValueImpl(TYPED_LITERAL,
                "<p xmlns=\"http://www.w3.org/1999/xhtml\">My name is <b>alice</b></p>", true,
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral"));
    }

    @Test
    public void langLiteralBinding() throws Exception {
        checkParser("<binding name=\"name\"><literal xml:lang=\"en\">Bob</literal></binding>", "name",
            new TypeValueImpl(SparqlResultType.LITERAL, "Bob", false, "en"));
    }

    private void checkParser(final String xmlBinding, final String variable, final TypeValue expectedValue)
        throws Exception {
        final InputStream stream = new StringInputStream(xmlBinding);
        final XMLStreamReader streamReader = XML_INPUT_FACTORY.createXMLStreamReader(stream);
        final TypeValueFactory typeFactory = new TypeValueFactoryImpl();
        streamReader.next();
        final SparqlXmlResultParser xmlParser = new SparqlXmlResultParserImpl(streamReader, typeFactory);
        final HashMap<String, TypeValue> binding = new HashMap<String, TypeValue>();
        xmlParser.getOneBinding(binding);
        assertThat(binding.containsKey(variable), is(true));
        assertThat(binding.get(variable), equalTo(expectedValue));
    }
}
