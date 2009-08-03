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

import static org.jrdf.query.answer.SparqlResultType.BLANK_NODE;
import static org.jrdf.query.answer.SparqlResultType.LITERAL;
import static org.jrdf.query.answer.SparqlResultType.TYPED_LITERAL;
import static org.jrdf.query.answer.SparqlResultType.URI_REFERENCE;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueImpl;
import org.jrdf.query.answer.TypeValueArrayFactory;
import org.jrdf.query.answer.TypeValueArrayFactoryImpl;
import org.jrdf.vocabulary.XSD;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;

public class JsonTestUtil {
    public static final String[] NO_LINKS = {};
    public static final String[] NO_VARIABLES = {};
    public static final String[] TEST_VARIABLES = {"abc", "123", "doh", "ray", "me"};
    public static final Map<String, TypeValue> TEST_BINDINGS_1 = new HashMap<String, TypeValue>() {
        {
            put("abc", new TypeValueImpl(BLANK_NODE, "r1"));
            put("123", new TypeValueImpl(URI_REFERENCE, "http://work.example.org/alice/"));
            put("ray", new TypeValueImpl(TYPED_LITERAL, "123", true, XSD.INT.toString()));
            put("me", new TypeValueImpl(LITERAL, ""));
        }
    };
    public static final Map<String, TypeValue> TEST_BINDINGS_2 = new HashMap<String, TypeValue>() {
        {
            put("abc", new TypeValueImpl(BLANK_NODE, "r2"));
            put("123", new TypeValueImpl(URI_REFERENCE, "http://work.example.org/bob/"));
            put("ray", new TypeValueImpl(TYPED_LITERAL, "321", true, XSD.INT.toString()));
            put("doh", new TypeValueImpl(LITERAL, "qwerty"));
        }
    };
    public static final Map<String, TypeValue> TEST_BINDINGS_3 = new HashMap<String, TypeValue>() {
        {
            put("123", new TypeValueImpl(URI_REFERENCE, "http://work.example.org/charles/"));
            put("ray", new TypeValueImpl(TYPED_LITERAL, "231", true, XSD.INT.toString()));
            put("doh", new TypeValueImpl(LITERAL, "asdf", false, "en"));
        }
    };

    public static String getFullJsonDocument(final String[] links, final String[] variables,
        final Map<String, TypeValue>... bindings) throws JSONException {
        final List<TypeValue[]> values = convertToList(variables, bindings);
        final StringWriter stringWriter = new StringWriter();
        final AnswerJsonWriter answerJsonWriter = new AnswerJsonWriterImpl(stringWriter, links, variables,
            values.iterator(), bindings.length);
        answerJsonWriter.writeFullDocument();
        return stringWriter.toString();
    }

    private static List<TypeValue[]> convertToList(String[] variables, Map<String, TypeValue>... bindings) {
        final TypeValueArrayFactory arrayFactory = new TypeValueArrayFactoryImpl();
        final List<TypeValue[]> values = new ArrayList<TypeValue[]>();
        for (final Map<String, TypeValue> binding : bindings) {
            values.add(arrayFactory.mapToArray(variables, binding));
        }
        return values;
    }
}
