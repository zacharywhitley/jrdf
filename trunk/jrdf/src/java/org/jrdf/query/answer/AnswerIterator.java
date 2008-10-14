package org.jrdf.query.answer;

import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.graph.Node;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AnswerIterator implements Iterator<String[]> {
    private Set<Attribute> heading;
    private Iterator<Tuple> tupleIterator;

    public AnswerIterator(Set<Attribute> newHeading, Iterator<Tuple> newTupleIterator) {
        this.heading = newHeading;
        this.tupleIterator = newTupleIterator;
    }

    public boolean hasNext() {
        return tupleIterator.hasNext();
    }

    public String[] next() {
        Tuple tuple = tupleIterator.next();
        return getDataWithValues(tuple.getAttributeValues());
    }

    private String[] getDataWithValues(Map<Attribute, ValueOperation> avps) {
        String[] results = new String[heading.size()];
        int index = 0;
        for (Attribute headingAttribute : heading) {
            Node value = avps.get(headingAttribute).getValue();
            results[index] = value.toString();
            index++;
        }
        return results;
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove values from an answer iterator");
    }
}
