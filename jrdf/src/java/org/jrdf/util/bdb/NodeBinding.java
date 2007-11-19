package org.jrdf.util.bdb;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.jrdf.graph.Node;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperImpl;
import org.jrdf.graph.local.mem.BlankNodeImpl;
import org.jrdf.graph.local.mem.LiteralImpl;
import org.jrdf.graph.local.mem.LocalizedNode;
import org.jrdf.graph.local.mem.URIReferenceImpl;
import org.jrdf.parser.ntriples.parser.NTripleUtilImpl;
import org.jrdf.parser.ntriples.parser.RegexLiteralMatcher;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;

public class NodeBinding extends TupleBinding {
    private RegexMatcherFactoryImpl regexFactory = new RegexMatcherFactoryImpl();
    private StringNodeMapper mapper = new StringNodeMapperImpl(new RegexLiteralMatcher(regexFactory,
        new NTripleUtilImpl(regexFactory)));

    public Object entryToObject(TupleInput tupleInput) {
        Object object;
        byte b = tupleInput.readByte();
        String str = tupleInput.readString();
        if (b == 0) {
            object = mapper.convertToBlankNode(str);
        } else if (b == 1) {
            object = mapper.convertToURIReference(str, tupleInput.readLong());
        } else if (b == 2) {
            object = mapper.convertToLiteral(str, tupleInput.readLong());
        } else {
            throw new IllegalArgumentException("Cannot read class type");
        }
        return object;
    }

    public void objectToEntry(Object object, TupleOutput tupleOutput) {
        if (BlankNodeImpl.class.isAssignableFrom(object.getClass())) {
            tupleOutput.writeByte(0);
            tupleOutput.writeString(mapper.convertToString((Node) object));
        } else if (URIReferenceImpl.class.isAssignableFrom(object.getClass())) {
            tupleOutput.writeByte(1);
            tupleOutput.writeString(mapper.convertToString((Node) object));
            tupleOutput.writeLong(((LocalizedNode) object).getId());
        } else if (LiteralImpl.class.isAssignableFrom(object.getClass())) {
            tupleOutput.writeByte(2);
            tupleOutput.writeString(mapper.convertToString((Node) object));
            tupleOutput.writeLong(((LocalizedNode) object).getId());
        } else {
            throw new IllegalArgumentException("Cannot persist class of type: " + object.getClass());
        }
    }
}
