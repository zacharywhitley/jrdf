/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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

package org.jrdf.query.answer.xml;

import junit.framework.TestCase;
import static org.jrdf.query.answer.xml.SparqlResultType.BLANK_NODE;
import static org.jrdf.query.answer.xml.SparqlResultType.LITERAL;
import static org.jrdf.query.answer.xml.SparqlResultType.TYPED_LITERAL;
import static org.jrdf.query.answer.xml.SparqlResultType.URI_REFERENCE;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.net.URL;
import static java.util.Arrays.asList;
import java.util.LinkedHashSet;
import java.util.List;

public class SparqlAnswerParserImplUnitTest extends TestCase {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private static final TypeValueImpl R1C1 = new TypeValueImpl(BLANK_NODE, "r1");
    private static final TypeValueImpl R2C1 = new TypeValueImpl(BLANK_NODE, "r2");
    private static final TypeValueImpl R1C2 = new TypeValueImpl(URI_REFERENCE, "http://work.example.org/alice/");
    private static final TypeValueImpl R2C2 = new TypeValueImpl(URI_REFERENCE, "http://work.example.org/bob/");
    private static final TypeValueImpl R1C3 = new TypeValueImpl(LITERAL, "Alice");
    private static final TypeValueImpl R2C3 = new TypeValueImpl(LITERAL, "Bob", false, "en");
    private static final TypeValueImpl R1C4 = new TypeValueImpl(LITERAL, "");
    private static final TypeValueImpl R2C4 = new TypeValueImpl(URI_REFERENCE, "mailto:bob@work.example.org");
    private static final TypeValueImpl R1C5 = new TypeValueImpl();
    private static final TypeValueImpl R2C5 = new TypeValueImpl(TYPED_LITERAL, "30", true,
        "http://www.w3.org/2001/XMLSchema#integer");
    private static final TypeValueImpl R1C6 = new TypeValueImpl(TYPED_LITERAL,
        "&lt;p xmlns=\"http://www.w3.org/1999/xhtml\"&gt;My name is &lt;b&gt;alice&lt;/b&gt;&lt;/p&gt;", true,
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");
    private static final TypeValueImpl R2C6 = new TypeValueImpl();
    private static final TypeValueImpl R1C7 = new TypeValueImpl(BLANK_NODE, "r2");
    private static final TypeValueImpl R2C7 = new TypeValueImpl(BLANK_NODE, "r1");
    public static final LinkedHashSet<String> EXPECTED_VARIABLES = new LinkedHashSet<String>(asList("x", "hpage",
        "name", "mbox", "age", "blurb", "friend"));
    public static final List<TypeValueImpl> ROW_1 = asList(R1C1, R1C2, R1C3, R1C4, R1C5, R1C6, R1C7);
    public static final List<TypeValueImpl> ROW_2 = asList(R2C1, R2C2, R2C3, R2C4, R2C5, R2C6, R2C7);
    private SparqlAnswerParser parser;

    public void testParse() throws Exception {
        URL resource = getClass().getClassLoader().getResource("org/jrdf/query/answer/xml/data/output.xml");
        InputStream stream = resource.openStream();
        XMLStreamReader streamReader = INPUT_FACTORY.createXMLStreamReader(stream);
        parser = new SparqlAnswerParserImpl(streamReader);
        assertTrue(parser.hasMoreResults());
        assertEquals(EXPECTED_VARIABLES, parser.getVariables());
        checkHasMoreAndGetResult(ROW_1);
        checkHasMoreAndGetResult(ROW_2);
        assertFalse(parser.hasMoreResults());
        assertFalse(parser.hasMoreResults());
    }

    private void checkHasMoreAndGetResult(List<TypeValueImpl> row) {
        assertTrue(parser.hasMoreResults());
        assertTrue(parser.hasMoreResults());
        TypeValue[] results = parser.getResults();
        checkRow(results, row);
    }

    public static void checkRow(TypeValue[] actualResults, List<TypeValueImpl> execptedResults) {
        for (int i = 0; i < execptedResults.size(); i++) {
            assertEquals(execptedResults.get(i), actualResults[i]);
        }
    }
}
