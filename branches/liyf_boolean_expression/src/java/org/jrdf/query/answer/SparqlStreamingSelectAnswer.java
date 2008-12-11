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

package org.jrdf.query.answer;

import org.jrdf.query.answer.xml.parser.SparqlAnswerStreamParser;
import org.jrdf.query.answer.xml.parser.SparqlAnswerStreamParserImpl;
import org.jrdf.query.answer.xml.TypeValue;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;

// TODO AN/YF - Can we do time taken and number of tuples (maybe based on how much so far?)
public class SparqlStreamingSelectAnswer implements SelectAnswer {
    private SparqlAnswerStreamParser answerStreamParser;
    private TypeValueToString typeValueToString = new TypeValueToStringImpl();

    public SparqlStreamingSelectAnswer(InputStream inputStream) throws XMLStreamException, InterruptedException {
        this(new SparqlAnswerStreamParserImpl(inputStream));
    }

    public SparqlStreamingSelectAnswer(SparqlAnswerStreamParser answerStreamParser) {
        this.answerStreamParser = answerStreamParser;
    }

    public String[] getVariableNames() {
        LinkedHashSet<String> existingVariables = answerStreamParser.getVariables();
        String[] existingVariablesArray = existingVariables.toArray(new String[existingVariables.size()]);
        String[] variables = new String[existingVariables.size()];
        System.arraycopy(existingVariablesArray, 0, variables, 0, existingVariablesArray.length);
        return variables;
    }

    public Iterator<TypeValue[]> columnValuesIterator() {
        return new StreamingAnswerIterator(answerStreamParser);
    }

    // TODO AN/YF Remove - complete cut-and-past of AnswerImpl.
    public String[][] getColumnValues() {
        final LinkedHashSet<String> hashSet = answerStreamParser.getVariables();
        final int numberOfVariables = hashSet.size();
        final int numberOfTuples = (int) numberOfTuples();
        String table[][] = new String[numberOfTuples][numberOfVariables];
        int index = 0;
        Iterator<TypeValue[]> iterator = columnValuesIterator();
        while (iterator.hasNext()) {
            table[index] = typeValueToString.convert(iterator.next());
            index++;
        }
        return table;
    }

    public long numberOfTuples() {
        return -1;
    }

    public long getTimeTaken() {
        return -1;
    }

    public void accept(AnswerVisitor visitor) {
        visitor.visitSelectAnswer(this);
    }
}