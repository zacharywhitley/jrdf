package org.jrdf.urql.analysis;

import org.jrdf.query.expression.EmptyExpression;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicAndExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicNotExpression;
import org.jrdf.query.expression.logic.LogicOrExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import static org.jrdf.query.relation.constants.NullaryAttribute.NULLARY_ATTRIBUTE;
import org.jrdf.urql.builder.LiteralBuilder;
import org.jrdf.urql.builder.URIReferenceBuilder;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.ABooleanNotUnaryExpression;
import org.jrdf.urql.parser.node.ABracketedExpressionConstraint;
import org.jrdf.urql.parser.node.ABracketedExpressionPrimaryExpression;
import org.jrdf.urql.parser.node.AConditionalAndExpression;
import org.jrdf.urql.parser.node.AConditionalOrExpression;
import org.jrdf.urql.parser.node.AEMoreNumericExpression;
import org.jrdf.urql.parser.node.AFalseBooleanLiteral;
import org.jrdf.urql.parser.node.ALtMoreNumericExpression;
import org.jrdf.urql.parser.node.AMoreValueLogical;
import org.jrdf.urql.parser.node.ANeMoreNumericExpression;
import org.jrdf.urql.parser.node.APrimaryExpressionUnaryExpression;
import org.jrdf.urql.parser.node.ARelationalExpression;
import org.jrdf.urql.parser.node.ATrueBooleanLiteral;
import org.jrdf.urql.parser.node.PMoreConditionalAndExpression;
import org.jrdf.urql.parser.node.PMoreNumericExpression;
import org.jrdf.urql.parser.node.PMoreValueLogical;
import org.jrdf.urql.parser.node.PPrimaryExpression;
import org.jrdf.urql.parser.parser.ParserException;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class FilterAnalyserImpl<V extends ExpressionVisitor> extends DepthFirstAdapter implements FilterAnalyser {
    private Expression<V> expression = new EmptyExpression<V>();
    private ParserException exception;
    private VariableCollector collector;
    private LiteralBuilder literalBuilder;
    private URIReferenceBuilder uriBuilder;
    private NumericExpressionAnalyser numericExpressionAnalyser;

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
        return (LogicExpression<V>) expression;
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
    public void caseAPrimaryExpressionUnaryExpression(APrimaryExpressionUnaryExpression node) {
        try {
            PPrimaryExpression primaryExpression = node.getPrimaryExpression();
            if (primaryExpression instanceof ABracketedExpressionPrimaryExpression) {
                primaryExpression.apply(this);
            } else {
                primaryExpression.apply(numericExpressionAnalyser);
                expression = numericExpressionAnalyser.getExpression();
            }
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
            final LogicExpression<V> exp;
            PPrimaryExpression primaryExpression = node.getPrimaryExpression();
            if (primaryExpression instanceof ABracketedExpressionPrimaryExpression) {
                primaryExpression.apply(this);
                exp = (LogicExpression<V>) expression;
            } else {
                primaryExpression.apply(numericExpressionAnalyser);
                exp = (LogicExpression<V>) numericExpressionAnalyser.getExpression();
            }
            expression = new LogicNotExpression<V>(exp);
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
            LogicExpression<V> exp1 = (LogicExpression<V>) expression;
            final LinkedList<PMoreValueLogical> list = node.getMoreValueLogical();
            for (PMoreValueLogical rhs : list) {
                rhs.apply(this);
                final LogicExpression<V> exp2 = getExpression();
                exp1 = new LogicAndExpression<V>(exp1, exp2);
            }
            expression = exp1;
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAConditionalOrExpression(AConditionalOrExpression node) {
        try {
            node.getConditionalAndExpression().apply(this);
            LogicExpression<V> exp1 = (LogicExpression<V>) expression;
            final LinkedList<PMoreConditionalAndExpression> list = node.getMoreConditionalAndExpression();
            for (PMoreConditionalAndExpression rhs : list) {
                rhs.apply(this);
                final LogicExpression<V> exp2 = getExpression();
                exp1 = new LogicOrExpression<V>(exp1, exp2);
            }
            expression = exp1;
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAEMoreNumericExpression(AEMoreNumericExpression node) {
        try {
            Expression<V> lhsExp = expression;
            node.getNumericExpression().apply(numericExpressionAnalyser);
            Expression<V> rhsExp = numericExpressionAnalyser.getExpression();
            Expression<V>[] expressions = tryUpdateAttribute(lhsExp, rhsExp);
            expression = new EqualsExpression<V>(expressions[0], expressions[1]);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseANeMoreNumericExpression(ANeMoreNumericExpression node) {
        try {
            Expression<V> lhsExp = expression;
            node.getNumericExpression().apply(numericExpressionAnalyser);
            Expression<V> rhsExp = numericExpressionAnalyser.getExpression();
            Expression<V>[] expressions = tryUpdateAttribute(lhsExp, rhsExp);
            expression = new NEqualsExpression<V>(expressions[0], expressions[1]);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseALtMoreNumericExpression(ALtMoreNumericExpression node) {
        try {
            Expression<V> lhsExp = expression;
            node.getNumericExpression().apply(numericExpressionAnalyser);
            Expression<V> rhsExp = numericExpressionAnalyser.getExpression();
            Expression<V>[] expressions = tryUpdateAttribute(lhsExp, rhsExp);
            expression = new LessThanExpression<V>(expressions[0], expressions[1]);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseATrueBooleanLiteral(ATrueBooleanLiteral node) {
        try {
            node.apply(numericExpressionAnalyser);
            expression = (LogicExpression<V>) numericExpressionAnalyser.getExpression();
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAFalseBooleanLiteral(AFalseBooleanLiteral node) {
        try {
            node.apply(numericExpressionAnalyser);
            expression = (LogicExpression<V>) numericExpressionAnalyser.getExpression();
        } catch (ParserException e) {
            exception = e;
        }
    }

    private Expression<V>[] tryUpdateAttribute(Expression<V> lhs, Expression<V> rhs) {
        Expression<V>[] result = new Expression[2];
        Set<Attribute> lhsAttrs = lhs.getAVO().keySet();
        Set<Attribute> rhsAttrs = rhs.getAVO().keySet();
        result[0] = updateoneExpression(lhs, lhsAttrs, rhsAttrs);
        result[1] = updateoneExpression(rhs, rhsAttrs, lhsAttrs);
        return result;
    }

    private Expression<V> updateoneExpression(Expression<V> lhs, Set<Attribute> lhsAttrs, Set<Attribute> rhsAttrs) {
        if (lhs instanceof SingleValue && lhsAttrs.contains(NULLARY_ATTRIBUTE) && rhsAttrs.size() == 1) {
            Map<Attribute, ValueOperation> map = lhs.getAVO();
            ValueOperation vo = map.get(NULLARY_ATTRIBUTE);
            map.clear();
            map.put(rhsAttrs.iterator().next(), vo);
            ((SingleValue) lhs).setAVO(map);
        }
        return lhs;
    }
}