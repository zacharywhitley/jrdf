package org.jrdf.query.answer;

import org.jrdf.query.answer.xml.DatatypeType;
import static org.jrdf.query.answer.xml.DatatypeType.NONE;
import org.jrdf.query.answer.xml.SparqlAnswerParserStream;
import org.jrdf.query.answer.xml.TypeValue;

import java.util.Iterator;

public class StreamingAnswerIterator implements Iterator<String[]> {
    private SparqlAnswerParserStream answerStream;

    public StreamingAnswerIterator(SparqlAnswerParserStream newAnswerStream) {
        this.answerStream = newAnswerStream;
    }

    public boolean hasNext() {
        return answerStream.hasMoreResults();
    }

    public String[] next() {
        return convertResultsToString(answerStream.getResults());
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove values from an answer iterator");
    }

    private String[] convertResultsToString(TypeValue[] results) {
        String[] stringResults = new String[results.length];
        for (int i = 0; i < results.length; i++) {
            TypeValue result = results[i];
            StringBuffer stringResult = new StringBuffer();
            stringResult.append(result.getValue());
            if (result.getSuffixType() != NONE) {
                appendSuffix(result, stringResult);
            }
        }
        return stringResults;
    }

    private void appendSuffix(TypeValue result, StringBuffer stringResult) {
        if (result.getSuffixType().equals(DatatypeType.DATATYPE)) {
            stringResult.append("^^\"" + result.getSuffix() + "\"");
        } else if (result.getSuffixType().equals(DatatypeType.XML_LANG)) {
            stringResult.append("@@\"" + result.getSuffix() + "\"");
        }
    }
}