package org.jrdf.query.answer;

import static org.jrdf.query.answer.xml.SparqlResultType.BOOLEAN;
import org.jrdf.query.answer.xml.TypeValue;
import org.jrdf.query.answer.xml.TypeValueImpl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */

public class AskAnswerImpl implements AskAnswer, Serializable {
    private static final long serialVersionUID = 432026021050798815L;

    private long timeTaken;
    private boolean result;

    public AskAnswerImpl(long timeTaken, boolean result) {
        this.timeTaken = timeTaken;
        this.result = result;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public boolean getResult() {
        return result;
    }

    public long numberOfTuples() {
        return 1;
    }

    public String[] getVariableNames() {
        return new String[]{ASK_VARIABLE_NAME};
    }

    public String[][] getColumnValues() {
        return new String[][]{{Boolean.toString(result)}};
    }

    public Iterator<TypeValue[]> columnValuesIterator() {
        TypeValue typeValue = new TypeValueImpl(BOOLEAN, Boolean.toString(result));
        Set<TypeValue[]> set = new HashSet<TypeValue[]>();
        set.add(new TypeValue[]{typeValue});
        return set.iterator();
    }

    public String toString() {
        return ASK_VARIABLE_NAME + "\nValue " + result;
    }

    public void accept(AnswerVisitor visitor) {
        visitor.visitAskAnswer(this);
    }
}
