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

import org.jrdf.query.answer.SelectAnswer;
import org.jrdf.query.answer.xml.TypeValue;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.json.JSONException;
import org.json.JSONWriter;

import java.io.Writer;
import java.util.Iterator;

public class AnswerJsonWriterImpl implements AnswerJsonWriter {
    private SelectAnswer answer;
    private Iterator<TypeValue[]> iterator;
    private long maxRows;
    private JSONWriter jsonWriter;

    private AnswerJsonWriterImpl() {
    }

    public AnswerJsonWriterImpl(Writer writer, SelectAnswer answer) {
        checkNotNull(answer, writer);
        this.answer = answer;
        this.iterator = answer.columnValuesIterator();
        this.maxRows = answer.numberOfTuples();
        this.jsonWriter = new JSONWriter(writer);
    }

    public AnswerJsonWriterImpl(Writer writer, SelectAnswer answer, int maxRows) {
        this(writer, answer);
        this.maxRows = maxRows;
    }

    public boolean hasMoreResults() {
        return false;
    }

    public void writeStartDocument() throws JSONException {
        jsonWriter.object();
    }

    public void writeHead() throws JSONException {
        jsonWriter.key("head");
        jsonWriter.object().key("vars").array();
        for (final String variable : answer.getVariableNames()) {
            jsonWriter.value(variable);
        }
        jsonWriter.endArray();
        jsonWriter.endObject();
    }

    public void writeStartResults() {
    }

    public void writeEndResults() {
    }

    public void writeResult() {
    }

    public void writeEndDocument() throws JSONException {
        jsonWriter.endObject();
    }

    public void writeFullDocument() throws JSONException {
        writeStartDocument();
        writeHead();
        writeEndDocument();
    }

    public void flush() {
    }

    public void close() {
    }
}
