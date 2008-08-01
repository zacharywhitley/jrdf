package org.jrdf.urql.analysis;

import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.urql.parser.node.Switch;
import org.jrdf.urql.parser.parser.ParserException;

public interface NumericExpressionAnalyser extends Switch {
    AttributeValuePair getSingleAvp() throws ParserException;
}
