package org.jrdf.urql.analysis;

import org.jrdf.graph.Node;
import org.jrdf.query.expression.EmptyOperator;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AVPOperation;
import org.jrdf.query.relation.mem.EqAVPOperation;
import org.jrdf.query.relation.mem.NeqAVPOperation;
import org.jrdf.urql.builder.LiteralBuilder;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.AAdditiveExpression;
import org.jrdf.urql.parser.node.ABracketedExpression;
import org.jrdf.urql.parser.node.ABracketedExpressionConstraint;
import org.jrdf.urql.parser.node.ABuiltincallConstraint;
import org.jrdf.urql.parser.node.ABuiltincallPrimaryExpression;
import org.jrdf.urql.parser.node.AConditionalAndExpression;
import org.jrdf.urql.parser.node.AEMoreNumericExpression;
import org.jrdf.urql.parser.node.AFilterPattern;
import org.jrdf.urql.parser.node.ALangLiteralRdfLiteral;
import org.jrdf.urql.parser.node.AMoreConditionalAndExpression;
import org.jrdf.urql.parser.node.AMoreValueLogical;
import org.jrdf.urql.parser.node.AMultiplicativeExpression;
import org.jrdf.urql.parser.node.ANeMoreNumericExpression;
import org.jrdf.urql.parser.node.ANumericExpression;
import org.jrdf.urql.parser.node.ARdfLiteralPrimaryExpression;
import org.jrdf.urql.parser.node.ARelationalExpression;
import org.jrdf.urql.parser.node.AStrBuiltincall;
import org.jrdf.urql.parser.node.ATypedLiteralRdfLiteral;
import org.jrdf.urql.parser.node.AUntypedLiteralRdfLiteral;
import org.jrdf.urql.parser.node.AValueLogical;
import org.jrdf.urql.parser.node.AVariable;
import org.jrdf.urql.parser.node.AVariablePrimaryExpression;
import org.jrdf.urql.parser.node.PMoreValueLogical;
import org.jrdf.urql.parser.parser.ParserException;

import java.util.LinkedList;

public class FilterAnalyserImpl extends DepthFirstAdapter implements FilterAnalyser {
    private Expression<ExpressionVisitor> expression = new EmptyOperator<ExpressionVisitor>();
    private ParserException exception;
    private AVPOperation operation;
    private AttributeName attributeName;
    private Node value;
    private LiteralBuilder literalBuilder;

    public FilterAnalyserImpl(LiteralBuilder newLiteralBuilder) {
        this.literalBuilder = newLiteralBuilder;
    }

    public Expression<ExpressionVisitor> getExpression() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        System.err.println("Operation: " + operation);
        System.err.println("Attribute: " + attributeName);
        System.err.println("Value: " + value);
        return expression;
    }

    @Override
    public void caseAFilterPattern(AFilterPattern node) {
        node.getConstraint().apply(this);
    }

    @Override
    public void caseABuiltincallConstraint(ABuiltincallConstraint node) {
        node.getBuiltincall().apply(this);
    }

    @Override
    public void caseABracketedExpressionConstraint(ABracketedExpressionConstraint node) {
        node.getBracketedExpression().apply(this);
    }

    @Override
    public void caseAMoreConditionalAndExpression(AMoreConditionalAndExpression node) {
        node.getConditionalAndExpression().apply(this);
    }

    @Override
    public void caseABracketedExpression(ABracketedExpression node) {
        node.getConditionalOrExpression().apply(this);
    }

    @Override
    public void caseAConditionalAndExpression(AConditionalAndExpression node) {
        node.getValueLogical().apply(this);
        LinkedList<PMoreValueLogical> logicals = node.getMoreValueLogical();
        for (PMoreValueLogical logical : logicals) {
            logical.apply(this);
        }
    }

    @Override
    public void caseAValueLogical(AValueLogical node) {
        node.getRelationalExpression().apply(this);
    }

    @Override
    public void caseAMoreValueLogical(AMoreValueLogical node) {
        node.getValueLogical().apply(this);
    }

    @Override
    public void caseARelationalExpression(ARelationalExpression node) {
        node.getNumericExpression().apply(this);
        if (node.getMoreNumericExpression() != null) {
            node.getMoreNumericExpression().apply(this);
        }
    }

    @Override
    public void caseANumericExpression(ANumericExpression node) {
        node.getAdditiveExpression().apply(this);
    }

    @Override
    public void caseAAdditiveExpression(AAdditiveExpression node) {
        node.getMultiplicativeExpression().apply(this);
    }

    @Override
    public void caseAMultiplicativeExpression(AMultiplicativeExpression node) {
        node.getPrimaryExpression().apply(this);
    }

    @Override
    public void caseABuiltincallPrimaryExpression(ABuiltincallPrimaryExpression node) {
        node.getBuiltincall().apply(this);
    }

    @Override
    public void caseAVariablePrimaryExpression(AVariablePrimaryExpression node) {
        node.getVariable().apply(this);
    }

    @Override
    public void caseAStrBuiltincall(AStrBuiltincall node) {
        node.getBracketedExpression().apply(this);
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
            value = literalBuilder.createLiteral(node);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAVariable(AVariable node) {
        this.attributeName = new VariableName(node.getVariablename().getText());
    }

    @Override
    public void caseALangLiteralRdfLiteral(ALangLiteralRdfLiteral node) {
        System.err.println("Lang literal: " + node.getLanguage());
        System.err.println("Lang literal: " + node.getLiteralValue());
    }

    @Override
    public void caseATypedLiteralRdfLiteral(ATypedLiteralRdfLiteral node) {
        System.err.println("Typed literal: " + node.getDatatype());
        System.err.println("Typed literal: " + node.getLiteralValue());
    }

    @Override
    public void caseAUntypedLiteralRdfLiteral(AUntypedLiteralRdfLiteral node) {
        System.err.println("Untyped literal: " + node.getLiteralValue());
    }
}
