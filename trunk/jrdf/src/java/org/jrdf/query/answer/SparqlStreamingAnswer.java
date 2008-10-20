package org.jrdf.query.answer;

import org.jrdf.query.answer.xml.SparqlAnswerParserStream;
import org.jrdf.query.answer.xml.SparqlAnswerParserStreamImpl;
import org.jrdf.query.answer.xml.TypeValue;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;

// TODO AN/YF - Can we do time taken and number of tuples (maybe based on how much so far?)
public class SparqlStreamingAnswer implements Answer {
    private SparqlAnswerParserStream answerStream;
    private TypeValueToString typeValueToString = new TypeValueToStringImpl();

    public SparqlStreamingAnswer(InputStream inputStream) throws XMLStreamException, InterruptedException {
        this(new SparqlAnswerParserStreamImpl(inputStream));
    }

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

    public Iterator<TypeValue[]> columnValuesIterator() {
        return new StreamingAnswerIterator(answerStream);
    }

    // TODO AN/YF Remove - complete cut-and-past of AnswerImpl.
    public String[][] getColumnValues() {
        String table[][] = new String[(int) numberOfTuples()][answerStream.getVariables().size()];
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
}
