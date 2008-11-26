package org.jrdf.urql.analysis;

import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.EmptyOperator;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.urql.builder.LiteralBuilder;
import org.jrdf.urql.builder.URIReferenceBuilder;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.ABooleanNotUnaryExpression;
import org.jrdf.urql.parser.node.ABoundBuiltincall;
import org.jrdf.urql.parser.node.ABracketedExpressionConstraint;
import org.jrdf.urql.parser.node.AConditionalAndExpression;
import org.jrdf.urql.parser.node.AEMoreNumericExpression;
import org.jrdf.urql.parser.node.ALtMoreNumericExpression;
import org.jrdf.urql.parser.node.AMoreValueLogical;
import org.jrdf.urql.parser.node.ANeMoreNumericExpression;
import org.jrdf.urql.parser.node.ARelationalExpression;
import org.jrdf.urql.parser.node.AStrBuiltincall;
import org.jrdf.urql.parser.node.AVariable;
import org.jrdf.urql.parser.node.PMoreNumericExpression;
import org.jrdf.urql.parser.node.PMoreValueLogical;
import org.jrdf.urql.parser.parser.ParserException;

import java.util.LinkedList;
import java.util.Map;

public class FilterAnalyserImpl<V extends ExpressionVisitor> extends DepthFirstAdapter implements FilterAnalyser {
    private LogicExpression<V> expression = new EmptyOperator<V>();
    private ParserException exception;
    private LiteralBuilder literalBuilder;
    private VariableCollector collector;
    private Map<Attribute, ValueOperation> valuePair;
    private NumericExpressionAnalyser numericExpressionAnalyser;
    private URIReferenceBuilder uriBuilder;

    public FilterAnalyserImpl(LiteralBuilder newLiteralBuilder, VariableCollector newCollector,
                              URIReferenceBuilder newUriBuilder) {
        this.literalBuilder = newLiteralBuilder;
        this.collector = newCollector;
        this.uriBuilder = newUriBuilder;
        this.numericExpressionAnalyser = new NumericExpressionAnalyserImpl(literalBuilder, collector, uriBuilder);
    }

    public LogicExpression<V> getExpression() throws ParserException {
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
    public void caseAVariable(AVariable node) {
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
            expression = new BoundOperator<V>(valuePair);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAMoreValueLogical(AMoreValueLogical node) {
        FilterAnalyserImpl<V> analyzer = new FilterAnalyserImpl<V>(literalBuilder, collector, uriBuilder);
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
            final LogicExpression<V> exp = this.getExpression();
            expression = new LogicalNotExpression<V>(exp);
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
            LogicExpression<V> exp1 = expression;
            final LinkedList<PMoreValueLogical> list = node.getMoreValueLogical();
            for (PMoreValueLogical rhs : list) {
                rhs.apply(this);
                final LogicExpression<V> exp2 = getExpression();
                exp1 = new LogicalAndExpression<V>(exp1, exp2);
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
            expression = new EqualsExpression<V>(valuePair, moreValuePair);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseANeMoreNumericExpression(ANeMoreNumericExpression node) {
        try {
            node.getNumericExpression().apply(numericExpressionAnalyser);
            Map<Attribute, ValueOperation> moreValuePair = numericExpressionAnalyser.getSingleAvp();
            expression = new NEqualsExpression<V>(valuePair, moreValuePair);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseALtMoreNumericExpression(ALtMoreNumericExpression node) {
        try {
            node.getNumericExpression().apply(numericExpressionAnalyser);
            Map<Attribute, ValueOperation> moreValuePair = numericExpressionAnalyser.getSingleAvp();
            expression = new LessThanExpression<V>(valuePair, moreValuePair);
        } catch (ParserException e) {
            exception = e;
        }
    }
}
