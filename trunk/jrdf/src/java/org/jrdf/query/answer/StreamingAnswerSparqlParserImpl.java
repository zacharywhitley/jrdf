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

package org.jrdf.query.answer;

import org.jrdf.util.ClosableIterator;

import javax.xml.stream.XMLStreamException;
import static java.util.Arrays.asList;
import java.util.LinkedHashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StreamingAnswerSparqlParserImpl implements StreamingAnswerSparqlParser {
    private BlockingQueue<Answer> answerQueue;
    private LinkedHashSet<String> variables;
    private boolean gotVariables;
    private boolean hasMore;
    private TypeValue[] results;
    private ClosableIterator<TypeValue[]> currentIterator;
    private Answer currentAnswer;

    public StreamingAnswerSparqlParserImpl(final Answer... answers) {
        this.hasMore = false;
        this.variables = new LinkedHashSet<String>();
        this.answerQueue = new LinkedBlockingQueue<Answer>();
        tryAddAnswers(answers);
        setupNextParser();
    }

    public void addAnswer(final Answer answer) {
        tryAddAnswers(answer);
        if (!hasMore) {
            setupNextParser();
        }
    }

    public LinkedHashSet<String> getVariables() {
        return variables;
    }

    public boolean hasNext() {
        return hasMore;
    }

    public TypeValue[] next() {
        if (currentIterator.hasNext()) {
            results = currentIterator.next();
        }
        hasMore = hasMore();
        return results;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean close() {
        if (currentIterator != null) {
            currentIterator.close();
        }
        answerQueue.clear();
        return true;
    }

    private void tryAddAnswers(final Answer... answers) {
        try {
            for (final Answer stream : answers) {
                this.answerQueue.put(stream);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    private void setupNextParser() {
        currentAnswer = answerQueue.poll();
        if (currentAnswer != null) {
            if (currentIterator != null) {
                currentIterator.close();
            }
            currentIterator = currentAnswer.columnValuesIterator();
            parseVariables();
            if (!hasMore) {
                hasMore = hasMore();
            }
        } else {
            currentAnswer = null;
            currentIterator = null;
        }
    }

    private void parseVariables() {
        if (!gotVariables) {
            variables.addAll(asList(currentAnswer.getVariableNames()));
            gotVariables = true;
        }
    }

    private boolean hasMore() {
        try {
            return getToNextStreamResult();
        } catch (XMLStreamException e) {
            return false;
        }
    }

    private boolean getToNextStreamResult() throws XMLStreamException {
        boolean gotNext = false;
        while (currentAnswer != null && !gotNext) {
            gotNext = currentIterator.hasNext();
            if (!gotNext) {
                setupNextParser();
            }
        }
        return gotNext;
    }
}