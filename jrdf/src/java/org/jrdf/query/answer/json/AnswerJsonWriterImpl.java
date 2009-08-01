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

import org.jrdf.query.answer.DatatypeType;
import static org.jrdf.query.answer.SparqlProtocol.BINDINGS;
import static org.jrdf.query.answer.SparqlProtocol.DATATYPE;
import static org.jrdf.query.answer.SparqlProtocol.HEAD;
import static org.jrdf.query.answer.SparqlProtocol.JSON_XML_LANG;
import static org.jrdf.query.answer.SparqlProtocol.RESULTS;
import static org.jrdf.query.answer.SparqlProtocol.TYPE;
import static org.jrdf.query.answer.SparqlProtocol.VALUE;
import static org.jrdf.query.answer.SparqlProtocol.VARS;
import static org.jrdf.query.answer.SparqlResultType.UNBOUND;
import org.jrdf.query.answer.TypeValue;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.json.JSONException;
import org.json.JSONWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class AnswerJsonWriterImpl implements AnswerJsonWriter {
    private Writer writer;
    private String[] variableNames;
    private Iterator<TypeValue[]> iterator;
    private long maxRows;
    private long count;
    private JSONWriter jsonWriter;

    private AnswerJsonWriterImpl() {
    }

    public AnswerJsonWriterImpl(Writer writer, final String[] variableNames, final Iterator<TypeValue[]> iterator,
        final long maxRows) {
        checkNotNull(writer, variableNames, iterator);
        this.writer = writer;
        this.variableNames = variableNames;
        this.iterator = iterator;
        this.maxRows = maxRows;
        this.jsonWriter = new JSONWriter(this.writer);
    }

    public boolean hasMoreResults() {
        return iterator.hasNext() && ((maxRows == -1) || count < maxRows);
    }

    public void writeStartDocument() throws JSONException {
        jsonWriter.object();
    }

    public void writeHead() throws JSONException {
        jsonWriter.key(HEAD);
        jsonWriter.object().key(VARS).array();
        for (final String variable : variableNames) {
            jsonWriter.value(variable);
        }
        jsonWriter.endArray();
        jsonWriter.endObject();
    }

    public void writeStartResults() throws JSONException {
        jsonWriter.key(RESULTS);
        jsonWriter.object().key(BINDINGS).array();
    }

    public void writeEndResults() throws JSONException {
        jsonWriter.endArray();
        jsonWriter.endObject();
    }

    protected void writeAllResults() throws JSONException {
        writeStartResults();
        while (hasMoreResults()) {
            writeResult();
        }
        writeEndResults();
    }

    public void writeResult() throws JSONException {
        final String[] currentVariables = variableNames;
        if (iterator.hasNext()) {
            writeResult(currentVariables, iterator.next());
        }
        count++;
    }

    private void writeResult(String[] currentVariables, TypeValue[] results) throws JSONException {
        jsonWriter.object();
        int index = 0;
        for (TypeValue result : results) {
            writeBinding(result, currentVariables[index]);
            index++;
        }
        jsonWriter.endObject();
    }

    private void writeBinding(TypeValue result, String currentVariable) throws JSONException {
        if (!result.getType().equals(UNBOUND)) {
            realWriteBinding(result, currentVariable);
        }
    }

    private void realWriteBinding(TypeValue result, String currentVariable) throws JSONException {
        jsonWriter.key(currentVariable);
        jsonWriter.object();
        jsonWriter.key(VALUE);
        jsonWriter.value(result.getValue());
        jsonWriter.key(TYPE);
        jsonWriter.value(result.getType().toString());
        if (DatatypeType.NONE != result.getSuffixType()) {
            writeDatatypes(result);
        }
        jsonWriter.endObject();
    }

    private void writeDatatypes(TypeValue result) throws JSONException {
        if (DatatypeType.DATATYPE == result.getSuffixType()) {
            jsonWriter.key(DATATYPE);
        }
        if (DatatypeType.XML_LANG == result.getSuffixType()) {
            jsonWriter.key(JSON_XML_LANG);
        }
        jsonWriter.value(result.getSuffix());
    }

    public void writeEndDocument() throws JSONException {
        jsonWriter.endObject();
    }

    public void writeFullDocument() throws JSONException {
        writeStartDocument();
        writeHead();
        writeAllResults();
        writeEndDocument();
    }

    public void flush() throws JSONException {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    public void close() throws JSONException {
        try {
            writer.close();
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }
}
