package org.jrdf.urql.analysis;

import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.EmptyOperator;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.mem.AVPOperation;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;
import static org.jrdf.query.relation.mem.NeqAVPOperation.NEQUALS;
import org.jrdf.query.relation.mem.ValueOperationImpl;
import org.jrdf.urql.builder.LiteralBuilder;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.ABooleanNotUnaryExpression;
import org.jrdf.urql.parser.node.ABoundBuiltincall;
import org.jrdf.urql.parser.node.ABracketedExpressionConstraint;
import org.jrdf.urql.parser.node.AConditionalAndExpression;
import org.jrdf.urql.parser.node.AEMoreNumericExpression;
import org.jrdf.urql.parser.node.AMoreValueLogical;
import org.jrdf.urql.parser.node.ANeMoreNumericExpression;
import org.jrdf.urql.parser.node.ARelationalExpression;
import org.jrdf.urql.parser.node.AStrBuiltincall;
import org.jrdf.urql.parser.node.PMoreNumericExpression;
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
    private NumericExpressionAnalyser numericExpressionAnalyser;

    public FilterAnalyserImpl(LiteralBuilder newLiteralBuilder, VariableCollector collector) {
        this.literalBuilder = newLiteralBuilder;
        this.collector = collector;
        this.numericExpressionAnalyser = new NumericExpressionAnalyserImpl(literalBuilder, collector);
    }

    public Expression<ExpressionVisitor> getExpression() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        return expression;
    }

    @Override
    public void caseARelationalExpression(ARelationalExpression node) {
        node.getNumericExpression().apply(this);
        final PMoreNumericExpression moreExpressions = node.getMoreNumericExpression();
        if (moreExpressions != null) {
            moreExpressions.apply(this);
        } else {
            super.caseARelationalExpression(node);
        }
    }

    @Override
    public void caseAStrBuiltincall(AStrBuiltincall node) {
        try {
            node.apply(numericExpressionAnalyser);
            valuePair = numericExpressionAnalyser.getSingleAvp();
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseABoundBuiltincall(ABoundBuiltincall node) {
        try {
            node.apply(numericExpressionAnalyser);
            valuePair = numericExpressionAnalyser.getSingleAvp();
            expression = new BoundOperator<ExpressionVisitor>(valuePair);
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
    public void caseABooleanNotUnaryExpression(ABooleanNotUnaryExpression node) {
        try {
            node.getPrimaryExpression().apply(this);
            final LogicExpression<ExpressionVisitor> exp = (LogicExpression) this.getExpression();
            expression = new LogicalNotExpression<ExpressionVisitor>(exp);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseABracketedExpressionConstraint(ABracketedExpressionConstraint node) {
        node.getBracketedExpression().apply(this);
    }

    @Override
    public void caseAConditionalAndExpression(AConditionalAndExpression node) {
        try {
            node.getValueLogical().apply(this);
            Expression<ExpressionVisitor> exp1 = expression;
            final LinkedList<PMoreValueLogical> list = node.getMoreValueLogical();
            for (PMoreValueLogical rhs : list) {
                rhs.apply(this);
                LogicExpression<ExpressionVisitor> exp2 = (LogicExpression) getExpression();
                exp1 = new LogicalAndExpression<ExpressionVisitor>((LogicExpression) exp1, exp2);
            }
            expression = exp1;
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAEMoreNumericExpression(AEMoreNumericExpression node) {
        try {
            node.getNumericExpression().apply(numericExpressionAnalyser);
            Map<Attribute, ValueOperation> moreValuePair = numericExpressionAnalyser.getSingleAvp();
            expression = new EqualsExpression<ExpressionVisitor>(valuePair, moreValuePair);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseANeMoreNumericExpression(ANeMoreNumericExpression node) {
        try {
            node.getNumericExpression().apply(numericExpressionAnalyser);
            Map<Attribute, ValueOperation> moreValuePair = numericExpressionAnalyser.getSingleAvp();
            expression = new EqualsExpression<ExpressionVisitor>(valuePair, moreValuePair);
            boolean changed = negateAVP(valuePair);
            if (!changed) {
                negateAVP(moreValuePair);
            }
            expression = new EqualsExpression<ExpressionVisitor>(valuePair, moreValuePair);
        } catch (ParserException e) {
            exception = e;
        }
    }

    private boolean negateAVP(Map<Attribute, ValueOperation> valuePair) {
        final Attribute attribute = valuePair.keySet().iterator().next();
        final ValueOperation valueOperation = valuePair.get(attribute);
        final AVPOperation avpOperation = valueOperation.getOperation();
        if (avpOperation.equals(EQUALS)) {
            valuePair.put(attribute, new ValueOperationImpl(valueOperation.getValue(), NEQUALS));
            return true;
        }
        return false;
    }
}
