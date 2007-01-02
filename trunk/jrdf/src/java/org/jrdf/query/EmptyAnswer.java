package org.jrdf.query;

import java.io.Serializable;

public final class EmptyAnswer implements Answer, Serializable {
    private static final long serialVersionUID = -7374613298128439580L;

    /**
     * An empty answer the returns no columns, values and 0 time taken.
     */
    public static final Answer EMPTY_ANSWER = new EmptyAnswer();

    private EmptyAnswer() {
    }

    public String[] getColumnNames() {
        return new String[0];
    }

    public String[][] getColumnValues() {
        return new String[0][];
    }

    public long numberOfTuples() {
        return 0;
    }

    public long getTimeTaken() {
        return 0;
    }
}
