package org.jrdf.query.answer;

import org.jrdf.graph.Node;
import org.jrdf.query.answer.xml.TypeValue;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.constants.NullaryNode;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AnswerIterator implements Iterator<TypeValue[]> {
    private Set<Attribute> heading;
    private Iterator<Tuple> tupleIterator;
    private NodeToTypeValue nodeToTypeValue = new NodeToTypeValueImpl();

    public AnswerIterator(Set<Attribute> newHeading, Iterator<Tuple> newTupleIterator) {
        this.heading = newHeading;
        this.tupleIterator = newTupleIterator;
    }

    public boolean hasNext() {
        return tupleIterator.hasNext();
    }

    public TypeValue[] next() {
        Tuple tuple = tupleIterator.next();
        return getDataWithValues(tuple.getAttributeValues());
    }

    private TypeValue[] getDataWithValues(Map<Attribute, ValueOperation> avps) {
        TypeValue[] results = new TypeValue[heading.size()];
        int index = 0;
        for (Attribute headingAttribute : heading) {
            Node value;
            try {
                value = avps.get(headingAttribute).getValue();
            } catch (Exception e) {
                value = NullaryNode.NULLARY_NODE;
            }
            results[index] = nodeToTypeValue.convert(value);
            index++;
        }
        return results;
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove values from an answer iterator");
    }
}
