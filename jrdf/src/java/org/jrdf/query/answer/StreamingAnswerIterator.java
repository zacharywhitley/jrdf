package org.jrdf.query.answer;

import org.jrdf.query.answer.xml.parser.SparqlAnswerStreamParser;
import org.jrdf.query.answer.xml.TypeValue;

import java.util.Iterator;

public class StreamingAnswerIterator implements Iterator<TypeValue[]> {
    private SparqlAnswerStreamParser answerStreamParser;

    public StreamingAnswerIterator(SparqlAnswerStreamParser newAnswerStreamParser) {
        this.answerStreamParser = newAnswerStreamParser;
    }

    public boolean hasNext() {
        return answerStreamParser.hasMoreResults();
    }

    public TypeValue[] next() {
        return answerStreamParser.getResults();
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove values from an answer iterator");
    }
}