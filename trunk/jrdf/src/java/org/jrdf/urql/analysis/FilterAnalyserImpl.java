package org.jrdf.urql.analysis;

import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.EmptyOperator;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.urql.builder.LiteralBuilder;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.ABoundBuiltincall;
import org.jrdf.urql.parser.node.ARelationalExpression;
import org.jrdf.urql.parser.node.AStrBuiltincall;
import org.jrdf.urql.parser.parser.ParserException;

import java.util.Map;

public class FilterAnalyserImpl extends DepthFirstAdapter implements FilterAnalyser {
    private Expression<ExpressionVisitor> expression = new EmptyOperator<ExpressionVisitor>();
    private ParserException exception;
    private LiteralBuilder literalBuilder;
    private Map<Attribute, ValueOperation> valuePair;

    public FilterAnalyserImpl(LiteralBuilder newLiteralBuilder) {
        this.literalBuilder = newLiteralBuilder;
    }

    public Expression<ExpressionVisitor> getExpression() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        return expression;
    }

    @Override
    public void caseARelationalExpression(ARelationalExpression node) {
        NumericExpressionAnalyser numericExpressionAnalyser = new NumericExpressionAnalyserImpl(literalBuilder);
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
}
