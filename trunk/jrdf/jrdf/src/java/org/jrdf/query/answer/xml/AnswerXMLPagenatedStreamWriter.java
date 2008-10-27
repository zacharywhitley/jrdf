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

import org.jrdf.query.answer.Answer;
import static org.jrdf.query.answer.xml.DatatypeType.NONE;
import static org.jrdf.query.answer.xml.SparqlResultType.UNBOUND;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import static javax.xml.XMLConstants.XML_NS_PREFIX;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class AnswerXMLPagenatedStreamWriter extends AbstractXMLStreamWriter implements AnswerXMLWriter {
    private long maxRows;
    private long count;
    private Answer answer;
    private Iterator<TypeValue[]> iterator;

    private AnswerXMLPagenatedStreamWriter() {
    }

    public AnswerXMLPagenatedStreamWriter(Answer answer) {
        checkNotNull(answer);
        this.answer = answer;
        this.iterator = answer.columnValuesIterator();
        this.maxRows = answer.numberOfTuples();
    }

    public AnswerXMLPagenatedStreamWriter(Answer answer, Writer writer) throws XMLStreamException {
        this(answer);
        this.streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
    }

    public AnswerXMLPagenatedStreamWriter(Answer answer, Writer writer, int maxRows) throws XMLStreamException {
        this(answer, writer);
        this.maxRows = maxRows;
    }


    public void setWriter(Writer writer) throws XMLStreamException, IOException {
        close();
        this.streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
    }

    public boolean hasMoreResults() {
        return iterator.hasNext() && ((maxRows == -1) || count < maxRows);
    }

    public void write() throws XMLStreamException {
        checkNotNull(streamWriter);
        doWrite();
    }

    public void write(Writer writer) throws XMLStreamException {
        streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
        doWrite();
    }

    public void addStream(InputStream stream) throws InterruptedException, XMLStreamException {
        throw new UnsupportedOperationException("Cannot add a stream to this writer.");
    }

    private void doWrite() throws XMLStreamException {
        writeStartDocument();
        writeVariables();
        writeAllResults();
        writeEndDocument();
    }

    public void writeVariables() throws XMLStreamException {
        streamWriter.writeStartElement(HEAD);
        for (String variable : answer.getVariableNames()) {
            streamWriter.writeStartElement(VARIABLE);
            streamWriter.writeAttribute(NAME, variable);
            streamWriter.writeEndElement();
        }
        streamWriter.writeEndElement();
    }

    protected void writeAllResults() throws XMLStreamException {
        writeStartResults();
        while (hasMoreResults()) {
            writeResult();
        }
        writeEndResults();
        streamWriter.flush();
    }

    // TODO AN/YF This is a duplicate of MultiAnswerXMLStreamQueueWriter except uses iterator instead of streamParser.
    // Same with next method
    public void writeResult() throws XMLStreamException {
        if (iterator.hasNext()) {
            streamWriter.writeStartElement(RESULT);
            TypeValue[] results = iterator.next();
            String[] currentVariables = answer.getVariableNames();
            int index = 0;
            for (TypeValue result : results) {
                String currentVariable = currentVariables[index];
                if (!result.getType().equals(UNBOUND)) {
                    writeBinding(result, currentVariable);
                }
                index++;
            }
            streamWriter.writeEndElement();
        }
        count++;
        streamWriter.flush();
    }

    private void writeBinding(TypeValue result, String currentVariable) throws XMLStreamException {
        streamWriter.writeStartElement(BINDING);
        streamWriter.writeAttribute(NAME, currentVariable);
        streamWriter.writeStartElement(result.getType().getXmlElementName());
        if (result.getSuffixType() != NONE) {
            if (result.getSuffixType().equals(DatatypeType.DATATYPE)) {
                streamWriter.writeAttribute(DATATYPE, result.getSuffix());
            } else if (result.getSuffixType().equals(DatatypeType.XML_LANG)) {
                streamWriter.writeAttribute(XML_NS_PREFIX + ":lang", result.getSuffix());
            }
        }
        streamWriter.writeCharacters(result.getValue());
        streamWriter.writeEndElement();
        streamWriter.writeEndElement();
    }
}
