package org.jrdf.query.answer;

import org.jrdf.query.answer.xml.SparqlAnswerParserStream;
import org.jrdf.query.answer.xml.TypeValue;

import java.util.Iterator;

public class StreamingAnswerIterator implements Iterator<TypeValue[]> {
    private SparqlAnswerParserStream answerStream;

    public StreamingAnswerIterator(SparqlAnswerParserStream newAnswerStream) {
        this.answerStream = newAnswerStream;
    }

    public boolean hasNext() {
        return answerStream.hasMoreResults();
    }

    public TypeValue[] next() {
        return answerStream.getResults();
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove values from an answer iterator");
    }
}