package org.jrdf.urql.analysis;

import org.jrdf.graph.AnyNode;
import org.jrdf.graph.Node;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.LangOperator;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.expression.logic.TrueExpression;
import org.jrdf.query.expression.logic.FalseExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.constants.NullaryAttribute;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AVPOperation;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.BoundAVPOperation;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;
import org.jrdf.query.relation.mem.LangAVPOperator;
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
import org.jrdf.urql.parser.node.ALangBuiltincall;
import org.jrdf.urql.parser.node.APrefixedNameIriRefOrPrefixedName;
import org.jrdf.urql.parser.node.ARdfLiteralPrimaryExpression;
import org.jrdf.urql.parser.node.AStrBuiltincall;
import org.jrdf.urql.parser.node.ATrueBooleanLiteral;
import org.jrdf.urql.parser.node.AVariable;
import org.jrdf.urql.parser.node.AFalseBooleanLiteral;
import org.jrdf.urql.parser.parser.ParserException;

import java.util.HashMap;
import java.util.Map;

public class NumericExpressionAnalyserImpl<V extends ExpressionVisitor> extends DepthFirstAdapter
    implements NumericExpressionAnalyser {
    private ParserException exception;
    private AVPOperation operation;
    private AttributeName attributeName;
    private Node value;
    private LiteralBuilder literalBuilder;
    private VariableCollector collector;
    private URIReferenceBuilder uriBuilder;
    private Expression<V> expression;

    public NumericExpressionAnalyserImpl(LiteralBuilder newLiteralBuilder, VariableCollector collector,
                                         URIReferenceBuilder uriBuilder) {
        this.literalBuilder = newLiteralBuilder;
        this.collector = collector;
        this.uriBuilder = uriBuilder;
    }

    public Expression<V> getExpression() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        return expression;
    }

    private Map<Attribute, ValueOperation> getSingleAvp() throws ParserException {
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
        node.getBracketedVar().apply(this);
        this.operation = BoundAVPOperation.BOUND;
        try {
            this.expression = new BoundOperator<V>(getSingleAvp());
        } catch (ParserException e) {
            this.exception = e;
        }
    }

    @Override
    public void caseALangBuiltincall(ALangBuiltincall node) {
        node.getBracketedExpression().apply(this);
        this.operation = LangAVPOperator.LANG;
        try {
            this.expression = new LangOperator<V>(getSingleAvp());
        } catch (ParserException e) {
            this.exception = e;
        }
    }

    @Override
    public void caseARdfLiteralPrimaryExpression(ARdfLiteralPrimaryExpression node) {
        try {
            this.operation = EQUALS;
            this.value = literalBuilder.createLiteral(node);
            this.expression = new SingleValue<V>(getSingleAvp());
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAStrBuiltincall(AStrBuiltincall node) {
        try {
            node.getBracketedExpression().apply(this);
            this.operation = StrAVPOperation.STR;
            this.expression = new StrOperator<V>(getSingleAvp());
        } catch (ParserException e) {
            this.exception = e;
        }
    }

    @Override
    public void caseAVariable(AVariable node) {
        this.attributeName = new VariableName(node.getVariablename().getText());
        this.value = AnyNode.ANY_NODE;
        this.operation = EQUALS;
        try {
            this.expression = new SingleValue<V>(getSingleAvp());
        } catch (ParserException e) {
            this.exception = e;
        }
    }

    @Override
    public void caseAIriRefIriRefOrPrefixedName(AIriRefIriRefOrPrefixedName node) {
        try {
            this.operation = EQUALS;
            this.value = uriBuilder.createURIReference(node);
            this.expression = new SingleValue<V>(getSingleAvp());
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAPrefixedNameIriRefOrPrefixedName(APrefixedNameIriRefOrPrefixedName node) {
        try {
            this.operation = EQUALS;
            this.value = uriBuilder.createURIReference(node);
            this.expression = new SingleValue<V>(getSingleAvp());
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseATrueBooleanLiteral(ATrueBooleanLiteral node) {
        try {
            if (attributeName == null) {
                attributeName = NullaryAttribute.NULLARY_ATTRIBUTE.getAttributeName();
            }
            this.operation = EQUALS;
            this.value = literalBuilder.createLiteral(node);
            this.expression = new TrueExpression<V>(getSingleAvp());
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAFalseBooleanLiteral(AFalseBooleanLiteral node) {
        try {
            if (attributeName == null) {
                attributeName = NullaryAttribute.NULLARY_ATTRIBUTE.getAttributeName();
            }
            this.operation = EQUALS;
            this.value = literalBuilder.createLiteral(node);
            this.expression = new FalseExpression<V>(getSingleAvp());
        } catch (ParserException e) {
            exception = e;
        }
    }
}
