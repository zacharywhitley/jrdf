package org.jrdf.urql.analysis;

import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.EmptyOperator;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.urql.builder.LiteralBuilder;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.ABooleanNotUnaryExpression;
import org.jrdf.urql.parser.node.ABoundBuiltincall;
import org.jrdf.urql.parser.node.AConditionalAndExpression;
import org.jrdf.urql.parser.node.AMoreValueLogical;
import org.jrdf.urql.parser.node.ARelationalExpression;
import org.jrdf.urql.parser.node.AStrBuiltincall;
import org.jrdf.urql.parser.node.PMoreValueLogical;
import org.jrdf.urql.parser.parser.ParserException;

import java.util.LinkedList;
import java.util.Map;

public class FilterAnalyserImpl extends DepthFirstAdapter implements FilterAnalyser {
    private Expression<ExpressionVisitor> expression = new EmptyOperator<ExpressionVisitor>();
    private ParserException exception;
    private LiteralBuilder literalBuilder;
    private VariableCollector collector;
    private Map<Attribute, ValueOperation> valuePair;

    public FilterAnalyserImpl(LiteralBuilder newLiteralBuilder, VariableCollector collector) {
        this.literalBuilder = newLiteralBuilder;
        this.collector = collector;
    }

    public Expression<ExpressionVisitor> getExpression() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        return expression;
    }

    @Override
    public void caseARelationalExpression(ARelationalExpression node) {
        NumericExpressionAnalyser numericExpressionAnalyser =
            new NumericExpressionAnalyserImpl(literalBuilder, collector);
        node.apply(numericExpressionAnalyser);
        try {
            valuePair = numericExpressionAnalyser.getSingleAvp();
        } catch (ParserException e) {
            exception = e;
        }
        super.caseARelationalExpression(node);
    }

    @Override
    public void caseAStrBuiltincall(AStrBuiltincall node) {
        expression = new StrOperator<ExpressionVisitor>(valuePair);
    }

    @Override
    public void caseABoundBuiltincall(ABoundBuiltincall node) {
        expression = new BoundOperator<ExpressionVisitor>(valuePair);
    }

    @Override
    public void caseABooleanNotUnaryExpression(ABooleanNotUnaryExpression node) {
        node.getPrimaryExpression().apply(this);
        try {
            final Expression<ExpressionVisitor> exp = this.getExpression();
            expression = new LogicalNotExpression<ExpressionVisitor>(exp);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAMoreValueLogical(AMoreValueLogical node) {
        FilterAnalyser analyzer = new FilterAnalyserImpl(literalBuilder, collector);
        node.getValueLogical().apply(analyzer);
        try {
            expression = analyzer.getExpression();
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAConditionalAndExpression(AConditionalAndExpression node) {
        try {
            node.getValueLogical().apply(this);
            Expression<ExpressionVisitor> exp1 = getExpression();
            final LinkedList<PMoreValueLogical> list = node.getMoreValueLogical();
            for (PMoreValueLogical rhs : list) {
                rhs.apply(this);
                Expression<ExpressionVisitor> exp2 = getExpression();
                exp1 = new LogicalAndExpression(exp1, exp2);
            }
            expression = exp1;
        } catch (ParserException e) {
            exception = e;
        }
    }
}
