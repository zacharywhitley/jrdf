package org.jrdf.query.answer;

import org.jrdf.query.answer.xml.AnswerXMLWriter;
import org.jrdf.query.answer.xml.SparqlAnswerParserStream;

import javax.xml.stream.XMLStreamException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.io.Writer;
import java.io.IOException;

public class SparqlStreamingAnswer implements Answer {
    private SparqlAnswerParserStream answerStream;

    public SparqlStreamingAnswer(SparqlAnswerParserStream answerStream) {
        this.answerStream = answerStream;
    }

    public String[] getVariableNames() {
        LinkedHashSet<String> existingVariables = answerStream.getVariables();
        String[] existingVariablesArray = existingVariables.toArray(new String[existingVariables.size()]);
        String[] variables = new String[existingVariables.size()];
        System.arraycopy(existingVariablesArray, 0, variables, 0, existingVariablesArray.length);
        return variables;
    }

    public Iterator<String[]> columnValuesIterator() {
        return new StreamingAnswerIterator(answerStream);
    }

    // TODO AN/YF Remove - complete cut-and-past of AnswerImpl.
    public String[][] getColumnValues() {
        String table[][] = new String[(int) numberOfTuples()][answerStream.getVariables().size()];
        int index = 0;
        Iterator<String[]> iterator = columnValuesIterator();
        while (iterator.hasNext()) {
            table[index] = iterator.next();
            index++;
        }
        return table;
    }

    public long numberOfTuples() {
        return 0;
    }

    public long getTimeTaken() {
        return 0;
    }

    public AnswerXMLWriter getXMLWriter(Writer writer) throws XMLStreamException, IOException {
        return null;
    }

    public AnswerXMLWriter getXMLWriter(Writer writer, int maxRows) throws XMLStreamException, IOException {
        return null;
    }
}
