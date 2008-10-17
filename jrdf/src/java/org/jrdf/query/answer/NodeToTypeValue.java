package org.jrdf.query.answer;

import org.jrdf.graph.Node;
import org.jrdf.graph.TypedNodeVisitor;
import org.jrdf.query.answer.xml.TypeValue;

public interface NodeToTypeValue extends TypedNodeVisitor {
    TypeValue convert(Node value);
}
