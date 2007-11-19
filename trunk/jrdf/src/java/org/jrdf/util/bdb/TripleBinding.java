package org.jrdf.util.bdb;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.global.TripleImpl;

public class TripleBinding extends TupleBinding {
    TupleBinding nodeBinding = new NodeBinding();

    public Object entryToObject(TupleInput input) {
        SubjectNode subject = (SubjectNode) nodeBinding.entryToObject(input);
        PredicateNode predicate = (PredicateNode) nodeBinding.entryToObject(input);
        ObjectNode object = (ObjectNode) nodeBinding.entryToObject(input);
        return new TripleImpl(subject, predicate, object);
    }

    public void objectToEntry(Object object, TupleOutput output) {
        Triple triple = (Triple) object;
        nodeBinding.objectToEntry(triple.getSubject(), output);
        nodeBinding.objectToEntry(triple.getPredicate(), output);
        nodeBinding.objectToEntry(triple.getObject(), output);
    }
}
