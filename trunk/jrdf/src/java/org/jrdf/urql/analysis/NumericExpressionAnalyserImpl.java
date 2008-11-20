package org.jrdf.urql.analysis;

import org.jrdf.graph.AnyNode;
import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AVPOperation;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.BoundAVPOperation;
import org.jrdf.query.relation.mem.EqAVPOperation;
import org.jrdf.query.relation.mem.StrAVPOperation;
import org.jrdf.query.relation.mem.ValueOperationImpl;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PositionalNodeType;
import org.jrdf.urql.builder.LiteralBuilder;
import org.jrdf.urql.builder.URIReferenceBuilder;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.ABoundBuiltincall;
import org.jrdf.urql.parser.node.AIriRefIriRefOrPrefixedName;
import org.jrdf.urql.parser.node.APrefixedNameIriRefOrPrefixedName;
import org.jrdf.urql.parser.node.ARdfLiteralPrimaryExpression;
import org.jrdf.urql.parser.node.AStrBuiltincall;
import org.jrdf.urql.parser.node.AVariable;
import org.jrdf.urql.parser.parser.ParserException;

import java.util.HashMap;
import java.util.Map;

public class NumericExpressionAnalyserImpl extends DepthFirstAdapter implements NumericExpressionAnalyser {
    private ParserException exception;
    private AVPOperation operation;
    private AttributeName attributeName;
    private Node value;
    private LiteralBuilder literalBuilder;
    private VariableCollector collector;
    private URIReferenceBuilder uriBuilder;

    public NumericExpressionAnalyserImpl(LiteralBuilder newLiteralBuilder, VariableCollector collector,
                                         URIReferenceBuilder uriBuilder) {
        this.literalBuilder = newLiteralBuilder;
        this.collector = collector;
        this.uriBuilder = uriBuilder;
    }

    public Map<Attribute, ValueOperation> getSingleAvp() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        Map<Attribute, ValueOperation> returnValue = new HashMap<Attribute, ValueOperation>();
        final Map<AttributeName, PositionalNodeType> namePosMap = collector.getAttributes();
        NodeType type = namePosMap.get(attributeName);
        type = (type != null) ? type : new ObjectNodeType();
        Attribute attribute = new AttributeImpl(attributeName, type);
        ValueOperation vo = new ValueOperationImpl(value, operation);
        returnValue.put(attribute, vo);
        collector.addConstraints(returnValue);
        return returnValue;
    }

    @Override
    public void caseABoundBuiltincall(ABoundBuiltincall node) {
        this.operation = BoundAVPOperation.BOUND;
        node.getBracketedVar().apply(this);
    }

    @Override
    public void caseARdfLiteralPrimaryExpression(ARdfLiteralPrimaryExpression node) {
        try {
            this.operation = EqAVPOperation.EQUALS;
            this.value = literalBuilder.createLiteral(node);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAStrBuiltincall(AStrBuiltincall node) {
        this.operation = StrAVPOperation.STR;
        node.getBracketedExpression().apply(this);
    }

    @Override
    public void caseAVariable(AVariable node) {
        this.attributeName = new VariableName(node.getVariablename().getText());
        this.value = AnyNode.ANY_NODE;
    }

    @Override
    public void caseAIriRefIriRefOrPrefixedName(AIriRefIriRefOrPrefixedName node) {
        try {
            this.value = uriBuilder.createURIReference(node);
            this.operation = EqAVPOperation.EQUALS;
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAPrefixedNameIriRefOrPrefixedName(APrefixedNameIriRefOrPrefixedName node) {
        try {
            this.value = uriBuilder.createURIReference(node);
            this.operation = EqAVPOperation.EQUALS;
        } catch (ParserException e) {
            exception = e;
        }
    }
}
