package org.jrdf.util.bdb;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Node;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperImpl;
import org.jrdf.graph.local.mem.LiteralImpl;
import org.jrdf.graph.local.mem.LocalizedNode;
import org.jrdf.graph.local.mem.URIReferenceImpl;
import org.jrdf.parser.ntriples.parser.NTripleUtilImpl;
import org.jrdf.parser.ntriples.parser.RegexLiteralMatcher;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;

public class NodeBinding extends TupleBinding {
    private static final byte BLANK_NODE = 0;
    private static final byte URI_REFERENCE = 1;
    private static final byte LITERAL = 2;
    private RegexMatcherFactoryImpl regexFactory = new RegexMatcherFactoryImpl();
    private StringNodeMapper mapper = new StringNodeMapperImpl(new RegexLiteralMatcher(regexFactory,
        new NTripleUtilImpl(regexFactory)));

    public Object entryToObject(TupleInput tupleInput) {
        Object object;
        byte b = tupleInput.readByte();
        String str = tupleInput.readString();
        if (b == BLANK_NODE) {
            object = mapper.convertToBlankNode(str);
        } else if (b == URI_REFERENCE) {
            object = mapper.convertToURIReference(str, tupleInput.readLong());
        } else if (b == LITERAL) {
            object = mapper.convertToLiteral(str, tupleInput.readLong());
        } else {
            throw new IllegalArgumentException("Cannot read class type");
        }
        return object;
    }

    public void objectToEntry(Object object, TupleOutput tupleOutput) {
        if (BlankNode.class.isAssignableFrom(object.getClass())) {
            writeBlankNode(object, tupleOutput);
        } else if (URIReferenceImpl.class.isAssignableFrom(object.getClass())) {
            writeURIReference(object, tupleOutput);
        } else if (LiteralImpl.class.isAssignableFrom(object.getClass())) {
            writeLiteral(object, tupleOutput);
        } else {
            throw new IllegalArgumentException("Cannot persist class of type: " + object.getClass());
        }
    }

    private void writeBlankNode(Object object, TupleOutput tupleOutput) {
        tupleOutput.writeByte(BLANK_NODE);
        tupleOutput.writeString(mapper.convertToString((Node) object));
    }

    private void writeURIReference(Object object, TupleOutput tupleOutput) {
        tupleOutput.writeByte(URI_REFERENCE);
        tupleOutput.writeString(mapper.convertToString((Node) object));
        tupleOutput.writeLong(((LocalizedNode) object).getId());
    }

    private void writeLiteral(Object object, TupleOutput tupleOutput) {
        tupleOutput.writeByte(LITERAL);
        tupleOutput.writeString(mapper.convertToString((Node) object));
        tupleOutput.writeLong(((LocalizedNode) object).getId());
    }
}
