package org.jrdf.urql.analysis;

import org.jrdf.graph.Node;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AVPOperation;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.AttributeValuePairImpl;
import org.jrdf.query.relation.mem.EqAVPOperation;
import org.jrdf.query.relation.mem.NeqAVPOperation;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.urql.builder.LiteralBuilder;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.AEMoreNumericExpression;
import org.jrdf.urql.parser.node.ANeMoreNumericExpression;
import org.jrdf.urql.parser.node.ARdfLiteralPrimaryExpression;
import org.jrdf.urql.parser.node.AVariable;
import org.jrdf.urql.parser.parser.ParserException;

public class NumericExpressionAnalyserImpl extends DepthFirstAdapter implements NumericExpressionAnalyser {
    private ParserException exception;
    private AVPOperation operation;
    private AttributeName attributeName;
    private Node value;
    private LiteralBuilder literalBuilder;

    public NumericExpressionAnalyserImpl(LiteralBuilder newLiteralBuilder) {
        this.literalBuilder = newLiteralBuilder;
    }

    public AttributeValuePair getSingleAvp() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        // TODO AN Revist node type creation!
        return new AttributeValuePairImpl(new AttributeImpl(attributeName, new ObjectNodeType()), value, operation);
    }

    @Override
    public void caseAEMoreNumericExpression(AEMoreNumericExpression node) {
        this.operation = new EqAVPOperation();
        node.getNumericExpression().apply(this);
    }

    @Override
    public void caseANeMoreNumericExpression(ANeMoreNumericExpression node) {
        this.operation = new NeqAVPOperation();
        node.getNumericExpression().apply(this);
    }

    @Override
    public void caseARdfLiteralPrimaryExpression(ARdfLiteralPrimaryExpression node) {
        try {
            this.value = literalBuilder.createLiteral(node);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAVariable(AVariable node) {
        this.attributeName = new VariableName(node.getVariablename().getText());
    }
}
