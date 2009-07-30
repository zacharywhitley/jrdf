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
import static org.codehaus.jackson.JsonToken.END_OBJECT;
import static org.codehaus.jackson.JsonToken.FIELD_NAME;
import static org.codehaus.jackson.JsonToken.START_OBJECT;
import static org.codehaus.jackson.JsonToken.*;
import static org.jrdf.query.answer.SparqlProtocol.HEAD;
import static org.jrdf.query.answer.SparqlProtocol.LINK;
import static org.jrdf.query.answer.SparqlProtocol.VARS;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueFactoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;

public class SparqlAnswerJsonParserImpl implements SparqlAnswerJsonParser {
    private InputStreamReader reader;
    private JsonParser parser;
    private boolean parsedHead;
    private SparqlAnswerResultsJsonParser resultsParser;
    private LinkedHashSet<String> variables;
    private LinkedHashSet<String> links;

    public SparqlAnswerJsonParserImpl(InputStream inputStream) throws IOException {
        this.reader = new InputStreamReader(inputStream);
        this.parser = new JsonFactory().createJsonParser(inputStream);
        getVariables();
        this.resultsParser = new SparqlAnswerResultsJsonParserImpl(variables, parser, new TypeValueFactoryImpl());
    }

    public LinkedHashSet<String> getVariables() throws IOException {
        if (!parsedHead) {
            reallyGetHead();
        }
        return variables;
    }

    public LinkedHashSet<String> getLink() throws IOException {
        if (!parsedHead) {
            reallyGetHead();
        }
        return links;
    }

    public boolean hasNext() {
        return resultsParser.hasNext();
    }

    public TypeValue[] next() {
        return resultsParser.next();
    }

    public boolean close() {
        try {
            reader.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove from this iterator");
    }

    private void reallyGetHead() throws IOException {
        variables = new LinkedHashSet<String>();
        links = new LinkedHashSet<String>();
        if (parser.nextToken() == START_OBJECT && parser.nextToken() == FIELD_NAME &&
            parser.getCurrentName().equals(HEAD)) {
            while (parser.nextToken() != END_OBJECT) {
                getHeadValues();
            }
        }
        parsedHead = true;
    }

    private void getHeadValues() throws IOException {
        if (parser.getCurrentToken() == FIELD_NAME) {
            final String nextField = parser.getCurrentName();
            parseStringArray(nextField, VARS, variables);
            parseStringArray(nextField, LINK, links);
        }
    }

    private void parseStringArray(final String nextField, final String token, final LinkedHashSet<String> values)
        throws IOException {
        if (token.equals(nextField)) {
            if (parser.nextToken() == START_ARRAY) {
                while (parser.nextToken() != END_ARRAY) {
                    values.add(parser.getText());
                }
            }
        }
    }
}
