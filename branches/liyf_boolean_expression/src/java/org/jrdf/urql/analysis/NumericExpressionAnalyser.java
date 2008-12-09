package org.jrdf.urql.analysis;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.urql.parser.node.Switch;
import org.jrdf.urql.parser.parser.ParserException;

import java.util.Map;

public interface NumericExpressionAnalyser extends Switch {
    Map<Attribute, ValueOperation> getSingleAvp() throws ParserException;
}
