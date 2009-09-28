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

import javax.xml.stream.XMLStreamException;
import java.util.LinkedHashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StreamingAnswerSparqlParserImpl implements StreamingAnswerSparqlParser {
    private SparqlParser parser;
    private BlockingQueue<Answer> answerQueue;
    private LinkedHashSet<String> variables;
    private LinkedHashSet<String> link;
    private boolean gotLink;
    private boolean gotVariables;
    private boolean hasMore;
    private TypeValue[] results;

    public StreamingAnswerSparqlParserImpl(Answer... streams) {
        this.hasMore = false;
        this.variables = new LinkedHashSet<String>();
        this.answerQueue = new LinkedBlockingQueue<Answer>();
        tryAddStreams(streams);
        setupNextParser();
    }

    public void addAnswer(Answer answer) throws InterruptedException {
        answerQueue.put(answer);
        if (!hasMore) {
            setupNextParser();
        }
    }

    public LinkedHashSet<String> getVariables() {
        return variables;
    }

    public LinkedHashSet<String> getLink() {
        return link;
    }

    public boolean hasNext() {
        return hasMore;
    }

    public TypeValue[] next() {
        if (parser.hasNext()) {
            results = parser.next();
        }
        hasMore = hasMore();
        return results;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean close() {
        if (parser != null) {
            parser.close();
        }
        answerQueue.clear();
        return true;
    }

    private void tryAddStreams(Answer... streams) {
        try {
            for (final Answer stream : streams) {
                this.answerQueue.put(stream);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    private void setupNextParser() {
        final Answer currentAnswer = answerQueue.poll();
        if (currentAnswer != null) {
            if (parser != null) {
                parser.close();
            }
            parseVariables();
            parseHead();
            if (!hasMore) {
                hasMore = hasMore();
            }
        } else {
            parser = null;
        }
    }

    private void parseVariables() {
        if (!gotVariables) {
            variables = parser.getVariables();
            gotVariables = true;
        }
    }

    private void parseHead() {
        if (!gotLink) {
            link = parser.getLink();
            gotLink = true;
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
        while (parser != null && !gotNext) {
            gotNext = parser.hasNext();
            if (!gotNext) {
                setupNextParser();
            }
        }
        return gotNext;
    }
}