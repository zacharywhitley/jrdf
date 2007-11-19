package org.jrdf.util.bdb;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.jrdf.graph.local.mem.BlankNodeImpl;
import org.jrdf.graph.local.mem.LiteralImpl;
import org.jrdf.graph.local.mem.URIReferenceImpl;

import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: Nov 14, 2007
 * Time: 1:04:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodeBinding extends TupleBinding {
    public Object entryToObject(TupleInput tupleInput) {
        Object object;
        try {
            object = BlankNodeImpl.valueOf(tupleInput.readString());
        } catch (Exception e) {
            try {
                object = URI.create(tupleInput.readString());
            } catch (Exception ex) {
                object = new LiteralImpl(tupleInput.readString());
            }
        }
        return object;
    }

    public void objectToEntry(Object object, TupleOutput tupleOutput) {
        if (URIReferenceImpl.class.isAssignableFrom(object.getClass())) {
            URIReferenceImpl node = (URIReferenceImpl) object;
            tupleOutput.writeString(node.toString());
        } else if (BlankNodeImpl.class.isAssignableFrom(object.getClass())) {
            BlankNodeImpl node = (BlankNodeImpl) object;
            tupleOutput.writeString(node.toString());
        } else if (LiteralImpl.class.isAssignableFrom(object.getClass())) {
            LiteralImpl node = (LiteralImpl) object;
            tupleOutput.writeString(node.toString());
        } else {
            throw new IllegalArgumentException("Cannot persist class of type: " + object.getClass());
        }
    }
}
